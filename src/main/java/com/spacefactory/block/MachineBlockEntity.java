package com.spacefactory.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;

public abstract class MachineBlockEntity extends LockableContainerBlockEntity {
    protected final DefaultedList<ItemStack> slots;

    protected MachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.slots = this.createSlotsList();
    }

    protected abstract DefaultedList<ItemStack> createSlotsList();

    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
        this.slots.clear();
        Inventories.readNbt(tag, this.slots);
    }

    @Override
    public void writeNbt(NbtCompound tag) {
        super.writeNbt(tag);
        Inventories.writeNbt(tag, this.slots);
    }

    @Override
    protected Text getContainerName() {
        return this.getCachedState().getBlock().getName();
    }

    // region Inventory
    @Override
    public int size() {
        return this.slots.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : this.slots) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getStack(int slot) {
        return this.slots.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        return Inventories.splitStack(this.slots, slot, amount);
    }

    @Override
    public ItemStack removeStack(int slot) {
        return Inventories.removeStack(this.slots, slot);
    }

    @Override
    public void setStack(int slot, ItemStack newStack) {
        this.slots.set(slot, newStack);
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public boolean canPlayerUse(PlayerEntity player) {
        return this.world.getBlockEntity(this.pos) == this
                && this.pos.getSquaredDistance(player.getPos()) <= 64;
    }

    @Override
    public void clear() {
        this.slots.clear();
    }
    // endregion
}
