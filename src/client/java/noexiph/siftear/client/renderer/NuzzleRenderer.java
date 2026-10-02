package noexiph.siftear.client.renderer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import noexiph.siftear.Siftear;
import noexiph.siftear.client.model.NuzzleModel;
import noexiph.siftear.client.model.SiftearModelLayers;
import noexiph.siftear.client.renderer.entity.feature.NuzzleWoolFeatureRenderer;
import noexiph.siftear.entity.nuzzle.NuzzleEntity;

@Environment(EnvType.CLIENT)
public class NuzzleRenderer extends MobRenderer<NuzzleEntity, NuzzleModel<NuzzleEntity>> {
    private static final ResourceLocation BASE_TEXTURE = Siftear.id("textures/entity/nuzzle/nuzzle.png");

    public NuzzleRenderer(EntityRendererProvider.Context context) {
        super(context, new NuzzleModel<>(context.bakeLayer(SiftearModelLayers.NUZZLE)), 0.7F);
        this.addLayer(new NuzzleWoolFeatureRenderer(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(NuzzleEntity entity) {
        return BASE_TEXTURE;
    }
}