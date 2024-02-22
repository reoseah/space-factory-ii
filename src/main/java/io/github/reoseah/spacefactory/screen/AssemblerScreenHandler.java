package io.github.reoseah.spacefactory.screen;

import io.github.reoseah.spacefactory.api.ProcessingMachine;
import io.github.reoseah.spacefactory.block.AssemblerBlockEntity;
import io.github.reoseah.spacefactory.recipe.AssemblerRecipe;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.recipe.RecipeType;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.*;
import net.minecraft.screen.slot.Slot;

public class AssemblerScreenHandler extends ProcessingMachineScreenHandler {
    public static final ScreenHandlerType<AssemblerScreenHandler> TYPE = new ScreenHandlerType<>(AssemblerScreenHandler::new, FeatureFlags.DEFAULT_ENABLED_FEATURES);

    public AssemblerScreenHandler(int syncId, AssemblerBlockEntity be, PlayerInventory playerInv) {
        super(TYPE, syncId, be, playerInv);
    }

    public AssemblerScreenHandler(int syncId, PlayerInventory playerInv) {
        super(TYPE, syncId, ProcessingMachine.ASSEMBLER.inventorySize, playerInv);
    }

    @Override
    protected void addMachineSlots() {
        for (int y = 0; y < 2; y++) {
            for (int x = 0; x < 3; x++) {
                this.addSlot(new Slot(inventory, x + y * 3, 44 + x * 18, 18 + y * 18));
            }
        }
        this.addSlot(new SimpleOutputSlot(inventory, 6, 140, 27));
    }

    @Override
    protected ProcessingMachine getMachineType() {
        return ProcessingMachine.ASSEMBLER;
    }
}
