package noexiph.siftear.client.datagen;

import noexiph.siftear.block.SiftearBlocks;
import noexiph.siftear.entity.SiftearEntities;
import noexiph.siftear.item.SiftearItemGroups;
import noexiph.siftear.item.SiftearItems;
import noexiph.siftear.tags.SiftearItemTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;
import noexiph.siftear.world.level.SiftearGameRules;

import java.util.concurrent.CompletableFuture;

public class SiftearEnglishLanguageProvider extends FabricLanguageProvider {

    public SiftearEnglishLanguageProvider(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(dataOutput, "en_us", registryLookup);
    }

    @Override
    public void generateTranslations(HolderLookup.Provider registryLookup, TranslationBuilder builder) {
        generateGameruleTranslations(builder);

        builder.add(SiftearItemGroups.SIFTEAR_GROUP, "Siftear group");

        builder.add(SiftearItems.JELLY_BALL, "Jelly Ball");
        builder.add(SiftearItems.BLUB_SPAWN_EGG, "Blub Spawn Egg");
        builder.add(SiftearItems.NUZZLE_SPAWN_EGG, "Nuzzle Spawn Egg");

        generateBlockTranslations(builder);
        generateEntityTranslations(builder);
        generateItemTagTranslations(builder);
    }

    private static void generateEntityTranslations(TranslationBuilder builder) {
        builder.add(SiftearEntities.BLUB, "Blub");
        builder.add(SiftearEntities.NUZZLE, "Nuzzle");
        builder.add(SiftearEntities.RIFT, "Rift");
    }

    private static void generateGameruleTranslations(TranslationBuilder builder) {
        //builder.add(SiftearGameRules.DO_RIFT_SPAWNING.getDescriptionId(), "Do Rift Spawning");
    }

    private static void generateItemTagTranslations(TranslationBuilder builder) {
        builder.add(SiftearItemTags.BLUB_FOOD, "Blub Food");
    }

    private static void generateBlockTranslations(TranslationBuilder builder) {
        builder.add(SiftearBlocks.JELLY_BLOCK, "Jelly Block");
    }
}