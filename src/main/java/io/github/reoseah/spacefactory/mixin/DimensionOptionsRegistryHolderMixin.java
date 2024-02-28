package io.github.reoseah.spacefactory.mixin;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Lifecycle;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionOptionsRegistryHolder;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

@Mixin(DimensionOptionsRegistryHolder.class)
public class DimensionOptionsRegistryHolderMixin {
    @Shadow
    @Final
    @Mutable
    private static Set<RegistryKey<DimensionOptions>> VANILLA_KEYS;
    @Shadow
    @Final
    @Mutable
    private static int VANILLA_KEY_COUNT;

    static {
        VANILLA_KEYS = new ImmutableSet.Builder<RegistryKey<DimensionOptions>>()
                .addAll(VANILLA_KEYS)
                .add(RegistryKey.of(RegistryKeys.DIMENSION, new Identifier("spacefactory:geostationary_orbit")))
                .build();
        VANILLA_KEY_COUNT = VANILLA_KEYS.size();
    }

    @Inject(at = @At("RETURN"), method = "getLifecycle", cancellable = true)
    private static void getLifecycle(RegistryKey<DimensionOptions> key, DimensionOptions stem, CallbackInfoReturnable<Lifecycle> cir) {
        if (key.getValue().getNamespace().equals("spacefactory")
                && key.getValue().getPath().equals("geostationary_orbit")) {
            cir.setReturnValue(Lifecycle.stable());
        }
    }

}
