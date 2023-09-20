package io.github.reoseah.spacefactory.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.reoseah.spacefactory.SpaceFactory;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ExtractorScreen extends HandledScreen<ExtractorScreenHandler> {
    private static final Identifier TEXTURE = new Identifier("spacefactory:textures/gui/extractor.png");

    public ExtractorScreen(ExtractorScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundHeight = 149;
        this.playerInventoryTitleY = 55;
    }

    @Override
    protected void init() {
        super.init();
        this.titleX = (this.backgroundWidth - this.textRenderer.getWidth(this.title)) / 2;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(context, mouseX, mouseY);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        RenderSystem.enableBlend();
        context.drawTexture(TEXTURE, this.x, this.y, 0, 0, this.backgroundWidth, this.backgroundHeight);
        int energy = this.handler.properties.get(AssemblerScreenHandler.Properties.ENERGY);
        if (energy > 0) {
            int capacity = this.handler.properties.get(AssemblerScreenHandler.Properties.ENERGY_CAPACITY);
            if (capacity <= 0) {
                capacity = SpaceFactory.config.getAssemblerEnergyCapacity();
            }
            int energyBarHeight = Math.max(energy * 32 / capacity, 1);
            context.drawTexture(TEXTURE, this.x + 11, this.y + 51 - energyBarHeight, 208, 32 - energyBarHeight, 10, energyBarHeight);
        }
        int duration = this.handler.properties.get(AssemblerScreenHandler.Properties.RECIPE_ENERGY);
        if (duration == 0) {
            duration = 10000;
        }
        int progressToDisplay = this.handler.properties.get(AssemblerScreenHandler.Properties.RECIPE_PROGRESS) * 24 / duration;
        context.drawTexture(TEXTURE, this.x + 71, this.y + 27, 176, 0, progressToDisplay + 1, 16);
    }
}
