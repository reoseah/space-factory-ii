package io.github.reoseah.spacefactory.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import io.github.reoseah.spacefactory.SpaceFactory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.*;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public class AssemblerRecipe implements Recipe<Inventory>, Comparable<AssemblerRecipe> {
    public static final RecipeType<AssemblerRecipe> TYPE = new RecipeType<>() {
    };

    public static final RecipeSerializer<AssemblerRecipe> SERIALIZER = new Serializer(10_000, 1, 6);

    protected final Identifier id;
    protected final DefaultedList<Ingredient> ingredients;
    public final ItemStack output;
    public final int energy;

    public AssemblerRecipe(Identifier id, DefaultedList<Ingredient> ingredients, ItemStack output, int energy) {
        this.id = id;
        this.ingredients = ingredients;
        this.output = output;
        this.energy = energy;
    }


    @Override
    public boolean matches(Inventory inventory, World world) {
        for (int i = 0; i < this.ingredients.size(); i++) {
            Ingredient ingredient = this.ingredients.get(i);
            ItemStack stack = inventory.getStack(i);
            if (!ingredient.test(stack)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack craft(Inventory inventory, DynamicRegistryManager registryManager) {
        return this.getOutput(registryManager).copy();
    }

    @Override
    public boolean fits(int width, int height) {
        return false;
    }

    @Override
    public ItemStack getOutput(DynamicRegistryManager registryManager) {
        return this.output;
    }

    @Override
    public boolean isIgnoredInRecipeBook() {
        return true;
    }

    @Override
    public ItemStack createIcon() {
        return new ItemStack(SpaceFactory.ASSEMBLER);
    }

    @Override
    public Identifier getId() {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return TYPE;
    }

    @Override
    public DefaultedList<Ingredient> getIngredients() {
        return this.ingredients;
    }

    @Override
    public int compareTo(@NotNull AssemblerRecipe other) {
        // normal items before rare
        int result = Integer.compare( //
                this.output.getRarity().ordinal(), //
                other.output.getRarity().ordinal());
        if (result != 0) {
            return result;
        }
        // vanilla items before modded
        Identifier id1 = Registries.ITEM.getId(this.output.getItem());
        Identifier id2 = Registries.ITEM.getId(other.output.getItem());
        result = -Boolean.compare( //
                id1.getNamespace().equals("minecraft"), //
                id2.getNamespace().equals("minecraft"));
        if (result != 0) {
            return result;
        }
//        return id1.getPath().compareTo(id2.getPath());
        return Integer.compare(Registries.ITEM.getRawId(this.output.getItem()),
                Registries.ITEM.getRawId(other.output.getItem()));
    }

    public static class Serializer implements RecipeSerializer<AssemblerRecipe> {
        protected final int defaultEnergy;
        protected final int minIngredients, maxIngredients;

        public Serializer(int defaultEnergy, int minIngredients, int maxIngredients) {
            this.defaultEnergy = defaultEnergy;
            this.minIngredients = minIngredients;
            this.maxIngredients = maxIngredients;
        }

        @Override
        public AssemblerRecipe read(Identifier id, JsonObject json) {
            DefaultedList<Ingredient> ingredients;
            if (json.has("ingredient") && json.has("ingredients")) {
                throw new JsonParseException("Expected recipe to have either ingredient or ingredients entry, not both");
            } else if (json.has("ingredients")) {
                JsonArray ingredientsJson = JsonHelper.getArray(json, "ingredients");
                int ingredientCount = ingredientsJson.size();
                if (ingredientCount < this.minIngredients) {
                    throw new JsonParseException(String.format("Recipe %s requires at least %s ingredients, but got %s", this, this.minIngredients, ingredientsJson));
                } else if (ingredientCount > this.maxIngredients) {
                    throw new JsonParseException(String.format("Recipe %s allows at most %s ingredients, but got %s", this, this.maxIngredients, ingredientsJson));
                }
                ingredients = DefaultedList.ofSize(ingredientCount, Ingredient.EMPTY);
                for (int i = 0; i < ingredientCount; i++) {
                    JsonElement ingredientJson = ingredientsJson.get(i);
                    Ingredient ingredient = Ingredient.fromJson(ingredientJson);

                    ingredients.set(i, ingredient);
                }
            } else {
                if (this.minIngredients > 1) {
                    throw new JsonParseException(String.format("Recipe %s requires at least %s ingredients", this, this.minIngredients));
                }
                ingredients = DefaultedList.ofSize(1, Ingredient.EMPTY);
                JsonElement ingredientJson = json.get("ingredient");
                Ingredient ingredient = Ingredient.fromJson(ingredientJson);
                ingredients.set(0, ingredient);
            }

            JsonElement resultsJson = json.get("result");
            if (!resultsJson.isJsonObject()) {
                throw new JsonParseException(String.format("Expected result to be an object, but got %s", resultsJson));
            }
            ItemStack result = ShapedRecipe.outputFromJson(resultsJson.getAsJsonObject());

            int duration = JsonHelper.getInt(json, "energy", this.defaultEnergy);

            return new AssemblerRecipe(id, ingredients, result, duration);
        }

        @Override
        public AssemblerRecipe read(Identifier id, PacketByteBuf buf) {
            int ingredientCount = buf.readInt();
            DefaultedList<Ingredient> ingredients = DefaultedList.ofSize(ingredientCount, Ingredient.EMPTY);
            for (int i = 0; i < ingredientCount; i++) {
                Ingredient ingredient = Ingredient.fromPacket(buf);
                ingredients.set(i, ingredient);
            }
            ItemStack result = buf.readItemStack();
            int energy = buf.readInt();

            return new AssemblerRecipe(id, ingredients, result, energy);
        }

        @Override
        public void write(PacketByteBuf buf, AssemblerRecipe recipe) {
            buf.writeInt(recipe.ingredients.size());
            for (Ingredient ingredient : recipe.ingredients) {
                ingredient.write(buf);
            }
            buf.writeItemStack(recipe.output);
            buf.writeInt(recipe.energy);
        }
    }
}
