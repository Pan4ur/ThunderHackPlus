package com.mrzak34.thunderhack.util;

import com.mrzak34.thunderhack.Thunderhack;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.util.CombatRules;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.*;
import net.minecraft.world.Explosion;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class CrystalUtils {
    public static Minecraft mc = Minecraft.getMinecraft();

    private static final List<Block> valid = Arrays.asList(Blocks.OBSIDIAN, Blocks.BEDROCK, Blocks.ENDER_CHEST, Blocks.ANVIL);


    public static float getBlastReduction(EntityLivingBase entity, float damageInput, Explosion explosion) {
        float damage = damageInput;

        if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            DamageSource damageSource = DamageSource.causeExplosionDamage(explosion);

            damage = CombatRules.getDamageAfterAbsorb(
                    damage,
                    (float) player.getTotalArmorValue(),
                    (float) player.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue()
            );

            int protectionModifier = 0;
            try {
                protectionModifier = EnchantmentHelper.getEnchantmentModifierDamage(player.getArmorInventoryList(), damageSource);
            } catch (Exception ignored) {
            }

            float protectionRatio = MathHelper.clamp(protectionModifier, 0.0F, 20.0F);
            damage = damage * (1.0F - protectionRatio / 25.0F);

            if (entity.isPotionActive(MobEffects.RESISTANCE)) {
                damage -= (damage / 4);
            }

            damage = Math.max(damage, 0.0F);
            return damage;
        }

        damage = CombatRules.getDamageAfterAbsorb(
                damage,
                (float) entity.getTotalArmorValue(),
                (float) entity.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue()
        );

        return damage;
    }


    public static float getDamageMultiplied(float damage) {
        int difficultyId = mc.world.getDifficulty().getId();

        if (difficultyId == 0) {
            return 0;
        } else if (difficultyId == 1) {
            return damage * 0.5f;
        } else if (difficultyId == 2) {
            return damage;
        } else if (difficultyId == 3) {
            return damage * 1.5f;
        }

        return damage; // Unknown difficulty, return original damage
    }


    public static Vec3d getEntityPosVec(Entity entity, int ticks) {
        Vec3d currentPosition = entity.getPositionVector();
        Vec3d motionVec = getMotionVec(entity, ticks);

        return currentPosition.add(motionVec);
    }


    public static Vec3d getMotionVec(Entity entity, int ticks) {
        double deltaX = entity.posX - entity.prevPosX;
        double deltaZ = entity.posZ - entity.prevPosZ;
        double entityMotionPosX = 0;
        double entityMotionPosZ = 0;

        for (int i = 1; i <= ticks; i++) {
            double futurePosX = entity.posX + deltaX * i;
            double futurePosZ = entity.posZ + deltaZ * i;
            BlockPos futurePosBlock = new BlockPos(futurePosX, entity.posY, futurePosZ);

            if (mc.world.getBlockState(futurePosBlock).getBlock() instanceof BlockAir) {
                entityMotionPosX = deltaX * i;
                entityMotionPosZ = deltaZ * i;
            } else {
                break;
            }
        }

        return new Vec3d(entityMotionPosX, 0, entityMotionPosZ);
    }


    public static int ping() {
        if (mc.getConnection() == null) {
            return 150;
        } else if (mc.player == null) {
            return 150;
        } else {
            try {
                return mc.getConnection().getPlayerInfo(mc.player.getUniqueID()).getResponseTime();
            } catch (NullPointerException ignored) {
            }
            return 150;
        }
    }

    public static int getCrystalSlot() {
        int heldCrystalSlot = findCrystalSlot(Util.mc.player.getHeldItemMainhand());
        if (heldCrystalSlot != -1) {
            return heldCrystalSlot;
        }

        for (int slot = 0; slot < 9; ++slot) {
            ItemStack slotStack = Util.mc.player.inventory.getStackInSlot(slot);
            int inventoryCrystalSlot = findCrystalSlot(slotStack);
            if (inventoryCrystalSlot != -1) {
                return inventoryCrystalSlot;
            }
        }

        return -1; // No crystals found
    }

    private static int findCrystalSlot(ItemStack itemStack) {
        if (itemStack.getItem() == Items.END_CRYSTAL) {
            return Util.mc.player.inventory.getSlotFor(itemStack);
        }
        return -1;
    }



    public static boolean canPlaceCrystal(BlockPos blockPos) {
        BlockPos boost = blockPos.add(0, 1, 0);
        BlockPos boost2 = blockPos.add(0, 2, 0);
        try {
            if (mc.world.getBlockState(blockPos).getBlock() != Blocks.BEDROCK && mc.world.getBlockState(blockPos).getBlock() != Blocks.OBSIDIAN) {
                return false;
            }

            if (!(mc.world.getBlockState(boost).getBlock() == Blocks.AIR && mc.world.getBlockState(boost2).getBlock() == Blocks.AIR)) {
                return false;
            }

            for (Entity entity : mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost))) {
                if (!(entity instanceof EntityEnderCrystal)) {
                    return false;
                }
            }

            for (Entity entity : mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost2))) {
                if (!(entity instanceof EntityEnderCrystal)) {
                    return false;
                }
            }
        } catch (Exception ignored) {
            return false;
        }

        return true;
    }

    @Nullable
    public static RayTraceResult rayTraceBlocks(Vec3d start, Vec3d end) {
        return rayTraceBlocks(start, end, false, false, false);
    }

    @Nullable
    public static RayTraceResult rayTraceBlocks(Vec3d vec31, Vec3d vec32, boolean stopOnLiquid, boolean ignoreBlockWithoutBoundingBox, boolean returnLastUncollidableBlock) {
        if (!Double.isNaN(vec31.x) && !Double.isNaN(vec31.y) && !Double.isNaN(vec31.z)) {
            if (!Double.isNaN(vec32.x) && !Double.isNaN(vec32.y) && !Double.isNaN(vec32.z)) {
                int i = MathHelper.floor(vec32.x);
                int j = MathHelper.floor(vec32.y);
                int k = MathHelper.floor(vec32.z);
                int l = MathHelper.floor(vec31.x);
                int i1 = MathHelper.floor(vec31.y);
                int j1 = MathHelper.floor(vec31.z);
                BlockPos blockpos = new BlockPos(l, i1, j1);
                IBlockState iblockstate = mc.world.getBlockState(blockpos);
                Block block = iblockstate.getBlock();

                if (!valid.contains(block)) {
                    block = Blocks.AIR;
                    iblockstate = Blocks.AIR.getBlockState().getBaseState();
                }

                if ((!ignoreBlockWithoutBoundingBox || iblockstate.getCollisionBoundingBox(mc.world, blockpos) != Block.NULL_AABB) && block.canCollideCheck(iblockstate, stopOnLiquid)) {
                    RayTraceResult raytraceresult = iblockstate.collisionRayTrace(mc.world, blockpos, vec31, vec32);

                    if (raytraceresult != null) {
                        return raytraceresult;
                    }
                }

                RayTraceResult raytraceresult2 = null;
                int k1 = 200;

                while (k1-- >= 0) {
                    if (Double.isNaN(vec31.x) || Double.isNaN(vec31.y) || Double.isNaN(vec31.z)) {
                        return null;
                    }

                    if (l == i && i1 == j && j1 == k) {
                        return returnLastUncollidableBlock ? raytraceresult2 : null;
                    }

                    boolean flag2 = true;
                    boolean flag = true;
                    boolean flag1 = true;
                    double d0 = 999.0D;
                    double d1 = 999.0D;
                    double d2 = 999.0D;

                    if (i > l) {
                        d0 = (double) l + 1.0D;
                    } else if (i < l) {
                        d0 = (double) l + 0.0D;
                    } else {
                        flag2 = false;
                    }

                    if (j > i1) {
                        d1 = (double) i1 + 1.0D;
                    } else if (j < i1) {
                        d1 = (double) i1 + 0.0D;
                    } else {
                        flag = false;
                    }

                    if (k > j1) {
                        d2 = (double) j1 + 1.0D;
                    } else if (k < j1) {
                        d2 = (double) j1 + 0.0D;
                    } else {
                        flag1 = false;
                    }

                    double d3 = 999.0D;
                    double d4 = 999.0D;
                    double d5 = 999.0D;
                    double d6 = vec32.x - vec31.x;
                    double d7 = vec32.y - vec31.y;
                    double d8 = vec32.z - vec31.z;

                    if (flag2) {
                        d3 = (d0 - vec31.x) / d6;
                    }

                    if (flag) {
                        d4 = (d1 - vec31.y) / d7;
                    }

                    if (flag1) {
                        d5 = (d2 - vec31.z) / d8;
                    }

                    if (d3 == -0.0D) {
                        d3 = -1.0E-4D;
                    }

                    if (d4 == -0.0D) {
                        d4 = -1.0E-4D;
                    }

                    if (d5 == -0.0D) {
                        d5 = -1.0E-4D;
                    }

                    EnumFacing enumfacing;

                    if (d3 < d4 && d3 < d5) {
                        enumfacing = i > l ? EnumFacing.WEST : EnumFacing.EAST;
                        vec31 = new Vec3d(d0, vec31.y + d7 * d3, vec31.z + d8 * d3);
                    } else if (d4 < d5) {
                        enumfacing = j > i1 ? EnumFacing.DOWN : EnumFacing.UP;
                        vec31 = new Vec3d(vec31.x + d6 * d4, d1, vec31.z + d8 * d4);
                    } else {
                        enumfacing = k > j1 ? EnumFacing.NORTH : EnumFacing.SOUTH;
                        vec31 = new Vec3d(vec31.x + d6 * d5, vec31.y + d7 * d5, d2);
                    }

                    l = MathHelper.floor(vec31.x) - (enumfacing == EnumFacing.EAST ? 1 : 0);
                    i1 = MathHelper.floor(vec31.y) - (enumfacing == EnumFacing.UP ? 1 : 0);
                    j1 = MathHelper.floor(vec31.z) - (enumfacing == EnumFacing.SOUTH ? 1 : 0);
                    blockpos = new BlockPos(l, i1, j1);
                    IBlockState iblockstate1 = mc.world.getBlockState(blockpos);
                    Block block1 = iblockstate1.getBlock();

                    if (!valid.contains(block1)) {
                        block1 = Blocks.AIR;
                        iblockstate1 = Blocks.AIR.getBlockState().getBaseState();
                    }

                    if (!ignoreBlockWithoutBoundingBox || iblockstate1.getMaterial() == Material.PORTAL || iblockstate1.getCollisionBoundingBox(mc.world, blockpos) != Block.NULL_AABB) {
                        if (block1.canCollideCheck(iblockstate1, stopOnLiquid)) {
                            RayTraceResult raytraceresult1 = iblockstate1.collisionRayTrace(mc.world, blockpos, vec31, vec32);

                            if (raytraceresult1 != null) {
                                return raytraceresult1;
                            }
                        } else {
                            raytraceresult2 = new RayTraceResult(RayTraceResult.Type.MISS, vec31, enumfacing, blockpos);
                        }
                    }
                }

                return returnLastUncollidableBlock ? raytraceresult2 : null;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public static float calculateDamage2(BlockPos pos, Entity entity) {
        return calculateDamage(pos.getX(), pos.getY() + 1, pos.getZ(), entity);
    }


    public static float calculateDamage(double posX, double posY, double posZ, Entity entity) {
        float doubleExplosionSize = 12.0F;
        Vec3d entityPosVec = getEntityPosVec(entity, 3);
        double distance = entityPosVec.distanceTo(new Vec3d(posX, posY, posZ)) / (doubleExplosionSize);
        Vec3d impactPos = new Vec3d(posX, posY, posZ);

        double blockDensity = 0.0D;
        try {
            blockDensity = entity.world.getBlockDensity(impactPos, entity.getEntityBoundingBox().offset(getMotionVec(entity, 3)));
        } catch (Exception e) {
            Thunderhack.LOG.error(e);
        }

        double scaledDistance = 1.0D - distance;
        double scaledDensity = scaledDistance * blockDensity;
        double damageValue = (scaledDensity * scaledDensity + scaledDensity) / 2.0D * 7.0D * doubleExplosionSize + 1.0D;

        if (entity instanceof EntityLivingBase) {
            EntityLivingBase livingEntity = (EntityLivingBase) entity;
            float damageMultiplied = getDamageMultiplied((float) damageValue);
            double blastReduction = getBlastReduction(livingEntity, damageMultiplied, new Explosion(mc.world, mc.player, posX, posY, posZ, 6F, false, true));
            return (float) blastReduction;
        }

        return (float) damageValue;
    }



    public static float calculateDamage(EntityEnderCrystal crystal, Entity entity) {
        if (crystal == null || entity == null) {
            return 0.0F; // invalid inputs. return minimum damage
        }

        double crystalX = crystal.posX;
        double crystalY = crystal.posY;
        double crystalZ = crystal.posZ;

        return calculateDamage(crystalX, crystalY, crystalZ, entity);
    }



}