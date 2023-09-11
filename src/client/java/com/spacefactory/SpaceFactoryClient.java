package com.spacefactory;

import com.spacefactory.screen.AssemblerScreen;
import com.spacefactory.screen.AssemblerScreenHandler;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class SpaceFactoryClient {
    public static void initialize() {
        HandledScreens.register(AssemblerScreenHandler.TYPE, AssemblerScreen::new);
    }
}