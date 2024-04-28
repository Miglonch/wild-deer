package com.Miglon;

import com.Miglon.client.DeerRenderer;
import com.Miglon.client.ModModelLayers;
import com.Miglon.client.deerModel;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class DeerModClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		EntityRendererRegistry.register(DeerMod.DEER_ENTITY, DeerRenderer::new);
		EntityModelLayerRegistry.registerModelLayer(ModModelLayers.DEER, deerModel::getTexturedModelData);
	}
}