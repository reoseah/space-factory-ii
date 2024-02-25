package io.github.reoseah.spacefactory.recipe;

import io.github.reoseah.spacefactory.screen.SimpleOutputSlot;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Rarity;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

public abstract class ProcessingRecipeType<T extends Recipe<Inventory>> implements RecipeType<T>, Comparator<T> {
    public static final ProcessingRecipeType<ExtractorRecipe> EXTRACTOR = new ProcessingRecipeType<ExtractorRecipe>(1, 4) {
        @Override
        public int getRecipeEnergy(ExtractorRecipe recipe) {
            return recipe.energy;
        }

        @Override
        public int getIngredientCount(ExtractorRecipe recipe, int index) {
            return recipe.ingredientCount;
        }

        @Override
        public List<ItemStack> getOutputStacks(ExtractorRecipe recipe, @Nullable DynamicRegistryManager registryManager) {
            return Arrays.stream(recipe.outputs).map(Pair::key).toList();
        }

        @Override
        public List<ItemStack> getOutputStacks(ExtractorRecipe recipe, Inventory inventory, Random random, DynamicRegistryManager registryManager) {
            return Arrays.stream(recipe.outputs) //
                    .map(pair -> random.nextFloat() < pair.valueFloat() ? pair.key() : ItemStack.EMPTY) //
                    .toList();
        }

        @Override
        public void addSlots(Consumer<Slot> slots, Inventory inventory) {
            slots.accept(new Slot(inventory, 0, 45, 27));
            slots.accept(new SimpleOutputSlot(inventory, 1, 107, 18));
            slots.accept(new SimpleOutputSlot(inventory, 2, 125, 18));
            slots.accept(new SimpleOutputSlot(inventory, 3, 107, 36));
            slots.accept(new SimpleOutputSlot(inventory, 4, 125, 36));
        }
    };
    public static final ProcessingRecipeType<AssemblerRecipe> ASSEMBLER = new ProcessingRecipeType<AssemblerRecipe>(6, 1) {
        @Override
        public int getRecipeEnergy(AssemblerRecipe recipe) {
            return recipe.energy;
        }

        @Override
        public int getIngredientCount(AssemblerRecipe recipe, int index) {
            return 1;
        }

        @Override
        public List<ItemStack> getOutputStacks(AssemblerRecipe recipe, @Nullable DynamicRegistryManager registryManager) {
            return List.of(recipe.getOutput(registryManager));
        }

        @Override
        public List<ItemStack> getOutputStacks(AssemblerRecipe recipe, Inventory inventory, Random random, DynamicRegistryManager registryManager) {
            return this.getOutputStacks(recipe, registryManager);
        }

        @Override
        public void addSlots(Consumer<Slot> slots, Inventory inventory) {
            for (int y = 0; y < 2; y++) {
                for (int x = 0; x < 3; x++) {
                    slots.accept(new Slot(inventory, x + y * 3, 44 + x * 18, 18 + y * 18));
                }
            }
            slots.accept(new SimpleOutputSlot(inventory, 6, 140, 27));
        }
    };

    public final int inputCount, outputCount;

    public ProcessingRecipeType(int inputCount, int outputCount) {
        this.inputCount = inputCount;
        this.outputCount = outputCount;
    }

    public abstract int getRecipeEnergy(T recipe);

    public abstract int getIngredientCount(T recipe, int index);

    public abstract List<ItemStack> getOutputStacks(T recipe, DynamicRegistryManager registryManager);

    public abstract List<ItemStack> getOutputStacks(T recipe, Inventory inventory, Random random, DynamicRegistryManager registryManager);

    public abstract void addSlots(Consumer<Slot> handler, Inventory inventory);

    public boolean canAcceptOutput(Inventory inventory, T recipe, DynamicRegistryManager registryManager) {
        List<ItemStack> outputs = this.getOutputStacks(recipe, registryManager);
        for (int i = 0; i < outputs.size(); i++) {
            if (i >= outputCount) {
                return false;
            }
            if (!canFullyAddStack(inventory, this.inputCount + i, outputs.get(i))) {
                return false;
            }
        }
        return true;
    }

    protected static boolean canFullyAddStack(Inventory inventory, int slot, ItemStack offer) {
        ItemStack stack = inventory.getStack(slot);
        if (stack.isEmpty() || offer.isEmpty()) {
            return true;
        }
        if (!ItemStack.canCombine(stack, offer)) {
            return false;
        }
        int maxCount = Math.min(stack.getMaxCount(), inventory.getMaxCountPerStack());
        return stack.getCount() + offer.getCount() <= maxCount;
    }

    public void craftRecipe(Inventory inventory, T recipe, Random random, World world) {
        assert recipe.matches(inventory, world) : "Cannot craft " + recipe + " in " + inventory;
        assert this.canAcceptOutput(inventory, recipe, world.getRegistryManager()) : "Cannot craft " + recipe.getOutput(null) + " in " + inventory;

        List<ItemStack> outputs = this.getOutputStacks(recipe, inventory, random, world.getRegistryManager());
        for (int i = 0; i < outputs.size(); i++) {
            if (i >= outputCount) {
                break;
            }
            ItemStack output = outputs.get(i);
            if (!output.isEmpty()) {
                addStack(inventory, this.inputCount + i, output);
            }
        }

        for (int i = 0; i < this.inputCount; i++) {
            ItemStack input = inventory.getStack(i);
            input.decrement(this.getIngredientCount(recipe, i));
            inventory.setStack(i, input);
        }
    }

    protected static void addStack(Inventory inventory, int slot, ItemStack stack) {
        assert canFullyAddStack(inventory, slot, stack) : "Cannot add " + stack + " to " + inventory.getStack(slot);

        ItemStack stackInSlot = inventory.getStack(slot);
        if (stackInSlot.isEmpty()) {
            inventory.setStack(slot, stack.copy());
        } else {
            stackInSlot.increment(stack.getCount());
        }
        inventory.markDirty();
    }


    @Override
    public int compare(T recipe1, T recipe2) {
        ItemStack output1 = recipe1.getOutput(null);
        ItemStack output2 = recipe2.getOutput(null);
        return compare(output1, output2);
    }

    protected static int compare(ItemStack stack1, ItemStack stack2) {
        // normal items before rare
        Rarity rarity1 = stack1.getRarity();
        Rarity rarity2 = stack2.getRarity();
        int result = Integer.compare(rarity1.ordinal(), rarity2.ordinal());
        if (result != 0) {
            return result;
        }
        // vanilla items before modded
        Item item1 = stack1.getItem();
        Item item2 = stack2.getItem();
        boolean isVanilla1 = Registries.ITEM.getId(item1).getNamespace().equals("minecraft");
        boolean isVanilla2 = Registries.ITEM.getId(item2).getNamespace().equals("minecraft");
        result = Boolean.compare(isVanilla1, isVanilla2);
        if (result != 0) {
            return -result;
        }
        // compare by raw id, which gives same order on server and client
        int rawId1 = Registries.ITEM.getRawId(item1);
        int rawId2 = Registries.ITEM.getRawId(item2);
        return Integer.compare(rawId1, rawId2);
    }
}
