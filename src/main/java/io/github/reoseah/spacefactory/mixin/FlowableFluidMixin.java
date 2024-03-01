package io.github.reoseah.spacefactory.mixin;

import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FlowableFluid.class)
public class FlowableFluidMixin {
    @Inject(at = @At("HEAD"), method = "tryFlow", cancellable = true)
    protected void tryFlow(World world, BlockPos fluidPos, FluidState state, CallbackInfo ci) {
        if (world.getDimensionKey().getValue().getNamespace().equals("spacefactory")) {
            ci.cancel();
        }
    }
}
