package noexiph.siftear.entity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import noexiph.siftear.Siftear;
import noexiph.siftear.entity.blub.BlubEntity;
import noexiph.siftear.entity.nuzzle.NuzzleEntity;
import noexiph.siftear.entity.rift.RiftEntity;

public class SiftearEntities {
    public static final EntityType<BlubEntity> BLUB = register(
            "blub",
            EntityType.Builder.of(BlubEntity::new, MobCategory.CREATURE)
                    .sized(0.7F, 0.55F)
                    .clientTrackingRange(10)
    );

    public static final EntityType<NuzzleEntity> NUZZLE = register(
            "nuzzle",
            EntityType.Builder.of(NuzzleEntity::new, MobCategory.CREATURE)
                    .sized(0.9F, 1.1F)
                    .clientTrackingRange(10)
    );

    public static final EntityType<RiftEntity> RIFT = register(
            "rift",
            EntityType.Builder.of(RiftEntity::new, MobCategory.MISC)
                    .sized(1.0F, 1.0F)
                    .clientTrackingRange(10)
                    .updateInterval(1)
    );

    private static <T extends Entity> EntityType<T> register(String name, EntityType.Builder<T> builder) {
        return Registry.register(
                BuiltInRegistries.ENTITY_TYPE,
                ResourceLocation.fromNamespaceAndPath(Siftear.MOD_ID, name),
                builder.build()
        );
    }

    public static void initialize() {
        registerAttributes();
    }

    private static void registerAttributes() {
        FabricDefaultAttributeRegistry.register(BLUB, BlubEntity.createBlubAttributes());
        FabricDefaultAttributeRegistry.register(NUZZLE, NuzzleEntity.createAttributes());
    }
}
