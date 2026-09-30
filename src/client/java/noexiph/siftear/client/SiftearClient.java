package noexiph.siftear.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.renderer.RenderType;
import noexiph.siftear.block.SiftearBlocks;
import noexiph.siftear.client.model.BlubModel;
import noexiph.siftear.client.model.RiftModel;
import noexiph.siftear.client.model.SiftearModelLayers;
import noexiph.siftear.client.renderer.BlubRenderer;
import noexiph.siftear.client.renderer.RiftRenderer;
import noexiph.siftear.entity.SiftearEntities;

public class SiftearClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		registerModelLayers();
		registerEntityRenderers();
		registerRenderLayers();
	}

	private static void registerEntityRenderers() {
		EntityRendererRegistry.register(SiftearEntities.BLUB, BlubRenderer::new);
		EntityRendererRegistry.register(SiftearEntities.RIFT, RiftRenderer::new);
	}

	private static void registerModelLayers() {
		EntityModelLayerRegistry.registerModelLayer(SiftearModelLayers.BLUB, BlubModel::createBodyLayer);
		EntityModelLayerRegistry.registerModelLayer(SiftearModelLayers.RIFT, RiftModel::getTexturedModelData);
	}

	private static void registerRenderLayers() {
		BlockRenderLayerMap.INSTANCE.putBlock(SiftearBlocks.JELLY_BLOCK, RenderType.translucent());
	}
}