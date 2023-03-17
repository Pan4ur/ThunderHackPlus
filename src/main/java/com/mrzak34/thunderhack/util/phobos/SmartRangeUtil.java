package com.mrzak34.thunderhack.util.phobos;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;

import static com.mrzak34.thunderhack.util.Util.mc;

public class SmartRangeUtil {
    public static boolean isInSmartRange(BlockPos pos, Entity entity,
                                         double rangeSq, int smartTicks) {
        return isInSmartRange(pos.getX() + 0.5f, pos.getY() + 1, pos.getZ()
                + 0.5f, entity, rangeSq, smartTicks);
    }

    public static boolean isInSmartRange(
            double crystalX, double crystalY, double crystalZ,
            Entity entity, double rangeSq, int smartTicks) {
        double x = entity.posX + entity.motionX * smartTicks;
        double y = entity.posY + entity.motionY * smartTicks;
        double z = entity.posZ + entity.motionZ * smartTicks;

        return distanceSq(crystalX, crystalY, crystalZ, x, y, z) < rangeSq;
    }

    // TODO: we could try to calculate the violation level?
    public static boolean isInStrictBreakRange(
            double crystalX, double crystalY, double crystalZ, double rangeSq,
            double entityX, double entityY, double entityZ) {
        final double height = 2.0f;
        // TODO: ehhhh???
        final double pY = entityY + mc.player.getEyeHeight();
        final double dY = crystalY;
        // LALALALALALA THIS IS FROM NCP idc
        //noinspection StatementWithEmptyBody
        if (pY <= dY) ;
        else //noinspection ManualMinMaxCalculation
            if (pY >= dY + height) {
                crystalY = dY + height;
            } else {
                crystalY = pY;
            }

        double x = crystalX - entityX;
        double y = crystalY - pY;
        double z = crystalZ - entityZ;

        return x * x + y * y + z * z <= rangeSq;
    }

    public static double distanceSq(double x, double y, double z, Entity entity) {
        return distanceSq(x, y, z, entity.posX, entity.posY, entity.posZ);
    }

    public static double distanceSq(double x, double y, double z, double x1, double y1, double z1) {
        double xDist = x - x1;
        double yDist = y - y1;
        double zDist = z - z1;
        return xDist * xDist + yDist * yDist + zDist * zDist;
    }
}