package com.mrzak34.thunderhack.util;

import com.mrzak34.thunderhack.command.Command;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import static com.mrzak34.thunderhack.util.Util.mc;


public class SilentRotaionUtil {

    public static void lookAtVector(Vec3d vec) {
        float[] angle = calcAngle(mc.player.getPositionEyes(mc.getRenderPartialTicks()), vec);
        setPlayerRotations(angle[0], angle[1]);
        mc.player.renderYawOffset = angle[0];
        mc.player.rotationYawHead = angle[0];
    }


    public static void lookAtEntity(Entity entity) {
        float[] angle = calcAngle(mc.player.getPositionEyes(mc.getRenderPartialTicks()), entity.getPositionEyes(mc.getRenderPartialTicks()));
        lookAtAngles(angle[0], angle[1]);
    }


    public static void lookAtAngles(float yaw, float pitch) {
        setPlayerRotations(yaw, pitch);
       // mc.player.renderYawOffset = yaw;
        mc.player.rotationYawHead = yaw;

    }


    public static void lookAtBlock(BlockPos blockPos) {
        float[] angle = calcAngle(mc.player.getPositionEyes(mc.getRenderPartialTicks()), new Vec3d(blockPos));
        setPlayerRotations(angle[0], angle[1]);
        mc.player.renderYawOffset = angle[0];
        mc.player.rotationYawHead = angle[0];

    }

    public static void setPlayerRotations(float yaw, float pitch) {
        mc.player.rotationYaw = yaw;
        mc.player.rotationYawHead = yaw;
        mc.player.rotationPitch = pitch;
    }


    public static float[] calcAngle(Vec3d to) {
        if(to == null){
            return null;
        }
        double difX = to.x - mc.player.getPositionEyes(1).x;
        double difY = (to.y - mc.player.getPositionEyes(1).y) * -1.0;
        double difZ = to.z - mc.player.getPositionEyes(1).z;
        double dist = MathHelper.sqrt(difX * difX + difZ * difZ);
        return new float[]{(float) MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(difZ, difX)) - 90.0), (float) MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(difY, dist)))};
    }

    public static float[] calcAngle(Vec3d from, Vec3d to) {
        double difX = to.x - from.x;
        double difY = (to.y - from.y) * -1.0;
        double difZ = to.z - from.z;
        double dist = MathHelper.sqrt(difX * difX + difZ * difZ);
        return new float[]{(float) MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(difZ, difX)) - 90.0), (float) MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(difY, dist)))};
    }


    public static float[] calculateAngle(Vec3d from, Vec3d to) {
        double difX = to.x - from.x;
        double difY = (to.y - from.y) * -1.0;
        double difZ = to.z - from.z;
        double dist = MathHelper.sqrt((double)(difX * difX + difZ * difZ));
        float yD = (float)MathHelper.wrapDegrees((double)(Math.toDegrees(Math.atan2(difZ, difX)) - 90.0));
        float pD = (float)MathHelper.wrapDegrees((double)Math.toDegrees(Math.atan2(difY, dist)));
        if (pD > 90F) {
            pD = 90F;
        } else if (pD < -90F) {
            pD = -90F;
        }
        return new float[]{yD, pD};
    }
}
