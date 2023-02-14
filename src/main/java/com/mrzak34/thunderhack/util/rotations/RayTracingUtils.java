package com.mrzak34.thunderhack.util.rotations;


import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.command.Command;
import com.mrzak34.thunderhack.modules.combat.Aura;
import com.mrzak34.thunderhack.util.math.MathUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.*;

import javax.annotation.Nullable;
import javax.vecmath.Vector2f;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.mrzak34.thunderhack.util.Util.mc;

public class RayTracingUtils {


    public static ArrayList<Vec3d> getHitBoxPoints(Vec3d position, float fakeBoxScale){



        float head_height = 1.6f + interpolateRandom(-0.4f,0.2f);
        float chest_height = 0.8f + interpolateRandom(-0.2f,0.2f);
        float leggs_height = 0.225f + interpolateRandom(-0.1f,0.1f);

        Vec3d head1 = position.add(-fakeBoxScale, head_height, fakeBoxScale);
        Vec3d head2 = position.add(0, head_height, fakeBoxScale);
        Vec3d head3 = position.add(fakeBoxScale, head_height, fakeBoxScale);
        Vec3d head4 = position.add(-fakeBoxScale, head_height, 0);
        Vec3d head5 = position.add(fakeBoxScale, head_height, 0);
        Vec3d head6 = position.add(-fakeBoxScale, head_height, -fakeBoxScale);
        Vec3d head7 = position.add(0, head_height, -fakeBoxScale);
        Vec3d head8 = position.add(fakeBoxScale, head_height, -fakeBoxScale);

        Vec3d chest1 = position.add(-fakeBoxScale, chest_height, fakeBoxScale);
        Vec3d chest2 = position.add(0, chest_height, fakeBoxScale);
        Vec3d chest3 = position.add(fakeBoxScale, chest_height, fakeBoxScale);
        Vec3d chest4 = position.add(-fakeBoxScale,chest_height, 0);
        Vec3d chest5 = position.add(fakeBoxScale, chest_height, 0);
        Vec3d chest6 = position.add(-fakeBoxScale, chest_height, -fakeBoxScale);
        Vec3d chest7 = position.add(0, chest_height, -fakeBoxScale);
        Vec3d chest8 = position.add(fakeBoxScale, chest_height, -fakeBoxScale);

        Vec3d legs1 = position.add(-fakeBoxScale, leggs_height, fakeBoxScale);
        Vec3d legs2 = position.add(0, leggs_height, fakeBoxScale);
        Vec3d legs3 = position.add(fakeBoxScale, leggs_height, fakeBoxScale);
        Vec3d legs4 = position.add(-fakeBoxScale,leggs_height, 0);
        Vec3d legs5 = position.add(fakeBoxScale, leggs_height, 0);
        Vec3d legs6 = position.add(-fakeBoxScale, leggs_height, -fakeBoxScale);
        Vec3d legs7 = position.add(0, leggs_height, -fakeBoxScale);
        Vec3d legs8 = position.add(fakeBoxScale, leggs_height, -fakeBoxScale);

        return new ArrayList<>(Arrays.asList(
                head1,  head2,  head3,  head4,  head5,  head6,  head7,  head8,
                chest1, chest2, chest3, chest4, chest5, chest6, chest7, chest8,
                legs1,  legs2,  legs3,  legs4,  legs5,  legs6,  legs7,  legs8
        ));
    }

    public static float interpolateRandom(float var0, float var1) {
        return (float) (var0 + (var1 - var0) * Math.random());
    }


