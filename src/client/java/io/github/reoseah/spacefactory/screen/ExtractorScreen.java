package io.github.reoseah.spacefactory.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.reoseah.spacefactory.SpaceFactory;
import io.github.reoseah.spacefactory.api.EnergyI18n;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.Arrays;

public class ExtractorScreen extends HandledScreen<ExtractorScreenHandler> {
    private static final Identifier TEXTURE = new Identifier("spacefactory:textures/gui/extractor.png");

    public ExtractorScreen(ExtractorScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundHeight = 151;
        this.playerInventoryTitleY = this.backgroundHeight - 94;
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

    @Override
    protected void drawMouseoverTooltip(DrawContext context, int mouseX, int mouseY) {
        if (this.isPointWithinBounds(8, 16, 18, 40, mouseX, mouseY)) {
            int energy = this.handler.properties.get(AssemblerScreenHandler.Properties.ENERGY);
            int capacity = this.handler.properties.get(AssemblerScreenHandler.Properties.ENERGY_CAPACITY);
            float energyPerTick = this.handler.properties.get(AssemblerScreenHandler.Properties.ENERGY_PER_TICK_TIMES_100) / 100F;

            Text textEnergy = EnergyI18n.ENERGY;
            Text textStored = EnergyI18n.energyAndCapacity(energy, capacity).formatted(Formatting.GRAY);
            Text textEuPerTick = EnergyI18n.averageInputPerTick(energyPerTick).formatted(Formatting.GRAY);
            context.drawTooltip(this.textRenderer, Arrays.asList(textEnergy, textStored, textEuPerTick), mouseX, mouseY);
            return;
        }
        if (this.isPointWithinBounds(71, 27, 24, 16, mouseX, mouseY)) {
            int totalEnergy = this.handler.properties.get(AssemblerScreenHandler.Properties.RECIPE_ENERGY);
            int progress = this.handler.properties.get(AssemblerScreenHandler.Properties.RECIPE_PROGRESS);
            Text textProgress = Text.translatable("spacefactory.progress");
            Text textEnergy = EnergyI18n.energyAndCapacity(progress, totalEnergy).formatted(Formatting.GRAY);
            context.drawTooltip(this.textRenderer, Arrays.asList(textProgress, textEnergy), mouseX, mouseY);
            return;
        }

        super.drawMouseoverTooltip(context, mouseX, mouseY);
    }
}
