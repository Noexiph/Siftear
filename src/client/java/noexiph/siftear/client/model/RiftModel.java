package noexiph.siftear.client.model;

import noexiph.siftear.entity.rift.RiftEntity;
import noexiph.siftear.client.renderer.entity.animation.RiftModelAnimation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public class RiftModel<T extends RiftEntity> extends HierarchicalModel<T> {
    private final ModelPart root;
    private final ModelPart topLeftCube;
    private final ModelPart topRightCube;
    private final ModelPart bottomLeftCube;
    private final ModelPart body;
    private final ModelPart portal;

    public RiftModel(ModelPart root) {
        this.root = root.getChild("root");
        this.topLeftCube = this.root.getChild("top_left_cube");
        this.topRightCube = this.root.getChild("top_right_cube");
        this.bottomLeftCube = this.root.getChild("bottom_left_cube");
        this.body = this.root.getChild("body");
        this.portal = this.body.getChild("portal");
    }

    public static LayerDefinition getTexturedModelData() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(-1.75F, -22.0F, -1.25F));

        root.addOrReplaceChild("top_left_cube", CubeListBuilder.create().texOffs(0, 0)
                .addBox(13.0F, -4.0F, -1.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-27.25F, -23.0F, 1.25F));

        root.addOrReplaceChild("top_right_cube", CubeListBuilder.create().texOffs(0, 0)
                .addBox(-2.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(30.75F, -8.0F, 2.25F));

        root.addOrReplaceChild("bottom_left_cube", CubeListBuilder.create().texOffs(0, 0)
                .addBox(-2.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-28.25F, 18.0F, 2.25F));

        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(130, 64)
                .addBox(1.0F, -17.0F, -1.0F, 32.0F, 16.0F, 16.0F, new CubeDeformation(0.0F))
                .texOffs(0, 33).addBox(-47.0F, -32.0F, -1.0F, 48.0F, 48.0F, 16.0F, new CubeDeformation(0.0F))
                .texOffs(0, 98).addBox(-63.0F, -17.0F, -1.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.0F))
                .texOffs(73, 0).addBox(-31.0F, -48.0F, -1.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(24.75F, 13.0F, -5.75F));

        body.addOrReplaceChild("portal", CubeListBuilder.create().texOffs(48, 131)
                .addBox(1.0F, -16.0F, 7.0F, 16.0F, 16.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(0, 162).addBox(-31.0F, 15.0F, 7.0F, 16.0F, 16.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(32, 147).addBox(-15.0F, 0.0F, 7.0F, 48.0F, 48.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(128, 163).addBox(33.0F, 15.0F, 7.0F, 32.0F, 16.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(-32.0F, -32.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 256, 256);
    }

    @Override
    public @NotNull ModelPart root() {
        return this.root;
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        this.animate(entity.openAnimationState, RiftModelAnimation.appear, ageInTicks);
        this.animate(entity.idleAnimationState, RiftModelAnimation.idle, ageInTicks);
        this.animate(entity.closeAnimationState, RiftModelAnimation.disappear, ageInTicks);
    }
}