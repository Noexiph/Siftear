package noexiph.siftear.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import noexiph.siftear.Siftear;
import noexiph.siftear.block.SiftearBlocks;

public class SiftearItemGroups {
    public static final ResourceKey<CreativeModeTab> SIFTEAR_GROUP = ResourceKey.create(
            Registries.CREATIVE_MODE_TAB,
            ResourceLocation.fromNamespaceAndPath(Siftear.MOD_ID, "siftear_group")
    );

    public static final CreativeModeTab SIFTEAR_ITEM_GROUP = FabricItemGroup.builder()
            .icon(() -> new ItemStack(SiftearItems.JELLY_BALL))
            .title(Component.translatable("itemGroup.siftear.siftear_group"))
            .displayItems((context, entries) -> {
                // Materials
                entries.accept(SiftearItems.JELLY_BALL);
                entries.accept(SiftearBlocks.JELLY_BLOCK);
            })
            .build();

    public static void registerToVanillaItemGroups() {
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.BUILDING_BLOCKS).register(content -> {
        });

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS).register(content -> {
        });

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.REDSTONE_BLOCKS).register(content -> {
            content.addAfter(Items.HONEY_BLOCK, SiftearBlocks.JELLY_BLOCK);
        });

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(content -> {
        });

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.COMBAT).register(content -> {
        });

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.FOOD_AND_DRINKS).register(content -> {
        });

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.INGREDIENTS).register(content -> {
            content.addAfter(Items.SLIME_BALL, SiftearItems.JELLY_BALL);
        });

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.SPAWN_EGGS).register(content -> {
            content.addAfter(Items.BLAZE_SPAWN_EGG, SiftearItems.BLUB_SPAWN_EGG);
        });
    }

    public static void initialize() {
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, SIFTEAR_GROUP, SIFTEAR_ITEM_GROUP);
        registerToVanillaItemGroups();

        Siftear.LOGGER.info("Registering item groups for " + Siftear.MOD_ID);
    }
}
