package noexiph.siftear.client.renderer.entity.feature;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.Util;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import noexiph.siftear.Siftear;
import noexiph.siftear.client.model.NuzzleModel;
import noexiph.siftear.client.model.NuzzleWoolModel;
import noexiph.siftear.client.model.SiftearModelLayers;
import noexiph.siftear.entity.nuzzle.NuzzleEntity;

import java.util.Map;

@Environment(EnvType.CLIENT)
public class NuzzleWoolFeatureRenderer extends RenderLayer<NuzzleEntity, NuzzleModel<NuzzleEntity>> {
    private static final Map<DyeColor, ResourceLocation> WOOL_TEXTURES = Util.make(Maps.newEnumMap(DyeColor.class), map -> {
        for (DyeColor color : DyeColor.values()) {
            map.put(color, Siftear.id("textures/entity/nuzzle/wool/nuzzle_wool_" + color.getName() + ".png"));
        }
    });

    private final NuzzleWoolModel<NuzzleEntity> woolModel;

    public NuzzleWoolFeatureRenderer(RenderLayerParent<NuzzleEntity, NuzzleModel<NuzzleEntity>> parent, EntityModelSet entityModelSet) {
        super(parent);
        this.woolModel = new NuzzleWoolModel<>(entityModelSet.bakeLayer(SiftearModelLayers.NUZZLE_WOOL));
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, NuzzleEntity entity, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!entity.isSheared()) {
            ResourceLocation textureLocation = WOOL_TEXTURES.get(entity.getColor());
            coloredCutoutModelCopyLayerRender(this.getParentModel(), this.woolModel, textureLocation, poseStack, bufferSource, packedLight, entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, partialTick, -1);
        }
    }
}