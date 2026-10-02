package noexiph.siftear.client.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import noexiph.siftear.client.renderer.entity.animation.NuzzleModelAnimation;
import noexiph.siftear.entity.nuzzle.NuzzleEntity;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public class NuzzleModel<T extends NuzzleEntity> extends HierarchicalModel<T> {
    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart leftHorn;
    private final ModelPart rightHorn;
    private final ModelPart frontRightLeg;
    private final ModelPart frontLeftLeg;
    private final ModelPart hindRightLeg;
    private final ModelPart hindLeftLeg;

    public NuzzleModel(ModelPart root) {
        this.root = root.getChild("root");
        this.body = this.root.getChild("body");
        this.head = this.body.getChild("head");
        this.leftHorn = this.body.getChild("left_horn");
        this.rightHorn = this.body.getChild("right_horn");
        this.frontRightLeg = this.root.getChild("front_right_leg");
        this.frontLeftLeg = this.root.getChild("front_left_leg");
        this.hindRightLeg = this.root.getChild("hind_right_leg");
        this.hindLeftLeg = this.root.getChild("hind_left_leg");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();

        PartDefinition root = partDefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 13.0F, -6.0F));

        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create()
                        .texOffs(7, 2).addBox(-6.0F, -6.0F, -6.0F, 12.0F, 12.0F, 12.0F, new CubeDeformation(0.0F)),
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

    @Override
    public @NotNull ModelPart root() {
        return this.root;
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);

        float clampedYaw = Mth.clamp(netHeadYaw, -7.5F, 7.5F);
        float clampedPitch = Mth.clamp(headPitch, -7.5F, 7.5F);
        this.head.yRot = clampedYaw * ((float) Math.PI / 180F);
        this.head.xRot = clampedPitch * ((float) Math.PI / 180F);

        if (entity.isPanicking()) {
            this.animateWalk(NuzzleModelAnimation.run, limbSwing, limbSwingAmount, 2.0F, 2.5F);
        } else {
            this.animateWalk(NuzzleModelAnimation.walk, limbSwing, limbSwingAmount, 2.0F, 2.5F);
        }

        this.animate(entity.idleAnimationState, NuzzleModelAnimation.idle, ageInTicks);
        this.animate(entity.eatAnimationState, NuzzleModelAnimation.eat_grass, ageInTicks);
    }
}