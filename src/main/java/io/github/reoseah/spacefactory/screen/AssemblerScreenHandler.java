package io.github.reoseah.spacefactory.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;

public class AssemblerScreenHandler extends ScreenHandler {
    public static final ScreenHandlerType<AssemblerScreenHandler> TYPE = new ScreenHandlerType<>(AssemblerScreenHandler::new, FeatureFlags.DEFAULT_ENABLED_FEATURES);

    protected final Inventory inventory;

    public AssemblerScreenHandler(int syncId, Inventory inventory, PlayerInventory playerInv) {
        super(TYPE, syncId);
        this.inventory = inventory;
    }

    public AssemblerScreenHandler(int syncId, PlayerInventory playerInv) {
        this(syncId, new SimpleInventory(0), playerInv);
    }


    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }
}
