package io.github.reoseah.spacefactory;

import io.github.reoseah.spacefactory.screen.AssemblerScreen;
import io.github.reoseah.spacefactory.screen.AssemblerScreenHandler;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class SpaceFactoryClient {
    public static void initialize() {
        HandledScreens.register(AssemblerScreenHandler.TYPE, AssemblerScreen::new);
    }
}