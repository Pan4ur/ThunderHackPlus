package com.mrzak34.thunderhack.util.DeadCodeUtils;

import java.util.List;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import net.minecraft.entity.Entity;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

import static com.mrzak34.thunderhack.util.Util.mc;


public class RaytraceUtils {
    public static double a() {
        return (double)mc.playerController.getBlockReachDistance() - 1.5;
    }

    public static double b() {
        double d2 = RaytraceUtils.a();
        return d2;
    }

    public static RayTraceResult a(double d2) {
        return RaytraceUtils.a(d2, mc.getRenderPartialTicks());
    }

    public static RayTraceResult a(double d2, float f2, float f3) {
        return RaytraceUtils.a(d2, f2, f3, mc.getRenderPartialTicks());
    }

    public static Entity b(double d2) {
        return RaytraceUtils.b(d2, mc.getRenderPartialTicks());
    }

    public static Entity b(double d2, float f2, float f3) {
        return RaytraceUtils.b(d2, f2, f3, mc.getRenderPartialTicks());
    }

    public static RayTraceResult a(double d2, float f2) {
        Entity entity = mc.getRenderViewEntity();
        Vec3d vec3d = entity.getPositionEyes(f2);
        Vec3d vec3d2 = entity.getLook(f2);
        Vec3d vec3d3 = vec3d.add(vec3d2.x * d2, vec3d2.y * d2, vec3d2.z * d2);
        return mc.world.rayTraceBlocks(vec3d, vec3d3, false, false, true);
    }

    public static RayTraceResult a(double d2, float f2, float f3, float f4) {
        Entity entity = mc.getRenderViewEntity();
        Vec3d vec3d = entity.getPositionEyes(f4);
        Vec3d vec3d2 = RaytraceUtils.a(f3, f2);
        Vec3d vec3d3 = vec3d.add(vec3d2.x * d2, vec3d2.y * d2, vec3d2.z * d2);
        return mc.world.rayTraceBlocks(vec3d, vec3d3, false, false, true);
    }

    public static Entity b(double d2, float f2) {
        Entity entity = mc.getRenderViewEntity();
        Vec3d vec3d = entity.getPositionEyes(f2);
        Vec3d vec3d2 = entity.getLook(f2);
        Vec3d vec3d3 = vec3d.add(vec3d2.x * d2, vec3d2.y * d2, vec3d2.z * d2);
        return RaytraceUtils.a(entity, d2, vec3d, vec3d2, vec3d3);
    }

    public static Entity b(double d2, float f2, float f3, float f4) {
        Entity entity = mc.getRenderViewEntity();
        Vec3d vec3d = entity.getPositionEyes(f4);
        Vec3d vec3d2 = RaytraceUtils.a(f3, f2);
        Vec3d vec3d3 = vec3d.add(vec3d2.x * d2, vec3d2.y * d2, vec3d2.z * d2);
        return RaytraceUtils.a(entity, d2, vec3d, vec3d2, vec3d3);
    }

    public static Entity a(Entity entity, double d2, Vec3d vec3d, Vec3d vec3d2, Vec3d vec3d3) {
        Entity entity2 = null;
        try {
            List list = mc.world.getEntitiesInAABBexcluding(entity, entity.getEntityBoundingBox().expand(vec3d2.x * d2, vec3d2.y * d2, vec3d2.z * d2).grow(1.0, 1.0, 1.0), Predicates.and((Predicate)EntitySelectors.NOT_SPECTATING, (Predicate)new H()));
            for (int i2 = 0; i2 < list.size(); ++i2) {
                double d3;
                Entity entity3 = (Entity)list.get(i2);
                AxisAlignedBB axisAlignedBB = entity3.getEntityBoundingBox().grow((double)entity3.getCollisionBorderSize());
                RayTraceResult rayTraceResult = axisAlignedBB.calculateIntercept(vec3d, vec3d3);
                if (axisAlignedBB.contains(vec3d)) {
                    if (!(d2 >= 0.0)) continue;
                    entity2 = entity3;
                    d2 = 0.0;
                    continue;
                }
                if (rayTraceResult == null || !((d3 = vec3d.distanceTo(rayTraceResult.hitVec)) < d2) && d2 != 0.0) continue;
                if (entity3.getLowestRidingEntity() == entity.getLowestRidingEntity() && !entity3.canRiderInteract()) {
                    if (d2 != 0.0) continue;
                    entity2 = entity3;
                    continue;
                }
                entity2 = entity3;
            }
        }
        catch (Exception exception) {
        }
        return entity2;
    }

    public static Vec3d a(float f2, float f3) {
        float f4 = MathHelper.cos((float)(-f3 * ((float)Math.PI / 180) - (float)Math.PI));
        float f5 = MathHelper.sin((float)(-f3 * ((float)Math.PI / 180) - (float)Math.PI));
        float f6 = -MathHelper.cos((float)(-f2 * ((float)Math.PI / 180)));
        float f7 = MathHelper.sin((float)(-f2 * ((float)Math.PI / 180)));
        return new Vec3d((double)(f5 * f6), (double)f7, (double)(f4 * f6));
    }


}