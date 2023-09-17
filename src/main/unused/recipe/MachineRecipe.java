package io.github.reoseah.spacefactory.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.objects.ObjectFloatPair;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public abstract class MachineRecipe<I extends Inventory> implements Recipe<I> {
    protected final Identifier id;
    public final DefaultedList<Ingredient> ingredients;
    public final int[] ingredientCounts;
    public final int energy;
    public final ObjectFloatPair<ItemStack>[] outputs;

    public MachineRecipe(Identifier id, DefaultedList<Ingredient> ingredients, int[] ingredientCounts, int energy, ObjectFloatPair<ItemStack>[] outputs) {
        this.id = id;
        this.ingredients = ingredients;
        this.ingredientCounts = ingredientCounts;
        this.energy = energy;
        this.outputs = outputs;
    }

    @Override
    public Identifier getId() {
        return this.id;
    }

    @Override
    public boolean isIgnoredInRecipeBook() {
        return true;
    }

    @Override
    public boolean fits(int width, int height) {
        return false;
    }

    @Override
    public boolean matches(I inventory, World world) {
        for (int i = 0; i < this.ingredients.size(); i++) {
            Ingredient ingredient = this.ingredients.get(i);
            ItemStack stack = inventory.getStack(i);
            if (!ingredient.test(stack) || stack.getCount() < this.ingredientCounts[i]) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack craft(I inventory, DynamicRegistryManager registryManager) {
        if (this.outputs.length > 1 || this.outputs[0].valueFloat() != 1F) {
            throw new UnsupportedOperationException();
        }
        return this.outputs[0].key();
    }

    @Override
    public DefaultedList<Ingredient> getIngredients() {
        return this.ingredients;
    }

    public ObjectFloatPair<ItemStack>[] getOutputs() {
        return this.outputs;
    }

    @Override
    public ItemStack getOutput(DynamicRegistryManager registryManager) {
        return this.outputs.length > 0 ? this.outputs[0].left() : ItemStack.EMPTY;
    }

    public interface Factory<R extends MachineRecipe<?>> {
        R create(Identifier id, DefaultedList<Ingredient> ingredients, int[] ingredientAmounts, int energy, ObjectFloatPair<ItemStack>[] outputs);
    }

    public static class Serializer<R extends MachineRecipe<?>> implements RecipeSerializer<R> {
        protected final Factory<R> factory;

        public final int defaultEnergy;
        public final int minIngredients, maxIngredients;
        public final int minResults, maxResults;

        public Serializer(Factory<R> factory, int defaultEnergy, int minIngredients, int maxIngredients, int minResults, int maxResults) {
            this.factory = factory;
            this.defaultEnergy = defaultEnergy;
            this.minIngredients = minIngredients;
            this.maxIngredients = maxIngredients;
            this.minResults = minResults;
            this.maxResults = maxResults;
        }

        @Override
        public String toString() {
            Identifier id = Registries.RECIPE_SERIALIZER.getId(this);
            return id != null ? id.toString() : super.toString();
        }

        @Override
        public R read(Identifier id, JsonObject json) {
            Pair<DefaultedList<Ingredient>, int[]> ingredients = readIngredients(json, this.minIngredients, this.maxIngredients);
            ObjectFloatPair<ItemStack>[] outputs = readOutputs(json, this.minResults, this.maxResults);

            int energy = JsonHelper.getInt(json, "energy", this.defaultEnergy);

            return this.factory.create(id, ingredients.left(), ingredients.right(), energy, outputs);
        }

        protected static Pair<DefaultedList<Ingredient>, int[]> readIngredients(JsonObject json, int minIngredients, int maxIngredients) {
            DefaultedList<Ingredient> ingredients = DefaultedList.of();
            IntList counts = new IntArrayList();

            if (JsonHelper.hasJsonObject(json, "ingredient")) {
                JsonObject obj = JsonHelper.getObject(json, "ingredient");
                Ingredient ingredient = Ingredient.fromJson(obj);
                int count = JsonHelper.getInt(obj, "count", 1);
                if (count < 1 || count > 64) {
                    throw new JsonParseException(String.format("Ingredient count should be between 1 and 64, but got %s", count));
                }
                ingredients.add(ingredient);
                counts.add(count);
            } else {
                JsonArray array = JsonHelper.getArray(json, "ingredients");
                for (int i = 0; i < array.size(); i++) {
                    JsonObject ingredientJson = array.get(i).getAsJsonObject();
                    Ingredient ingredient = Ingredient.fromJson(ingredientJson);
                    int count = JsonHelper.getInt(ingredientJson, "count", 1);
                    if (count < 1 || count > 64) {
                        throw new JsonParseException(String.format("Ingredient count should be between 1 and 64, but got %s", count));
                    }
                    ingredients.add(ingredient);
                    counts.add(count);
                }
            }

            if (ingredients.size() < minIngredients) {
                throw new JsonParseException(String.format("This recipe type requires at least %s ingredients, but got: %s", minIngredients, ingredients.size()));
            }
            if (ingredients.size() > maxIngredients) {
                throw new JsonParseException(String.format("This recipe type allows at most %s ingredients, but got: %s", maxIngredients, ingredients.size()));
            }
            return Pair.of(ingredients, counts.toIntArray());
        }

        @SuppressWarnings("unchecked")
        protected static ObjectFloatPair<ItemStack>[] readOutputs(JsonObject json, int minResults, int maxResults) {
            ObjectFloatPair<ItemStack>[] outputs;
            if (JsonHelper.hasJsonObject(json, "result")) {
                JsonObject obj = json.getAsJsonObject("result");
                ItemStack stack = TagHelper.outputFromJson(obj);
                float chance = JsonHelper.getFloat(obj, "chance", 1.0F);
                if (chance < 0F || chance > 1F) {
                    throw new JsonParseException(String.format("Result chance should be between 0 and 1, but got %s", chance));
                }
                outputs = new ObjectFloatPair[]{ObjectFloatPair.of(stack, chance)};
            } else {
                JsonArray array = JsonHelper.getArray(json, "result");
                outputs = new ObjectFloatPair[array.size()];
                for (int i = 0; i < outputs.length; i++) {
                    JsonObject output = array.get(i).getAsJsonObject();
                    ItemStack stack = TagHelper.outputFromJson(output);
                    float chance = JsonHelper.getFloat(output, "chance", 1.0F);
                    if (chance < 0F || chance > 1F) {
                        throw new JsonParseException(String.format("Result chance should be between 0 and 1, but got %s", chance));
                    }
                    outputs[i] = ObjectFloatPair.of(stack, chance);
                }
            }
            if (outputs.length < minResults) {
                throw new JsonParseException(String.format("This recipe type requires at least %s results, but got: %s", minResults, outputs.length));
            }
            if (outputs.length > maxResults) {
                throw new JsonParseException(String.format("This recipe type allows at most %s results, but got: %s", maxResults, outputs.length));
            }
            if (outputs[0].valueFloat() < 1) {
                throw new JsonParseException(String.format("First recipe result should have 100%% chance, but got: %s", outputs[0].valueFloat()));
            }
            return outputs;
        }

        @Override
        public R read(Identifier id, PacketByteBuf buf) {
            int ingredientsSize = buf.readVarInt();
            DefaultedList<Ingredient> ingredients = DefaultedList.ofSize(ingredientsSize, Ingredient.EMPTY);
            for (int i = 0; i < ingredientsSize; i++) {
                ingredients.set(i, Ingredient.fromPacket(buf));
            }
            int[] ingredientCounts = new int[ingredientsSize];
            for (int i = 0; i < ingredientsSize; i++) {
                ingredientCounts[i] = buf.readVarInt();
            }
            int energy = buf.readVarInt();
            ObjectFloatPair<ItemStack>[] outputs = readOutputs(buf);

            return this.factory.create(id, ingredients, ingredientCounts, energy, outputs);
        }

        public static ObjectFloatPair<ItemStack>[] readOutputs(PacketByteBuf buf) {
            int outputsCount = buf.readVarInt();
            @SuppressWarnings("unchecked") ObjectFloatPair<ItemStack>[] outputs = new ObjectFloatPair[outputsCount];
            for (int i = 0; i < outputsCount; i++) {
                ItemStack stack = buf.readItemStack();
                float chance = buf.readFloat();
                outputs[i] = ObjectFloatPair.of(stack, chance);
            }
            return outputs;
        }

        @Override
        public void write(PacketByteBuf buf, R recipe) {
            buf.writeVarInt(recipe.ingredients.size());
            for (Ingredient ingredient : recipe.ingredients) {
                ingredient.write(buf);
            }
            for (int count : recipe.ingredientCounts) {
                buf.writeVarInt(count);
            }
            buf.writeVarInt(recipe.energy);
            writeOutputs(buf, recipe.outputs);
        }

        public static void writeOutputs(PacketByteBuf buf, ObjectFloatPair<ItemStack>[] outputs) {
            buf.writeVarInt(outputs.length);
            for (ObjectFloatPair<ItemStack> output : outputs) {
                buf.writeItemStack(output.left());
                buf.writeFloat(output.rightFloat());
            }
        }
    }
}
