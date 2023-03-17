package com.mrzak34.thunderhack.util.phobos;

import com.mrzak34.thunderhack.mixin.mixins.IItemTool;
import net.minecraft.block.BlockAnvil;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockWeb;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Enchantments;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.CombatRules;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;

import static com.mrzak34.thunderhack.util.Util.mc;

public class DamageUtil {
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean canBreakWeakness(boolean checkStack) {
        if (!mc.player.isPotionActive(MobEffects.WEAKNESS)) {
            return true;
        }

        int strengthAmp = 0;
        PotionEffect effect =
                mc.player.getActivePotionEffect(MobEffects.STRENGTH);

        if (effect != null) {
            strengthAmp = effect.getAmplifier();
        }

        if (strengthAmp >= 1) {
            return true;
        }

        return checkStack && canBreakWeakness(mc.player.getHeldItemMainhand());
    }


    public static boolean canBreakWeakness(ItemStack stack) {
        if (stack.getItem() instanceof ItemSword) {
            return true;
        }

        if (stack.getItem() instanceof ItemTool) {
            // get the attribute modifiers and stuff?
            IItemTool tool = (IItemTool) stack.getItem();
            return tool.getAttackDamage() > 4.0f;
        }

        return false;
    }

    public static int findAntiWeakness() {
        int slot = -1;
        for (int i = 8; i > -1; i--) {
            if (DamageUtil.canBreakWeakness(
                    mc.player.inventory.getStackInSlot(i))) {
                slot = i;
                if (mc.player.inventory.currentItem == i) {
                    break;
                }
            }
        }

        return slot;
    }

    /**
     * Returns the durability for the given ItemStack.
     *
     * @param stack the stack.
     * @return durability of the stack.
     */
    public static int getDamage(ItemStack stack) {
        return stack.getMaxDamage() - stack.getItemDamage();
    }

    /**
     * Returns the durability in percent
     * for the given ItemStack.
     *
     * @param stack the stack.
     * @return durability% of the stack.
     */




    public static float calculate(double x, double y, double z, AxisAlignedBB bb, EntityLivingBase base) {
        return calculate(x, y, z, bb, base, false);
    }

    public static float calculate(double x, double y, double z, AxisAlignedBB bb, EntityLivingBase base, boolean terrainCalc) {
        return calculate(x, y, z, bb, base, mc.world, terrainCalc);
    }

    public static float calculate(double x, double y, double z, AxisAlignedBB bb, EntityLivingBase base, IBlockAccess world, boolean terrainCalc) {
        return calculate(x, y, z, bb, base, world, terrainCalc, false);
    }

    public static float calculate(double x, double y, double z, AxisAlignedBB bb, EntityLivingBase base, IBlockAccess world, boolean terrainCalc, boolean anvils) {
        return calculate(x, y, z, bb, base, world, terrainCalc, anvils, 6.0f);
    }


    public static float calculate(double x, double y, double z, AxisAlignedBB bb, EntityLivingBase base, IBlockAccess world, boolean terrainCalc, boolean anvils, float power) {
        float f = 12.0f;
        double d5 = base.getDistance(x, y, z) / 12.0;
        if (d5 > 1.0) {
            return 0.0f;
        }
        double d6 = base.world.getBlockDensity(new Vec3d(x, y, z), base.getEntityBoundingBox());
        d5 = (1.0 - d5) * d6;
        f = (int) ((d5 * d5 + d5) / 2.0 * 7.0 * 12.0 + 1.0);
        f = getDifDamage(f);
        DamageSource dmsrc = DamageSource.causeExplosionDamage(new Explosion(mc.world, mc.player, x, y, z, 6.0f, false, true));
        f = CombatRules.getDamageAfterAbsorb(f, (float) base.getTotalArmorValue(), (float) base.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());
        int n = EnchantmentHelper.getEnchantmentModifierDamage(base.getArmorInventoryList(), dmsrc);
        if (n > 0) {
            f = CombatRules.getDamageAfterMagicAbsorb(f, (float) n);
        }
        if (base.getActivePotionEffect(MobEffects.RESISTANCE) != null) {
            f = f * (float) (25 - (base.getActivePotionEffect(MobEffects.RESISTANCE).getAmplifier() + 1) * 5) / 25.0f;
        }
        f = Math.max(f, 0.0f);
        return f;
    }


