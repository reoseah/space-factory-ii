package io.github.reoseah.spacefactory.emi;

import com.google.common.collect.ImmutableList;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import io.github.reoseah.spacefactory.SpaceFactory;
import io.github.reoseah.spacefactory.api.EnergyI18n;
import io.github.reoseah.spacefactory.recipe.ExtractorRecipe;
import io.github.reoseah.spacefactory.screen.AssemblerScreen;
import io.github.reoseah.spacefactory.screen.ExtractorScreen;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class ExtractorEmiRecipe implements EmiRecipe {
    public static final EmiTexture RECIPE_ARROW = new EmiTexture(ExtractorScreen.TEXTURE, 103, 26, 24, 16);
    public static final EmiTexture RECIPE_ARROW_PROGRESS = new EmiTexture(ExtractorScreen.TEXTURE, 208, 0, 24, 16);

    public final ExtractorRecipe recipe;
    private final EmiIngredient input;
    private final List<EmiStack> outputs;

    public ExtractorEmiRecipe(ExtractorRecipe recipe) {
        this.recipe = recipe;

        this.input = EmiIngredient.of(recipe.ingredient, recipe.ingredientCount) //
                .setAmount(recipe.ingredientCount);
        this.outputs = Arrays.stream(recipe.outputs) //
                .map(stackAndChance -> EmiStack.of(stackAndChance.first()) //
                        .setChance(stackAndChance.secondFloat())) //
                .toList();
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return SpaceFactoryEMI.EXTRACTOR;
    }

    @Override
    public @Nullable Identifier getId() {
        return this.recipe.getId();
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return ImmutableList.of(this.input);
    }

    @Override
    public List<EmiStack> getOutputs() {
        return this.outputs;
    }

    @Override
    public int getDisplayWidth() {
        return 90;
    }

    @Override
    public int getDisplayHeight() {
        return 36;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addSlot(this.input, 0, 8);
        for (int i = 0; i < 4; i++) {
            int x = 54 + (i % 2) * 18;
            int y = (i / 2) * 18;
            EmiStack ingredient = i < this.outputs.size() ? this.outputs.get(i) : EmiStack.EMPTY;
            widgets.addSlot(ingredient, x, y).recipeContext(this);
        }

        float time = ((float) this.recipe.energy) / SpaceFactory.config.getExtractorEnergyConsumption() / 20;

        widgets.addTexture(RECIPE_ARROW, 24, 9).tooltipText(List.of(Text.translatable("emi.cooking.time", time), EnergyI18n.amountAndAmountPerTick(this.recipe.energy, SpaceFactory.config.getExtractorEnergyConsumption()).formatted(Formatting.GRAY)));
        widgets.addAnimatedTexture(RECIPE_ARROW_PROGRESS, 24, 9, (int) (time * 1000), true, false, false);
    }
}
