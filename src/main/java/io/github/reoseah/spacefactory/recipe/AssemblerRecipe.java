package io.github.reoseah.spacefactory.recipe;

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
    public static final RecipeType<AssemblerRecipe> TYPE = ProcessingRecipeType.ASSEMBLER;

    public static final RecipeSerializer<AssemblerRecipe> SERIALIZER = new Serializer();

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
        protected final int defaultEnergy = 10_000;
        protected final int minIngredients = 1, maxIngredients = 6;

        @Override
        public AssemblerRecipe read(Identifier id, JsonObject json) {
            DefaultedList<Ingredient> ingredients = DefaultedList.of();
            if (json.has("ingredient") && json.has("ingredients")) {
                throw new JsonParseException("Expected recipe to have either ingredient or ingredients entry, not both");
            } else if (json.has("ingredients")) {
                for (JsonElement element : JsonHelper.getArray(json, "ingredients")) {
                    ingredients.add(Ingredient.fromJson(element));
                }
                if (ingredients.size() < this.minIngredients) {
                    throw new JsonParseException(String.format("Recipe %s requires at least %s ingredients, but got %s", this, this.minIngredients, JsonHelper.getArray(json, "ingredients")));
                } else if (ingredients.size() > this.maxIngredients) {
                    throw new JsonParseException(String.format("Recipe %s allows at most %s ingredients, but got %s", this, this.maxIngredients, JsonHelper.getArray(json, "ingredients")));
                }
            } else {
                ingredients.add(Ingredient.fromJson(json.get("ingredient")));
            }

            ItemStack result = ShapedRecipe.outputFromJson(JsonHelper.getObject(json, "result"));
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
