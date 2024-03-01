package io.github.reoseah.spacefactory.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    @Shadow
    public abstract Vec3d applyMovementInput(Vec3d movementInput, float slipperiness);

    @Shadow
    protected boolean jumping;

    @Shadow
    public abstract boolean hasNoDrag();

    @Shadow
    public abstract void updateLimbs(boolean flutter);

    private LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

//    @ModifyConstant(method = "travel(Lnet/minecraft/util/math/Vec3d;)V", constant = @Constant(doubleValue = 0.08D, ordinal = 0))
//    private double modifyGravity(double original) {
//        if (this.getWorld().getDimensionKey().getValue().getNamespace().equals("spacefactory")) {
//            return 0.0001D;
//        }
//        return original;
//    }

    @Inject(at = @At("HEAD"), method = "travel(Lnet/minecraft/util/math/Vec3d;)V", cancellable = true)
    private void travel(Vec3d movementInput, CallbackInfo ci) {
        if (this.getWorld().getDimensionKey().getValue().getNamespace().equals("spacefactory")) {
            if (!this.isLogicalSideForUpdatingMovement()) {
                return;
            }

            BlockPos velocityAffectingPos = this.getVelocityAffectingPos();
            boolean touchesSomething = false;
            boolean onGround = this.getWorld().getBlockState(velocityAffectingPos.down()).isSolidBlock(this.getWorld(), velocityAffectingPos.down());
            for (Direction direction : Direction.values()) {
                if (this.getWorld() //
                        .getBlockState(velocityAffectingPos.offset(direction)) //
                        .isSolidBlock(this.getWorld(), velocityAffectingPos.offset(direction))) {
                    touchesSomething = true;
                    break;
                }
            }

            float blockFriction = (this.getWorld().getBlockState(velocityAffectingPos).getBlock().getSlipperiness() + 1) / 2;

            if (!touchesSomething) {
                movementInput = movementInput.multiply(0.01D);
            }
            Vec3d movement = this.applyMovementInput(movementInput, blockFriction);

            double movementY = movement.y;
            if (touchesSomething && ((Object) this) instanceof PlayerEntity player) {
                if (player.isSneaking()) {
                    movementY -= 0.01D;
                } else if (this.jumping) {
                    movementY += 0.01D;
                }
            }
            if (this.hasNoDrag()) {
                this.setVelocity(movement.x, movementY, movement.z);
            } else {
                float drag = touchesSomething ? blockFriction * (onGround ? 0.9F : 0.999F) : 0.999F;
                this.setVelocity(movement.x * drag, movementY * 0.98D, movement.z * drag);
            }
            this.updateLimbs(false);
            ci.cancel();
        }
    }

}
