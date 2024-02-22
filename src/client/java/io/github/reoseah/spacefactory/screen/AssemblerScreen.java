package io.github.reoseah.spacefactory.screen;

import io.github.reoseah.spacefactory.api.EnergyI18n;
import io.github.reoseah.spacefactory.recipe.AssemblerRecipe;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.Arrays;

public class AssemblerScreen extends ProcessingMachineScreen {
    public static final Identifier TEXTURE = new Identifier("spacefactory:textures/gui/assembler.png");

    public AssemblerScreen(ProcessingMachineScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected Identifier getTexture() {
        return TEXTURE;
    }



    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        super.drawBackground(context, delta, mouseX, mouseY);
        int duration = this.handler.properties.get(ProcessingMachineScreenHandler.Properties.RECIPE_ENERGY);
        if (duration == 0) {
            duration = 10000;
        }
        int progressToDisplay = this.handler.properties.get(ProcessingMachineScreenHandler.Properties.RECIPE_PROGRESS) * 24 / duration;
        context.drawTexture(this.getTexture(), this.x + 103, this.y + 26, 208, 0, progressToDisplay + 1, 16);
    }

    @Override
    protected void drawMouseoverTooltip(DrawContext context, int mouseX, int mouseY) {
        if (this.isPointWithinBounds(103, 26, 24, 16, mouseX, mouseY)) {
            int recipeEnergy = this.handler.properties.get(ProcessingMachineScreenHandler.Properties.RECIPE_ENERGY);
            int progress = this.handler.properties.get(ProcessingMachineScreenHandler.Properties.RECIPE_PROGRESS);
            Text textProgress = Text.translatable("spacefactory.progress");
            Text textEnergy = EnergyI18n.energyAndCapacity(progress, recipeEnergy).formatted(Formatting.GRAY);
            context.drawTooltip(this.textRenderer, Arrays.asList(textProgress, textEnergy), mouseX, mouseY);
            return;
        }

        super.drawMouseoverTooltip(context, mouseX, mouseY);
    }
}
