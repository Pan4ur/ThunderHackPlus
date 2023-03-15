package com.mrzak34.thunderhack.util.phobos;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;

import static com.mrzak34.thunderhack.util.Util.mc;

public class DistanceUtil {
    public static double distanceSq2Crystal(BlockPos pos) {
        return distanceSq2Crystal(pos, mc.player);
    }

    public static double distanceSq2Crystal(BlockPos pos, Entity entity) {
        return distanceSq(pos.getX() + 0.5f, pos.getY() + 1, pos.getZ() + 0.5f, entity.posX, entity.posY, entity.posZ);
    }

    public static double distanceSq2Crystal(BlockPos pos, double x, double y, double z) {
        return distanceSq(pos.getX() + 0.5f, pos.getY() + 1, pos.getZ() + 0.5f, x, y, z);
    }

    public static double distanceSq2Bottom(BlockPos pos) {
        return distanceSq(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, mc.player);
    }

    public static double distanceSq2Bottom(BlockPos pos, Entity entity) {
        return distanceSq(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, entity);
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