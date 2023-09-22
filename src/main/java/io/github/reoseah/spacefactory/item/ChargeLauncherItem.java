package io.github.reoseah.spacefactory.item;

import io.github.reoseah.spacefactory.SpaceFactory;
import net.minecraft.item.ToolItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;

public class ChargeLauncherItem extends ToolItem {
    public static final ToolMaterial MATERIAL = new ToolMaterial() {
        @Override
        public int getDurability() {
            return 255;
        }

        @Override
        public float getMiningSpeedMultiplier() {
            return 0;
        }

        @Override
        public float getAttackDamage() {
            return 0;
        }

        @Override
        public int getMiningLevel() {
            return 0;
        }

        @Override
        public int getEnchantability() {
            return 0;
        }

        @Override
        public Ingredient getRepairIngredient() {
            return Ingredient.ofItems(SpaceFactory.STEEL_SHEET);
        }
    };

    public ChargeLauncherItem(Settings settings) {
        super(MATERIAL, settings);
    }
}
