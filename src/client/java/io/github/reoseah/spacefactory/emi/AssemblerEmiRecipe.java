package io.github.reoseah.spacefactory.emi;

import com.google.common.collect.ImmutableList;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import io.github.reoseah.spacefactory.SpaceFactory;
import io.github.reoseah.spacefactory.SpaceFactoryConfig;
import io.github.reoseah.spacefactory.api.EnergyI18n;
import io.github.reoseah.spacefactory.recipe.AssemblerRecipe;
import io.github.reoseah.spacefactory.screen.AssemblerScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AssemblerEmiRecipe implements EmiRecipe {
    public static final EmiTexture RECIPE_ARROW = new EmiTexture(AssemblerScreen.TEXTURE, 103, 26, 24, 16);
    public static final EmiTexture RECIPE_ARROW_PROGRESS = new EmiTexture(AssemblerScreen.TEXTURE, 208, 0, 24, 16);

    private final AssemblerRecipe recipe;
    private final List<EmiIngredient> input;
    private final EmiStack output;

    public AssemblerEmiRecipe(AssemblerRecipe recipe) {
        this.recipe = recipe;
        this.input = recipe.getIngredients().stream().map(EmiIngredient::of).toList();
        this.output = EmiStack.of(recipe.output);
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return SpaceFactoryEMI.ASSEMBLER;
    }

    @Override
    public @Nullable Identifier getId() {
        return this.recipe.getId();
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return this.input;
    }

    @Override
    public List<EmiStack> getOutputs() {
        return ImmutableList.of(this.output);
    }

    @Override
    public int getDisplayWidth() {
        return 118;
    }

    @Override
    public int getDisplayHeight() {
        return 36;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        for (int i = 0; i < 6; i++) {
            EmiIngredient ingredient = i < input.size() ? input.get(i) : EmiStack.EMPTY;
            widgets.addSlot(ingredient, i % 3 * 18, i / 3 * 18);
        }
        widgets.addSlot(output, 92, 6).large(true).recipeContext(this);

        float time = ((float) this.recipe.energy) / SpaceFactory.config.getAssemblerEnergyConsumption() / 20;

        widgets.addTexture(RECIPE_ARROW, 60, 10).tooltipText(List.of(
                Text.translatable("emi.cooking.time", time),
                EnergyI18n.amountAndAmountPerTick(this.recipe.energy, SpaceFactory.config.getAssemblerEnergyConsumption()).formatted(Formatting.GRAY)));
        widgets.addAnimatedTexture(RECIPE_ARROW_PROGRESS, 60, 10, (int) (time * 1000), true, false, false);
    }
}
