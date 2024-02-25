package io.github.reoseah.spacefactory.block;

import io.github.reoseah.spacefactory.SpaceFactory;
import io.github.reoseah.spacefactory.recipe.ProcessingRecipeType;
import it.unimi.dsi.fastutil.ints.IntArrays;
import lombok.Getter;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.recipe.Recipe;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.stream.IntStream;

public abstract class ProcessingMachineBlockEntity<R extends Recipe<Inventory>> extends MachineBlockEntity implements SidedInventory {
    protected @Nullable R selectedRecipe;
    // we don't have RecipeManager when reading NBT, so store the ID
    // and resolve it later in the tick method
    protected @Nullable Identifier selectedRecipeId;

    @Getter
    protected int recipeProgress;

    protected ProcessingMachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public abstract ProcessingRecipeType<R> getRecipeType();

    public abstract int getEnergyConsumption();

    public @Nullable R getSelectedRecipe() {
        return this.selectedRecipe;
    }

    public void setSelectedRecipe(@Nullable R selectedRecipe) {
        this.selectedRecipe = selectedRecipe;
        this.recipeProgress = 0;
        this.markDirty();
    }

    @Override
    protected void tick() {
        super.tick();
        assert this.world != null;

        if (this.selectedRecipeId != null) {
            //noinspection unchecked
            this.setSelectedRecipe((R) this.world.getRecipeManager().get(this.selectedRecipeId) //
                    .filter(recipe -> recipe.getType() == this.getRecipeType()) //
                    .orElse(null));
            this.selectedRecipeId = null;
        }

        boolean wasActive = this.getCachedState().get(Properties.LIT);
        boolean active = false;

        R recipe = this.getSelectedRecipe();
        if (recipe != null && recipe.matches(this, this.world) && this.getRecipeType().canAcceptOutput(this, recipe, this.world.getRegistryManager())) {
            if (this.energy > 0) {
                int amount = Math.min(Math.min(this.energy, this.getEnergyConsumption()), this.getRecipeType().getRecipeEnergy(recipe) - this.recipeProgress);
                this.energy -= amount;
                this.recipeProgress += amount;
                active = true;
                if (this.recipeProgress >= this.getRecipeType().getRecipeEnergy(recipe)) {
                    this.getRecipeType().craftRecipe(this, recipe, this.world.random, this.world);
                    this.recipeProgress = 0;
                }
                this.markDirty();
            }
        } else if (this.recipeProgress > 0) {
            this.recipeProgress = 0;
            this.markDirty();
        }

        if (active != wasActive) {
            this.world.setBlockState(this.pos, this.getCachedState().with(MachineBlock.LIT, active));
        }
    }

    @Override
    protected DefaultedList<ItemStack> createSlotsList() {
        return DefaultedList.ofSize(this.getRecipeType().inputCount + this.getRecipeType().outputCount, ItemStack.EMPTY);
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
        if (slot >= this.getRecipeType().inputCount) {
            return false;
        }
        return this.getSelectedRecipe() != null //
                && slot < this.getSelectedRecipe().getIngredients().size() //
                && this.getSelectedRecipe().getIngredients().get(slot).test(stack);
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return true;
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        int inputCount = this.getRecipeType().inputCount;
        if (side == Direction.DOWN) {
            int outputCount = this.getRecipeType().outputCount;
            return IntStream.range(inputCount, inputCount + outputCount).toArray();
        }
        if (inputCount == 1) {
            return new int[]{0};
        }
        int[] slots = IntStream.range(0, inputCount).toArray();
        IntArrays.mergeSort(slots, (a, b) -> Integer.compare(this.getStack(a).getCount(), this.getStack(b).getCount()));
        return slots;
    }
}
