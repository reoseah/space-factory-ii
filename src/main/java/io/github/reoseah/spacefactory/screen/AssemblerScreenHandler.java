package io.github.reoseah.spacefactory.screen;

import com.google.common.collect.Lists;
import io.github.reoseah.spacefactory.block.AssemblerBlockEntity;
import io.github.reoseah.spacefactory.recipe.AssemblerRecipe;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.*;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AssemblerScreenHandler extends ScreenHandler {
    public static final ScreenHandlerType<AssemblerScreenHandler> TYPE = new ScreenHandlerType<>(AssemblerScreenHandler::new, FeatureFlags.DEFAULT_ENABLED_FEATURES);

    protected final Inventory inventory;
    protected final PropertyDelegate properties;
    protected List<AssemblerRecipe> availableRecipes;
    private Property selectedRecipe;

    protected AssemblerScreenHandler(int syncId, Inventory inventory, PropertyDelegate properties, PlayerInventory playerInv) {
        super(TYPE, syncId);
        this.inventory = inventory;
        this.addProperties(this.properties = properties);

        for (int y = 0; y < 2; y++) {
            for (int x = 0; x < 3; x++) {
                this.addSlot(new Slot(inventory, x + y * 3, 44 + x * 18, 18 + y * 18));
            }
        }
        this.addSlot(new Slot(inventory, 6, 140, 27) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return false;
            }
        });

        for (int y = 0; y < 3; ++y) {
            for (int x = 0; x < 9; ++x) {
                this.addSlot(new Slot(playerInv, x + y * 9 + 9, 8 + x * 18, 114 + y * 18));
            }
        }

        for (int x = 0; x < 9; ++x) {
            this.addSlot(new Slot(playerInv, x, 8 + x * 18, 172));
        }

        this.availableRecipes = new ArrayList<>();
        List<AssemblerRecipe> recipes = playerInv.player.getWorld() //
                .getRecipeManager() //
                .listAllOfType(AssemblerRecipe.TYPE);
        this.availableRecipes = DefaultedList.ofSize(recipes.size());
        this.availableRecipes.addAll(recipes);
        Collections.sort(this.availableRecipes);
    }

    public AssemblerScreenHandler(int syncId, AssemblerBlockEntity be, PlayerInventory playerInv) {
        this(syncId, be, new Properties(be), playerInv);
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

    public AssemblerScreenHandler(int syncId, PlayerInventory playerInv) {
        this(syncId, new SimpleInventory(AssemblerBlockEntity.INVENTORY_SIZE), new ArrayPropertyDelegate(Properties.SIZE), playerInv);
        this.addProperty(this.selectedRecipe = Property.create());
    }

    public List<AssemblerRecipe> getAvailableRecipes() {
        return this.availableRecipes;
    }

    public int getSelectedRecipeIdx() {
        return this.selectedRecipe.get();
    }

    public AssemblerRecipe getSelectedRecipe() {
        return this.availableRecipes.get(this.getSelectedRecipeIdx());
    }

    public boolean onButtonClick(PlayerEntity player, int id) {
        if (id >= 0 && id < this.availableRecipes.size()) {
            AssemblerRecipe recipe = getSelectedRecipe();
            if (!this.canSwitchToRecipe(recipe)) {
                player.sendMessage(Text.translatable("spacefactory.cannot_switch_recipe_with_current_ingredients"), true);
                return true;
            }

            this.selectedRecipe.set(id);
        }

        return true;
    }

    public boolean canSwitchToRecipe(AssemblerRecipe recipe) {
        for (int i = 0; i < recipe.getIngredients().size(); i++) {
            if (!this.getSlot(i).getStack().isEmpty() && !recipe.getIngredients().get(i).test(this.getSlot(i).getStack())) {
                return false;
            }
        }
        for (int i = recipe.getIngredients().size(); i < 6; i++) {
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
        int playerSlotsStart = AssemblerBlockEntity.INVENTORY_SIZE;
        if (index < playerSlotsStart) {
            if (!this.insertItem(stack, playerSlotsStart, playerSlotsStart + 36, true)) {
                return ItemStack.EMPTY;
            }
            slot.onQuickTransfer(stack, previous);
        } else {
            AssemblerRecipe recipe = getSelectedRecipe();
            boolean matchedIngredient = false;

            IntArrayList validSlots = new IntArrayList(6);
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

    public record Properties(AssemblerBlockEntity be) implements PropertyDelegate {
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
                case RECIPE_ENERGY -> this.be.getSelectedRecipe() != null ? this.be.getSelectedRecipe().energy : 0;
                default -> throw new IllegalArgumentException();
            };
        }

        @Override
        public void set(int index, int value) {
            // not needed
        }
    }
}
