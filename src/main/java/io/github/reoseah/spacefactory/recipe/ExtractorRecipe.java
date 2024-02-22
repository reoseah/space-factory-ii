package io.github.reoseah.spacefactory.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import it.unimi.dsi.fastutil.objects.ObjectFloatPair;
import lombok.Getter;
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

public class ExtractorRecipe implements Recipe<Inventory>, Comparable<ExtractorRecipe> {
    public static final RecipeType<ExtractorRecipe> TYPE = new RecipeType<ExtractorRecipe>() {
    };
    public static final RecipeSerializer<ExtractorRecipe> SERIALIZER = new Serializer();

    protected final Identifier id;
    public final Ingredient ingredient;
    public final int ingredientCount;
    public final int energy;
    @Getter
    public final ObjectFloatPair<ItemStack>[] outputs;

    public ExtractorRecipe(Identifier id, Ingredient ingredient, int ingredientCount, int energy, ObjectFloatPair<ItemStack>[] outputs) {
        this.id = id;
        this.ingredient = ingredient;
        this.ingredientCount = ingredientCount;
        this.energy = energy;
        this.outputs = outputs;
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
    public boolean isIgnoredInRecipeBook() {
        return true;
    }

    @Override
    public boolean fits(int width, int height) {
        return false;
    }

    @Override
    public boolean matches(Inventory inventory, World world) {
        ItemStack stack = inventory.getStack(0);
        return this.ingredient.test(stack) //
                && stack.getCount() >= this.ingredientCount;
    }

    @Override
    public ItemStack craft(Inventory inventory, DynamicRegistryManager registryManager) {
        throw new UnsupportedOperationException("This recipe type has multiple outputs with chances, which can't be supported through this API");
    }

    @Override
    public ItemStack getOutput(DynamicRegistryManager registryManager) {
        return this.outputs[0].left();
    }

    @Override
    public DefaultedList<Ingredient> getIngredients() {
        return DefaultedList.copyOf(Ingredient.EMPTY, this.ingredient);
    }

    @Override
    public int compareTo(@NotNull ExtractorRecipe other) {
        // normal items before rare
        int result = Integer.compare( //
                this.outputs[0].key().getRarity().ordinal(), //
                other.outputs[0].key().getRarity().ordinal());
        if (result != 0) {
            return result;
        }
        // vanilla items before modded
        Identifier id1 = Registries.ITEM.getId(this.outputs[0].key().getItem());
        Identifier id2 = Registries.ITEM.getId(other.outputs[0].key().getItem());
        result = -Boolean.compare( //
                id1.getNamespace().equals("minecraft"), //
                id2.getNamespace().equals("minecraft"));
        if (result != 0) {
            return result;
        }
//        return id1.getPath().compareTo(id2.getPath());
        return Integer.compare(Registries.ITEM.getRawId(this.outputs[0].key().getItem()),
                Registries.ITEM.getRawId(other.outputs[0].key().getItem()));
    }

    public static class Serializer implements RecipeSerializer<ExtractorRecipe> {
        public final int defaultEnergy = 15_000;
        public final int minResults = 1, maxResults = 4;

        @Override
        public ExtractorRecipe read(Identifier id, JsonObject json) {
            Ingredient ingredient = Ingredient.fromJson(JsonHelper.getObject(json, "ingredient"));
            int ingredientCount = JsonHelper.getInt(JsonHelper.getObject(json, "ingredient"), "count", 1);

            ObjectFloatPair<ItemStack>[] outputs = readOutputs(json, this.minResults, this.maxResults);

            int energy = JsonHelper.getInt(json, "energy", this.defaultEnergy);

            return new ExtractorRecipe(id, ingredient, ingredientCount, energy, outputs);
        }

        @SuppressWarnings("unchecked")
        protected static ObjectFloatPair<ItemStack>[] readOutputs(JsonObject json, int minResults, int maxResults) {
            ObjectFloatPair<ItemStack>[] outputs;
            if (JsonHelper.hasJsonObject(json, "result")) {
                JsonObject obj = json.getAsJsonObject("result");
                ItemStack stack = ShapedRecipe.outputFromJson(obj);
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
                    ItemStack stack = ShapedRecipe.outputFromJson(output);
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
                throw new JsonParseException(String.format("First result in recipe results should have 100%% chance, but got: %s", outputs[0].valueFloat()));
            }
            return outputs;
        }

        @Override
        public ExtractorRecipe read(Identifier id, PacketByteBuf buf) {
            Ingredient ingredient = Ingredient.fromPacket(buf);
            int ingredientCount = buf.readVarInt();
            int energy = buf.readVarInt();
            ObjectFloatPair<ItemStack>[] outputs = readOutputs(buf);

            return new ExtractorRecipe(id, ingredient, ingredientCount, energy, outputs);
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
        public void write(PacketByteBuf buf, ExtractorRecipe recipe) {
            recipe.ingredient.write(buf);
            buf.writeVarInt(recipe.ingredientCount);
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
