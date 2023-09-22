package io.github.reoseah.spacefactory.emi;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiStack;
import io.github.reoseah.spacefactory.SpaceFactory;
import io.github.reoseah.spacefactory.recipe.AssemblerRecipe;
import io.github.reoseah.spacefactory.screen.AssemblerScreen;
import net.minecraft.util.Identifier;

@EmiEntrypoint
public class SpaceFactoryEMI implements EmiPlugin {
    public static final EmiRecipeCategory ASSEMBLER = new EmiRecipeCategory( //
            new Identifier("spacefactory:assembler"), //
            EmiStack.of(SpaceFactory.ASSEMBLER), //
            new EmiTexture(AssemblerScreen.TEXTURE, 240, 240, 16, 16));

    @Override
    public void register(EmiRegistry registry) {
        registry.addCategory(ASSEMBLER);

        registry.addWorkstation(ASSEMBLER, EmiStack.of(SpaceFactory.ASSEMBLER));

        for (AssemblerRecipe recipe : registry.getRecipeManager().listAllOfType(AssemblerRecipe.TYPE)) {
            registry.addRecipe(new AssemblerEmiRecipe(recipe));
        }
    }
}
