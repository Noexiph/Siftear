package noexiph.siftear;

import net.fabricmc.api.ModInitializer;

import net.minecraft.resources.ResourceLocation;

import noexiph.siftear.block.SiftearBlocks;
import noexiph.siftear.entity.SiftearEntities;
import noexiph.siftear.item.SiftearItemGroups;
import noexiph.siftear.item.SiftearItems;
import noexiph.siftear.world.level.SiftearGameRules;
import noexiph.siftear.world.spawner.RiftSpawner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Siftear implements ModInitializer {
	public static final String MOD_ID = "siftear";
	
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Bootstrapping {} mod", MOD_ID);

		//SiftearConfig.load();
		SiftearGameRules.initialize();
		//SiftearComponents.registerDataComponents();

		SiftearEntities.initialize();
		SiftearBlocks.initialize();
		//SiftearBlockEntities.initialize();
		SiftearItems.initialize();
		SiftearItemGroups.initialize();
		//SiftearDispenserBehaviors.initialize();
		//SiftearScreenHandlers.initialize();
		//SiftearRecipes.registerRecipes();

		RiftSpawner.register();
	}

	public static ResourceLocation id(String path) {
		return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
	}
}
