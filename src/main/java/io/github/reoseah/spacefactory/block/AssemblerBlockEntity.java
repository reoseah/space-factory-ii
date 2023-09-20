package io.github.reoseah.spacefactory.block;

import com.google.common.collect.ImmutableSet;
import io.github.reoseah.spacefactory.SpaceFactory;
import io.github.reoseah.spacefactory.recipe.AssemblerRecipe;
import io.github.reoseah.spacefactory.screen.AssemblerScreenHandler;
import it.unimi.dsi.fastutil.ints.IntArrays;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

public class AssemblerBlockEntity extends MachineBlockEntity implements NamedScreenHandlerFactory, SidedInventory {
    public static final BlockEntityType<AssemblerBlockEntity> TYPE = new BlockEntityType<>(AssemblerBlockEntity::new, ImmutableSet.of(SpaceFactory.ASSEMBLER), null);

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
        return SpaceFactory.config.getAssemblerEnergyCapacity();
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

    @Override
    public int[] getAvailableSlots(Direction side) {
        return switch (side) {
            case DOWN -> new int[]{OUTPUT_SLOT};
            default -> {
                int[] slots = IntArrays.copy(INPUT_SLOTS);
                IntArrays.mergeSort(slots, (a, b) -> Integer.compare(this.getStack(a).getCount(), this.getStack(b).getCount()));
                yield slots;
            }
        };
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return switch (slot) {
            case OUTPUT_SLOT -> false;
            default -> this.getSelectedRecipe() != null
                    && slot < this.getSelectedRecipe().getIngredients().size()
                    && this.getSelectedRecipe().getIngredients().get(slot).test(stack);
        };
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return slot == OUTPUT_SLOT;
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
        boolean wasActive = this.getCachedState().get(Properties.LIT);
        boolean isActive = false;
        if (this.selectedRecipeId != null) {
            this.setSelectedRecipe((AssemblerRecipe) this.world.getRecipeManager()
                    .get(this.selectedRecipeId)
                    .filter(recipe -> recipe instanceof AssemblerRecipe)
                    .orElse(null));
            this.selectedRecipeId = null;
        }
        AssemblerRecipe recipe = this.getSelectedRecipe();
        if (recipe != null && recipe.matches(this, this.world) && this.canAcceptRecipeOutput(recipe)) {
            if (this.energy > 0) {
                int amount = Math.min(Math.min(this.energy, SpaceFactory.config.getAssemblerEnergyConsumption()), recipe.energy - this.recipeProgress);
                this.energy -= amount;
                this.recipeProgress += amount;
                isActive = true;
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

        if (isActive != wasActive) {
            this.world.setBlockState(this.pos, this.getCachedState().with(AssemblerBlock.LIT, isActive));
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
