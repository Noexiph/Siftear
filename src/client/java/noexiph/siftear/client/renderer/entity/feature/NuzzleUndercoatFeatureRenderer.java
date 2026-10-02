package noexiph.siftear.client.renderer.entity.feature;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.Util;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import noexiph.siftear.Siftear;
import noexiph.siftear.client.model.NuzzleModel;
import noexiph.siftear.entity.nuzzle.NuzzleEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

@Environment(EnvType.CLIENT)
public class NuzzleUndercoatFeatureRenderer extends RenderLayer<NuzzleEntity, NuzzleModel<NuzzleEntity>> {
    private static final Map<DyeColor, ResourceLocation> UNDERCOAT_TEXTURES = Util.make(Maps.newEnumMap(DyeColor.class), map -> {
        for (DyeColor color : DyeColor.values()) {
            map.put(color, Siftear.id("textures/entity/nuzzle/undercoat/nuzzle_undercoat_" + color.getName() + ".png"));
        }
    });

    public NuzzleUndercoatFeatureRenderer(RenderLayerParent<NuzzleEntity, NuzzleModel<NuzzleEntity>> renderer) {
        super(renderer);
    }

    @Override
    public void render(
            @NotNull PoseStack poseStack,
            @NotNull MultiBufferSource bufferSource,
            int packedLight,
            @NotNull NuzzleEntity entity,
            float limbSwing,
            float limbSwingAmount,
            float partialTick,
            float ageInTicks,
            float netHeadYaw,
            float headPitch
    ) {
        if (entity.isSheared() && !entity.isInvisible()) {
            ResourceLocation texture = UNDERCOAT_TEXTURES.get(entity.getColor());
            coloredCutoutModelCopyLayerRender(
                    this.getParentModel(),
                    this.getParentModel(),
                    texture,
                    poseStack,
                    bufferSource,
                    packedLight,
                    entity,
                    limbSwing,
                    limbSwingAmount,
                    ageInTicks,
                    netHeadYaw,
                    headPitch,
                    partialTick,
                    -1
            );
        }
    }
}