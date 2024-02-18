package io.github.reoseah.spacefactory.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.reoseah.spacefactory.SpaceFactory;
import io.github.reoseah.spacefactory.api.EnergyI18n;
import io.github.reoseah.spacefactory.block.GhostSlotMachineBlockEntity;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.MathHelper;

import java.util.Arrays;
import java.util.List;

public abstract class GhostSlotMachineScreen<R extends Recipe<? super GhostSlotMachineBlockEntity<R>> & Comparable<R>, H extends GhostSlotMachineScreenHandler<R>> extends HandledScreen<H> {
    private int scrollOffset = 0;
    private float ghostSlotsTime = 0F;

    public GhostSlotMachineScreen(H handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundHeight = 196;
        this.playerInventoryTitleY = this.backgroundHeight - 94;
    }

    protected abstract Identifier getTexture();

    protected abstract ItemStack getOutput(R recipe);

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
        context.drawTexture(this.getTexture(), this.x, this.y, 0, 0, this.backgroundWidth, this.backgroundHeight);

        int energy = this.handler.properties.get(GhostSlotMachineScreenHandler.Properties.ENERGY);
        if (energy > 0) {
            int capacity = this.handler.properties.get(GhostSlotMachineScreenHandler.Properties.ENERGY_CAPACITY);
            if (capacity <= 0) {
                capacity = SpaceFactory.config.getAssemblerEnergyCapacity();
            }
            int energyBarHeight = Math.max(energy * 32 / capacity, 1);
            context.drawTexture(this.getTexture(), this.x + 11, this.y + 51 - energyBarHeight, 240, 32 - energyBarHeight, 10, energyBarHeight);
        }

