package noexiph.siftear.entity.blub.ai;

import noexiph.siftear.entity.blub.BlubEntity;
import noexiph.siftear.entity.blub.BlubState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.IntStream;

public class BlubDigGoal extends Goal {
    private static final int DIG_TOTAL_TICKS = 25;
    private static final int ITEM_DROP_TICK = 15;
    private static final int DIG_COOLDOWN_TICKS = 6000;
    private static final double NAVIGATION_SPEED = 1.15;
    private static final double ARRIVAL_DISTANCE_SQR = 2.25;

    private final BlubEntity blub;
    private BlockPos digTarget;
    private int digTicks;
    private boolean isDigging;
    private boolean itemSpawned;

    public BlubDigGoal(BlubEntity blub) {
        this.blub = blub;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
    }

    @Override
    public boolean canUse() {
        if (!this.blub.isTame() || this.blub.isBaby() || this.blub.isInWater() || !this.blub.onGround() || this.blub.isPassenger()) {
            return false;
        }
        if (this.blub.isOrderedToSit() || this.blub.getState() == BlubState.STAY) {
            return false;
        }
        if (this.blub.getDigCooldown() > 0 || this.blub.getRandom().nextInt(100) != 0) {
            return false;
        }

        Optional<BlockPos> target = this.findDigTarget();
        if (target.isEmpty()) {
            return false;
        }

        this.digTarget = target.get();
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        if (!this.blub.isTame() || this.blub.isBaby() || this.blub.isInWater() || this.blub.isPassenger()) {
            return false;
        }
        if (this.blub.isOrderedToSit() || this.blub.getState() == BlubState.STAY) {
            return false;
        }
        if (this.isDigging) {
            return this.digTicks < DIG_TOTAL_TICKS;
        }
        return this.digTarget != null && this.blub.getNavigation().isInProgress();
    }

    @Override
    public void start() {
        this.digTicks = 0;
        this.isDigging = false;
        this.itemSpawned = false;

        if (this.digTarget != null) {
            this.blub.getNavigation().moveTo(
                    this.digTarget.getX() + 0.5,
                    this.digTarget.getY() + 1.0,
                    this.digTarget.getZ() + 0.5,
                    NAVIGATION_SPEED
            );
        }
    }

    @Override
    public void stop() {
        this.blub.setDigging(false);
        this.isDigging = false;
        this.digTarget = null;
        this.digTicks = 0;
        this.itemSpawned = false;
    }

    @Override
    public void tick() {
        if (this.digTarget == null) {
            return;
        }

        Vec3 targetCenter = Vec3.atCenterOf(this.digTarget.above());
        double distanceSq = this.blub.distanceToSqr(targetCenter);

        if (!this.isDigging) {
            if (distanceSq <= ARRIVAL_DISTANCE_SQR) {
                this.blub.getNavigation().stop();
                this.blub.getLookControl().setLookAt(targetCenter);
                this.isDigging = true;
                this.blub.setDigging(true);
            } else if (!this.blub.getNavigation().isInProgress()) {
                this.blub.getNavigation().moveTo(
                        targetCenter.x(),
                        targetCenter.y(),
                        targetCenter.z(),
                        NAVIGATION_SPEED
                );
            }
        } else {
            this.blub.getNavigation().stop();
            this.digTicks++;

            if (!this.itemSpawned && this.digTicks >= ITEM_DROP_TICK) {
                this.blub.dropDiggingLoot();
                this.itemSpawned = true;
            }

            if (this.digTicks >= DIG_TOTAL_TICKS) {
                this.blub.storeExploredPosition(this.digTarget);
                this.blub.setDigCooldown(DIG_COOLDOWN_TICKS);
                this.blub.setDigging(false);
                this.stop();
            }
        }
    }

    private Optional<BlockPos> findDigTarget() {
        BlockPos currentGround = this.blub.blockPosition().below();
        if (this.blub.canDig(currentGround)) {
            return Optional.of(currentGround);
        }

        return IntStream.range(0, 5)
                .mapToObj(i -> LandRandomPos.getPos(this.blub, 10 + 2 * i, 3))
                .filter(Objects::nonNull)
                .map(BlockPos::containing)
                .filter(pos -> this.blub.level().getWorldBorder().isWithinBounds(pos))
                .map(BlockPos::below)
                .filter(this.blub::canDig)
                .findFirst();
    }
}