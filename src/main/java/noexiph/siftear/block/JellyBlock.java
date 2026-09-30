package noexiph.siftear.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class JellyBlock extends HalfTransparentBlock {
    public static final MapCodec<JellyBlock> CODEC = simpleCodec(JellyBlock::new);

    @Override
    public @NotNull MapCodec<JellyBlock> codec() {
        return CODEC;
    }

    public JellyBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
        if (entity.isSuppressingBounce()) {
            super.fallOn(level, state, pos, entity, fallDistance);
        } else {
            entity.causeFallDamage(fallDistance, 0.0F, level.damageSources().fall());
        }
    }

    @Override
    public void updateEntityAfterFallOn(BlockGetter level, Entity entity) {
        if (entity.isSuppressingBounce()) {
            super.updateEntityAfterFallOn(level, entity);
        } else {
            this.bounceEntity(entity);
        }
    }

    private void bounceEntity(Entity entity) {
        Vec3 deltaMovement = entity.getDeltaMovement();
        if (deltaMovement.y < 0.0) {
            double restitution = entity instanceof LivingEntity ? 0.6 : 0.4;
            entity.setDeltaMovement(deltaMovement.x, -deltaMovement.y * restitution, deltaMovement.z);
        }
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        double verticalSpeed = Math.abs(entity.getDeltaMovement().y);
        if (verticalSpeed < 0.1 && !entity.isSteppingCarefully()) {
            double speedModifier = 0.8;
            entity.setDeltaMovement(entity.getDeltaMovement().multiply(speedModifier, 1.0, speedModifier));
        }
        super.stepOn(level, pos, state, entity);
    }
}