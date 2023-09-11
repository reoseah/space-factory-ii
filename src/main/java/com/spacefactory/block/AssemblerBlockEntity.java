package com.spacefactory.block;

import com.google.common.collect.ImmutableSet;
import com.spacefactory.SpaceFactory;
import com.spacefactory.screen.AssemblerScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class AssemblerBlockEntity extends BlockEntity implements Inventory, NamedScreenHandlerFactory {
    public static final BlockEntityType<AssemblerBlockEntity> TYPE = new BlockEntityType<>(AssemblerBlockEntity::new, ImmutableSet.of(SpaceFactory.ASSEMBLER), null);

    public AssemblerBlockEntity(BlockPos pos, BlockState state) {
        super(TYPE, pos, state);
    }

    @Override
    public Text getDisplayName() {
        return SpaceFactory.ASSEMBLER.getName();
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new AssemblerScreenHandler(syncId, this, playerInventory);
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public ItemStack getStack(int slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeStack(int slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {

    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return this.pos.getSquaredDistance(player.getPos()) < 64;
    }

    @Override
    public void clear() {

    }
}
