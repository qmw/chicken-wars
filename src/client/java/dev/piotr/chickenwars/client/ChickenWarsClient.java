package dev.piotr.chickenwars.client;

import dev.piotr.chickenwars.ChickenWars;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;

public class ChickenWarsClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// the carrier is invisible — the riding vanilla chicken IS the visual
		EntityRendererRegistry.register(ChickenWars.CHICKEN_PROJECTILE, NoopRenderer::new);
		EntityRendererRegistry.register(ChickenWars.WAR_EGG, ThrownItemRenderer::new);
	}
}
