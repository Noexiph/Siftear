package noexiph.siftear.client.renderer;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public abstract class SiftearRenderTypes extends RenderType {
    public SiftearRenderTypes(String name, VertexFormat format, VertexFormat.Mode mode, int bufferSize, boolean affectsCrumbling, boolean sortOnUpload, Runnable setupState, Runnable clearState) {
        super(name, format, mode, bufferSize, affectsCrumbling, sortOnUpload, setupState, clearState);
    }

    public static final Function<ResourceLocation, RenderType> RIFT_EMISSIVE = Util.memoize(texture -> {
        CompositeState compositeState = CompositeState.builder()
                .setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_EMISSIVE_SHADER)
                .setTextureState(new TextureStateShard(texture, false, false))
                .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                .setCullState(NO_CULL)
                .setWriteMaskState(COLOR_DEPTH_WRITE)
                .setOverlayState(OVERLAY)
                .createCompositeState(true);

        return create(
                "rift_emissive",
                DefaultVertexFormat.NEW_ENTITY,
                VertexFormat.Mode.QUADS,
                1536,
                true,
                true,
                compositeState
        );
    });
}