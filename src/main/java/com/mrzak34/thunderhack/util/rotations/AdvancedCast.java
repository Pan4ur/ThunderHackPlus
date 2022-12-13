package com.mrzak34.thunderhack.util.rotations;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

public class AdvancedCast {
    public static AdvancedCast instance = new AdvancedCast();

    public Entity getMouseOver(Entity target, float yaw, float pitch, double distance, boolean ignoreWalls) {
        Minecraft mc = Minecraft.getMinecraft();
        Entity pointedEntity;
        RayTraceResult objectMouseOver;
        Entity entity = mc.getRenderViewEntity();
        if (entity != null && mc.world != null) {
            objectMouseOver = ignoreWalls ? null : rayTrace(distance, yaw, pitch);
            Vec3d vec3d = entity.getPositionEyes(1);
            boolean flag = false;
            double d1 = distance;
            if (distance > 3) {
                flag = true;
            }
            if (objectMouseOver != null) {
                d1 = objectMouseOver.hitVec.distanceTo(vec3d);
            }
            Vec3d vec3d1 = getVectorForRotation(pitch, yaw);
            Vec3d vec3d2 = vec3d.add(vec3d1.x * distance, vec3d1.y * distance, vec3d1.z * distance);
            pointedEntity = null;
            Vec3d vec3d3 = null;
            double d2 = d1;
            AxisAlignedBB axisalignedbb = target.getEntityBoundingBox().expand(target.getCollisionBorderSize(),target.getCollisionBorderSize(),target.getCollisionBorderSize());
            RayTraceResult raytraceresult = axisalignedbb.calculateIntercept(vec3d, vec3d2);
            if (axisalignedbb.contains(vec3d)) {
                if (d2 >= 0.0D) {
                    pointedEntity = target;
                    vec3d3 = raytraceresult == null ? vec3d : raytraceresult.hitVec;
                    d2 = 0.0D;
                }
            } else if (raytraceresult != null) {
                double d3 = vec3d.distanceTo(raytraceresult.hitVec);

                if (d3 < d2 || d2 == 0.0D) {
                    boolean flag1 = false;

                    if (!flag1 && target.getLowestRidingEntity() == entity.getLowestRidingEntity()) {
                        if (d2 == 0.0D) {
                            pointedEntity = target;
                            vec3d3 = raytraceresult.hitVec;
                        }
                    } else {
                        pointedEntity = target;
                        vec3d3 = raytraceresult.hitVec;
                        d2 = d3;
                    }
                }
            }
            if (pointedEntity != null && flag && vec3d.distanceTo(vec3d3) > distance) {
                pointedEntity = null;
                objectMouseOver = new RayTraceResult(RayTraceResult.Type.MISS, vec3d3, null,
                        new BlockPos(vec3d3));
            }
            if (pointedEntity != null && (d2 < d1 || objectMouseOver == null)) {
                objectMouseOver = new RayTraceResult(pointedEntity, vec3d3);
            }
            if (objectMouseOver == null)
                return null;
            return objectMouseOver.entityHit;
        }
        return null;
    }

    public RayTraceResult rayTrace(double blockReachDistance, float yaw, float pitch) {
        Vec3d vec3d = Minecraft.getMinecraft().player.getPositionEyes(1);
        Vec3d vec3d1 = getVectorForRotation(pitch, yaw);
        Vec3d vec3d2 = vec3d.add(vec3d1.x * blockReachDistance, vec3d1.y * blockReachDistance,
                vec3d1.z * blockReachDistance);
        return Minecraft.getMinecraft().world.rayTraceBlocks(vec3d, vec3d2, true, true, true);
    }

    protected final Vec3d getVectorForRotation(float pitch, float yaw) {
        float f = MathHelper.cos(-yaw * 0.017453292F - (float) Math.PI);
        float f1 = MathHelper.sin(-yaw * 0.017453292F - (float) Math.PI);
        float f2 = -MathHelper.cos(-pitch * 0.017453292F);
        float f3 = MathHelper.sin(-pitch * 0.017453292F);
        return new Vec3d(f1 * f2, f3, f * f2);
    }

}
