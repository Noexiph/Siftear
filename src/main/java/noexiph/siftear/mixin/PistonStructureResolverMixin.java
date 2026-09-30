package noexiph.siftear.mixin;

import noexiph.siftear.block.SiftearBlocks;
import net.minecraft.world.level.block.piston.PistonStructureResolver;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PistonStructureResolver.class)
public abstract class PistonStructureResolverMixin {

    @Shadow
    private static boolean isSticky(BlockState blockState) {
        throw new AssertionError();
    }

    @Inject(method = "isSticky", at = @At("RETURN"), cancellable = true)
    private static void siftear$isSticky(BlockState state, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValueZ() && state.is(SiftearBlocks.JELLY_BLOCK)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "canStickToEachOther", at = @At("HEAD"), cancellable = true)
    private static void siftear$canStickToEachOther(BlockState firstState, BlockState secondState, CallbackInfoReturnable<Boolean> cir) {
        boolean firstIsJelly = firstState.is(SiftearBlocks.JELLY_BLOCK);
        boolean secondIsJelly = secondState.is(SiftearBlocks.JELLY_BLOCK);

        if (!firstIsJelly && !secondIsJelly) {
            return;
        }

        if (firstIsJelly && secondIsJelly) {
            cir.setReturnValue(true);
            return;
        }

        if (firstIsJelly) {
            cir.setReturnValue(!isSticky(secondState));
        } else {
            cir.setReturnValue(!isSticky(firstState));
        }
    }
}