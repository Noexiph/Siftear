package noexiph.siftear.client;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import noexiph.siftear.client.datagen.*;

public class SiftearDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

		//pack.addProvider(SiftearRecipeProvider::new);
		pack.addProvider(SiftearItemTagProvider::new);
		//pack.addProvider(SiftearBlockTagProvider::new);
		//pack.addProvider(SiftearEnchantmentGenerator::new);
		//pack.addProvider(SiftearEnchantmentTagProvider::new);
		pack.addProvider(SiftearModelProvider::new);
		pack.addProvider(SiftearLootTableProvider::new);
		//pack.addProvider(SiftearEntityTypeTagProvider::new);
		pack.addProvider(SiftearEnglishLanguageProvider::new);
		//pack.addProvider(SiftearRegistryDataGenerator::new);
	}
}
