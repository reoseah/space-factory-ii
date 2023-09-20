package io.github.reoseah.spacefactory.block;

import com.google.common.collect.ImmutableSet;
import io.github.reoseah.spacefactory.SpaceFactory;
import io.github.reoseah.spacefactory.recipe.ExtractorRecipe;
import io.github.reoseah.spacefactory.screen.ExtractorScreenHandler;
import lombok.Getter;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.state.property.Properties;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ExtractorBlockEntity extends MachineBlockEntity implements SidedInventory {
    public static final BlockEntityType<ExtractorBlockEntity> TYPE = new BlockEntityType<>(ExtractorBlockEntity::new, ImmutableSet.of(SpaceFactory.ASSEMBLER), null);

    public static final int INVENTORY_SIZE = 5, INPUTS_COUNT = 1, RESULTS_COUNT = 4;

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    protected @Nullable Optional<ExtractorRecipe> cachedRecipe;
    @Getter
    protected int recipeProgress;

    public ExtractorBlockEntity(BlockPos pos, BlockState state) {
        super(TYPE, pos, state);
    }

    @Override
    protected DefaultedList<ItemStack> createSlotsList() {
        return DefaultedList.ofSize(INVENTORY_SIZE, ItemStack.EMPTY);
    }

    @Override
    public int getEnergyCapacity() {
        return SpaceFactory.config.getExtractorEnergyCapacity();
    }

    @Override
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return new ExtractorScreenHandler(syncId, this, playerInventory);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.recipeProgress = Math.max(nbt.getInt("RecipeProgress"), 0);
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putInt("RecipeProgress", this.recipeProgress);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        // could test if current recipe still matches instead of searching for a recipe again
        // for theoretical performance improvements
        if (slot < INPUTS_COUNT && shouldUpdateRecipe(stack, this.slots.get(slot))) {
            this.resetCachedRecipe();
        }
        this.slots.set(slot, stack);
    }

    private static boolean shouldUpdateRecipe(ItemStack stack, ItemStack previous) {
        return stack.isEmpty() || !ItemStack.areEqual(stack, previous);
    }

    @SuppressWarnings("OptionalAssignedToNull")
    protected void resetCachedRecipe() {
        this.cachedRecipe = null;
        this.recipeProgress = 0;
    }

    @SuppressWarnings("OptionalAssignedToNull")
    protected ExtractorRecipe getRecipe(World world) {
        if (this.cachedRecipe != null) {
            return this.cachedRecipe.orElse(null);
        }
        if (world == null) {
            return null;
        }

        // small optimization to not search recipe for an empty machine
        boolean hasInput = false;
        for (int i = 0; i < INPUTS_COUNT; i++) {
            if (!this.getStack(i).isEmpty()) {
                hasInput = true;
                break;
            }
        }
        if (hasInput) {
            this.cachedRecipe = this.findRecipeInternal(world);
        }
        return this.cachedRecipe == null ? null : this.cachedRecipe.orElse(null);
    }

    protected Optional<ExtractorRecipe> findRecipeInternal(@NotNull World world) {
        return world.getRecipeManager() //
                .getFirstMatch(ExtractorRecipe.TYPE, this, world);
    }

    public int getRecipeEnergy() {
        return this.cachedRecipe != null && this.cachedRecipe.isPresent() ? this.cachedRecipe.orElse(null).energy : 0;
    }

    @Override
    public void tick() {
        super.tick();

        boolean wasActive = getCachedState().get(Properties.LIT);
        boolean active = false;

        ExtractorRecipe recipe = this.getRecipe(this.world);

        if (recipe != null && this.canCraft(recipe)) {
            int energyConsumption = Math.min(SpaceFactory.config.getExtractorEnergyConsumption(),
                    recipe.energy - this.recipeProgress);
            if (this.energy >= energyConsumption) {
                this.recipeProgress += energyConsumption;
                this.energy -= energyConsumption;
                active = true;

                if (this.recipeProgress >= recipe.energy) {
                    this.craftRecipe(recipe);
                    this.recipeProgress = 0;
                }
            } else {
                this.recipeProgress = Math.max(this.recipeProgress - 2, 0);
            }
            this.markDirty();
        } else if (this.recipeProgress > 0) {
            this.recipeProgress = 0;
            this.markDirty();
        }
        if (wasActive != active) {
            this.world.setBlockState(this.pos, this.getCachedState().with(Properties.LIT, active), 3);
        }
    }

    protected boolean canCraft(ExtractorRecipe recipe) {
//        if (!super.canCraft(recipe)) {
//            return false;
//        }
        for (int i = 0; i < recipe.outputs.length && i < RESULTS_COUNT; i++) {
            if (!this.canFullyAddStack(INPUTS_COUNT + i, recipe.outputs[i].left())) {
                return false;
            }
        }
        return true;
    }

    protected void craftRecipe(ExtractorRecipe recipe) {
        ItemStack input = this.slots.get(0);
        input.decrement(recipe.ingredientCount);
        // also updates cached recipe if needed
        this.setStack(0, input);

        for (int i = 0; i < recipe.outputs.length && i < RESULTS_COUNT; i++) {
            if (this.world.random.nextFloat() <= recipe.outputs[i].rightFloat()) {
                this.addStack(INPUTS_COUNT + i, recipe.outputs[i].left().copy());
            }
        }
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        return switch (side) {
            case UP -> new int[]{0};
            default -> new int[]{0, 1, 2, 3, 4};
        };
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction side) {
        return slot == 0;
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return true;
    }
}
