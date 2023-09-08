package com.spacefactory;

import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.render.RenderLayer;

public class SpaceFactoryClient {
    public static void initialize() {
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getTranslucent(),
                SpaceFactory.RED_FORCEFIELD,
                SpaceFactory.GREEN_FORCEFIELD,
                SpaceFactory.BLUE_FORCEFIELD);
    }
}