package noexiph.siftear.item;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import noexiph.siftear.Siftear;
import noexiph.siftear.entity.SiftearEntities;

public class SiftearItems {
    public static final Item JELLY_BALL = registerItem("jelly_ball",
            new Item(new Item.Properties()));

    public static final Item BLUB_SPAWN_EGG = registerItem("blub_spawn_egg",
            new SpawnEggItem(SiftearEntities.BLUB, 0xFFFFFF, 0xFFFFFF, new Item.Properties()));

    public static Item registerItem(String id, Item item) {
        ResourceLocation itemID = ResourceLocation.fromNamespaceAndPath(Siftear.MOD_ID, id);
        return Registry.register(BuiltInRegistries.ITEM, itemID, item);
    }

    public static void initialize() {
        Siftear.LOGGER.info("Registering Items for " + Siftear.MOD_ID);
    }
}
