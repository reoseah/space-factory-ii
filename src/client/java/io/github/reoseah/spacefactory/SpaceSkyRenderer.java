package io.github.reoseah.spacefactory;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.CubeMapRenderer;
import net.minecraft.util.Identifier;

public class SpaceSkyRenderer {
    private static final CubeMapRenderer SPACE_SKYBOX = new CubeMapRenderer(new Identifier("spacefactory:textures/environment/space"));

    public static void render(WorldRenderContext ctx) {
        var client = MinecraftClient.getInstance();
        var worldKey = client.player.getWorld().getRegistryKey();

        if (worldKey.getValue().getNamespace().equals("spacefactory")) {
            var camera = client.gameRenderer.getCamera();
            SPACE_SKYBOX.draw(client, camera.getPitch(), -camera.getYaw(), 1f);
        }
    }
}
