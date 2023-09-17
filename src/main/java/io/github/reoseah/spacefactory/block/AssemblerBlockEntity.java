package io.github.reoseah.spacefactory.block;

import com.google.common.collect.ImmutableSet;
import io.github.reoseah.spacefactory.SpaceFactory;
import io.github.reoseah.spacefactory.recipe.AssemblerRecipe;
import io.github.reoseah.spacefactory.screen.AssemblerScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class AssemblerBlockEntity extends MachineBlockEntity implements NamedScreenHandlerFactory {
    public static final BlockEntityType<AssemblerBlockEntity> TYPE = new BlockEntityType<>(AssemblerBlockEntity::new, ImmutableSet.of(SpaceFactory.ASSEMBLER), null);

    public static final int ENERGY_CAPACITY = 100_000;
    public static final int INVENTORY_SIZE = 7;
    public static final int[] INPUT_SLOTS = {0, 1, 2, 3, 4, 5};
    public static final int OUTPUT_SLOT = 6;

    private @Nullable AssemblerRecipe selectedRecipe;
    // we don't have RecipeManager when reading NBT, so store the ID
    // and resolve it later in the tick method
    protected @Nullable Identifier selectedRecipeId;

    private int recipeProgress;

    public AssemblerBlockEntity(BlockPos pos, BlockState state) {
        super(TYPE, pos, state);
    }

    @Override
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return new AssemblerScreenHandler(syncId, this, playerInventory);
    }

    @Override
    protected DefaultedList<ItemStack> createSlotsList() {
        return DefaultedList.ofSize(INVENTORY_SIZE, ItemStack.EMPTY);
    }

    @Override
    public int getEnergyCapacity() {
        return ENERGY_CAPACITY;
    }

    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
        if (tag.contains("SelectedRecipe", NbtElement.STRING_TYPE)) {
            try {
                this.selectedRecipeId = new Identifier(tag.getString("SelectedRecipe"));
            } catch (Exception e) {
                SpaceFactory.LOGGER.error("Error reading selected recipe", e);
            }
        } else {
            this.selectedRecipeId = null;
        }
        this.recipeProgress = tag.getInt("RecipeProgress");
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        if (this.getSelectedRecipe() != null) {
            nbt.putString("SelectedRecipe", this.getSelectedRecipe().getId().toString());
        }
        nbt.putInt("RecipeProgress", this.recipeProgress);
    }

    public @Nullable AssemblerRecipe getSelectedRecipe() {
        return this.selectedRecipe;
    }

    public void setSelectedRecipe(@Nullable AssemblerRecipe selectedRecipe) {
        this.selectedRecipe = selectedRecipe;
        this.recipeProgress = 0;
        this.markDirty();
    }

    @Override
    protected void tick() {
        super.tick();
//        boolean wasBurning = this.getCachedState().get(Properties.LIT);
        if (this.selectedRecipeId != null) {
            this.setSelectedRecipe((AssemblerRecipe) world.getRecipeManager()
                    .get(this.selectedRecipeId)
                    .filter(recipe -> recipe instanceof AssemblerRecipe)
                    .orElse(null));
            this.selectedRecipeId = null;
        }
        AssemblerRecipe recipe = this.getSelectedRecipe();
        if (recipe != null && recipe.matches(this, world) && this.canAcceptRecipeOutput(recipe)) {
            if (this.energy > 0) {
                this.energy--;
                this.recipeProgress++;
                if (this.recipeProgress >= recipe.energy) {
                    this.craftRecipe(recipe);
                    this.recipeProgress = 0;
                }
                this.markDirty();
            }
        } else if (this.recipeProgress > 0) {
            this.recipeProgress = 0;
            this.markDirty();
        }
    }

    protected boolean canAcceptRecipeOutput(AssemblerRecipe recipe) {
        return this.canAcceptStack(OUTPUT_SLOT, recipe.getOutput(this.world.getRegistryManager()));
    }

    protected void craftRecipe(AssemblerRecipe recipe) {
        for (int i = 0; i < recipe.getIngredients().size(); i++) {
            this.getStack(i).decrement(1);
        }
        this.acceptStack(OUTPUT_SLOT, recipe.craft(this, this.world.getRegistryManager()));
    }

    public int getRecipeProgress() {
        return this.recipeProgress;
    }
}
