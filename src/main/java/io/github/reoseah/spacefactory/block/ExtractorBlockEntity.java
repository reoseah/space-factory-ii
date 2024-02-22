package io.github.reoseah.spacefactory.block;

import com.google.common.collect.ImmutableSet;
import io.github.reoseah.spacefactory.SpaceFactory;
import io.github.reoseah.spacefactory.api.ProcessingMachine;
import io.github.reoseah.spacefactory.recipe.ExtractorRecipe;
import io.github.reoseah.spacefactory.screen.ExtractorScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

public class ExtractorBlockEntity extends ProcessingMachineBlockEntity<ExtractorRecipe> {
    public static final BlockEntityType<ExtractorBlockEntity> TYPE = new BlockEntityType<>(ExtractorBlockEntity::new, ImmutableSet.of(SpaceFactory.EXTRACTOR), null);

    public static final int INPUTS_COUNT = 1, RESULTS_COUNT = 4;

    public ExtractorBlockEntity(BlockPos pos, BlockState state) {
        super(TYPE, pos, state);
    }

    @Override
    public int getEnergyCapacity() {
        return SpaceFactory.config.getExtractorEnergyCapacity();
    }

    @Override
    public int getEnergyConsumption() {
        return SpaceFactory.config.getExtractorEnergyConsumption();
    }

    @Override
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return new ExtractorScreenHandler(syncId, this, playerInventory);
    }

    @Override
    protected ProcessingMachine getMachineType() {
        return ProcessingMachine.EXTRACTOR;
    }

    @Override
    protected boolean canAcceptRecipeOutput(ExtractorRecipe recipe) {
        for (int i = 0; i < recipe.outputs.length && i < RESULTS_COUNT; i++) {
            if (!this.canFullyAddStack(INPUTS_COUNT + i, recipe.outputs[i].left())) {
                return false;
            }
        }
        return true;
    }

    @Override
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
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return true;
    }
}
