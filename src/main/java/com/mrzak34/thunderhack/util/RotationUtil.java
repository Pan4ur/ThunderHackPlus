package com.mrzak34.thunderhack.util;

import com.mrzak34.thunderhack.util.rotations.GCDFix;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.*;
import net.minecraft.world.IBlockAccess;
import org.apache.commons.lang3.RandomUtils;

import static com.mrzak34.thunderhack.util.CrystalUtils.mc;

public class RotationUtil
        implements Util {


    public static EntityPlayer getRotationPlayer()
    {
        EntityPlayer rotationEntity = mc.player;
        return rotationEntity == null ? mc.player : rotationEntity;
    }

    public static float[] getRotations(BlockPos pos, EnumFacing facing)
    {
        return getRotations(pos, facing, RotationUtil.getRotationPlayer());
    }

    public static float[] getRotations(BlockPos pos, EnumFacing facing, Entity from)
    {
        return getRotations(pos, facing, from, mc.world, mc.world.getBlockState(pos));
    }

    public static float[] getRotations(BlockPos pos,
                                       EnumFacing facing,
                                       Entity from,
                                       IBlockAccess world,
                                       IBlockState state)
    {
        AxisAlignedBB bb = state.getBoundingBox(world, pos);

        double x = pos.getX() + (bb.minX + bb.maxX) / 2.0;
        double y = pos.getY() + (bb.minY + bb.maxY) / 2.0;
        double z = pos.getZ() + (bb.minZ + bb.maxZ) / 2.0;

        if (facing != null)
        {
            x += facing.getDirectionVec().getX() * ((bb.minX + bb.maxX) / 2.0);
            y += facing.getDirectionVec().getY() * ((bb.minY + bb.maxY) / 2.0);
            z += facing.getDirectionVec().getZ() * ((bb.minZ + bb.maxZ) / 2.0);
        }

        return getRotations(x, y, z, from);
    }

    public static float[] getRotations(double x, double y, double z, Entity f)
    {
        return getRotations(x, y, z, f.posX, f.posY, f.posZ, f.getEyeHeight());
    }
    public static float[] getRotations(double x,
                                       double y,
                                       double z,
                                       double fromX,
                                       double fromY,
                                       double fromZ,
                                       float fromHeight)
    {
        double xDiff = x - fromX;
        double yDiff = y - (fromY + fromHeight);
        double zDiff = z - fromZ;
        double dist = MathHelper.sqrt(xDiff * xDiff + zDiff * zDiff);

        float yaw = (float) (Math.atan2(zDiff, xDiff) * 180.0 / Math.PI) - 90.0f;
        float pitch = (float) (-(Math.atan2(yDiff, dist) * 180.0 / Math.PI));
        // Is there a better way than to use the previous yaw?
        float prevYaw = mc.player.rotationYaw;
        float diff = yaw - prevYaw;

        if (diff < -180.0f || diff > 180.0f)
        {
            float round = Math.round(Math.abs(diff / 360.0f));
            diff = diff < 0.0f ? diff + 360.0f * round : diff - (360.0f * round);
        }

        return new float[]{ prevYaw + diff, pitch };
    }



    public static Vec2f getRotationTo(Vec3d posTo) {
        EntityPlayerSP player = mc.player;
        return player != null ? getRotationTo(player.getPositionEyes(1.0f), posTo) : Vec2f.ZERO;
    }

    public static Vec2f getRotationTo(Vec3d posFrom, Vec3d posTo) {
        return getRotationFromVec(posTo.subtract(posFrom));
    }
    public static Vec2f getRotationFromVec(Vec3d vec) {
        double lengthXZ = Math.hypot(vec.x, vec.z);
        double yaw = normalizeAngle(Math.toDegrees(Math.atan2(vec.z, vec.x)) - 90.0);
        double pitch = normalizeAngle(Math.toDegrees(-Math.atan2(vec.y, lengthXZ)));

        return new Vec2f((float) yaw, (float) pitch);
    }
    public static double normalizeAngle(double angle) {
        angle %= 360.0;

        if (angle >= 180.0) {
            angle -= 360.0;
        }

        if (angle < -180.0) {
            angle += 360.0;
        }

        return angle;
    }


    public static float[] getNeededRotations(final Entity entityLivingBase) {
        final double d = entityLivingBase.posX - Util.mc.player.posX;
        final double d2 = entityLivingBase.posZ - Util.mc.player.posZ;
        final double d3 = entityLivingBase.posY - (Util.mc.player.getEntityBoundingBox().minY + (Util.mc.player.getEntityBoundingBox().maxY - Minecraft.getMinecraft().player.getEntityBoundingBox().minY));
        final double d4 = MathHelper.sqrt(d * d + d2 * d2);
        final float f = (float)(MathHelper.atan2(d2, d) * 180.0 / 3.141592653589793) - 90.0f;
        final float f2 = (float)(-(MathHelper.atan2(d3, d4) * 180.0 / 3.141592653589793));
        return new float[] { f, f2 };
    }


    public static double angle(Vec3d vec3d, Vec3d other) {
        double lengthSq = vec3d.length() * other.length();

        if (lengthSq < 1.0E-4D) {
            return 0.0;
        }

        double dot = vec3d.dotProduct(other);
        double arg = dot / lengthSq;

        if (arg > 1) {
            return 0.0;
        } else if (arg < -1) {
            return 180.0;
        }

        return Math.acos(arg) * 180.0f / Math.PI;
    }

    public static double angle(float[] rotation1, float[] rotation2)
    {
        Vec3d r1Vec = getVec3d(rotation1[0], rotation1[1]);
        Vec3d r2Vec = getVec3d(rotation2[0], rotation2[1]);
        return angle(r1Vec, r2Vec);
    }

    public static Vec3d getVec3d(float yaw, float pitch)
    {
        float vx = -MathHelper.sin(MathUtil.rad(yaw)) * MathHelper.cos(MathUtil.rad(pitch));
        float vz = MathHelper.cos(MathUtil.rad(yaw)) * MathHelper.cos(MathUtil.rad(pitch));
        float vy = -MathHelper.sin(MathUtil.rad(pitch));
        return new Vec3d(vx, vy, vz);
    }

    public static int getDirection4D() {
        return MathHelper.floor((double) (RotationUtil.mc.player.rotationYaw * 4.0f / 360.0f) + 0.5) & 3;
    }

    public static boolean isInFov(Entity player) {
        return false;
    }


    public static float[] getNCPRotations(Entity entityIn, boolean interpolate) {
        double diffX;
        double diffZ;
        if(interpolate){
            diffX = entityIn.posX + (entityIn.posX - entityIn.prevPosX) * mc.getRenderPartialTicks() - mc.player.posX - mc.player.motionX *  mc.getRenderPartialTicks() ;
            diffZ = entityIn.posZ + (entityIn.posZ - entityIn.prevPosZ) * mc.getRenderPartialTicks() - mc.player.posZ - mc.player.motionZ * mc.getRenderPartialTicks();
        } else {
            diffX = entityIn.posX - mc.player.posX;
            diffZ = entityIn.posZ - mc.player.posZ;
        }

        double diffY;

        if (entityIn instanceof EntityLivingBase) {
            diffY = entityIn.posY + entityIn.getEyeHeight() - (mc.player.posY + mc.player.getEyeHeight()) - 0.2f;
        } else {
            diffY = (entityIn.getEntityBoundingBox().minY + entityIn.getEntityBoundingBox().maxY) / 2 - (mc.player.posY + mc.player.getEyeHeight());
        }
        if (!mc.player.canEntityBeSeen(entityIn)) {
            diffY = entityIn.posY + entityIn.height - (mc.player.posY + mc.player.getEyeHeight());
        }
        final double diffXZ = MathHelper.sqrt(diffX * diffX + diffZ * diffZ);

        float yaw = (float) ((Math.toDegrees(Math.atan2(diffZ, diffX)) - 90));
        float pitch = (float) ((Math.toDegrees(-Math.atan2(diffY, diffXZ))));

        yaw = (mc.player.rotationYaw + GCDFix.getFixedRotation(MathHelper.wrapDegrees(yaw - mc.player.rotationYaw)));
        pitch = mc.player.rotationPitch + GCDFix.getFixedRotation(MathHelper.wrapDegrees(pitch - mc.player.rotationPitch));
        pitch = MathHelper.clamp(pitch, -90F, 90F);

        return new float[]{yaw, pitch};
    }

}

