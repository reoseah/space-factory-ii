package io.github.reoseah.spacefactory;

import io.github.reoseah.spacefactory.screen.*;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.screen.ScreenHandler;

public class SpaceFactoryClient {
    public static void initialize() {
        HandledScreens.register(AssemblerScreenHandler.TYPE, AssemblerScreen::new);
        HandledScreens.register(ExtractorScreenHandler.TYPE, ExtractorScreen::new);
    }
}