    private static float getDifDamage(float f) {
        EnumDifficulty enumDifficulty = mc.world.getDifficulty();
        if (enumDifficulty == EnumDifficulty.PEACEFUL) {
            f = 0.0f;
            return 0.0f;
        }
        if (enumDifficulty == EnumDifficulty.EASY) {
            f = Math.min(f / 2.0f + 1.0f, f);
            return f;
        }
        if (enumDifficulty == EnumDifficulty.HARD) {
            f = f * 3.0f / 2.0f;
        }
        return f;
    }







    public static float getBlockDensity(Vec3d vec, AxisAlignedBB bb, IBlockAccess world, boolean ignoreWebs, boolean ignoreBeds, boolean terrainCalc, boolean anvils) {
        double x = 1.0 / ((bb.maxX - bb.minX) * 2.0 + 1.0);
        double y = 1.0 / ((bb.maxY - bb.minY) * 2.0 + 1.0);
        double z = 1.0 / ((bb.maxZ - bb.minZ) * 2.0 + 1.0);
        double xFloor = (1.0 - Math.floor(1.0 / x) * x) / 2.0;
        double zFloor = (1.0 - Math.floor(1.0 / z) * z) / 2.0;

        if (x >= 0.0D && y >= 0.0D && z >= 0.0D) {
            int air = 0;
            int traced = 0;

            for (float a = 0.0F; a <= 1.0F; a = (float) (a + x)) {
                for (float b = 0.0F; b <= 1.0F; b = (float) (b + y)) {
                    for (float c = 0.0F; c <= 1.0F; c = (float) (c + z)) {
                        double xOff = bb.minX + (bb.maxX - bb.minX) * a;
                        double yOff = bb.minY + (bb.maxY - bb.minY) * b;
                        double zOff = bb.minZ + (bb.maxZ - bb.minZ) * c;

                        RayTraceResult result = rayTraceBlocks(
                                new Vec3d(xOff + xFloor, yOff, zOff + zFloor),
                                vec,
                                world,
                                false,
                                false,
                                false,
                                ignoreWebs,
                                ignoreBeds,
                                terrainCalc,
                                anvils);

                        if (result == null) {
                            air++;
                        }

                        traced++;
                    }
                }
            }

            return (float) air / (float) traced;
        } else {
            return 0.0F;
        }
    }

    /**
     * Calls {@link RayTracer#
     * trace(World, IBlockAccess, Vec3d, Vec3d, boolean, boolean, boolean)}
     *
     * @param start                 same as the original param.
     * @param end                   same as the original param.
     * @param stopOnLiquid          same as the original param.
     * @param ignoreNoBox           same as the original param.
     * @param lastUncollidableBlock same as the original param.
     * @param ignoreWebs            handles webs like air.
     * @param ignoreBeds            handles beds like air.
     * @return a RayTraceResult...
     */
    @SuppressWarnings("deprecation")
    public static RayTraceResult rayTraceBlocks(Vec3d start,
                                                Vec3d end,
                                                IBlockAccess world,
                                                boolean stopOnLiquid,
                                                boolean ignoreNoBox,
                                                boolean lastUncollidableBlock,
                                                boolean ignoreWebs,
                                                boolean ignoreBeds,
                                                boolean terrainCalc,
                                                boolean anvils) {
        return RayTracer.trace(mc.world,
                world,
                start,
                end,
                stopOnLiquid,
                ignoreNoBox,
                lastUncollidableBlock,
                (b, p) ->
                        !(terrainCalc
                                && b.getExplosionResistance(mc.player)
                                < 100
                                && p.distanceSq(end.x, end.y, end.z)
                                <= 36.0
                                || ignoreBeds && b instanceof BlockBed
                                || ignoreWebs && b instanceof BlockWeb)
                                || anvils && b instanceof BlockAnvil);
    }

}