    public static Entity getPointedEntity(Vector2f rot, double dst, boolean walls, Entity target) {
        double d0 = dst;
        RayTraceResult objectMouseOver = rayTrace(d0, rot.x, rot.y, walls);
        Vec3d vec3d = mc.player.getPositionEyes(1);
        boolean flag = false;
        double d1 = d0;
        if (objectMouseOver != null) {
            d1 = objectMouseOver.hitVec.distanceTo(vec3d);
        }
        Vec3d vec3d1 = getLook(rot.x, rot.y);
        Vec3d vec3d2 = vec3d.add(vec3d1.x * d0, vec3d1.y * d0, vec3d1.z * d0);
        Entity pointedEntity = null;
        Vec3d vec3d3 = null;
        double d2 = d1;
        Entity entity1 = target;
        AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().expand(entity1.getCollisionBorderSize(),entity1.getCollisionBorderSize(),entity1.getCollisionBorderSize());
        RayTraceResult raytraceresult = axisalignedbb.calculateIntercept(vec3d, vec3d2);
        if (axisalignedbb.contains(vec3d)) {
            if (d2 >= 0.0D) {
                pointedEntity = entity1;
                vec3d3 = raytraceresult == null ? vec3d : raytraceresult.hitVec;
                d2 = 0.0D;
            }
        } else if (raytraceresult != null) {
            double d3 = vec3d.distanceTo(raytraceresult.hitVec);

            if (d3 < d2 || d2 == 0.0D) {
                boolean flag1 = false;
                if (!flag1 && entity1.getLowestRidingEntity() == mc.player.getLowestRidingEntity()) {
                    if (d2 == 0.0D) {
                        pointedEntity = entity1;
                        vec3d3 = raytraceresult.hitVec;
                    }
                } else {
                    pointedEntity = entity1;
                    vec3d3 = raytraceresult.hitVec;
                    d2 = d3;
                }
            }
        }
        if (pointedEntity != null && flag && vec3d.distanceTo(vec3d3) > dst) {
            pointedEntity = null;
            objectMouseOver = new RayTraceResult(RayTraceResult.Type.MISS, vec3d3, null, new BlockPos(vec3d3));
        }
        if (pointedEntity != null && (d2 < d1 || objectMouseOver == null)) {
            objectMouseOver = new RayTraceResult(pointedEntity, vec3d3);
        }
        return objectMouseOver != null ? (objectMouseOver.entityHit instanceof Entity ? (Entity) objectMouseOver.entityHit : null) : null;
    }

    public static RayTraceResult rayTrace(double blockReachDistance, float yaw, float pitch, boolean walls) {
        if (!walls) {return null;}
        Vec3d vec3d = mc.player.getPositionEyes(1);
        Vec3d vec3d1 = getLook(yaw, pitch);
        Vec3d vec3d2 = vec3d.add(vec3d1.x * blockReachDistance, vec3d1.y * blockReachDistance, vec3d1.z * blockReachDistance);
        return mc.world.rayTraceBlocks(vec3d, vec3d2, true, true, true);
    }

    static Vec3d getVectorForRotation(float pitch, float yaw) {
        float f = MathHelper.cos(-yaw * 0.017453292F - (float) Math.PI);
        float f1 = MathHelper.sin(-yaw * 0.017453292F - (float) Math.PI);
        float f2 = -MathHelper.cos(-pitch * 0.017453292F);
        float f3 = MathHelper.sin(-pitch * 0.017453292F);
        return new Vec3d((double) (f1 * f2), (double) f3, (double) (f * f2));
    }

    public static Entity getMouseOver(Entity target, float yaw, float pitch, double distance, boolean ignoreWalls) {
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


    public static RayTraceResult rayTrace(double blockReachDistance, float yaw, float pitch) {
        Vec3d vec3d = Minecraft.getMinecraft().player.getPositionEyes(1);
        Vec3d vec3d1 = getVectorForRotation(pitch, yaw);
        Vec3d vec3d2 = vec3d.add(vec3d1.x * blockReachDistance, vec3d1.y * blockReachDistance, vec3d1.z * blockReachDistance);
        return Minecraft.getMinecraft().world.rayTraceBlocks(vec3d, vec3d2, true, true, true);
    }

    static Vec3d getLook(float yaw, float pitch) {
        return getVectorForRotation(pitch, yaw);
    }
}
