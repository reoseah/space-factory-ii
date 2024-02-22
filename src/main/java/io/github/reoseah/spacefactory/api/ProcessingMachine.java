package io.github.reoseah.spacefactory.api;

import io.github.reoseah.spacefactory.recipe.AssemblerRecipe;
import io.github.reoseah.spacefactory.recipe.ExtractorRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

import java.util.Comparator;

public enum ProcessingMachine implements Comparator<Recipe<?>> {
    EXTRACTOR(1, 4) {
        @SuppressWarnings("unchecked")
        @Override
        public RecipeType<ExtractorRecipe> getRecipeType() {
            return ExtractorRecipe.TYPE;
        }

        @Override
        public int getRecipeEnergy(Recipe<?> recipe) {
            return ((ExtractorRecipe) recipe).energy;
        }
    }, ASSEMBLER(6, 1) {
        @SuppressWarnings("unchecked")
        @Override
        public RecipeType<AssemblerRecipe> getRecipeType() {
            return AssemblerRecipe.TYPE;
        }

        @Override
        public int getRecipeEnergy(Recipe<?> recipe) {
            return ((AssemblerRecipe) recipe).energy;
        }
    };

    public final int inputCount, outputCount;
    public final int inventorySize;

    ProcessingMachine(int inputCount, int outputCount) {
        this.inputCount = inputCount;
        this.outputCount = outputCount;
        this.inventorySize = inputCount + outputCount;
    }

    public abstract <R extends Recipe<?>, T extends RecipeType<R> & Comparable<R>> T getRecipeType();

    public abstract int getRecipeEnergy(Recipe<?> recipe);

    @Override
    public int compare(Recipe<?> recipe1, Recipe<?> recipe2) {
        ItemStack output1 = recipe1.getOutput(null);
        ItemStack output2 = recipe2.getOutput(null);
        return compare(output1, output2);
    }

    private static int compare(ItemStack stack1, ItemStack stack2) {
        // normal items before rare
        Rarity rarity1 = stack1.getRarity();
        Rarity rarity2 = stack2.getRarity();
        int result = Integer.compare(rarity1.ordinal(), rarity2.ordinal());
        if (result != 0) {
            return result;
        }
        // vanilla items before modded
        Identifier id1 = Registries.ITEM.getId(stack1.getItem());
        Identifier id2 = Registries.ITEM.getId(stack2.getItem());
        result = -Boolean.compare(id1.getNamespace().equals("minecraft"), id2.getNamespace().equals("minecraft"));
        if (result != 0) {
            return result;
        }
        int rawId1 = Registries.ITEM.getRawId(stack1.getItem());
        int rawId2 = Registries.ITEM.getRawId(stack2.getItem());
        return Integer.compare(rawId1, rawId2);
    }
}
