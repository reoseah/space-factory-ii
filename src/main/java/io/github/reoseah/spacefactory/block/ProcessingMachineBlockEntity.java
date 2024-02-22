package io.github.reoseah.spacefactory.block;

import io.github.reoseah.spacefactory.SpaceFactory;
import io.github.reoseah.spacefactory.api.ProcessingMachine;
import lombok.Getter;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

public abstract class ProcessingMachineBlockEntity<R extends Recipe<? super ProcessingMachineBlockEntity<R>>> extends MachineBlockEntity implements SidedInventory {
    protected @Nullable R selectedRecipe;
    // we don't have RecipeManager when reading NBT, so store the ID
    // and resolve it later in the tick method
    protected @Nullable Identifier selectedRecipeId;

    @Getter
    protected int recipeProgress;

    protected ProcessingMachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public @Nullable R getSelectedRecipe() {
        return this.selectedRecipe;
    }

    public void setSelectedRecipe(@Nullable R selectedRecipe) {
        this.selectedRecipe = selectedRecipe;
        this.recipeProgress = 0;
        this.markDirty();
    }

    protected abstract ProcessingMachine getMachineType();

    protected abstract boolean canAcceptRecipeOutput(R recipe);

    protected abstract void craftRecipe(R recipe);

    public abstract int getEnergyConsumption();

    protected RecipeType<R> getRecipeType() {
        return this.getMachineType().getRecipeType();
    }

    public int getRecipeEnergy(R recipe) {
        return this.getMachineType().getRecipeEnergy(recipe);
    }

    @Override
    protected DefaultedList<ItemStack> createSlotsList() {
        return DefaultedList.ofSize(this.getMachineType().inventorySize, ItemStack.EMPTY);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        if (nbt.contains("SelectedRecipe", NbtElement.STRING_TYPE)) {
            try {
                this.selectedRecipeId = new Identifier(nbt.getString("SelectedRecipe"));
            } catch (Exception e) {
                SpaceFactory.LOGGER.error("Error reading selected recipe", e);
            }
        } else {
            this.selectedRecipeId = null;
        }
        this.recipeProgress = nbt.getInt("RecipeProgress");
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        if (this.selectedRecipe != null) {
            nbt.putString("SelectedRecipe", this.selectedRecipe.getId().toString());
        }
        nbt.putInt("RecipeProgress", this.recipeProgress);
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        if (slot >= this.getMachineType().inputCount) {
            return false;
        }
        return this.getSelectedRecipe() != null
                && slot < this.getSelectedRecipe().getIngredients().size()
                && this.getSelectedRecipe().getIngredients().get(slot).test(stack);
    }

    @Override
    protected void tick() {
        super.tick();

        if (this.selectedRecipeId != null) {
            this.setSelectedRecipe(this.world.getRecipeManager()
                    .get(this.selectedRecipeId)
                    .filter(recipe -> recipe.getType() == this.getRecipeType())
                    .map(recipe -> (R) recipe)
                    .orElse(null));
            this.selectedRecipeId = null;
        }

        boolean wasActive = this.getCachedState().get(Properties.LIT);
        boolean active = false;

        R recipe = this.getSelectedRecipe();
        if (recipe != null && recipe.matches(this, this.world) && this.canAcceptRecipeOutput(recipe)) {
            if (this.energy > 0) {
                int amount = Math.min(Math.min(this.energy, this.getEnergyConsumption()), this.getRecipeEnergy(recipe) - this.recipeProgress);
                this.energy -= amount;
                this.recipeProgress += amount;
                active = true;
                if (this.recipeProgress >= this.getRecipeEnergy(recipe)) {
                    this.craftRecipe(recipe);
                    this.recipeProgress = 0;
                }
                this.markDirty();
            }
        } else if (this.recipeProgress > 0) {
            this.recipeProgress = 0;
            this.markDirty();
        }

        if (active != wasActive) {
            this.world.setBlockState(this.pos, this.getCachedState().with(AssemblerBlock.LIT, active));
        }
    }

}
