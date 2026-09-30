package noexiph.siftear.client.renderer.entity.layer;

import noexiph.siftear.Siftear;
import noexiph.siftear.entity.blub.BlubEntity;
import noexiph.siftear.client.model.BlubModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

@Environment(EnvType.CLIENT)
public class BlubCollarLayer extends RenderLayer<BlubEntity, BlubModel<BlubEntity>> {
    private static final ResourceLocation COLLAR_LOCATION =
            ResourceLocation.fromNamespaceAndPath(Siftear.MOD_ID, "textures/entity/blub/blub_collar.png");

    public BlubCollarLayer(RenderLayerParent<BlubEntity, BlubModel<BlubEntity>> renderer) {
        super(renderer);
    }

    @Override
    public void render(
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight,
            BlubEntity entity,
            float limbSwing,
            float limbSwingAmount,
            float partialTick,
            float ageInTicks,
            float netHeadYaw,
            float headPitch
    ) {
        if (!entity.isTame() || entity.isInvisible()) {
            return;
        }

        int diffuseColor = entity.getCollarColor().getTextureDiffuseColor();
        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(COLLAR_LOCATION));
        this.getParentModel().renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, diffuseColor);
    }
}