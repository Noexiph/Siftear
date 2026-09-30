package noexiph.siftear.world.spawner;

import noexiph.siftear.entity.SiftearEntities;
import noexiph.siftear.entity.rift.RiftEntity;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;

public final class RiftSpawner {
    private static final int CHECK_INTERVAL_TICKS = 24000;
    private static final int MIN_DISTANCE_FROM_PLAYER = 8;
    private static final int DISTANCE_VARIATION = 8;
    private static final int SPAWN_CHANCE_PERCENT = 35;

    private static final int LATERAL_MIN = -2;
    private static final int LATERAL_MAX = 3;
    private static final int CLEARANCE_HEIGHT = 5;

    private static int tickCounter;

    public static void register() {
        ServerTickEvents.END_WORLD_TICK.register(world -> {
            if (world.dimension() == Level.OVERWORLD) {
                tick(world);
            }
        });
    }

    public static void tick(ServerLevel level) {
        tickCounter++;
        if (tickCounter < CHECK_INTERVAL_TICKS) {
            return;
        }

        tickCounter = 0;

        RandomSource random = level.getRandom();
        if (random.nextInt(100) >= SPAWN_CHANCE_PERCENT) {
            return;
        }

        ServerPlayer player = level.getRandomPlayer();
        if (player == null || player.isSpectator()) {
            return;
        }

        double angle = random.nextDouble() * (Math.PI * 2.0);
        double distance = MIN_DISTANCE_FROM_PLAYER + random.nextInt(DISTANCE_VARIATION);
        int targetX = Mth.floor(player.getX() + Math.cos(angle) * distance);
        int targetZ = Mth.floor(player.getZ() + Math.sin(angle) * distance);

        BlockPos anchorGroundPos = level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, new BlockPos(targetX, 0, targetZ)).below();

        Optional<Direction> validFacing = findBestFacingDirection(level, anchorGroundPos, player.position());
        if (validFacing.isEmpty()) {
            return;
        }

        Direction facing = validFacing.get();
        RiftEntity rift = new RiftEntity(SiftearEntities.RIFT, level);
        rift.setPos(anchorGroundPos.getX() + 0.5, anchorGroundPos.getY() + 1.0, anchorGroundPos.getZ() + 0.5);

        float yaw = facing.toYRot();
        rift.setYRot(yaw);
        rift.setYHeadRot(yaw);

        level.addFreshEntity(rift);
    }

    private static Optional<Direction> findBestFacingDirection(ServerLevel level, BlockPos anchorPos, Vec3 playerPos) {
        Vec3 anchorCenter = Vec3.atCenterOf(anchorPos.above());
        Vec3 toPlayer = playerPos.subtract(anchorCenter).normalize();

        return Arrays.stream(Direction.values())
                .filter(direction -> direction.getAxis().isHorizontal())
                .sorted(Comparator.comparingDouble((Direction dir) -> -new Vec3(dir.getStepX(), 0.0, dir.getStepZ()).dot(toPlayer)))
                .filter(direction -> canRiftFit(level, anchorPos, direction))
                .findFirst();
    }

    private static boolean canRiftFit(ServerLevel level, BlockPos anchorPos, Direction facing) {
        Direction lateralDir = facing.getClockWise();

        for (int dx = LATERAL_MIN; dx <= LATERAL_MAX; dx++) {
            BlockPos groundPos = anchorPos.relative(lateralDir, dx);
            BlockState groundState = level.getBlockState(groundPos);

            if (!groundState.isFaceSturdy(level, groundPos, Direction.UP)) {
                return false;
            }

            for (int dy = 1; dy <= CLEARANCE_HEIGHT; dy++) {
                for (int dz = 0; dz <= 1; dz++) {
                    BlockPos clearancePos = groundPos.above(dy).relative(facing, dz);
                    BlockState clearanceState = level.getBlockState(clearancePos);

                    if (!clearanceState.getCollisionShape(level, clearancePos).isEmpty() || !clearanceState.getFluidState().isEmpty()) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    private RiftSpawner() {}
}