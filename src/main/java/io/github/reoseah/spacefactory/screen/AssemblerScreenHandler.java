package io.github.reoseah.spacefactory.screen;

import io.github.reoseah.spacefactory.block.AssemblerBlockEntity;
import io.github.reoseah.spacefactory.recipe.AssemblerRecipe;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeType;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.*;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AssemblerScreenHandler extends GhostSlotMachineScreenHandler<AssemblerRecipe> {
    public static final ScreenHandlerType<AssemblerScreenHandler> TYPE = new ScreenHandlerType<>(AssemblerScreenHandler::new, FeatureFlags.DEFAULT_ENABLED_FEATURES);

    public AssemblerScreenHandler(int syncId, AssemblerBlockEntity be, PlayerInventory playerInv) {
        super(TYPE, syncId, be, playerInv);
    }

    public AssemblerScreenHandler(int syncId, PlayerInventory playerInv) {
        super(TYPE, syncId, AssemblerBlockEntity.INVENTORY_SIZE, playerInv);
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
    protected int getInputSlots() {
        return 6;
    }

    @Override
    protected int getMachineSlotCount() {
        return AssemblerBlockEntity.INVENTORY_SIZE;
    }

    @Override
    protected RecipeType<AssemblerRecipe> getRecipeType() {
        return AssemblerRecipe.TYPE;
    }
}
