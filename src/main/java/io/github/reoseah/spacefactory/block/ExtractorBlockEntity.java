package io.github.reoseah.spacefactory.block;

import com.google.common.collect.ImmutableSet;
import io.github.reoseah.spacefactory.SpaceFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;

public class ExtractorBlockEntity extends MachineBlockEntity {
    public static final BlockEntityType<ExtractorBlockEntity> TYPE = new BlockEntityType<>(ExtractorBlockEntity::new, ImmutableSet.of(SpaceFactory.ASSEMBLER), null);

    public static final int INVENTORY_SIZE = 5;

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
        return null;
    }
}
