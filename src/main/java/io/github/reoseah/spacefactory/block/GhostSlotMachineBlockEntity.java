package io.github.reoseah.spacefactory.block;

import io.github.reoseah.spacefactory.SpaceFactory;
import lombok.Getter;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public abstract class GhostSlotMachineBlockEntity<R extends Recipe<? super GhostSlotMachineBlockEntity<R>>> extends MachineBlockEntity {
    protected @Nullable R selectedRecipe;
    // we don't have RecipeManager when reading NBT, so store the ID
    // and resolve it later in the tick method
    protected @Nullable Identifier selectedRecipeId;

    @Getter
    protected int recipeProgress;

    protected GhostSlotMachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
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

    protected abstract RecipeType<R> getRecipeType();

    public abstract int getRecipeEnergy(R recipe);

    protected abstract boolean canAcceptRecipeOutput(R recipe);

    protected abstract void craftRecipe(R recipe);

    public abstract int getEnergyConsumption();

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
    protected void tick() {
        super.tick();
        boolean wasActive = this.getCachedState().get(Properties.LIT);
        boolean active = false;
        if (this.selectedRecipeId != null) {
            this.setSelectedRecipe(this.world.getRecipeManager()
                    .get(this.selectedRecipeId)
                    .filter(recipe -> recipe.getType() == this.getRecipeType())
                    .map(recipe -> (R) recipe)
                    .orElse(null));
            this.selectedRecipeId = null;
        }
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
