package noexiph.siftear.client.model;

import noexiph.siftear.entity.blub.BlubEntity;
import noexiph.siftear.entity.blub.BlubState;
import noexiph.siftear.client.renderer.entity.animation.BlubModelAnimation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public class BlubModel<T extends BlubEntity> extends HierarchicalModel<T> {
    private static final float WANDER_EAR_PITCH = -15.0F * Mth.DEG_TO_RAD;
    private static final float WANDER_EAR_ROLL = 25.0F * Mth.DEG_TO_RAD;

    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart rightEar;
    private final ModelPart leftEar;
    private final ModelPart frontRightLeg;
    private final ModelPart frontLeftLeg;
    private final ModelPart hindRightLeg;
    private final ModelPart hindLeftLeg;

    public BlubModel(ModelPart root) {
        this.root = root.getChild("root");
        this.body = this.root.getChild("body");
        this.rightEar = this.body.getChild("right_ear");
        this.leftEar = this.body.getChild("left_ear");
        this.frontRightLeg = this.root.getChild("front_right_leg");
        this.frontLeftLeg = this.root.getChild("front_left_leg");
        this.hindRightLeg = this.root.getChild("hind_right_leg");
        this.hindLeftLeg = this.root.getChild("hind_left_leg");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();

        PartDefinition root = partDefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 18.5F, 0.0F));

        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0)
                .addBox(-5.0F, -3.5F, -5.0F, 10.0F, 7.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        body.addOrReplaceChild("right_ear", CubeListBuilder.create().texOffs(1, 3)
                .addBox(-1.5F, -5.0F, -0.5F, 3.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.5F, -2.5F, -2.5F));

        body.addOrReplaceChild("left_ear", CubeListBuilder.create().texOffs(31, 3)
                .addBox(-1.5F, -5.0F, -0.5F, 3.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(2.5F, -2.5F, -2.5F));

        root.addOrReplaceChild("front_right_leg", CubeListBuilder.create().texOffs(11, 18)
                .addBox(-1.0F, -1.0F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.5F, 2.5F, -3.5F));

        root.addOrReplaceChild("front_left_leg", CubeListBuilder.create().texOffs(20, 18)
                .addBox(-1.0F, -1.0F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(3.5F, 2.5F, -3.5F));

        root.addOrReplaceChild("hind_right_leg", CubeListBuilder.create().texOffs(11, 25)
                .addBox(-1.0F, -1.0F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.5F, 2.5F, 3.5F));

        root.addOrReplaceChild("hind_left_leg", CubeListBuilder.create().texOffs(20, 25)
                .addBox(-1.0F, -1.0F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(3.5F, 2.5F, 3.5F));

        return LayerDefinition.create(meshDefinition, 64, 32);
    }

    @Override
    public @NotNull ModelPart root() {
        return this.root;
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);

        this.applyHeadRotation(netHeadYaw, headPitch);

        if (entity.isTame() && entity.getState() == BlubState.WANDER) {
            this.animateWalk(BlubModelAnimation.walk_wander, limbSwing, limbSwingAmount, 2.0F, 2.5F);
            this.applyWanderIdleEars(limbSwingAmount);
        } else {
            this.animateWalk(BlubModelAnimation.walk, limbSwing, limbSwingAmount, 2.0F, 2.5F);
        }

        this.animate(entity.sitAnimationState, BlubModelAnimation.sit, ageInTicks);
        this.animate(entity.standAnimationState, BlubModelAnimation.stand, ageInTicks);
        this.animate(entity.digAnimationState, BlubModelAnimation.dig, ageInTicks);
    }

    private void applyHeadRotation(float netHeadYaw, float headPitch) {
        float clampedYaw = Mth.clamp(netHeadYaw, -5.0F, 5.0F) * Mth.DEG_TO_RAD;
        float clampedPitch = Mth.clamp(headPitch, -10.0F, 7.5F) * Mth.DEG_TO_RAD;

        this.body.yRot += clampedYaw;
        this.body.xRot += clampedPitch;
    }

    private void applyWanderIdleEars(float limbSwingAmount) {
        float walkWeight = Math.min(limbSwingAmount * 2.5F, 1.0F);
        float idleWeight = 1.0F - walkWeight;

        this.leftEar.xRot += WANDER_EAR_PITCH * idleWeight;
        this.leftEar.zRot += WANDER_EAR_ROLL * idleWeight;

        this.rightEar.xRot += WANDER_EAR_PITCH * idleWeight;
        this.rightEar.zRot += -WANDER_EAR_ROLL * idleWeight;
    }
}