        this.drawRecipeBackground(context, mouseX, mouseY);
        this.drawRecipeIcons(context);
        this.drawGhostSlots(context, delta);
    }

    @Override
    protected void drawMouseoverTooltip(DrawContext context, int mouseX, int mouseY) {
        if (this.isPointWithinBounds(8, 16, 18, 40, mouseX, mouseY)) {
            int energy = this.handler.properties.get(GhostSlotMachineScreenHandler.Properties.ENERGY);
            int capacity = this.handler.properties.get(GhostSlotMachineScreenHandler.Properties.ENERGY_CAPACITY);
            float energyPerTick = this.handler.properties.get(GhostSlotMachineScreenHandler.Properties.ENERGY_PER_TICK_TIMES_100) / 100F;

            Text textEnergy = EnergyI18n.ENERGY;
            Text textStored = EnergyI18n.energyAndCapacity(energy, capacity).formatted(Formatting.GRAY);
            Text textEuPerTick = EnergyI18n.averageInputPerTick(energyPerTick).formatted(Formatting.GRAY);
            context.drawTooltip(this.textRenderer, Arrays.asList(textEnergy, textStored, textEuPerTick), mouseX, mouseY);
            return;
        }

        if (this.drawRecipeIconTooltip(context, mouseX, mouseY)) {
            return;
        }
        if (this.drawGhostSlotTooltip(context, mouseX, mouseY)) {
            return;
        }

        super.drawMouseoverTooltip(context, mouseX, mouseY);
    }

    protected void drawRecipeBackground(DrawContext context, int mouseX, int mouseY) {
        for (int i = this.scrollOffset; i < this.scrollOffset + 12 && i < this.handler.getAvailableRecipes().size(); i++) {
            int pos = i - this.scrollOffset;

            int x = this.x + 9 + (pos % 8) * 18;
            int y = this.y + 63 + (pos / 8) * 18;

            int v = 16;
            if (i == this.handler.getSelectedRecipeIdx()) {
                v += 18;
            } else if (mouseX >= x && mouseY >= y && mouseX < x + 18 && mouseY < y + 18) {
                v += 36;
            }

            context.drawTexture(this.getTexture(), x, y, 176, v, 18, 18);
        }
    }

    protected void drawRecipeIcons(DrawContext context) {
        List<R> recipes = this.handler.getAvailableRecipes();

        for (int i = this.scrollOffset; i < this.scrollOffset + 12 && i < recipes.size(); i++) {
            int pos = i - this.scrollOffset;

            int x = this.x + 10 + (pos % 8) * 18;
            int y = this.y + 64 + (pos / 8) * 18;

            context.drawItem(this.getOutput(recipes.get(i)), x, y, i);
        }
    }

    protected boolean drawRecipeIconTooltip(DrawContext context, int mouseX, int mouseY) {
        List<R> recipes = this.handler.getAvailableRecipes();
        for (int idx = this.scrollOffset; idx < this.scrollOffset + 12 && idx < recipes.size(); idx++) {
            int pos = idx - this.scrollOffset;

            int x = this.x + 9 + (pos % 8) * 18;
            int y = this.y + 63 + (pos / 8) * 18;

            if (mouseX >= x && mouseY >= y && mouseX < x + 18 && mouseY < y + 18) {
                ItemStack output = this.getOutput(recipes.get(idx));
                List<Text> tooltip = this.getTooltipFromItem(output);
                if (idx != this.handler.getSelectedRecipeIdx()) {
                    R recipe = recipes.get(idx);
                    if (!this.handler.canSwitchToRecipe(recipe)) {
                        tooltip.add(Text.translatable("spacefactory.cannot_switch_recipe_with_current_ingredients").formatted(Formatting.RED));
                    }
                }
                context.drawTooltip(this.textRenderer, tooltip, mouseX, mouseY);

                return true;
            }
        }
        return false;
    }

    protected void drawGhostSlots(DrawContext context, float delta) {
        if (!Screen.hasControlDown()) {
            this.ghostSlotsTime += delta;
        }
        R recipe = this.handler.getAvailableRecipes().get(this.handler.getSelectedRecipeIdx());
        DefaultedList<Ingredient> ingredients = recipe.getIngredients();
        for (int i = 0; i < ingredients.size(); i++) {
            Slot slot = this.handler.getSlot(i);

            int x = this.x + slot.x;
            int y = this.y + slot.y;

            boolean noItem = this.handler.getSlot(i).getStack().isEmpty();
            boolean notValidOrNotEnough = !ingredients.get(i).test(this.handler.getSlot(i).getStack()) || ingredients.get(i).getMatchingStacks().length > 0 && this.handler.getSlot(i).getStack().getCount() < ingredients.get(i).getMatchingStacks()[0].getCount();
            if (notValidOrNotEnough) {
                context.fill(x, y, x + 16, y + 16, 0x30FF0000);

                boolean isTag = false;
                ItemStack ghostStack = null;
                if (noItem) {
                    Ingredient ingredient = ingredients.get(i);

                    if (ingredient.entries.length == 1 //
                            && ingredient.entries[0] instanceof Ingredient.TagEntry) {
                        ghostStack = ingredient.getMatchingStacks()[0]; // allow to set stack to specific one? similar to EMI tag models
                        isTag = true;
                    } else {
                        ItemStack[] stacks = ingredient.getMatchingStacks();
                        ghostStack = stacks.length > 0 ? stacks[MathHelper.floor(this.ghostSlotsTime / 30.0F) % stacks.length] : ItemStack.EMPTY;

                    }
                    context.drawItem(ghostStack, x, y);
                }
                RenderSystem.depthFunc(516);
                context.fill(RenderLayer.getGuiGhostRecipeOverlay(), x, y, x + 16, y + 16, 0x30FFFFFF);

                if (noItem) {
                    RenderSystem.depthFunc(515);
//                    context.drawItemTooltip(this.textRenderer, ghostStack, x, y);
                    if (isTag) {
//                        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
//                        RenderSystem.setShaderTexture(0, TEXTURE);

                        // TODO figure out where is z index now
//                        this.setZOffset(this.getZOffset() + 100);
//                        context.drawTexture(TEXTURE, x, y + 16 - 4, 0, 196, 4, 4);
//                        this.setZOffset(this.getZOffset() - 100);
                    }
                }
            }
        }
    }

    protected boolean drawGhostSlotTooltip(DrawContext context, int mouseX, int mouseY) {
        R recipe = this.handler.getAvailableRecipes() //
                .get(this.handler.getSelectedRecipeIdx());
        DefaultedList<Ingredient> ingredients = recipe.getIngredients();
        for (int i = 0; i < ingredients.size(); i++) {
            Slot slot = this.handler.getSlot(i);

            int x = this.x + slot.x - 1;
            int y = this.y + slot.y - 1;

            if (mouseX >= x && mouseY >= y && mouseX < x + 18 && mouseY < y + 18 //
                    && slot.getStack().isEmpty()) {
                // FIXME reimplement custom tag tooltips
//                this.renderTooltip(matrices, AssortechTooltips.of(ingredients.get(i), this.ghostSlotsTime),
//                        AssortechTooltips.dataFor(ingredients.get(i)), mouseX, mouseY);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        List<R> recipes = this.handler.getAvailableRecipes();
        for (int idx = this.scrollOffset; idx < this.scrollOffset + 12 && idx < recipes.size(); idx++) {
            int pos = idx - this.scrollOffset;

            int x = this.x + 9 + (pos % 8) * 18;
            int y = this.y + 63 + (pos / 8) * 18;

            if (mouseX >= x && mouseY >= y && mouseX < x + 18 && mouseY < y + 18 //
                    && this.handler.canSwitchToRecipe(recipes.get(idx))) {
                this.client.interactionManager.clickButton(this.handler.syncId, idx);
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }
}
