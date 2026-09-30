package noexiph.siftear.entity.blub.ai;

import noexiph.siftear.entity.blub.BlubEntity;
import noexiph.siftear.entity.blub.BlubState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class BlubWanderGoal extends WaterAvoidingRandomStrollGoal {
    private static final int OFFLINE_WANDER_RADIUS = 8;
    private final BlubEntity blub;
    @Nullable
    private BlockPos offlineAnchor;

    public BlubWanderGoal(BlubEntity blub, double speedModifier) {
        super(blub, speedModifier);
        this.blub = blub;
    }

    @Override
    public boolean canUse() {
        if (!this.blub.isTame()) {
            return super.canUse();
        }

        if (this.blub.isOrderedToSit() || this.blub.getState() == BlubState.STAY) {
            return false;
        }

        if (this.blub.getState() == BlubState.WANDER) {
            this.offlineAnchor = null;
            return super.canUse();
        }

        if (this.blub.getState() == BlubState.FOLLOW && this.blub.getOwner() == null) {
            if (this.offlineAnchor == null) {
                this.offlineAnchor = this.blub.blockPosition();
            }
            return super.canUse();
        }

        this.offlineAnchor = null;
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        if (!this.blub.isTame()) {
            return super.canContinueToUse();
        }

        if (this.blub.isOrderedToSit() || this.blub.getState() == BlubState.STAY) {
            return false;
        }

        if (this.blub.getState() == BlubState.WANDER) {
            return super.canContinueToUse();
        }

        return this.blub.getState() == BlubState.FOLLOW && this.blub.getOwner() == null && super.canContinueToUse();
    }

    @Nullable
    @Override
    protected Vec3 getPosition() {
        if (this.blub.getState() == BlubState.FOLLOW && this.offlineAnchor != null) {
            if (this.blub.blockPosition().distSqr(this.offlineAnchor) > (OFFLINE_WANDER_RADIUS * OFFLINE_WANDER_RADIUS)) {
                return LandRandomPos.getPosTowards(this.blub, OFFLINE_WANDER_RADIUS, 4, Vec3.atBottomCenterOf(this.offlineAnchor));
            }
            return LandRandomPos.getPos(this.blub, OFFLINE_WANDER_RADIUS / 2, 3);
        }

        if (this.blub.hasRestriction() && !this.blub.isWithinRestriction()) {
            BlockPos center = this.blub.getRestrictCenter();
            return LandRandomPos.getPosTowards(this.blub, 16, 7, Vec3.atBottomCenterOf(center));
        }

        return super.getPosition();
    }
}