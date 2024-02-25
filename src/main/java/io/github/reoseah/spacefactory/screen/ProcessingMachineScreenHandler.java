package io.github.reoseah.spacefactory.screen;

import io.github.reoseah.spacefactory.block.AssemblerBlockEntity;
import io.github.reoseah.spacefactory.block.ProcessingMachineBlockEntity;
import io.github.reoseah.spacefactory.recipe.ExtractorRecipe;
import io.github.reoseah.spacefactory.recipe.ProcessingRecipeType;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import lombok.Getter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.*;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ProcessingMachineScreenHandler extends ScreenHandler {
    public static final ScreenHandlerType<ProcessingMachineScreenHandler> EXTRACTOR_TYPE = new ScreenHandlerType<>(ProcessingMachineScreenHandler::createExtractor, FeatureFlags.DEFAULT_ENABLED_FEATURES);
    public static final ScreenHandlerType<ProcessingMachineScreenHandler> ASSEMBLER_TYPE = new ScreenHandlerType<>(ProcessingMachineScreenHandler::createAssembler, FeatureFlags.DEFAULT_ENABLED_FEATURES);

    public final ProcessingRecipeType<?> recipeType;
    protected final Inventory inventory;
    protected final PropertyDelegate properties;
    @Getter
    protected List<Recipe<?>> availableRecipes;
    private Property selectedRecipe;

    protected ProcessingMachineScreenHandler(ScreenHandlerType<?> type, ProcessingRecipeType<?> recipeType, int syncId, Inventory inventory, PropertyDelegate properties, PlayerInventory playerInv) {
        super(type, syncId);
        this.recipeType = recipeType;
        this.inventory = inventory;
        this.properties = properties;
        this.addProperties(this.properties);

        this.recipeType.addSlots(this::addSlot, this.inventory);

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                this.addSlot(new Slot(playerInv, x + y * 9 + 9, 8 + x * 18, 114 + y * 18));
            }
        }

        for (int x = 0; x < 9; x++) {
            this.addSlot(new Slot(playerInv, x, 8 + x * 18, 172));
        }

        this.availableRecipes = new ArrayList<>(playerInv.player.getWorld().getRecipeManager().listAllOfType(this.recipeType));
        Collections.sort(this.availableRecipes, (Comparator) this.recipeType);
    }

    protected ProcessingMachineScreenHandler(ScreenHandlerType<?> type, ProcessingRecipeType<?> recipeType, int syncId, ProcessingMachineBlockEntity<?> be, PlayerInventory playerInv) {
        this(type, recipeType, syncId, be, new Properties(be), playerInv);
        this.selectedRecipe = this.addProperty(new SelectedRecipeProperty(be));
    }

    protected ProcessingMachineScreenHandler(ScreenHandlerType<?> type, ProcessingRecipeType<?> recipeType, int syncId, PlayerInventory playerInv) {
        this(type, recipeType, syncId, new SimpleInventory(recipeType.inputCount + recipeType.outputCount), new ArrayPropertyDelegate(Properties.SIZE), playerInv);
        this.selectedRecipe = this.addProperty(Property.create());
    }

    public static ProcessingMachineScreenHandler createExtractor(int syncId, ProcessingMachineBlockEntity<ExtractorRecipe> be, PlayerInventory playerInv) {
        return new ProcessingMachineScreenHandler(EXTRACTOR_TYPE, ProcessingRecipeType.EXTRACTOR, syncId, be, playerInv);
    }

    private static ProcessingMachineScreenHandler createExtractor(int syncId, PlayerInventory playerInv) {
        return new ProcessingMachineScreenHandler(EXTRACTOR_TYPE, ProcessingRecipeType.EXTRACTOR, syncId, playerInv);
    }

    public static ProcessingMachineScreenHandler createAssembler(int syncId, AssemblerBlockEntity be, PlayerInventory playerInv) {
        return new ProcessingMachineScreenHandler(ASSEMBLER_TYPE, ProcessingRecipeType.ASSEMBLER, syncId, be, playerInv);
    }

    public static ProcessingMachineScreenHandler createAssembler(int syncId, PlayerInventory playerInv) {
        return new ProcessingMachineScreenHandler(ASSEMBLER_TYPE, ProcessingRecipeType.ASSEMBLER, syncId, playerInv);
    }

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
                case RECIPE_ENERGY -> {
                    if (this.be.getSelectedRecipe() != null) {
                        Recipe<Inventory> recipe = this.be.getSelectedRecipe();
                        yield this.be.getRecipeType().getRecipeEnergy(recipe);
                    } else {
                        yield 0;
                    }
                }
                default -> throw new IllegalArgumentException();
            };
        }

        @Override
        public void set(int index, int value) {
            // not needed
        }
    }

    protected class SelectedRecipeProperty extends Property {
        private final ProcessingMachineBlockEntity be;

        public SelectedRecipeProperty(ProcessingMachineBlockEntity be) {
            this.be = be;
        }

        @Override
        public int get() {
            return be.getSelectedRecipe() != null ? availableRecipes.indexOf(be.getSelectedRecipe()) : 0;
        }

        @Override
        public void set(int value) {
            be.setSelectedRecipe(availableRecipes.get(value));
        }
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
        for (int i = recipe.getIngredients().size(); i < this.recipeType.inputCount; i++) {
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
        int playerSlotsStart = this.recipeType.inputCount + this.recipeType.outputCount;
        if (index < playerSlotsStart) {
            if (!this.insertItem(stack, playerSlotsStart, playerSlotsStart + 36, true)) {
                return ItemStack.EMPTY;
            }
            slot.onQuickTransfer(stack, previous);
        } else {
            Recipe<?> recipe = getSelectedRecipe();
            boolean matchedIngredient = false;

            IntArrayList validSlots = new IntArrayList(this.recipeType.inputCount);
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
