package com.mrzak34.thunderhack.util.math;


import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;


public class PhobosRotationUtil {

    public static Vec3d getVec3d(float yaw, float pitch) {
        float vx = -MathHelper.sin(MathUtil.rad(yaw)) * MathHelper.cos(MathUtil.rad(pitch));
        float vz = MathHelper.cos(MathUtil.rad(yaw)) * MathHelper.cos(MathUtil.rad(pitch));
        float vy = -MathHelper.sin(MathUtil.rad(pitch));
        return new Vec3d(vx, vy, vz);
    }


    public static double angle(float[] rotation1, float[] rotation2) {
        Vec3d r1Vec = getVec3d(rotation1[0], rotation1[1]);
        Vec3d r2Vec = getVec3d(rotation2[0], rotation2[1]);
        return MathUtil.angle(r1Vec, r2Vec);
    }


    public static float updateRotation(float current,
                                       float intended,
                                       float factor) {
        float updated = MathHelper.wrapDegrees(intended - current);

        if (updated > factor) {
            updated = factor;
        }

        if (updated < -factor) {
            updated = -factor;
        }

        return current + updated;
    }
}