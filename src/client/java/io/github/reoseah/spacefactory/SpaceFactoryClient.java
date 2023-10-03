package io.github.reoseah.spacefactory;

import io.github.reoseah.spacefactory.screen.*;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class SpaceFactoryClient {
    public static void initialize() {
        HandledScreens.register(AssemblerScreenHandler.TYPE, AssemblerScreen::new);
        HandledScreens.register(ExtractorScreenHandler.TYPE, ExtractorScreen::new);
        HandledScreens.register(BedrockMinerScreenHandler.TYPE, BedrockMinerScreen::new);
    }
}