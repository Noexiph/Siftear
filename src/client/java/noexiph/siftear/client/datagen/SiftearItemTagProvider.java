package noexiph.siftear.client.datagen;

import noexiph.siftear.tags.SiftearItemTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;

public class SiftearItemTagProvider extends FabricTagProvider.ItemTagProvider {
    public SiftearItemTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider wrapperLookup) {
        getOrCreateTagBuilder(SiftearItemTags.BLUB_FOOD)
                .add(Items.SLIME_BALL);
    }
}