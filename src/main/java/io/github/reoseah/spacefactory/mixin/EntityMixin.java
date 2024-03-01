package io.github.reoseah.spacefactory.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow
    protected abstract Vec3d adjustMovementForCollisions(Vec3d movement);

    @Shadow
    private boolean onGround;

    @Shadow
    public abstract World getWorld();

    @Inject(at = @At("HEAD"), method = "setOnGround(Z)V", cancellable = true)
    public void setOnGround(boolean onGround, CallbackInfo ci) {
        if (this.getWorld().getDimensionKey().getValue().getNamespace().equals("spacefactory")) {
            if (this.adjustMovementForCollisions(new Vec3d(0, -0.08D, 0)).y > -0.08D) {
                this.onGround = true;
                ci.cancel();
            }
        }
    }
}
