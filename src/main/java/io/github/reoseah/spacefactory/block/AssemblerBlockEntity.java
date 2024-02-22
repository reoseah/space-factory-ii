package io.github.reoseah.spacefactory.block;

import com.google.common.collect.ImmutableSet;
import io.github.reoseah.spacefactory.SpaceFactory;
import io.github.reoseah.spacefactory.api.ProcessingMachine;
import io.github.reoseah.spacefactory.recipe.AssemblerRecipe;
import io.github.reoseah.spacefactory.screen.AssemblerScreenHandler;
import it.unimi.dsi.fastutil.ints.IntArrays;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

public class AssemblerBlockEntity extends ProcessingMachineBlockEntity<AssemblerRecipe> implements NamedScreenHandlerFactory, SidedInventory {
    public static final BlockEntityType<AssemblerBlockEntity> TYPE = new BlockEntityType<>(AssemblerBlockEntity::new, ImmutableSet.of(SpaceFactory.ASSEMBLER), null);

    public static final int[] INPUT_SLOTS = {0, 1, 2, 3, 4, 5};
    public static final int OUTPUT_SLOT = 6;

    public AssemblerBlockEntity(BlockPos pos, BlockState state) {
        super(TYPE, pos, state);
    }

    @Override
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return new AssemblerScreenHandler(syncId, this, playerInventory);
    }

    @Override
    public int getEnergyCapacity() {
        return SpaceFactory.config.getAssemblerEnergyCapacity();
    }

    @Override
    public int getEnergyConsumption() {
        return SpaceFactory.config.getAssemblerEnergyConsumption();
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        if (side == Direction.DOWN) {
            return new int[]{OUTPUT_SLOT};
        }
        int[] slots = IntArrays.copy(INPUT_SLOTS);
        IntArrays.mergeSort(slots, (a, b) -> Integer.compare(this.getStack(a).getCount(), this.getStack(b).getCount()));
        return slots;
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return slot == OUTPUT_SLOT;
    }

    @Override
    protected ProcessingMachine getMachineType() {
        return ProcessingMachine.ASSEMBLER;
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
}
