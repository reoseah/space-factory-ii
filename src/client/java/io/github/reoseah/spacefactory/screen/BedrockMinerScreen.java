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

public class BedrockMinerScreen extends HandledScreen<BedrockMinerScreenHandler> {
    public static final Identifier TEXTURE = new Identifier("spacefactory:textures/gui/bedrock_miner.png");

    public BedrockMinerScreen(BedrockMinerScreenHandler handler, PlayerInventory inventory, Text title) {
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
        int energy = this.handler.properties.get(BedrockMinerScreenHandler.Properties.ENERGY);
        if (energy > 0) {
            int capacity = this.handler.properties.get(BedrockMinerScreenHandler.Properties.ENERGY_CAPACITY);
            if (capacity <= 0) {
                capacity = SpaceFactory.config.getBedrockMinerEnergyCapacity();
            }
            int energyBarHeight = Math.max(energy * 32 / capacity, 1);
            context.drawTexture(TEXTURE, this.x + 11, this.y + 51 - energyBarHeight, 208, 32 - energyBarHeight, 10, energyBarHeight);
        }
        int drillSupply = this.handler.properties.get(BedrockMinerScreenHandler.Properties.DRILL_SUPPLY_LEFT);
        if (drillSupply > 0) {
            int drillSupplyTotal = this.handler.properties.get(BedrockMinerScreenHandler.Properties.DRILL_SUPPLY_TOTAL);
            if (drillSupplyTotal <= 0) {
                drillSupplyTotal = SpaceFactory.config.getBedrockMinerEnergyCapacity();
            }
            int drillSupplyHeight = Math.max(Math.round(drillSupply * 16f / drillSupplyTotal), 1);
            context.drawTexture(TEXTURE, this.x + 51, this.y + 54 - drillSupplyHeight, 176, 33 - drillSupplyHeight, 16, drillSupplyHeight);
        }
        int progressToDisplay = this.handler.properties.get(BedrockMinerScreenHandler.Properties.DRILL_PROGRESS) * 24 / SpaceFactory.config.getBedrockMinerDrillingDuration();
        context.drawTexture(TEXTURE, this.x + 76, this.y + 18, 176, 0, progressToDisplay + 1, 16);
    }

    @Override
    protected void drawMouseoverTooltip(DrawContext context, int mouseX, int mouseY) {
        if (this.isPointWithinBounds(8, 16, 18, 40, mouseX, mouseY)) {
            int energy = this.handler.properties.get(BedrockMinerScreenHandler.Properties.ENERGY);
            int capacity = this.handler.properties.get(BedrockMinerScreenHandler.Properties.ENERGY_CAPACITY);
            float energyPerTick = this.handler.properties.get(BedrockMinerScreenHandler.Properties.ENERGY_PER_TICK_TIMES_100) / 100F;

            Text textEnergy = EnergyI18n.ENERGY;
            Text textStored = EnergyI18n.energyAndCapacity(energy, capacity).formatted(Formatting.GRAY);
            Text textEuPerTick = EnergyI18n.averageInputPerTick(energyPerTick).formatted(Formatting.GRAY);
            context.drawTooltip(this.textRenderer, Arrays.asList(textEnergy, textStored, textEuPerTick), mouseX, mouseY);
            return;
        }

        super.drawMouseoverTooltip(context, mouseX, mouseY);
    }
}
