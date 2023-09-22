package io.github.reoseah.spacefactory.emi;

import com.google.common.collect.ImmutableList;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import io.github.reoseah.spacefactory.recipe.AssemblerRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AssemblerEmiRecipe implements EmiRecipe {
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
        return 132;
    }

    @Override
    public int getDisplayHeight() {
        return 36;
    }


    @Override
    public void addWidgets(WidgetHolder widgets) {
        for (int i = 0; i < 6; i++) {
            EmiIngredient ingredient = i < input.size() ? input.get(i) : EmiStack.EMPTY;
            widgets.addSlot(ingredient, 14 + i % 3 * 18, i / 3 * 18);
        }
        widgets.addSlot(output, 106, 6).large(true).recipeContext(this);
    }
}
