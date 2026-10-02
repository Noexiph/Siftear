package noexiph.siftear.client.renderer;

import noexiph.siftear.Siftear;
import noexiph.siftear.entity.blub.BlubEntity;
import noexiph.siftear.client.model.SiftearModelLayers;
import noexiph.siftear.client.model.BlubModel;
import noexiph.siftear.client.renderer.entity.feature.BlubCollarFeatureRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public class BlubRenderer extends MobRenderer<BlubEntity, BlubModel<BlubEntity>> {
    private static final ResourceLocation WILD_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Siftear.MOD_ID, "textures/entity/blub/blub.png");
    private static final ResourceLocation TAMED_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Siftear.MOD_ID, "textures/entity/blub/blub_tamed.png");

    public BlubRenderer(EntityRendererProvider.Context context) {
        super(context, new BlubModel<>(context.bakeLayer(SiftearModelLayers.BLUB)), 0.35F);
        this.addLayer(new BlubCollarFeatureRenderer(this));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(BlubEntity entity) {
        return entity.isTame() ? TAMED_TEXTURE : WILD_TEXTURE;
    }
}