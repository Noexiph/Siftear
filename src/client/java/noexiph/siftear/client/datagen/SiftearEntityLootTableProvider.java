package noexiph.siftear.client.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import noexiph.siftear.world.level.storage.loot.SiftearBuiltInLootTables;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class SiftearEntityLootTableProvider extends SimpleFabricLootTableProvider {
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

    public SiftearEntityLootTableProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(output, registryLookup, LootContextParamSets.ENTITY);
    }

    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> biConsumer) {
        SiftearBuiltInLootTables.NUZZLE_BY_DYE.forEach((dyeColor, lootTableKey) -> {
            ItemLike droppedItem = ITEM_BY_DYE.get(dyeColor);
            biConsumer.accept(lootTableKey, createColoredLootTable(droppedItem));
        });
    }

    private LootTable.Builder createColoredLootTable(ItemLike drop) {
        return LootTable.lootTable()
            .withPool(
                LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1.0F))
                    .add(LootItem.lootTableItem(drop))
            );
    }
}