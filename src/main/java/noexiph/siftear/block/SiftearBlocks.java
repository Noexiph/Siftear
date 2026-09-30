package noexiph.siftear.block;

import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import noexiph.siftear.Siftear;

public class SiftearBlocks implements ModInitializer {
    public static final Block JELLY_BLOCK = register(
            new JellyBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SLIME_BLOCK)
                    .noOcclusion()),
            "jelly_block",
            true
    );

    @Override
    public void onInitialize() {
        SiftearBlocks.initialize();
    }

    public static void initialize() {
        Siftear.LOGGER.info("Registering Blocks for " + Siftear.MOD_ID);
    }

    public static Block register(Block block, String name, boolean shouldRegisterItem) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(Siftear.MOD_ID, name);

        if (shouldRegisterItem) {
            BlockItem blockItem = new BlockItem(block, new Item.Properties());
            Registry.register(BuiltInRegistries.ITEM, id, blockItem);
        }

        return Registry.register(BuiltInRegistries.BLOCK, id, block);
    }
}
