package io.github.reoseah.spacefactory.screen;

import io.github.reoseah.spacefactory.api.ProcessingMachine;
import io.github.reoseah.spacefactory.block.ProcessingMachineBlockEntity;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.screen.*;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class ProcessingMachineScreenHandler extends ScreenHandler {
    protected final Inventory inventory;
    protected final PropertyDelegate properties;
    protected List<Recipe<?>> availableRecipes;
    private Property selectedRecipe;

    protected ProcessingMachineScreenHandler(ScreenHandlerType<?> type, int syncId, Inventory inventory, PropertyDelegate properties, PlayerInventory playerInv) {
        super(type, syncId);
        this.inventory = inventory;
        this.properties = properties;
        this.addProperties(this.properties);

        this.addMachineSlots();

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                this.addSlot(new Slot(playerInv, x + y * 9 + 9, 8 + x * 18, 114 + y * 18));
            }
        }

        for (int x = 0; x < 9; x++) {
            this.addSlot(new Slot(playerInv, x, 8 + x * 18, 172));
        }

        this.availableRecipes = new ArrayList<>(playerInv.player.getWorld().getRecipeManager()
                .listAllOfType(this.getMachineType().getRecipeType()));
        Collections.sort(this.availableRecipes, this.getMachineType());
    }

    public ProcessingMachineScreenHandler(ScreenHandlerType<?> type, int syncId, ProcessingMachineBlockEntity be, PlayerInventory playerInv) {
        this(type, syncId, be, new Properties(be), playerInv);
        this.addProperty(this.selectedRecipe = new Property() {
            @Override
            public int get() {
                return be.getSelectedRecipe() != null ? availableRecipes.indexOf(be.getSelectedRecipe()) : 0;
            }

            @Override
            public void set(int value) {
                be.setSelectedRecipe(availableRecipes.get(value));
            }
        });
    }

    public ProcessingMachineScreenHandler(ScreenHandlerType<?> type, int syncId, int slotCount, PlayerInventory playerInv) {
        this(type, syncId, new SimpleInventory(slotCount), new ArrayPropertyDelegate(Properties.SIZE), playerInv);
        this.addProperty(this.selectedRecipe = Property.create());
    }

    protected abstract void addMachineSlots();

    protected abstract ProcessingMachine getMachineType();

    @SuppressWarnings({"unchecked", "rawtypes"})
    public record Properties(ProcessingMachineBlockEntity be) implements PropertyDelegate {
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
                case RECIPE_ENERGY -> this.be.getSelectedRecipe() != null
                        ? this.be.getRecipeEnergy(this.be.getSelectedRecipe()) : 0;
                default -> throw new IllegalArgumentException();
            };
        }

        @Override
        public void set(int index, int value) {
            // not needed
        }
    }

    public List<Recipe<?>> getAvailableRecipes() {
        return this.availableRecipes;
    }

    public int getSelectedRecipeIdx() {
        return this.selectedRecipe.get();
    }

    public Recipe<?> getSelectedRecipe() {
        return this.availableRecipes.get(this.getSelectedRecipeIdx());
    }

    public boolean onButtonClick(PlayerEntity player, int id) {
        if (id >= 0 && id < this.availableRecipes.size()) {
            Recipe<?> recipe = getSelectedRecipe();
            if (!this.canSwitchToRecipe(recipe)) {
                player.sendMessage(Text.translatable("spacefactory.cannot_switch_recipe_with_current_ingredients"), true);
                return true;
            }

            this.selectedRecipe.set(id);
        }

        return true;
    }

    public boolean canSwitchToRecipe(Recipe<?> recipe) {
        for (int i = 0; i < recipe.getIngredients().size(); i++) {
            if (!this.getSlot(i).getStack().isEmpty() && !recipe.getIngredients().get(i).test(this.getSlot(i).getStack())) {
                return false;
            }
        }
        for (int i = recipe.getIngredients().size(); i < this.getMachineType().inputCount; i++) {
            if (!this.getSlot(i).getStack().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int index) {
        Slot slot = this.slots.get(index);
        ItemStack stack = slot.getStack();
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        ItemStack previous = stack.copy();
        int playerSlotsStart = this.getMachineType().inventorySize;
        if (index < playerSlotsStart) {
            if (!this.insertItem(stack, playerSlotsStart, playerSlotsStart + 36, true)) {
                return ItemStack.EMPTY;
            }
            slot.onQuickTransfer(stack, previous);
        } else {
            Recipe<?> recipe = getSelectedRecipe();
            boolean matchedIngredient = false;

            IntArrayList validSlots = new IntArrayList(this.getMachineType().inputCount);
            int total = 0;
            for (int i = 0; i < recipe.getIngredients().size(); i++) {
                if (recipe.getIngredients().get(i).test(stack)) {
                    matchedIngredient = true;

                    ItemStack slotStack = this.getSlot(i).getStack();
                    if (slotStack.isEmpty() || ItemStack.canCombine(slotStack, stack)) {
                        validSlots.add(i);
                        total += slotStack.getCount();
                    }
                }
            }
            if (!validSlots.isEmpty()) {
                int targetCount = (total + stack.getCount()) / validSlots.size();
                for (int idx : validSlots) {
                    ItemStack slotStack = this.getSlot(idx).getStack();
                    int slotCount = slotStack.getCount();
                    int change = Math.min(targetCount - slotCount, this.getSlot(idx).getMaxItemCount(stack) - slotCount);
                    if (change > 0) {
                        stack = this.getSlot(idx).insertStack(stack, change);
                    }
                }
                if (!stack.isEmpty()) {
                    for (int idx : validSlots) {
                        stack = this.getSlot(idx).insertStack(stack);
                    }
                }
            }
//            if (!matchedIngredient && AbstractFurnaceBlockEntity.canUseAsFuel(stack)) {
//                if (!this.insertItem(stack, 6, 7, false)) {
//                    return ItemStack.EMPTY;
//                }
//            }

            if (index < playerSlotsStart + 27) {
                if (!this.insertItem(stack, playerSlotsStart + 27, playerSlotsStart + 36, false)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (!this.insertItem(stack, playerSlotsStart, playerSlotsStart + 27, false)) {
                    return ItemStack.EMPTY;
                }
            }
        }

        if (stack.isEmpty()) {
            slot.setStack(ItemStack.EMPTY);
        } else {
            slot.markDirty();
        }

        if (stack.getCount() == previous.getCount()) {
            return ItemStack.EMPTY;
        }

        slot.onTakeItem(player, stack);
        return previous;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }
}
