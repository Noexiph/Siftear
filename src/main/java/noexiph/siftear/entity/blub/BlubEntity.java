package noexiph.siftear.entity.blub;

import noexiph.siftear.entity.SiftearEntities;
import noexiph.siftear.entity.blub.ai.BlubDigGoal;
import noexiph.siftear.entity.blub.ai.BlubFollowOwnerGoal;
import noexiph.siftear.entity.blub.ai.BlubWanderGoal;
import noexiph.siftear.item.SiftearItems;
import noexiph.siftear.tags.SiftearItemTags;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BlubEntity extends TamableAnimal {
    public static final int ROAMING_RADIUS = 32;
    private static final int SIT_ANIMATION_DURATION_TICKS = 15;
    private static final int MAX_EXPLORED_POSITIONS = 20;
    private static final Codec<List<GlobalPos>> EXPLORED_POSITIONS_CODEC = GlobalPos.CODEC.listOf();

    private static final EntityDataAccessor<Integer> DATA_COLLAR_COLOR =
            SynchedEntityData.defineId(BlubEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_STATE =
            SynchedEntityData.defineId(BlubEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_DIGGING =
            SynchedEntityData.defineId(BlubEntity.class, EntityDataSerializers.BOOLEAN);

    public final AnimationState sitAnimationState = new AnimationState();
    public final AnimationState standAnimationState = new AnimationState();
    public final AnimationState digAnimationState = new AnimationState();

    private final List<GlobalPos> exploredPositions = new ArrayList<>();
    private int digCooldown;

    @Nullable
    private BlockPos wanderAnchor;
    private BlubState previousState = null;

    public BlubEntity(EntityType<? extends TamableAnimal> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createBlubAttributes() {
        return TamableAnimal.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 14.0)
                .add(Attributes.MOVEMENT_SPEED, 0.25)
                .add(Attributes.FOLLOW_RANGE, 10.0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.4));
        this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(3, new BlubFollowOwnerGoal(this, 1.15, 10.0F, 2.0F));
        this.goalSelector.addGoal(4, new BlubDigGoal(this));
        this.goalSelector.addGoal(5, new BreedGoal(this, 1.0));
        this.goalSelector.addGoal(6, new TemptGoal(this, 1.1, stack -> stack.is(SiftearItemTags.BLUB_FOOD), false));
        this.goalSelector.addGoal(7, new BlubWanderGoal(this, 1.0));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_COLLAR_COLOR, DyeColor.RED.getId());
        builder.define(DATA_STATE, BlubState.WANDER.getId());
        builder.define(DATA_DIGGING, false);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putByte("CollarColor", (byte) this.getCollarColor().getId());
        compound.putInt("DigCooldown", this.digCooldown);

        BlubState.CODEC.encodeStart(NbtOps.INSTANCE, this.getState())
                .resultOrPartial()
                .ifPresent(tag -> compound.put("BlubState", tag));

        EXPLORED_POSITIONS_CODEC.encodeStart(NbtOps.INSTANCE, this.exploredPositions)
                .resultOrPartial()
                .ifPresent(tag -> compound.put("ExploredPositions", tag));

        if (this.wanderAnchor != null) {
            compound.putInt("WanderAnchorX", this.wanderAnchor.getX());
            compound.putInt("WanderAnchorY", this.wanderAnchor.getY());
            compound.putInt("WanderAnchorZ", this.wanderAnchor.getZ());
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("CollarColor", Tag.TAG_ANY_NUMERIC)) {
            this.setCollarColor(DyeColor.byId(compound.getByte("CollarColor")));
        }

        this.digCooldown = compound.getInt("DigCooldown");

        if (compound.contains("BlubState")) {
            BlubState.CODEC.parse(NbtOps.INSTANCE, compound.get("BlubState"))
                    .resultOrPartial()
                    .ifPresent(this::setState);
        }

        if (compound.contains("ExploredPositions")) {
            EXPLORED_POSITIONS_CODEC.parse(NbtOps.INSTANCE, compound.get("ExploredPositions"))
                    .resultOrPartial()
                    .ifPresent(positions -> {
                        this.exploredPositions.clear();
                        this.exploredPositions.addAll(positions);
                    });
        }

        if (compound.contains("WanderAnchorX", Tag.TAG_INT)) {
            this.wanderAnchor = new BlockPos(
                    compound.getInt("WanderAnchorX"),
                    compound.getInt("WanderAnchorY"),
                    compound.getInt("WanderAnchorZ")
            );
        }
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (DATA_STATE.equals(key)) {
            BlubState currentState = this.getState();

            if (this.previousState == null) {
                if (currentState == BlubState.STAY) {
                    this.sitAnimationState.start(this.tickCount - SIT_ANIMATION_DURATION_TICKS);
                }
            } else if (currentState == BlubState.STAY) {
                this.standAnimationState.stop();
                this.sitAnimationState.start(this.tickCount);
            } else if (this.previousState == BlubState.STAY) {
                this.sitAnimationState.stop();
                this.standAnimationState.start(this.tickCount);
            } else {
                this.sitAnimationState.stop();
                this.standAnimationState.stop();
            }

            this.previousState = currentState;
        }

        if (DATA_DIGGING.equals(key)) {
            if (this.isDigging()) {
                this.digAnimationState.start(this.tickCount);
            } else {
                this.digAnimationState.stop();
            }
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide() && this.digCooldown > 0) {
            this.digCooldown--;
        }

        if (this.isDigging()) {
            this.emitDiggingParticles();
        }
    }

    private void emitDiggingParticles() {
        BlockPos headBlock = this.getHeadBlock();
        BlockPos groundPos = headBlock.below();
        BlockState blockState = this.level().getBlockState(groundPos);

        if (blockState.getRenderShape() != RenderShape.INVISIBLE) {
            if (this.level().isClientSide()) {
                for (int i = 0; i < 8; i++) {
                    Vec3 center = Vec3.atCenterOf(headBlock).add(0.0, -0.65, 0.0);
                    this.level().addParticle(
                            new BlockParticleOption(ParticleTypes.BLOCK, blockState),
                            center.x(), center.y(), center.z(),
                            0.0, 0.0, 0.0
                    );
                }

                if (this.tickCount % 10 == 0) {
                    this.level().playLocalSound(
                            this.getX(), this.getY(), this.getZ(),
                            blockState.getSoundType().getHitSound(),
                            this.getSoundSource(),
                            0.5F, 0.5F, false
                    );
                }
            }

            if (!this.level().isClientSide() && this.tickCount % 10 == 0) {
                this.level().gameEvent(GameEvent.ENTITY_ACTION, headBlock, GameEvent.Context.of(this));
            }
        }
    }

    public boolean canDig(BlockPos blockPos) {
        GlobalPos globalPos = GlobalPos.of(this.level().dimension(), blockPos);
        return this.level().getBlockState(blockPos).is(BlockTags.SNIFFER_DIGGABLE_BLOCK)
                && !this.exploredPositions.contains(globalPos)
                && this.level().getBlockState(blockPos.above()).getCollisionShape(this.level(), blockPos.above()).isEmpty();
    }

    public void storeExploredPosition(BlockPos blockPos) {
        GlobalPos globalPos = GlobalPos.of(this.level().dimension(), blockPos);
        this.exploredPositions.remove(globalPos);
        this.exploredPositions.addFirst(globalPos);
        while (this.exploredPositions.size() > MAX_EXPLORED_POSITIONS) {
            this.exploredPositions.removeLast();
        }
    }

    public void dropDiggingLoot() {
        if (this.level().isClientSide()) {
            return;
        }

        ServerLevel serverLevel = (ServerLevel) this.level();
        BlockPos headBlock = this.getHeadBlock();
        double x = headBlock.getX() + 0.5;
        double y = headBlock.getY() + 0.1;
        double z = headBlock.getZ() + 0.5;

        ItemEntity jellyEntity = new ItemEntity(serverLevel, x, y, z, new ItemStack(SiftearItems.JELLY_BALL));
        jellyEntity.setDefaultPickUpDelay();
        jellyEntity.setDeltaMovement(this.random.triangle(0.0, 0.06), 0.22, this.random.triangle(0.0, 0.06));
        serverLevel.addFreshEntity(jellyEntity);

        ItemEntity carrotEntity = new ItemEntity(serverLevel, x, y, z, new ItemStack(Items.CARROT));
        carrotEntity.setDefaultPickUpDelay();
        carrotEntity.setDeltaMovement(this.random.triangle(0.0, 0.06), 0.22, this.random.triangle(0.0, 0.06));
        serverLevel.addFreshEntity(carrotEntity);

        this.playSound(SoundEvents.SNIFFER_DROP_SEED, 1.0F, 1.0F);
        this.playSound(SoundEvents.SNIFFER_DIGGING_STOP, 1.0F, 1.0F);
    }

    public BlockPos getHeadBlock() {
        Vec3 headPos = this.position().add(this.getForward().scale(0.6));
        return BlockPos.containing(headPos.x(), this.getY() + 0.2, headPos.z());
    }

    public boolean isDigging() {
        return this.entityData.get(DATA_DIGGING);
    }

    public void setDigging(boolean digging) {
        this.entityData.set(DATA_DIGGING, digging);
    }

    public int getDigCooldown() {
        return this.digCooldown;
    }

    public void setDigCooldown(int digCooldown) {
        this.digCooldown = digCooldown;
    }

    @Override
    public @NotNull InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        if (this.level().isClientSide()) {
            return this.isTame() && this.isOwnedBy(player) || this.isFood(itemStack)
                    ? InteractionResult.CONSUME
                    : InteractionResult.PASS;
        }

        if (this.isTame()) {
            if (this.isOwnedBy(player)) {
                if (itemStack.getItem() instanceof DyeItem dyeItem) {
                    DyeColor dyeColor = dyeItem.getDyeColor();
                    if (dyeColor != this.getCollarColor()) {
                        this.setCollarColor(dyeColor);
                        itemStack.consume(1, player);
                        return InteractionResult.SUCCESS;
                    }
                    return InteractionResult.PASS;
                }

                if (this.isFood(itemStack) && this.getHealth() < this.getMaxHealth()) {
                    this.heal(3.0F);
                    itemStack.consume(1, player);
                    this.playSound(SoundEvents.GENERIC_EAT, 1.0F, 1.0F);
                    return InteractionResult.SUCCESS;
                }

                InteractionResult breedResult = super.mobInteract(player, hand);
                if (breedResult.consumesAction()) {
                    return breedResult;
                }

                this.cycleState();
                return InteractionResult.SUCCESS;
            }
        } else if (this.isFood(itemStack)) {
            itemStack.consume(1, player);
            if (this.random.nextInt(3) == 0) {
                this.tame(player);
                this.navigation.stop();
                this.setTarget(null);
                this.setState(BlubState.FOLLOW);
                this.level().broadcastEntityEvent(this, EntityEvent.TAMING_SUCCEEDED);
            } else {
                this.level().broadcastEntityEvent(this, EntityEvent.TAMING_FAILED);
            }
            return InteractionResult.SUCCESS;
        }

        return super.mobInteract(player, hand);
    }

    private void cycleState() {
        this.setState(this.getState().next());
    }

    public void setState(BlubState state) {
        this.entityData.set(DATA_STATE, state.getId());
        this.setOrderedToSit(state == BlubState.STAY);
        this.updateWanderRestriction(state);
    }

    private void updateWanderRestriction(BlubState state) {
        if (this.isTame() && state == BlubState.WANDER) {
            if (this.wanderAnchor == null) {
                this.wanderAnchor = this.blockPosition();
            }
            this.restrictTo(this.wanderAnchor, ROAMING_RADIUS);
        } else {
            this.clearRestriction();
            this.wanderAnchor = null;
        }
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(SiftearItemTags.BLUB_FOOD);
    }

    @Override
    public boolean isOrderedToSit() {
        return this.getState() == BlubState.STAY;
    }

    @Override
    public boolean isInSittingPose() {
        return this.getState() == BlubState.STAY;
    }

    public BlubState getState() {
        return BlubState.fromId(this.entityData.get(DATA_STATE));
    }

    public DyeColor getCollarColor() {
        return DyeColor.byId(this.entityData.get(DATA_COLLAR_COLOR));
    }

    public void setCollarColor(DyeColor color) {
        this.entityData.set(DATA_COLLAR_COLOR, color.getId());
    }

    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        BlubEntity offspring = SiftearEntities.BLUB.create(level);
        if (offspring != null && otherParent instanceof BlubEntity otherBlub) {
            UUID ownerUuid = this.getOwnerUUID();
            if (ownerUuid != null) {
                offspring.setOwnerUUID(ownerUuid);
                offspring.setTame(true, true);
            }
            offspring.setCollarColor(this.random.nextBoolean() ? this.getCollarColor() : otherBlub.getCollarColor());
        }
        return offspring;
    }
}