package noexiph.siftear.entity.blub.ai;

import noexiph.siftear.entity.blub.BlubEntity;
import noexiph.siftear.entity.blub.BlubState;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;

public class BlubFollowOwnerGoal extends FollowOwnerGoal {
    private final BlubEntity blub;

    public BlubFollowOwnerGoal(BlubEntity blub, double speedModifier, float startDistance, float stopDistance) {
        super(blub, speedModifier, startDistance, stopDistance);
        this.blub = blub;
    }

    @Override
    public boolean canUse() {
        return this.blub.getState() == BlubState.FOLLOW && super.canUse();
    }

    @Override
    public boolean canContinueToUse() {
        return this.blub.getState() == BlubState.FOLLOW && super.canContinueToUse();
    }
}