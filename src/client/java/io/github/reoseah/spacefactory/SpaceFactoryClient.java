package io.github.reoseah.spacefactory;

import io.github.reoseah.spacefactory.screen.*;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class SpaceFactoryClient {
    public static void initialize() {
        HandledScreens.register(ProcessingMachineScreenHandler.ASSEMBLER_TYPE, AssemblerScreen::new);
        HandledScreens.register(ProcessingMachineScreenHandler.EXTRACTOR_TYPE, ExtractorScreen::new);
    }
}