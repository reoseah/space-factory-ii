package io.github.reoseah.spacefactory.screen;

import io.github.reoseah.spacefactory.block.ExtractorBlockEntity;
import io.github.reoseah.spacefactory.block.GhostSlotMachineBlockEntity;
import io.github.reoseah.spacefactory.recipe.ExtractorRecipe;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeType;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.world.World;

public class ExtractorScreenHandler extends GhostSlotMachineScreenHandler<ExtractorRecipe> {
    public static final ScreenHandlerType<ExtractorScreenHandler> TYPE = new ScreenHandlerType<>(ExtractorScreenHandler::new, FeatureFlags.DEFAULT_ENABLED_FEATURES);

    public ExtractorScreenHandler(int syncId, GhostSlotMachineBlockEntity<ExtractorRecipe> be, PlayerInventory playerInv) {
        super(TYPE, syncId, be, playerInv);
    }

    public ExtractorScreenHandler(int syncId, PlayerInventory playerInv) {
        super(TYPE, syncId, ExtractorBlockEntity.INVENTORY_SIZE, playerInv);
    }

    @Override
    protected void addMachineSlots() {
        this.addSlot(new Slot(inventory, 0, 45, 27));
        this.addSlot(new SimpleOutputSlot(inventory, 1, 107, 18));
        this.addSlot(new SimpleOutputSlot(inventory, 2, 125, 18));
        this.addSlot(new SimpleOutputSlot(inventory, 3, 107, 36));
        this.addSlot(new SimpleOutputSlot(inventory, 4, 125, 36));
    }

    @Override
    protected int getInputSlots() {
        return 1;
    }

    @Override
    protected int getMachineSlotCount() {
        return ExtractorBlockEntity.INVENTORY_SIZE;
    }

    @Override
    protected RecipeType<ExtractorRecipe> getRecipeType() {
        return ExtractorRecipe.TYPE;
    }
}
