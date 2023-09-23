package io.github.reoseah.spacefactory.item;

import com.google.common.collect.Lists;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class ChargeLauncherItem extends RangedWeaponItem {
    private static final String CHARGED_KEY = "Charged";
    private static final String CHARGED_PROJECTILES_KEY = "ChargedProjectiles";
    private static final int DEFAULT_PULL_TIME = 25;
    public static final int RANGE = 8;
    private boolean charged = false;
    private boolean loaded = false;
    private static final float DEFAULT_SPEED = 1;

    public ChargeLauncherItem(Settings settings) {
        super(settings);
    }

    @Override
    public Predicate<ItemStack> getHeldProjectiles() {
        return stack -> stack.isOf(Items.FIRE_CHARGE);
    }

    @Override
    public Predicate<ItemStack> getProjectiles() {
        return stack -> stack.isOf(Items.FIRE_CHARGE);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (isCharged(stack)) {
            shootAll(world, user, hand, stack, getSpeed(stack), 1.0f);
            setCharged(stack, false);
            return TypedActionResult.consume(stack);
        }
        if (!user.getProjectileType(stack).isEmpty()) {
            if (!isCharged(stack)) {
                this.charged = false;
                this.loaded = false;
                user.setCurrentHand(hand);
            }
            return TypedActionResult.consume(stack);
        }
        return TypedActionResult.fail(stack);
    }

    private static float getSpeed(ItemStack stack) {
//        if (hasProjectile(stack, Items.FIRE_CHARGE)) {
//            return FIREWORK_ROCKET_SPEED;
//        }
        return DEFAULT_SPEED;
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        int i = this.getMaxUseTime(stack) - remainingUseTicks;
        float f = getPullProgress(i, stack);
        if (f >= 1.0f && !isCharged(stack) && loadProjectiles(user, stack)) {
            setCharged(stack, true);
            SoundCategory soundCategory = user instanceof PlayerEntity ? SoundCategory.PLAYERS : SoundCategory.HOSTILE;
            world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ITEM_CROSSBOW_LOADING_END, soundCategory, 1.0f, 1.0f / (world.getRandom().nextFloat() * 0.5f + 1.0f) + 0.2f);
        }
    }

    private static boolean loadProjectiles(LivingEntity shooter, ItemStack crossbow) {
        int multishot = EnchantmentHelper.getLevel(Enchantments.MULTISHOT, crossbow);
        int count = multishot == 0 ? 1 : 3;
        boolean creativePlayer = shooter instanceof PlayerEntity && ((PlayerEntity) shooter).getAbilities().creativeMode;
        ItemStack projectile = shooter.getProjectileType(crossbow);
        if (shooter instanceof PlayerEntity player && player.getAbilities().creativeMode && projectile.isOf(Items.ARROW)) {
            projectile = new ItemStack(Items.FIRE_CHARGE);
        }
        ItemStack projectileCopy = projectile.copy();
        for (int i = 0; i < count; i++) {
            if (i > 0) {
                projectile = projectileCopy.copy();
            }
            if (projectile.isEmpty() && creativePlayer) {
                projectile = new ItemStack(Items.ARROW);
                projectileCopy = projectile.copy();
            }
            if (loadProjectile(shooter, crossbow, projectile, i > 0, creativePlayer)) continue;
            return false;
        }
        return true;
    }

    private static boolean loadProjectile(LivingEntity shooter, ItemStack crossbow, ItemStack projectile, boolean simulated, boolean creative) {
        if (projectile.isEmpty()) {
            return false;
        }
        boolean creativePlayerAndNormalArrow = creative && projectile.getItem() instanceof ArrowItem;
        ItemStack stack;
        if (!(creativePlayerAndNormalArrow || creative || simulated)) {
            stack = projectile.split(1);
            if (projectile.isEmpty() && shooter instanceof PlayerEntity) {
                ((PlayerEntity) shooter).getInventory().removeOne(projectile);
            }
        } else {
            stack = projectile.copy();
        }
        putProjectile(crossbow, stack);
        return true;
    }

    public static boolean isCharged(ItemStack stack) {
        NbtCompound nbt = stack.getNbt();
        return nbt != null && nbt.getBoolean(CHARGED_KEY);
    }

    public static void setCharged(ItemStack stack, boolean charged) {
        NbtCompound nbt = stack.getOrCreateNbt();
        nbt.putBoolean(CHARGED_KEY, charged);
    }

    private static void putProjectile(ItemStack crossbow, ItemStack projectile) {
        NbtCompound nbt = crossbow.getOrCreateNbt();
        NbtList projectilesNbt = nbt.contains(CHARGED_PROJECTILES_KEY, NbtElement.LIST_TYPE) ? nbt.getList(CHARGED_PROJECTILES_KEY, NbtElement.COMPOUND_TYPE) : new NbtList();
        NbtCompound projectileNbt = new NbtCompound();
        projectile.writeNbt(projectileNbt);
        projectilesNbt.add(projectileNbt);
        nbt.put(CHARGED_PROJECTILES_KEY, projectilesNbt);
    }

    private static List<ItemStack> getProjectiles(ItemStack crossbow) {
        NbtList nbtList;
        ArrayList<ItemStack> list = Lists.newArrayList();
        NbtCompound nbt = crossbow.getNbt();
        if (nbt != null && nbt.contains(CHARGED_PROJECTILES_KEY, NbtElement.LIST_TYPE)
                && (nbtList = nbt.getList(CHARGED_PROJECTILES_KEY, NbtElement.COMPOUND_TYPE)) != null) {
            for (int i = 0; i < nbtList.size(); ++i) {
                NbtCompound nbtCompound2 = nbtList.getCompound(i);
                list.add(ItemStack.fromNbt(nbtCompound2));
            }
        }
        return list;
    }

    private static void clearProjectiles(ItemStack crossbow) {
        NbtCompound nbt = crossbow.getNbt();
        if (nbt != null) {
            NbtList nbtList = nbt.getList(CHARGED_PROJECTILES_KEY, NbtElement.LIST_TYPE);
            nbtList.clear();
            nbt.put(CHARGED_PROJECTILES_KEY, nbtList);
        }
    }

    public static boolean hasProjectile(ItemStack crossbow, Item projectile) {
        return getProjectiles(crossbow).stream().anyMatch(s -> s.isOf(projectile));
    }

    private static void shoot(World world, LivingEntity shooter, Hand hand, ItemStack crossbow, ItemStack ammo, float soundPitch, boolean creative, float speed, float divergence, float simulated) {
        ProjectileEntity projectile = null;
        if (world.isClient) {
            return;
        }
        boolean fireCharge = ammo.isOf(Items.FIRE_CHARGE);
        if (fireCharge) {
            projectile = new SmallFireballEntity(world, shooter, 0, 0, 0);
            projectile.setPos(shooter.getX(), shooter.getEyeY() - (double) 0.15f, shooter.getZ());
        }

        Vec3d vec3d = shooter.getOppositeRotationVector(1.0f);
        Quaternionf quaternionf = new Quaternionf().setAngleAxis(simulated * ((float) Math.PI / 180), vec3d.x, vec3d.y, vec3d.z);
        Vec3d vec3d2 = shooter.getRotationVec(1.0f);
        Vector3f vector3f = vec3d2.toVector3f().rotate(quaternionf);
        projectile.setVelocity(vector3f.x(), vector3f.y(), vector3f.z(), speed, divergence);

        crossbow.damage(fireCharge ? 2 : 1, shooter, e -> e.sendToolBreakStatus(hand));
        world.spawnEntity(projectile);
        world.playSound(null, shooter.getX(), shooter.getY(), shooter.getZ(), SoundEvents.ITEM_CROSSBOW_SHOOT, SoundCategory.PLAYERS, 1.0f, soundPitch);
    }

    public static void shootAll(World world, LivingEntity entity, Hand hand, ItemStack stack, float speed, float divergence) {
        List<ItemStack> projectiles = getProjectiles(stack);
        float[] soundPitches = getSoundPitches(entity.getRandom());
        for (int i = 0; i < projectiles.size(); ++i) {
            boolean creativePlayer;
            ItemStack ammo = projectiles.get(i);
            creativePlayer = entity instanceof PlayerEntity
                    && ((PlayerEntity) entity).getAbilities().creativeMode;
            if (ammo.isEmpty()) continue;
            if (i == 0) {
                shoot(world, entity, hand, stack, ammo, soundPitches[i], creativePlayer, speed, divergence, 0.0f);
                continue;
            }
            if (i == 1) {
                shoot(world, entity, hand, stack, ammo, soundPitches[i], creativePlayer, speed, divergence, -10.0f);
                continue;
            }
            if (i != 2) {
                continue;
            }
            shoot(world, entity, hand, stack, ammo, soundPitches[i], creativePlayer, speed, divergence, 10.0f);
        }
        postShoot(world, entity, stack);
    }

    private static float[] getSoundPitches(Random random) {
        boolean flag = random.nextBoolean();
        return new float[]{1.0f, getSoundPitch(flag, random), getSoundPitch(!flag, random)};
    }

    private static float getSoundPitch(boolean flag, Random random) {
        float f = flag ? 0.63f : 0.43f;
        return 1.0f / (random.nextFloat() * 0.5f + 1.8f) + f;
    }

    private static void postShoot(World world, LivingEntity entity, ItemStack stack) {
        if (entity instanceof ServerPlayerEntity serverPlayerEntity) {
            if (!world.isClient) {
//                Criteria.SHOT_CROSSBOW.trigger(serverPlayerEntity, stack);
            }
            serverPlayerEntity.incrementStat(Stats.USED.getOrCreateStat(stack.getItem()));
        }
        clearProjectiles(stack);
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (!world.isClient) {
            int i = EnchantmentHelper.getLevel(Enchantments.QUICK_CHARGE, stack);
            SoundEvent soundEvent = this.getQuickChargeSound(i);
            SoundEvent soundEvent2 = i == 0 ? SoundEvents.ITEM_CROSSBOW_LOADING_MIDDLE : null;
            float f = (float) (stack.getMaxUseTime() - remainingUseTicks) / (float) getPullTime(stack);
            if (f < 0.2f) {
                this.charged = false;
                this.loaded = false;
            }
            if (f >= 0.2f && !this.charged) {
                this.charged = true;
                world.playSound(null, user.getX(), user.getY(), user.getZ(), soundEvent, SoundCategory.PLAYERS, 0.5f, 1.0f);
            }
            if (f >= 0.5f && soundEvent2 != null && !this.loaded) {
                this.loaded = true;
                world.playSound(null, user.getX(), user.getY(), user.getZ(), soundEvent2, SoundCategory.PLAYERS, 0.5f, 1.0f);
            }
        }
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return getPullTime(stack) + 3;
    }

    public static int getPullTime(ItemStack stack) {
        int i = EnchantmentHelper.getLevel(Enchantments.QUICK_CHARGE, stack);
        return i == 0 ? DEFAULT_PULL_TIME : DEFAULT_PULL_TIME - 5 * i;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.CROSSBOW;
    }

    private SoundEvent getQuickChargeSound(int stage) {
        switch (stage) {
            case 1: {
                return SoundEvents.ITEM_CROSSBOW_QUICK_CHARGE_1;
            }
            case 2: {
                return SoundEvents.ITEM_CROSSBOW_QUICK_CHARGE_2;
            }
            case 3: {
                return SoundEvents.ITEM_CROSSBOW_QUICK_CHARGE_3;
            }
        }
        return SoundEvents.ITEM_CROSSBOW_LOADING_START;
    }

    private static float getPullProgress(int useTicks, ItemStack stack) {
        float f = (float) useTicks / (float) getPullTime(stack);
        if (f > 1.0f) {
            f = 1.0f;
        }
        return f;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        List<ItemStack> ammoList = getProjectiles(stack);
        if (!isCharged(stack) || ammoList.isEmpty()) {
            return;
        }
        ItemStack ammo = ammoList.get(0);
        tooltip.add(Text.translatable("item.minecraft.crossbow.projectile").append(ScreenTexts.SPACE).append(ammo.toHoverableText()));
    }

    @Override
    public boolean isUsedOnRelease(ItemStack stack) {
        return stack.isOf(this);
    }

    @Override
    public int getRange() {
        return RANGE;
    }
}
