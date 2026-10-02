package noexiph.siftear.entity.rift;

import net.minecraft.util.RandomSource;
import noexiph.siftear.entity.SiftearEntities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class RiftEntity extends Entity {
    private static final int OPEN_DURATION_TICKS = 20;
    private static final int SPAWN_MOB_TICK = 22;
    private static final int DISAPPEAR_DURATION_TICKS = 8;
    private static final int TOTAL_LIFETIME_TICKS = 60;

    private static final EntityDataAccessor<Boolean> DATA_CLOSING =
            SynchedEntityData.defineId(RiftEntity.class, EntityDataSerializers.BOOLEAN);

    public final AnimationState openAnimationState = new AnimationState();
    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState closeAnimationState = new AnimationState();

    private int ageTicks;
    private boolean mobSpawned;

    public RiftEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
        this.noPhysics = true;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_CLOSING, false);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (DATA_CLOSING.equals(key) && this.isClosing()) {
            this.openAnimationState.stop();
            this.idleAnimationState.stop();
            this.closeAnimationState.start(this.tickCount);
        }
    }

    @Override
    public void tick() {
        super.tick();
        this.ageTicks++;

        if (this.level().isClientSide()) {
            if (!this.isClosing()) {
                if (this.ageTicks < OPEN_DURATION_TICKS) {
                    this.openAnimationState.startIfStopped(this.tickCount);
                } else {
                    this.openAnimationState.stop();
                    this.idleAnimationState.startIfStopped(this.tickCount);
                }
            }
            return;
        }

        if (this.ageTicks == 1) {
            this.level().playSound(null, this.blockPosition(), SoundEvents.PORTAL_TRIGGER, SoundSource.NEUTRAL, 1.2F, 1.3F);
        }

        if (!this.mobSpawned && this.ageTicks >= SPAWN_MOB_TICK) {
            this.ejectMob();
            this.mobSpawned = true;
        }

        if (this.ageTicks == TOTAL_LIFETIME_TICKS - DISAPPEAR_DURATION_TICKS) {
            this.entityData.set(DATA_CLOSING, true);
            this.level().playSound(null, this.blockPosition(), SoundEvents.BEACON_DEACTIVATE, SoundSource.NEUTRAL, 1.0F, 1.6F);
        }

        if (this.ageTicks >= TOTAL_LIFETIME_TICKS) {
            this.discard();
        }
    }

    public static Entity getRandomMob(ServerLevel serverLevel) {
        int weight = serverLevel.getRandom().nextInt(100);
        if (weight < 50) return SiftearEntities.NUZZLE.create(serverLevel);
        return SiftearEntities.BLUB.create(serverLevel);
    }

    private void ejectMob() {
        ServerLevel serverLevel = (ServerLevel) this.level();
        Entity mob = getRandomMob(serverLevel);
        if (mob == null) {
            return;
        }

        Vec3 ejectDirection = this.getLookAngle();
        Vec3 spawnOrigin = this.position().add(0.0, 2.0, 0.0).add(ejectDirection.scale(0.6));

        mob.moveTo(spawnOrigin.x(), spawnOrigin.y(), spawnOrigin.z(), this.getYRot(), 0.0F);
        mob.setDeltaMovement(ejectDirection.x() * 0.45, 0.25, ejectDirection.z() * 0.45);
        serverLevel.addFreshEntity(mob);

        serverLevel.playSound(null, this.blockPosition(), SoundEvents.ILLUSIONER_MIRROR_MOVE, SoundSource.NEUTRAL, 1.0F, 1.2F);
    }

    public boolean isClosing() {
        return this.entityData.get(DATA_CLOSING);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        this.ageTicks = compound.getInt("AgeTicks");
        this.mobSpawned = compound.getBoolean("MobSpawned");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        compound.putInt("AgeTicks", this.ageTicks);
        compound.putBoolean("MobSpawned", this.mobSpawned);
    }
}