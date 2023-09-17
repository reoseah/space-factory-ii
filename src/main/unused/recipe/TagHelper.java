package io.github.reoseah.spacefactory.recipe;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import net.fabricmc.fabric.impl.resource.conditions.ResourceConditionsImpl;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class TagHelper {
    private static final List<String> PREFERRED_NAMESPACES = Arrays.asList("minecraft", "spacefactory");

    public static ItemStack outputFromJson(JsonObject json) {
        if (!json.has("tag")) {
            return ShapedRecipe.outputFromJson(json);
        }
        Identifier id = new Identifier(JsonHelper.getString(json, "tag"));
        TagKey<Item> tag = TagKey.of(RegistryKeys.ITEM, id);

        Item item = TagHelper.findEntry(tag);
        if (item == null) {
            throw new JsonSyntaxException("Invalid tag: " + tag);
        }
        int count = JsonHelper.getInt(json, "count", 1);
        ItemStack stack = new ItemStack(item, count);
        if (json.has("data")) {
            JsonObject data = JsonHelper.getObject(json, "data");
            NbtElement nbt = Dynamic.convert(JsonOps.INSTANCE, NbtOps.INSTANCE, data);
            if (nbt instanceof NbtCompound compound) {
                stack.setNbt(compound);
            }
        }
        return stack;
    }

    public static Item findEntry(TagKey<Item> tag) {
        Map<Identifier, Collection<RegistryEntry<?>>> identifierToEntries = ResourceConditionsImpl.LOADED_TAGS.get().get(RegistryKeys.ITEM);
        Collection<RegistryEntry<?>> entries = identifierToEntries.get(tag.id());

        if (entries == null) {
            return null;
        }

        Item bestFit = null;
        int bestScore = Integer.MAX_VALUE;
        for (RegistryEntry<?> entry : entries) {
            int score = PREFERRED_NAMESPACES.indexOf(entry.getKey().get().getValue().getNamespace());
            if (score != -1 && score < bestScore) {
                bestScore = score;
                bestFit = (Item) entry.value();
            } else if (bestFit == null) {
                bestFit = (Item) entry.value();
            }
        }
        return bestFit;
    }
}
