package noexiph.siftear.client.renderer;

import net.minecraft.client.renderer.LightTexture;
import noexiph.siftear.Siftear;
import noexiph.siftear.entity.rift.RiftEntity;
import noexiph.siftear.client.model.SiftearModelLayers;
import noexiph.siftear.client.model.RiftModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class RiftRenderer extends EntityRenderer<RiftEntity> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Siftear.MOD_ID, "textures/entity/rift/rift.png");

    private final RiftModel<RiftEntity> model;

    public RiftRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new RiftModel<>(context.bakeLayer(SiftearModelLayers.RIFT));
    }

    @Override
    public void render(RiftEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        poseStack.translate(0.0F, 1.5F, 0.0F);
        poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(180.0F - entityYaw));
        poseStack.scale(1.0F, -1.0F, -1.0F);

        float ageInTicks = entity.tickCount + partialTick;
        this.model.setupAnim(entity, 0.0F, 0.0F, ageInTicks, 0.0F, 0.0F);

        VertexConsumer consumer = buffer.getBuffer(SiftearRenderTypes.RIFT_EMISSIVE.apply(this.getTextureLocation(entity)));
        this.model.renderToBuffer(poseStack, consumer, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF);

        poseStack.popPose();
        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(RiftEntity entity) {
        return TEXTURE;
    }
}