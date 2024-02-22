package io.github.reoseah.spacefactory.screen;

import io.github.reoseah.spacefactory.api.ProcessingMachine;
import io.github.reoseah.spacefactory.block.ExtractorBlockEntity;
import io.github.reoseah.spacefactory.block.ProcessingMachineBlockEntity;
import io.github.reoseah.spacefactory.recipe.ExtractorRecipe;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.recipe.RecipeType;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;

public class ExtractorScreenHandler extends ProcessingMachineScreenHandler {
    public static final ScreenHandlerType<ExtractorScreenHandler> TYPE = new ScreenHandlerType<>(ExtractorScreenHandler::new, FeatureFlags.DEFAULT_ENABLED_FEATURES);

    public ExtractorScreenHandler(int syncId, ProcessingMachineBlockEntity<ExtractorRecipe> be, PlayerInventory playerInv) {
        super(TYPE, syncId, be, playerInv);
    }

    public ExtractorScreenHandler(int syncId, PlayerInventory playerInv) {
        super(TYPE, syncId, ProcessingMachine.EXTRACTOR.inventorySize, playerInv);
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
    protected ProcessingMachine getMachineType() {
        return ProcessingMachine.EXTRACTOR;
    }
}
