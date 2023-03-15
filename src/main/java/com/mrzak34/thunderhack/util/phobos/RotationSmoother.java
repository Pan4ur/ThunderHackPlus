package com.mrzak34.thunderhack.util.phobos;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.manager.RotationManager;
import com.mrzak34.thunderhack.util.math.MathUtil;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import static com.mrzak34.thunderhack.util.math.PhobosRotationUtil.getVec3d;
import static com.mrzak34.thunderhack.util.math.PhobosRotationUtil.updateRotation;

// TODO: distinguish between yaw- and pitch speed?
public class RotationSmoother {
    private final RotationManager manager;
    private int rotationTicks;
    private boolean rotating;

    public RotationSmoother(RotationManager manager) {
        this.manager = manager;
    }

    public static float[] getRotations(double x,
                                       double y,
                                       double z,
                                       double fromX,
                                       double fromY,
                                       double fromZ,
                                       float fromHeight) {
        double xDiff = x - fromX;
        double yDiff = y - (fromY + fromHeight);
        double zDiff = z - fromZ;
        double dist = MathHelper.sqrt(xDiff * xDiff + zDiff * zDiff);

        float yaw = (float) (Math.atan2(zDiff, xDiff) * 180.0 / Math.PI) - 90.0f;
        float pitch = (float) (-(Math.atan2(yDiff, dist) * 180.0 / Math.PI));
        // Is there a better way than to use the previous yaw?
        float prevYaw = Thunderhack.rotationManager.getServerYaw();
        float diff = yaw - prevYaw;

        if (diff < -180.0f || diff > 180.0f) {
            float round = Math.round(Math.abs(diff / 360.0f));
            diff = diff < 0.0f ? diff + 360.0f * round : diff - (360.0f * round);
        }

        return new float[]{prevYaw + diff, pitch};
    }

    public static float[] faceSmoothly(double curYaw,
                                       double curPitch,
                                       double intendedYaw,
                                       double intendedPitch,
                                       double yawSpeed,
                                       double pitchSpeed) {
        float yaw = updateRotation((float) curYaw,
                (float) intendedYaw,
                (float) yawSpeed);

        float pitch = updateRotation((float) curPitch,
                (float) intendedPitch,
                (float) pitchSpeed);

        return new float[]{yaw, pitch};
    }

    public static double angle(float[] rotation1, float[] rotation2) {
        Vec3d r1Vec = getVec3d(rotation1[0], rotation1[1]);
        Vec3d r2Vec = getVec3d(rotation2[0], rotation2[1]);
        return MathUtil.angle(r1Vec, r2Vec);
    }

    public float[] getRotations(Entity from,
                                Entity entity,
                                double height,
                                double maxAngle) {
        return getRotations(entity,
                from.posX,
                from.posY,
                from.posZ,
                from.getEyeHeight(),
                height,
                maxAngle);
    }

    public float[] getRotations(Entity entity,
                                double fromX,
                                double fromY,
                                double fromZ,
                                float eyeHeight,
                                double height,
                                double maxAngle) {
        float[] rotations = getRotations(
                entity.posX,
                entity.posY + entity.getEyeHeight() * height,
                entity.posZ,
                fromX,
                fromY,
                fromZ,
                eyeHeight);

        return smoothen(rotations, maxAngle);
    }

    public float[] smoothen(float[] rotations,
                            double maxAngle) {
        float[] server = {manager.getServerYaw(), manager.getServerPitch()};
        return smoothen(server, rotations, maxAngle);
    }

    public float[] smoothen(float[] server,
                            float[] rotations,
                            double maxAngle) {
        if (maxAngle >= 180.0f
                || maxAngle <= 0.0f
                || angle(server, rotations) <= maxAngle) {
            rotating = false;
            return rotations;
        }

        rotationTicks = 0;
        rotating = true;
        return faceSmoothly(server[0],
                server[1],
                rotations[0],
                rotations[1],
                maxAngle,
                maxAngle);
    }

    public void incrementRotationTicks() {
        rotationTicks++;
    }

    public int getRotationTicks() {
        return rotationTicks;
    }

    public boolean isRotating() {
        return rotating;
    }

}