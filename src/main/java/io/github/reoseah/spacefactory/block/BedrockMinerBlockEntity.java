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
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.Identifier;
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
    public static final TagKey<Item> SUPPLIES = TagKey.of(RegistryKeys.ITEM, new Identifier("spacefactory:bedrock_miner_supplies"));
    public static final TagKey<Block> ORES = TagKey.of(RegistryKeys.BLOCK, new Identifier("spacefactory:bedrock_ores"));

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
            if (input.isIn(SUPPLIES)) {
                input.decrement(1);
                this.drillSupply = this.drillSupplyTotal = SpaceFactory.config.getBedrockMinerDrillSuppliesDuration();
            } else {
                return false;
            }
        }

        this.energy -= SpaceFactory.config.getBedrockMinerEnergyConsumption();
        this.drillSupply--;
        this.drillProgress++;
        if (this.drillProgress >= SpaceFactory.config.getBedrockMinerDrillingDuration()) {
            List<ObjectFloatPair<ItemStack>> output = MAP.get(bedrock);
            for (ObjectFloatPair<ItemStack> entry : output) {
                if (this.world.getRandom().nextFloat() < entry.rightFloat()) {
                    this.insertOutput(entry.left());
                }
            }
            this.drillProgress = 0;
        }
        return true;
    }

    public void insertOutput(ItemStack stack) {
        ItemStack copy = stack.copy();
        for (int i = INPUTS_COUNT; i < INPUTS_COUNT + RESULTS_COUNT; ++i) {
            ItemStack slot = this.getStack(i);
            if (!ItemStack.canCombine(slot, copy)) {
                continue;
            }
            int amount = Math.min(copy.getCount(), Math.min(this.getMaxCountPerStack(), slot.getMaxCount()) - slot.getCount());
            if (amount > 0) {
                slot.increment(amount);
                copy.decrement(amount);
                this.markDirty();
            }
            if (copy.isEmpty()) {
                break;
            }
        }
        if (copy.isEmpty()) {
            return;
        }
        for (int i = INPUTS_COUNT; i < INPUTS_COUNT + RESULTS_COUNT; ++i) {
            ItemStack slot = this.getStack(i);
            if (slot.isEmpty()) {
                this.setStack(i, copy.copyAndEmpty());
                break;
            }
        }
    }

}
