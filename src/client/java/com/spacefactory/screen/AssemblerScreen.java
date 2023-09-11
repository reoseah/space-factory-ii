package com.spacefactory.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class AssemblerScreen extends HandledScreen<AssemblerScreenHandler> {
    private static final Identifier TEXTURE = new Identifier("spacefactory:textures/gui/smeltery.png");

    public AssemblerScreen(AssemblerScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, ((MutableText) title).formatted(Formatting.AQUA));

        this.backgroundWidth = 184;
        this.backgroundHeight = 198;
        this.playerInventoryTitleY = this.backgroundHeight - 94 - 2;
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

    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        context.drawText(this.textRenderer, this.title, this.titleX, this.titleY, 4210752, false);
        context.drawText(this.textRenderer, Text.translatable("container.inventory").formatted(Formatting.AQUA), this.playerInventoryTitleX, this.playerInventoryTitleY, 4210752, false);
    }


    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        RenderSystem.enableBlend();
        context.drawTexture(TEXTURE, this.x, this.y, 0, 0, this.backgroundWidth, this.backgroundHeight);
    }
}
