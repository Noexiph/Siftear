package noexiph.siftear.entity.nuzzle;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Shearable;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.EatBlockGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.loot.LootTable;
import noexiph.siftear.entity.SiftearEntities;
import noexiph.siftear.world.level.storage.loot.SiftearBuiltInLootTables;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class NuzzleEntity extends Animal implements Shearable {
    private static final EntityDataAccessor<Byte> DATA_COLOR_ID = SynchedEntityData.defineId(NuzzleEntity.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Boolean> DATA_SHEARED = SynchedEntityData.defineId(NuzzleEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_PANICKING = SynchedEntityData.defineId(NuzzleEntity.class, EntityDataSerializers.BOOLEAN);

    private static final Map<DyeColor, ItemLike> ITEM_BY_DYE = Map.ofEntries(
            Map.entry(DyeColor.WHITE, Blocks.WHITE_WOOL),
            Map.entry(DyeColor.ORANGE, Blocks.ORANGE_WOOL),
            Map.entry(DyeColor.MAGENTA, Blocks.MAGENTA_WOOL),
            Map.entry(DyeColor.LIGHT_BLUE, Blocks.LIGHT_BLUE_WOOL),
            Map.entry(DyeColor.YELLOW, Blocks.YELLOW_WOOL),
            Map.entry(DyeColor.LIME, Blocks.LIME_WOOL),
            Map.entry(DyeColor.PINK, Blocks.PINK_WOOL),
            Map.entry(DyeColor.GRAY, Blocks.GRAY_WOOL),
            Map.entry(DyeColor.LIGHT_GRAY, Blocks.LIGHT_GRAY_WOOL),
            Map.entry(DyeColor.CYAN, Blocks.CYAN_WOOL),
            Map.entry(DyeColor.PURPLE, Blocks.PURPLE_WOOL),
            Map.entry(DyeColor.BLUE, Blocks.BLUE_WOOL),
            Map.entry(DyeColor.BROWN, Blocks.BROWN_WOOL),
            Map.entry(DyeColor.GREEN, Blocks.GREEN_WOOL),
            Map.entry(DyeColor.RED, Blocks.RED_WOOL),
            Map.entry(DyeColor.BLACK, Blocks.BLACK_WOOL)
    );

    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState eatAnimationState = new AnimationState();

    private int eatAnimationTick;
    private EatBlockGoal eatBlockGoal;
    private PanicGoal panicGoal;

    public NuzzleEntity(EntityType<? extends NuzzleEntity> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.@NotNull Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 8.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.23D);
    }

    @Override
    protected void registerGoals() {
        this.eatBlockGoal = new EatBlockGoal(this);
        this.panicGoal = new PanicGoal(this, 1.25D);
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, this.panicGoal);
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.1D, stack -> stack.is(ItemTags.SHEEP_FOOD), false));
        this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.1D));
        this.goalSelector.addGoal(5, this.eatBlockGoal);
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_COLOR_ID, (byte) DyeColor.RED.getId());
        builder.define(DATA_SHEARED, false);
        builder.define(DATA_PANICKING, false);
    }

    @Override
    public @NotNull ResourceKey<LootTable> getDefaultLootTable() {
        if (this.isSheared()) {
            return this.getType().getDefaultLootTable();
        }
        return SiftearBuiltInLootTables.NUZZLE_BY_DYE.getOrDefault(this.getColor(), SiftearBuiltInLootTables.NUZZLE_RED);
    }

    @Override
    public boolean isFood(ItemStack itemStack) {
        return itemStack.is(ItemTags.SHEEP_FOOD);
    }

    @Override
    public void handleEntityEvent(byte status) {
        if (status == 10) {
            this.eatAnimationTick = 40;
            this.eatAnimationState.start(this.tickCount);
        } else {
            super.handleEntityEvent(status);
        }
    }

    @Override
    protected void customServerAiStep() {
        this.eatAnimationTick = this.eatBlockGoal.getEatAnimationTick();
        super.customServerAiStep();
        this.entityData.set(DATA_PANICKING, this.panicGoal.isRunning());
    }

    @Override
    public void aiStep() {
        if (this.level().isClientSide()) {
            this.eatAnimationTick = Math.max(0, this.eatAnimationTick - 1);
            if (this.eatAnimationTick == 0) {
                this.idleAnimationState.startIfStopped(this.tickCount);
            }
        }
        super.aiStep();
    }

    @Override
    public @NotNull InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (itemStack.is(Items.SHEARS)) {
            if (!this.level().isClientSide() && this.readyForShearing()) {
                this.shear(SoundSource.PLAYERS);
                this.gameEvent(GameEvent.SHEAR, player);
                itemStack.hurtAndBreak(1, player, getSlotForHand(hand));
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.CONSUME;
        }

        if (itemStack.getItem() instanceof DyeItem dyeItem) {
            if (this.isAlive() && !this.isSheared() && this.getColor() != dyeItem.getDyeColor()) {
                this.level().playSound(player, this, SoundEvents.DYE_USE, SoundSource.PLAYERS, 1.0F, 1.0F);
                if (!this.level().isClientSide()) {
                    this.setColor(dyeItem.getDyeColor());
                    itemStack.consume(1, player);
                }
                return InteractionResult.sidedSuccess(this.level().isClientSide());
            }
            return InteractionResult.PASS;
        }

        return super.mobInteract(player, hand);
    }

    @Override
    public void shear(SoundSource soundSource) {
        this.level().playSound(null, this, SoundEvents.SHEEP_SHEAR, soundSource, 1.0F, 1.0F);
        this.setSheared(true);
        int count = 1 + this.random.nextInt(3);

        for (int i = 0; i < count; i++) {
            ItemEntity itemEntity = this.spawnAtLocation(ITEM_BY_DYE.get(this.getColor()), 1);
            if (itemEntity != null) {
                itemEntity.setDeltaMovement(
                        itemEntity.getDeltaMovement().add(
                                (this.random.nextFloat() - this.random.nextFloat()) * 0.1F,
                                this.random.nextFloat() * 0.05F,
                                (this.random.nextFloat() - this.random.nextFloat()) * 0.1F
                        )
                );
            }
        }
    }

    @Override
    public boolean readyForShearing() {
        return this.isAlive() && !this.isSheared() && !this.isBaby();
    }

    public boolean isSheared() {
        return this.entityData.get(DATA_SHEARED);
    }

    public void setSheared(boolean sheared) {
        this.entityData.set(DATA_SHEARED, sheared);
    }

    public boolean isPanicking() {
        return this.entityData.get(DATA_PANICKING);
    }

    public DyeColor getColor() {
        return DyeColor.byId(this.entityData.get(DATA_COLOR_ID));
    }

    public void setColor(DyeColor dyeColor) {
        this.entityData.set(DATA_COLOR_ID, (byte) dyeColor.getId());
    }

    @Override
    public void ate() {
        super.ate();
        this.setSheared(false);
        if (this.isBaby()) {
            this.ageUp(60);
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("Sheared", this.isSheared());
        tag.putByte("Color", (byte) this.getColor().getId());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setSheared(tag.getBoolean("Sheared"));
        this.setColor(DyeColor.byId(tag.getByte("Color")));
    }

    @Override
    public @NotNull SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        this.setColor(getRandomNuzzleColor(level.getRandom()));
        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }

    public static DyeColor getRandomNuzzleColor(RandomSource randomSource) {
        int weight = randomSource.nextInt(100);
        if (weight < 5) return DyeColor.PINK;
        if (weight < 10) return DyeColor.YELLOW;
        if (weight < 15) return DyeColor.CYAN;
        if (weight < 18) return DyeColor.BLACK;
        return randomSource.nextInt(500) == 0 ? DyeColor.WHITE : DyeColor.RED;
    }

    @Nullable
    @Override
    public NuzzleEntity getBreedOffspring(ServerLevel serverLevel, AgeableMob parent) {
        NuzzleEntity offspring = SiftearEntities.NUZZLE.create(serverLevel);
        if (offspring != null) {
            offspring.setColor(this.getOffspringColor(this, (NuzzleEntity) parent));
        }
        return offspring;
    }

    private DyeColor getOffspringColor(Animal firstParent, Animal secondParent) {
        DyeColor firstColor = ((NuzzleEntity) firstParent).getColor();
        DyeColor secondColor = ((NuzzleEntity) secondParent).getColor();
        CraftingInput craftingInput = CraftingInput.of(2, 1, List.of(
                new ItemStack(DyeItem.byColor(firstColor)),
                new ItemStack(DyeItem.byColor(secondColor))
        ));

        return this.level().getRecipeManager().getRecipeFor(RecipeType.CRAFTING, craftingInput, this.level())
                .map(recipe -> recipe.value().assemble(craftingInput, this.level().registryAccess()))
                .map(ItemStack::getItem)
                .filter(DyeItem.class::isInstance)
                .map(DyeItem.class::cast)
                .map(DyeItem::getDyeColor)
                .orElseGet(() -> this.level().random.nextBoolean() ? firstColor : secondColor);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.SHEEP_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.SHEEP_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.SHEEP_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.SHEEP_STEP, 0.15F, 1.0F);
    }
}