package io.github.reoseah.spacefactory.screen;

import io.github.reoseah.spacefactory.block.ExtractorBlockEntity;
import io.github.reoseah.spacefactory.recipe.ExtractorRecipe;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.*;
import net.minecraft.screen.slot.Slot;
import net.minecraft.world.World;

public class ExtractorScreenHandler extends ScreenHandler {
    public static final ScreenHandlerType<ExtractorScreenHandler> TYPE = new ScreenHandlerType<>(ExtractorScreenHandler::new, FeatureFlags.DEFAULT_ENABLED_FEATURES);

    protected final World world;
    protected final Inventory inventory;
    protected final PropertyDelegate properties;

    protected ExtractorScreenHandler(int syncId, Inventory inventory, PropertyDelegate properties, PlayerInventory playerInv) {
        super(TYPE, syncId);
        this.world = playerInv.player.getWorld();
        this.inventory = inventory;
        this.addProperties(this.properties = properties);

        this.addSlot(new Slot(inventory, 0, 45, 27));
        this.addSlot(new SimpleOutputSlot(inventory, 1, 107, 18));
        this.addSlot(new SimpleOutputSlot(inventory, 2, 125, 18));
        this.addSlot(new SimpleOutputSlot(inventory, 3, 107, 36));
        this.addSlot(new SimpleOutputSlot(inventory, 4, 125, 36));
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                this.addSlot(new Slot(playerInv, x + y * 9 + 9, 8 + x * 18, 66 + y * 18));
            }
        }
        for (int x = 0; x < 9; x++) {
            this.addSlot(new Slot(playerInv, x, 8 + x * 18, 124));
        }
    }

    public ExtractorScreenHandler(int syncId, ExtractorBlockEntity be, PlayerInventory playerInv) {
        this(syncId, be, new ExtractorScreenHandler.Properties(be), playerInv);
    }

    public ExtractorScreenHandler(int syncId, PlayerInventory playerInv) {
        this(syncId, new SimpleInventory(ExtractorBlockEntity.INVENTORY_SIZE),
                new ArrayPropertyDelegate(ExtractorScreenHandler.Properties.SIZE), playerInv);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int index) {
        Slot slot = this.slots.get(index);
        if (!slot.hasStack()) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = slot.getStack();
        ItemStack copy = stack.copy();
        int firstPlayerSlot = ExtractorBlockEntity.INVENTORY_SIZE;
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
        return this.world.getRecipeManager().getFirstMatch(ExtractorRecipe.TYPE,
                new SimpleInventory(stack), this.world).isPresent();
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    public record Properties(ExtractorBlockEntity be) implements PropertyDelegate {
        public static final int ENERGY = 0, //
                ENERGY_CAPACITY = 1, //
                ENERGY_PER_TICK_TIMES_100 = 2, //
                RECIPE_PROGRESS = 3, //
                RECIPE_ENERGY = 4;
        public static final int SIZE = 5;

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
                case RECIPE_PROGRESS -> this.be.getRecipeProgress();
                case RECIPE_ENERGY -> this.be.getRecipeEnergy();
                default -> throw new IllegalArgumentException();
            };
        }

        @Override
        public void set(int index, int value) {
            // not needed
        }
    }
}
