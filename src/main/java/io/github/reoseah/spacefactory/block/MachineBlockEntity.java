package io.github.reoseah.spacefactory.block;

import lombok.Getter;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
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
import net.minecraft.util.math.MathHelper;
import team.reborn.energy.api.EnergyStorage;

public abstract class MachineBlockEntity extends LockableContainerBlockEntity {
    protected final DefaultedList<ItemStack> slots;
    @Getter
    protected int energy, energyPerTick;
    @Getter
    protected float averageEnergyPerTick;

    protected MachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.slots = this.createSlotsList();
    }

    protected abstract DefaultedList<ItemStack> createSlotsList();

    public abstract int getEnergyCapacity();

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.slots.clear();
        Inventories.readNbt(nbt, this.slots);
        this.energy = MathHelper.clamp(nbt.getInt("Energy"), 0, this.getEnergyCapacity());
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, this.slots);
        nbt.putInt("Energy", this.energy);
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


    protected void tick() {
        this.averageEnergyPerTick = MathHelper.lerp(0.05F, this.averageEnergyPerTick, this.energyPerTick);
        this.energyPerTick = 0;
    }

    public EnergyStorage createEnergyStorage() {
        return new EnergyStorageImpl(this);
    }

    @SuppressWarnings("UnstableApiUsage")
    protected static class EnergyStorageImpl implements EnergyStorage {
        protected final MachineBlockEntity be;

        public EnergyStorageImpl(MachineBlockEntity be) {
            this.be = be;
        }

        @Override
        public long insert(long maxAmount, TransactionContext transaction) {
            int amount = (int) Math.min(this.getCapacity() - be.energy, maxAmount);
            transaction.addCloseCallback((ctx, result) -> {
                if (result == TransactionContext.Result.COMMITTED) {
                    be.energy += amount;
                    be.energyPerTick += amount;
                }
            });
            return amount;
        }

        @Override
        public long extract(long maxAmount, TransactionContext transaction) {
            return 0;
        }

        @Override
        public long getAmount() {
            return be.energy;
        }

        @Override
        public long getCapacity() {
            return be.getEnergyCapacity();
        }
    }
//    protected boolean canFullyAddStack(int slot, ItemStack offer) {
//        ItemStack stackInSlot = this.getStack(slot);
//        if (stackInSlot.isEmpty() || offer.isEmpty()) {
//            return true;
//        }
//        return ItemStack.canCombine(stackInSlot, offer) //
//                && stackInSlot.getCount() + offer.getCount() <= Math.min(stackInSlot.getMaxCount(), this.getMaxCountPerStack());
//    }
//
//    protected void addStack(int slot, ItemStack stack) {
//        ItemStack stackInSlot = this.getStack(slot);
//        if (stackInSlot.isEmpty()) {
//            this.setStack(slot, stack);
//        } else if (stackInSlot.getItem() == stack.getItem()) {
//            stackInSlot.increment(stack.getCount());
//        }
//        this.markDirty();
//    }
}
