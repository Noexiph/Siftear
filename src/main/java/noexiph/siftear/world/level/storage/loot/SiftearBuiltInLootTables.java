package noexiph.siftear.world.level.storage.loot;

import com.google.common.collect.Maps;
import net.minecraft.Util;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.storage.loot.LootTable;
import noexiph.siftear.Siftear;

import java.util.Map;

public class SiftearBuiltInLootTables {
    public static final ResourceKey<LootTable> NUZZLE_WHITE = register("entities/nuzzle/white");
    public static final ResourceKey<LootTable> NUZZLE_ORANGE = register("entities/nuzzle/orange");
    public static final ResourceKey<LootTable> NUZZLE_MAGENTA = register("entities/nuzzle/magenta");
    public static final ResourceKey<LootTable> NUZZLE_LIGHT_BLUE = register("entities/nuzzle/light_blue");
    public static final ResourceKey<LootTable> NUZZLE_YELLOW = register("entities/nuzzle/yellow");
    public static final ResourceKey<LootTable> NUZZLE_LIME = register("entities/nuzzle/lime");
    public static final ResourceKey<LootTable> NUZZLE_PINK = register("entities/nuzzle/pink");
    public static final ResourceKey<LootTable> NUZZLE_GRAY = register("entities/nuzzle/gray");
    public static final ResourceKey<LootTable> NUZZLE_LIGHT_GRAY = register("entities/nuzzle/light_gray");
    public static final ResourceKey<LootTable> NUZZLE_CYAN = register("entities/nuzzle/cyan");
    public static final ResourceKey<LootTable> NUZZLE_PURPLE = register("entities/nuzzle/purple");
    public static final ResourceKey<LootTable> NUZZLE_BLUE = register("entities/nuzzle/blue");
    public static final ResourceKey<LootTable> NUZZLE_BROWN = register("entities/nuzzle/brown");
    public static final ResourceKey<LootTable> NUZZLE_GREEN = register("entities/nuzzle/green");
    public static final ResourceKey<LootTable> NUZZLE_RED = register("entities/nuzzle/red");
    public static final ResourceKey<LootTable> NUZZLE_BLACK = register("entities/nuzzle/black");

    public static final Map<DyeColor, ResourceKey<LootTable>> NUZZLE_BY_DYE = Util.make(Maps.newEnumMap(DyeColor.class), map -> {
        map.put(DyeColor.WHITE, NUZZLE_WHITE);
        map.put(DyeColor.ORANGE, NUZZLE_ORANGE);
        map.put(DyeColor.MAGENTA, NUZZLE_MAGENTA);
        map.put(DyeColor.LIGHT_BLUE, NUZZLE_LIGHT_BLUE);
        map.put(DyeColor.YELLOW, NUZZLE_YELLOW);
        map.put(DyeColor.LIME, NUZZLE_LIME);
        map.put(DyeColor.PINK, NUZZLE_PINK);
        map.put(DyeColor.GRAY, NUZZLE_GRAY);
        map.put(DyeColor.LIGHT_GRAY, NUZZLE_LIGHT_GRAY);
        map.put(DyeColor.CYAN, NUZZLE_CYAN);
        map.put(DyeColor.PURPLE, NUZZLE_PURPLE);
        map.put(DyeColor.BLUE, NUZZLE_BLUE);
        map.put(DyeColor.BROWN, NUZZLE_BROWN);
        map.put(DyeColor.GREEN, NUZZLE_GREEN);
        map.put(DyeColor.RED, NUZZLE_RED);
        map.put(DyeColor.BLACK, NUZZLE_BLACK);
    });

    private static ResourceKey<LootTable> register(String path) {
        return ResourceKey.create(Registries.LOOT_TABLE, Siftear.id(path));
    }
}