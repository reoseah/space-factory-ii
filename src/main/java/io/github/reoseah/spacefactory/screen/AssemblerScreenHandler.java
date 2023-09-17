package io.github.reoseah.spacefactory.screen;

import io.github.reoseah.spacefactory.block.AssemblerBlockEntity;
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

public class AssemblerScreenHandler extends ScreenHandler {
    public static final ScreenHandlerType<AssemblerScreenHandler> TYPE = new ScreenHandlerType<>(AssemblerScreenHandler::new, FeatureFlags.DEFAULT_ENABLED_FEATURES);

    protected final Inventory inventory;
    protected final PropertyDelegate properties;

    protected AssemblerScreenHandler(int syncId, Inventory inventory, PropertyDelegate properties, PlayerInventory playerInv) {
        super(TYPE, syncId);
        this.inventory = inventory;
        this.addProperties(this.properties = properties);

        for (int y = 0; y < 2; y++) {
            for (int x = 0; x < 3; x++) {
                this.addSlot(new Slot(inventory, x + y * 3, 44 + x * 18, 18 + y * 18));
            }
        }
        this.addSlot(new Slot(inventory, 6, 140, 27));

        for (int y = 0; y < 3; ++y) {
            for (int x = 0; x < 9; ++x) {
                this.addSlot(new Slot(playerInv, x + y * 9 + 9, 8 + x * 18, 114 + y * 18));
            }
        }

        for (int x = 0; x < 9; ++x) {
            this.addSlot(new Slot(playerInv, x, 8 + x * 18, 172));
        }
    }

    public AssemblerScreenHandler(int syncId, AssemblerBlockEntity be, PlayerInventory playerInv) {
        this(syncId, be, new Properties(be), playerInv);
    }

    public AssemblerScreenHandler(int syncId, PlayerInventory playerInv) {
        this(syncId, new SimpleInventory(AssemblerBlockEntity.INVENTORY_SIZE), new ArrayPropertyDelegate(Properties.SIZE), playerInv);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        // TODO implement
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    public record Properties(AssemblerBlockEntity be) implements PropertyDelegate {
        public static final int ENERGY = 0,
                ENERGY_CAPACITY = 1,
                ENERGY_PER_TICK_TIMES_100 = 2;
        public static final int SIZE = 3;

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
                default -> throw new IllegalArgumentException();
            };
        }

        @Override
        public void set(int index, int value) {
            // not needed
        }
    }
}
