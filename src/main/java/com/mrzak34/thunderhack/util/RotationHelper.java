package com.mrzak34.thunderhack.util;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.modules.combat.Aura;
import com.mrzak34.thunderhack.util.MathematicHelper;
import com.mrzak34.thunderhack.util.rotations.GCDFix;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import static com.mrzak34.thunderhack.util.CrystalUtils.mc;

public class RotationHelper {


    public static Vec3d interpolatedEyePos()
    {
        return mc.player.getPositionEyes(mc.getRenderPartialTicks());
    }
    public static Vec3d interpolatedEyeVec()
    {
        return mc.player.getLook(mc.getRenderPartialTicks());
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


    public static float[] getNCPRotationsBT(Vec3d btpos) {
        double diffX = btpos.x - mc.player.posX;
        double diffZ = btpos.z - mc.player.posZ;
        double diffY = btpos.y  - (mc.player.posY + mc.player.getEyeHeight());

        final double diffXZ = MathHelper.sqrt(diffX * diffX + diffZ * diffZ);

        float yaw = (float) ((Math.toDegrees(Math.atan2(diffZ, diffX)) - 90));
        float pitch = (float) ((Math.toDegrees(-Math.atan2(diffY, diffXZ))));

        yaw = (mc.player.rotationYaw + GCDFix.getFixedRotation(MathHelper.wrapDegrees(yaw - mc.player.rotationYaw)));
        pitch = mc.player.rotationPitch + GCDFix.getFixedRotation(MathHelper.wrapDegrees(pitch - mc.player.rotationPitch));
        pitch = MathHelper.clamp(pitch, -90F, 90F);

        return new float[]{yaw, pitch};
    }

    public static float[] getNCPRotationsNoRandom(Entity entityIn) {
        double diffX;
        double diffZ;
        diffX = entityIn.posX + (entityIn.posX - entityIn.prevPosX) * mc.getRenderPartialTicks() - mc.player.posX - mc.player.motionX *  mc.getRenderPartialTicks() ;
        diffZ = entityIn.posZ + (entityIn.posZ - entityIn.prevPosZ) * mc.getRenderPartialTicks() - mc.player.posZ - mc.player.motionZ * mc.getRenderPartialTicks();
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
