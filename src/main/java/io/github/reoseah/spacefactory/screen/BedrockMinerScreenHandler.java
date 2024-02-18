package io.github.reoseah.spacefactory.screen;

import io.github.reoseah.spacefactory.block.BedrockMinerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;

public class BedrockMinerScreenHandler extends ScreenHandler {
    public static final ScreenHandlerType<BedrockMinerScreenHandler> TYPE = new ScreenHandlerType<>(BedrockMinerScreenHandler::new, FeatureFlags.DEFAULT_ENABLED_FEATURES);

    protected final Inventory inventory;
    public final PropertyDelegate properties;

    public BedrockMinerScreenHandler(int syncId, Inventory inventory, PropertyDelegate properties, PlayerInventory playerInv) {
        super(TYPE, syncId);

        this.inventory = inventory;
        this.properties = properties;

        this.addProperties(this.properties);

        this.addSlot(new Slot(inventory, 0, 52, 18));

        for (int y = 0; y < 2; y++) {
            for (int x = 0; x < 3; x++) {
                this.addSlot(new SimpleOutputSlot(inventory, 1 + x + y * 3, 107 + x * 18, 18 + y * 18));
            }
        }

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                this.addSlot(new Slot(playerInv, x + y * 9 + 9, 8 + x * 18, 68 + y * 18));
            }
        }
        for (int x = 0; x < 9; x++) {
            this.addSlot(new Slot(playerInv, x, 8 + x * 18, 126));
        }
    }

    public BedrockMinerScreenHandler(int syncId, BedrockMinerBlockEntity be, PlayerInventory playerInv) {
        this(syncId, be, new Properties(be), playerInv);
    }

    public BedrockMinerScreenHandler(int syncId, PlayerInventory playerInv) {
        this(syncId, new SimpleInventory(BedrockMinerBlockEntity.INVENTORY_SIZE), new ArrayPropertyDelegate(Properties.SIZE), playerInv);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int index) {
        Slot slot = this.slots.get(index);
        if (!slot.hasStack()) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = slot.getStack();
        ItemStack copy = stack.copy();
        int firstPlayerSlot = BedrockMinerBlockEntity.INVENTORY_SIZE;
        if (1 <= index && index < firstPlayerSlot) {
            if (!this.insertItem(stack, firstPlayerSlot, firstPlayerSlot + 36, true)) {
                return ItemStack.EMPTY;
            }
            slot.onQuickTransfer(stack, copy);
        } else if (index < 1) {
            if (!this.insertItem(stack, firstPlayerSlot, firstPlayerSlot + 36, false)) {
                return ItemStack.EMPTY;
            }
        } else if (this.isProcessable(stack)) {
            if (!this.insertItem(stack, 0, 1, false)) {
                return ItemStack.EMPTY;
            }
        } else if (index < firstPlayerSlot + 27) {
            if (!this.insertItem(stack, firstPlayerSlot + 27, firstPlayerSlot + 36, false)) {
                return ItemStack.EMPTY;
            }
        } else if (index < firstPlayerSlot + 36) {
            if (!this.insertItem(stack, firstPlayerSlot, firstPlayerSlot + 27, false)) {
                return ItemStack.EMPTY;
            }
        }
        if (stack.isEmpty()) {
            slot.setStack(ItemStack.EMPTY);
        } else {
            slot.markDirty();
        }
        if (stack.getCount() == copy.getCount()) {
            return ItemStack.EMPTY;
        }
        slot.onTakeItem(player, stack);
        return copy;
    }

    protected boolean isProcessable(ItemStack stack) {
        return stack.isIn(BedrockMinerBlockEntity.SUPPLIES);
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    public record Properties(BedrockMinerBlockEntity be) implements PropertyDelegate {
        public static final int SIZE = 7;

        public static final int ENERGY = 0, //
                ENERGY_CAPACITY = 1, //
                ENERGY_PER_TICK_TIMES_100 = 2, //
                DRILL_SUPPLY_LEFT = 3, //
                DRILL_SUPPLY_TOTAL = 4, //
                DRILL_PROGRESS = 5, //
                IS_VALID_FLOOR = 6;

        @Override
        public int size() {
            return SIZE;
        }

        @Override
        public int get(int index) {
            return switch (index) {
                case ENERGY -> this.be.getEnergy();
                case ENERGY_CAPACITY -> this.be.getEnergyCapacity();
                case ENERGY_PER_TICK_TIMES_100 -> (int) (this.be.getAverageEnergyPerTick() * 100);
                case DRILL_SUPPLY_LEFT -> this.be.getDrillSupply();
                case DRILL_SUPPLY_TOTAL -> this.be.getDrillSupplyTotal();
                case DRILL_PROGRESS -> this.be.getDrillProgress();
                case IS_VALID_FLOOR -> this.be.getWorld().getBlockState(this.be.getPos().down()) //
                        .isIn(BedrockMinerBlockEntity.ORES) ? 1 : 0;
                default -> throw new IllegalArgumentException();
            };
        }

        @Override
        public void set(int index, int value) {
            // not needed
        }
    }
}
