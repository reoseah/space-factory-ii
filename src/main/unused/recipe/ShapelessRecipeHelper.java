package io.github.reoseah.spacefactory.recipe;

import it.unimi.dsi.fastutil.ints.Int2IntArrayMap;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;

import java.util.BitSet;
import java.util.List;

public class ShapelessRecipeHelper {
    public static boolean matches(Inventory inventory, int beginning, int end, List<Ingredient> ingredients, int[] amounts) {
        BitSet consumed = new BitSet(ingredients.size());

        for (int i = 0; i < ingredients.size(); i++) {
            Ingredient ingredient = ingredients.get(i);
            int amount = amounts[i];

            boolean found = false;
            for (int slot = beginning; slot < end; slot++) {
                if (consumed.get(slot)) {
                    continue;
                }
                ItemStack stack = inventory.getStack(slot);
                if (ingredient.test(stack) && stack.getCount() >= amount) {
                    consumed.set(slot);
                    found = true;
                    break;
                }
            }
            if (!found) {
                return false;
            }
        }
        for (int slot = beginning; slot < end; slot++) {
            if (!consumed.get(slot) && !inventory.getStack(slot).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public static void consumeInputs(Inventory inventory, int beginning, int end, List<Ingredient> ingredients, int[] amounts) {
        Int2IntMap map = new Int2IntArrayMap(ingredients.size());
        for (int i = 0; i < ingredients.size(); i++) {
            Ingredient ingredient = ingredients.get(i);
            int amount = amounts[i];

            boolean found = false;
            for (int slot = beginning; slot < end; slot++) {
                if (map.containsKey(slot)) {
                    continue;
                }
                ItemStack stack = inventory.getStack(slot);
                if (ingredient.test(stack) && stack.getCount() >= amount) {
                    map.put(slot, amount);
                    found = true;
                    break;
                }
            }
            if (!found) {
                throw new UnsupportedOperationException("Can't consume items because inventory doesn't match the recipe!");
            }
        }
        for (Int2IntMap.Entry entry : map.int2IntEntrySet()) {
            int slot = entry.getIntKey();
            int amount = entry.getIntValue();
            ItemStack stack = inventory.getStack(slot);
            stack.decrement(amount);
        }
    }
}
