package io.github.reoseah.spacefactory.emi;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiStack;
import io.github.reoseah.spacefactory.SpaceFactory;
import io.github.reoseah.spacefactory.recipe.AssemblerRecipe;
import io.github.reoseah.spacefactory.recipe.ExtractorRecipe;
import io.github.reoseah.spacefactory.recipe.ProcessingRecipeType;
import io.github.reoseah.spacefactory.screen.AssemblerScreen;
import io.github.reoseah.spacefactory.screen.ExtractorScreen;
import net.minecraft.util.Identifier;

import java.util.Comparator;

@EmiEntrypoint
public class SpaceFactoryEMI implements EmiPlugin {
    public static final EmiRecipeCategory ASSEMBLER = new EmiRecipeCategory( //
            new Identifier("spacefactory:assembler"), //
            EmiStack.of(SpaceFactory.ASSEMBLER), //
            new EmiTexture(AssemblerScreen.TEXTURE, 240, 240, 16, 16),
            Comparator.comparing(emi -> ((AssemblerEmiRecipe) emi).recipe, ProcessingRecipeType.ASSEMBLER));

    public static final EmiRecipeCategory EXTRACTOR = new EmiRecipeCategory( //
            new Identifier("spacefactory:extractor"), //
            EmiStack.of(SpaceFactory.EXTRACTOR), //
            new EmiTexture(ExtractorScreen.TEXTURE, 240, 240, 16, 16),
            Comparator.comparing(emi -> ((ExtractorEmiRecipe) emi).recipe, ProcessingRecipeType.EXTRACTOR));

    @Override
    public void register(EmiRegistry registry) {
        registry.addCategory(EXTRACTOR);
        registry.addCategory(ASSEMBLER);

        registry.addWorkstation(EXTRACTOR, EmiStack.of(SpaceFactory.EXTRACTOR));
        registry.addWorkstation(ASSEMBLER, EmiStack.of(SpaceFactory.ASSEMBLER));

        for (ExtractorRecipe recipe : registry.getRecipeManager().listAllOfType(ExtractorRecipe.TYPE)) {
            registry.addRecipe(new ExtractorEmiRecipe(recipe));
        }
        for (AssemblerRecipe recipe : registry.getRecipeManager().listAllOfType(AssemblerRecipe.TYPE)) {
            registry.addRecipe(new AssemblerEmiRecipe(recipe));
        }
    }
}
