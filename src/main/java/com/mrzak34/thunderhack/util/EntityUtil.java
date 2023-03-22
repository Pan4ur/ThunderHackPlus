package com.mrzak34.thunderhack.util;

import com.mrzak34.thunderhack.mixin.ducks.IEntity;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public class EntityUtil implements Util {


    public static boolean isDead(Entity entity) {
        return entity.isDead
                || ((IEntity) entity).isPseudoDeadT()
                || entity instanceof EntityLivingBase
                && ((EntityLivingBase) entity).getHealth() <= 0.0f;
    }

    public static void attackEntity(Entity entity, boolean packet, boolean swingArm) {
        if (packet) {
            mc.player.connection.sendPacket(new CPacketUseEntity(entity));
        } else {
            mc.playerController.attackEntity(mc.player, entity);
        }
        if (swingArm) {
            mc.player.swingArm(EnumHand.MAIN_HAND);
        }
    }

    public static Vec3d interpolateEntity(Entity entity, float time) {
        return new Vec3d(
                entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double) time,
                entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double) time,
                entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double) time);
    }

    public static boolean isSafe(Entity entity, int height, boolean floor) {
        return EntityUtil.getUnsafeBlocks(entity, height, floor).size() == 0;
    }

    public static boolean isSafe(Entity entity) {
        return EntityUtil.isSafe(entity, 0, false);
    }


    public static List<Vec3d> getUnsafeBlocks(Entity entity, int height, boolean floor) {
        return EntityUtil.getUnsafeBlocksFromVec3d(entity.getPositionVector(), height, floor);
    }


    public static List<Vec3d> getUnsafeBlocksFromVec3d(Vec3d pos, int height, boolean floor) {
        ArrayList<Vec3d> vec3ds = new ArrayList<Vec3d>();
        for (Vec3d vector : EntityUtil.getOffsets(height, floor)) {
            BlockPos targetPos = new BlockPos(pos).add(vector.x, vector.y, vector.z);
            Block block = mc.world.getBlockState(targetPos).getBlock();
            if (!(block instanceof BlockAir) && !(block instanceof BlockLiquid) && !(block instanceof BlockTallGrass) && !(block instanceof BlockFire) && !(block instanceof BlockDeadBush) && !(block instanceof BlockSnow))
                continue;
            vec3ds.add(vector);
        }
        return vec3ds;
    }


    public static List<Vec3d> getOffsetList(int y, boolean floor) {
        ArrayList<Vec3d> offsets = new ArrayList<Vec3d>();
        offsets.add(new Vec3d(-1.0, y, 0.0));
        offsets.add(new Vec3d(1.0, y, 0.0));
        offsets.add(new Vec3d(0.0, y, -1.0));
        offsets.add(new Vec3d(0.0, y, 1.0));
        if (floor) {
            offsets.add(new Vec3d(0.0, y - 1, 0.0));
        }
        return offsets;
    }

    public static Vec3d[] getOffsets(int y, boolean floor) {
        List<Vec3d> offsets = EntityUtil.getOffsetList(y, floor);
        Vec3d[] array = new Vec3d[offsets.size()];
        return offsets.toArray(array);
    }

    public static boolean isLiving(Entity entity) {
        return entity instanceof EntityLivingBase;
    }


    public static float getHealth(Entity entity) {
        if (EntityUtil.isLiving(entity)) {
            EntityLivingBase livingBase = (EntityLivingBase) entity;
            return livingBase.getHealth() + livingBase.getAbsorptionAmount();
        }
        return 0.0f;
    }

    public static float getHealth(Entity entity, boolean absorption) {
        if (EntityUtil.isLiving(entity)) {
            EntityLivingBase livingBase = (EntityLivingBase) entity;
            return livingBase.getHealth() + (absorption ? livingBase.getAbsorptionAmount() : 0.0f);
        }
        return 0.0f;
    }

    public static boolean canSeeEntityAtFov(final Entity entityLiving, final float scope) {
        final double diffX = entityLiving.posX - mc.player.posX;
        final double diffZ = entityLiving.posZ - mc.player.posZ;
        final float yaw = (float) (Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0);
        final double difference = angleDifference(yaw, mc.player.rotationYaw);
        return difference <= scope;
    }

    public static double angleDifference(final float oldYaw, final float newYaw) {
        float yaw = Math.abs(oldYaw - newYaw) % 360.0f;
        if (yaw > 180.0f) {
            yaw = 360.0f - yaw;
        }
        return yaw;
    }

    public static boolean canEntityBeSeen(Entity entityIn) {
        return mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posX + (double) mc.player.getEyeHeight(), mc.player.posZ), new Vec3d(entityIn.posX, entityIn.posY + (double) entityIn.getEyeHeight(), entityIn.posZ), false, true, false) == null;
    }
}

