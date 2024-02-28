package io.github.reoseah.spacefactory;

import io.github.reoseah.spacefactory.screen.*;
import net.fabricmc.fabric.api.client.rendering.v1.DimensionRenderingRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class SpaceFactoryClient {
    public static void initialize() {
        HandledScreens.register(ProcessingMachineScreenHandler.ASSEMBLER_TYPE, AssemblerScreen::new);
        HandledScreens.register(ProcessingMachineScreenHandler.EXTRACTOR_TYPE, ExtractorScreen::new);

        DimensionRenderingRegistry.registerSkyRenderer(RegistryKey.of(RegistryKeys.WORLD, new Identifier("spacefactory:geostationary_orbit")), SpaceSkyRenderer::render);
        DimensionRenderingRegistry.registerDimensionEffects(new Identifier("spacefactory", "space"), new SpaceDimensionEffects());
    }
}