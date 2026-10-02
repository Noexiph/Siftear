package noexiph.siftear.client.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import noexiph.siftear.entity.nuzzle.NuzzleEntity;

@Environment(EnvType.CLIENT)
public class NuzzleWoolModel<T extends NuzzleEntity> extends NuzzleModel<T> {
    public NuzzleWoolModel(ModelPart root) {
        super(root);
    }

    public static LayerDefinition createWoolLayer() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();

        PartDefinition root = partDefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 13.0F, -6.0F));

        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create()
                        .texOffs(4, 0).addBox(-7.0F, -7.0F, -7.0F, 14.0F, 14.0F, 14.0F, new CubeDeformation(0.0F))
                        .texOffs(15, 29).addBox(-7.0F, -7.0F, 7.0F, 14.0F, 14.0F, 3.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 0.0F, 6.0F));

        body.addOrReplaceChild("head", CubeListBuilder.create()
                        .texOffs(20, 48).addBox(-4.0F, -4.0F, -2.0F, 8.0F, 8.0F, 4.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 0.0F, -6.0F));

        body.addOrReplaceChild("left_horn", CubeListBuilder.create()
                        .texOffs(47, 1).addBox(-1.0F, -10.0F, -1.0F, 2.0F, 10.0F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offset(4.0F, -6.0F, -5.0F));

        body.addOrReplaceChild("right_horn", CubeListBuilder.create()
                        .texOffs(9, 1).addBox(-1.0F, -10.0F, -1.0F, 2.0F, 10.0F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offset(-4.0F, -6.0F, -5.0F));

        root.addOrReplaceChild("front_right_leg", CubeListBuilder.create()
                        .texOffs(0, 45).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 5.0F, 4.0F, new CubeDeformation(-0.02F)),
                PartPose.offset(-4.0F, 6.0F, 2.0F));

        root.addOrReplaceChild("front_left_leg", CubeListBuilder.create()
                        .texOffs(48, 45).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 5.0F, 4.0F, new CubeDeformation(-0.02F)),
                PartPose.offset(4.0F, 6.0F, 2.0F));

        root.addOrReplaceChild("hind_right_leg", CubeListBuilder.create()
                        .texOffs(0, 55).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 5.0F, 4.0F, new CubeDeformation(-0.02F)),
                PartPose.offset(-4.0F, 6.0F, 10.0F));

        root.addOrReplaceChild("hind_left_leg", CubeListBuilder.create()
                        .texOffs(48, 55).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 5.0F, 4.0F, new CubeDeformation(-0.02F)),
                PartPose.offset(4.0F, 6.0F, 10.0F));

        return LayerDefinition.create(meshDefinition, 64, 64);
    }
}