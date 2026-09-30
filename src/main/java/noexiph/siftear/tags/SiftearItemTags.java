package noexiph.siftear.tags;

import noexiph.siftear.Siftear;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class SiftearItemTags {
    public static final TagKey<Item> BLUB_FOOD = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Siftear.MOD_ID, "blub_food"));
}
