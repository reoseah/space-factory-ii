package io.github.reoseah.spacefactory.block;

import com.google.common.collect.ImmutableSet;
import io.github.reoseah.spacefactory.SpaceFactory;
import io.github.reoseah.spacefactory.screen.BedrockMinerScreenHandler;
import it.unimi.dsi.fastutil.objects.ObjectFloatPair;
import lombok.Getter;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.Util;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BedrockMinerBlockEntity extends MachineBlockEntity {
    public static final BlockEntityType<BedrockMinerBlockEntity> TYPE = new BlockEntityType<>(BedrockMinerBlockEntity::new, ImmutableSet.of(SpaceFactory.BEDROCK_MINER), null);

    public static final int INVENTORY_SIZE = 7, INPUTS_COUNT = 1, RESULTS_COUNT = 6;
    public static final int DRILL_SUPPLIES_TOTAL = 120 * 20;
    public static final int DRILLLING_DURATION = 10 * 20;

    public static final Map<Block, List<ObjectFloatPair<ItemStack>>> MAP = Util.make(new HashMap<>(), map -> {
        map.put(Blocks.BEDROCK, Util.make(new ArrayList<>(), list -> {
            list.add(ObjectFloatPair.of(new ItemStack(Items.RAW_IRON), 0.05F));
            list.add(ObjectFloatPair.of(new ItemStack(Items.RAW_COPPER), 0.05F));
            list.add(ObjectFloatPair.of(new ItemStack(Items.REDSTONE), 0.025F));
            list.add(ObjectFloatPair.of(new ItemStack(Items.RAW_GOLD), 0.01F));
        }));
        map.put(SpaceFactory.BEDROCK_IRON_ORE, Util.make(new ArrayList<>(), list -> {
            list.add(ObjectFloatPair.of(new ItemStack(Items.RAW_IRON), 0.5F));
            list.add(ObjectFloatPair.of(new ItemStack(Items.REDSTONE), 0.05F));
        }));
        map.put(SpaceFactory.BEDROCK_COPPER_ORE, Util.make(new ArrayList<>(), list -> {
            list.add(ObjectFloatPair.of(new ItemStack(Items.RAW_COPPER), 0.5F));
            list.add(ObjectFloatPair.of(new ItemStack(Items.REDSTONE), 0.05F));
        }));
        map.put(SpaceFactory.BEDROCK_GOLD_ORE, Util.make(new ArrayList<>(), list -> {
            list.add(ObjectFloatPair.of(new ItemStack(Items.RAW_GOLD), 0.25F));
            list.add(ObjectFloatPair.of(new ItemStack(Items.GOLD_NUGGET), 0.5F));
            list.add(ObjectFloatPair.of(new ItemStack(Items.REDSTONE), 0.05F));
        }));
        map.put(SpaceFactory.BEDROCK_REDSTONE_ORE, Util.make(new ArrayList<>(), list -> {
            list.add(ObjectFloatPair.of(new ItemStack(Items.REDSTONE), 0.75F));
            list.add(ObjectFloatPair.of(new ItemStack(Items.RAW_IRON), 0.05F));
            list.add(ObjectFloatPair.of(new ItemStack(Items.RAW_COPPER), 0.05F));
        }));
        map.put(SpaceFactory.BEDROCK_EMERALD_ORE, Util.make(new ArrayList<>(), list -> {
            list.add(ObjectFloatPair.of(new ItemStack(Items.EMERALD), 0.25F));
            list.add(ObjectFloatPair.of(new ItemStack(Items.RAW_IRON), 0.05F));
            list.add(ObjectFloatPair.of(new ItemStack(Items.RAW_COPPER), 0.05F));
        }));
    });

    @Getter
    protected int drillSupply, drillSupplyTotal, drillProgress;

    public BedrockMinerBlockEntity(BlockPos pos, BlockState state) {
        super(TYPE, pos, state);
    }

    @Override
    protected DefaultedList<ItemStack> createSlotsList() {
        return DefaultedList.ofSize(INVENTORY_SIZE, ItemStack.EMPTY);
    }

    @Override
    public int getEnergyCapacity() {
        return SpaceFactory.config.getBedrockMinerEnergyCapacity();
    }

    @Override
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return new BedrockMinerScreenHandler(syncId, this, playerInventory);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.drillSupply = nbt.getInt("DrillSupplyLeft");
        this.drillSupplyTotal = nbt.getInt("DrillSupplyFull");
        this.drillProgress = nbt.getInt("DrillProgress");
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putInt("DrillSupplyLeft", this.drillSupply);
        nbt.putInt("DrillSupplyFull", this.drillSupplyTotal);
        nbt.putInt("DrillProgress", this.drillProgress);
    }

    @Override
    protected void tick() {
        super.tick();

        var isMining = tryMine();
        if (isMining != this.getCachedState().get(BedrockMinerBlock.LIT)) {
            this.world.setBlockState(this.pos, this.getCachedState().with(BedrockMinerBlock.LIT, isMining));
        }
    }

    protected boolean tryMine() {
        if (this.energy < SpaceFactory.config.getBedrockMinerEnergyConsumption()) {
            return false;
        }

        Block bedrock = this.world.getBlockState(this.pos.down()).getBlock();
        if (!MAP.containsKey(bedrock)) {
            return false;
        }

        if (this.drillSupply == 0) {
            ItemStack input = this.getStack(0);
            if (input.isOf(SpaceFactory.DRILL_SUPPLIES)) {
                input.decrement(1);
                this.drillSupply = this.drillSupplyTotal = DRILL_SUPPLIES_TOTAL;
            } else {
                return false;
            }
        }

        this.drillSupply--;
        this.drillProgress++;
        if (this.drillProgress >= DRILLLING_DURATION) {
            List<ObjectFloatPair<ItemStack>> output = MAP.get(bedrock);
            for (ObjectFloatPair<ItemStack> entry : output) {
                if (this.world.getRandom().nextFloat() < entry.rightFloat()) {
                    this.addStack(entry.left().copy());
                }
            }
            this.drillProgress = 0;
        }
        return true;
    }

    public ItemStack addStack(ItemStack stack) {
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        ItemStack itemStack = stack.copy();
        this.addToExistingSlot(itemStack);
        if (itemStack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        this.addToNewSlot(itemStack);
        if (itemStack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        return itemStack;
    }

    private void addToNewSlot(ItemStack stack) {
        for (int i = 1; i < this.size(); ++i) {
            ItemStack itemStack = this.getStack(i);
            if (!itemStack.isEmpty()) continue;
            this.setStack(i, stack.copyAndEmpty());
            return;
        }
    }

    private void addToExistingSlot(ItemStack stack) {
        for (int i = 1; i < this.size(); ++i) {
            ItemStack itemStack = this.getStack(i);
            if (!ItemStack.canCombine(itemStack, stack)) continue;
            this.transfer(stack, itemStack);
            if (!stack.isEmpty()) continue;
            return;
        }
    }

    private void transfer(ItemStack source, ItemStack target) {
        int i = Math.min(this.getMaxCountPerStack(), target.getMaxCount());
        int j = Math.min(source.getCount(), i - target.getCount());
        if (j > 0) {
            target.increment(j);
            source.decrement(j);
            this.markDirty();
        }
    }
}
