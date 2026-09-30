package noexiph.siftear.client.datagen;

import noexiph.siftear.block.SiftearBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.core.HolderLookup;
import java.util.concurrent.CompletableFuture;

public class SiftearLootTableProvider extends FabricBlockLootTableProvider {
    public SiftearLootTableProvider(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generate() {
        dropSelf(SiftearBlocks.JELLY_BLOCK);
    }
}