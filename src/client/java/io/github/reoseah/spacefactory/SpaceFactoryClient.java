package io.github.reoseah.spacefactory;

import io.github.reoseah.spacefactory.screen.AssemblerScreen;
import io.github.reoseah.spacefactory.screen.AssemblerScreenHandler;
import io.github.reoseah.spacefactory.screen.ExtractorScreen;
import io.github.reoseah.spacefactory.screen.ExtractorScreenHandler;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class SpaceFactoryClient {
    public static void initialize() {
        HandledScreens.register(AssemblerScreenHandler.TYPE, AssemblerScreen::new);
        HandledScreens.register(ExtractorScreenHandler.TYPE, ExtractorScreen::new);
    }
}