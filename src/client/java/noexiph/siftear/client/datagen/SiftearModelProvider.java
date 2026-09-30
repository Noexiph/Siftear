package noexiph.siftear.client.datagen;

import net.minecraft.data.models.model.ModelLocationUtils;
import noexiph.siftear.block.SiftearBlocks;
import noexiph.siftear.item.SiftearItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.resources.ResourceLocation;

public class SiftearModelProvider extends FabricModelProvider {

    public SiftearModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
        ResourceLocation jellyBlockModelId = ModelLocationUtils.getModelLocation(SiftearBlocks.JELLY_BLOCK);
        blockStateModelGenerator.blockStateOutput.accept(
                BlockModelGenerators.createSimpleBlock(SiftearBlocks.JELLY_BLOCK, jellyBlockModelId)
        );
        blockStateModelGenerator.delegateItemModel(SiftearBlocks.JELLY_BLOCK, jellyBlockModelId);
    }

    @Override
    public void generateItemModels(ItemModelGenerators itemModelGenerator) {
        itemModelGenerator.generateFlatItem(SiftearItems.JELLY_BALL, ModelTemplates.FLAT_HANDHELD_ITEM);
        generateSpawnEggTranslations(itemModelGenerator);
    }

    private static void generateSpawnEggTranslations(ItemModelGenerators itemModelGenerator) {
        itemModelGenerator.generateFlatItem(SiftearItems.BLUB_SPAWN_EGG, ModelTemplates.FLAT_ITEM);
    }
}