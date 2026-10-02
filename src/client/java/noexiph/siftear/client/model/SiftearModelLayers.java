package noexiph.siftear.client.model;

import noexiph.siftear.Siftear;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;

public class SiftearModelLayers {
        public static final ModelLayerLocation BLUB = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(Siftear.MOD_ID, "blub"), "main"
    );

    public static final ModelLayerLocation NUZZLE = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(Siftear.MOD_ID, "nuzzle"), "main"
    );

    public static final ModelLayerLocation NUZZLE_WOOL = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(Siftear.MOD_ID, "nuzzle_wool"), "main"
    );

    public static final ModelLayerLocation RIFT = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(Siftear.MOD_ID, "rift"), "main"
    );
}
