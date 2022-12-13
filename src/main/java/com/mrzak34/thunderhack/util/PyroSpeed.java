package com.mrzak34.thunderhack.util;

import com.mrzak34.thunderhack.event.events.EventMove;
import net.minecraft.entity.Entity;
import net.minecraft.util.MovementInput;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.List;

import static com.mrzak34.thunderhack.util.Util.mc;

public class PyroSpeed {


    public static AxisAlignedBB Method5403(double d) {
        int n;
        int n2;
        double[] arrd = Method732(0.2f);
        double d2 = arrd[0];
        double d3 = arrd[1];
        double d4 = d2;
        double d6 = d3;
        List<AxisAlignedBB> list = mc.world.getCollisionBoxes((Entity)mc.player, mc.player.getEntityBoundingBox().expand(d2, 0.0, d3));
        AxisAlignedBB axisAlignedBB = mc.player.getEntityBoundingBox();
        if (d2 != 0.0) {
            n2 = list.size();
            for (n = 0; n < n2; ++n) {
                d2 = list.get(n).calculateXOffset(axisAlignedBB, d2);
            }
            if (d2 != 0.0) {
                axisAlignedBB = axisAlignedBB.offset(d2, 0.0, 0.0);
            }
        }
        if (d3 != 0.0) {
            n2 = list.size();
            for (n = 0; n < n2; ++n) {
                d3 = list.get(n).calculateZOffset(axisAlignedBB, d3);
            }
            if (d3 != 0.0) {
                axisAlignedBB = axisAlignedBB.offset(0.0, 0.0, d3);
            }
        }
        double d7 = d2;
        double d9 = d3;
        AxisAlignedBB axisAlignedBB2 = mc.player.getEntityBoundingBox();
        double d10 = d;
        List<AxisAlignedBB> list2 = mc.world.getCollisionBoxes((Entity)mc.player, axisAlignedBB.expand(d4, d10, d6));
        AxisAlignedBB axisAlignedBB3 = axisAlignedBB;
        AxisAlignedBB axisAlignedBB4 = axisAlignedBB3.expand(d4, 0.0, d6);
        double d11 = d10;
        for (AxisAlignedBB axisAlignedBB7 : list2) {
            d11 = axisAlignedBB7.calculateYOffset(axisAlignedBB4, d11);
        }
        axisAlignedBB3 = axisAlignedBB3.offset(0.0, d11, 0.0);
        double d12 = d4;
        for (AxisAlignedBB axisAlignedBB1 : list2) {
            d12 = axisAlignedBB1.calculateXOffset(axisAlignedBB3, d12);
        }
        axisAlignedBB3 = axisAlignedBB3.offset(d12, 0.0, 0.0);
        double d13 = d6;
        for (AxisAlignedBB element : list2) {
            d13 = element.calculateZOffset(axisAlignedBB3, d13);
        }
        axisAlignedBB3 = axisAlignedBB3.offset(0.0, 0.0, d13);
        AxisAlignedBB axisAlignedBB5 = axisAlignedBB;
        double d14 = d10;
        for (AxisAlignedBB item : list2) {
            d14 = item.calculateYOffset(axisAlignedBB5, d14);
        }
        axisAlignedBB5 = axisAlignedBB5.offset(0.0, d14, 0.0);
        double d15 = d4;
        for (AxisAlignedBB value : list2) {
            d15 = value.calculateXOffset(axisAlignedBB5, d15);
        }
        axisAlignedBB5 = axisAlignedBB5.offset(d15, 0.0, 0.0);
        double d16 = d6;
        for (AxisAlignedBB bb : list2) {
            d16 = bb.calculateZOffset(axisAlignedBB5, d16);
        }
        axisAlignedBB5 = axisAlignedBB5.offset(0.0, 0.0, d16);
        double d17 = d12 * d12 + d13 * d13;
        double d18 = d15 * d15 + d16 * d16;
        AxisAlignedBB axisAlignedBB6;
        if (d17 > d18) {
            d2 = d12;
            d3 = d13;
            d10 = -d11;
            axisAlignedBB6 = axisAlignedBB3;
        } else {
            d2 = d15;
            d3 = d16;
            d10 = -d14;
            axisAlignedBB6 = axisAlignedBB5;
        }
        for (AxisAlignedBB alignedBB : list2) {
            d10 = alignedBB.calculateYOffset(axisAlignedBB6, d10);
        }
        axisAlignedBB6 = axisAlignedBB6.offset(0.0, d10, 0.0);
        if (d7 * d7 + d9 * d9 >= d2 * d2 + d3 * d3) {
            axisAlignedBB6 = axisAlignedBB2;
        }
        return axisAlignedBB6;
    }
    public static boolean isMovingClient() {
        return mc.player != null && (mc.player.movementInput.moveForward != 0.0f || mc.player.movementInput.moveStrafe != 0.0f);
    }
    public static void Method744(EventMove event, double d) {
        MovementInput movementInput = mc.player.movementInput;
        double d2 = movementInput.moveForward;
        double d3 = movementInput.moveStrafe;

        float f = mc.player.rotationYaw;
        if (d2 == 0.0 && d3 == 0.0) {
            event.set_x(0);
            event.set_z(0);
        } else {
            if (d2 != 0.0) {
                if (d3 > 0.0) {
                    f += (float)(d2 > 0.0 ? -45 : 45);
                } else if (d3 < 0.0) {
                    f += (float)(d2 > 0.0 ? 45 : -45);
                }
                d3 = 0.0;
                if (d2 > 0.0) {
                    d2 = 1.0;
                } else if (d2 < 0.0) {
                    d2 = -1.0;
                }
            }
            event.set_x(d2 * d * Math.cos(Math.toRadians(f + 90.0f)) + d3 * d * Math.sin(Math.toRadians(f + 90.0f)));
            event.set_z(d2 * d * Math.sin(Math.toRadians(f + 90.0f)) - d3 * d * Math.cos(Math.toRadians(f + 90.0f)));
        }
    }

    public static double Method5402(double d) {
        if (!mc.player.onGround) return 0.0;
        if (!mc.player.collidedHorizontally) return 0.0;
        if (mc.player.fallDistance != 0.0f) return 0.0;
        if (mc.player.isInWater()) return 0.0;
        if (mc.player.isInLava()) return 0.0;
        if (mc.player.isOnLadder()) return 0.0;
        if (mc.player.movementInput.jump) return 0.0;
        if (mc.player.movementInput.sneak) return 0.0;
        return Method5403(d).minY - mc.player.getEntityBoundingBox().minY;
    }

    public static double Method718() {
        float f = mc.player.rotationYaw;
        if (mc.player.moveForward < 0.0f) {
            f += 180.0f;
        }
        float f2 = 1.0f;
        if (mc.player.moveForward < 0.0f) {
            f2 = -0.5f;
        } else if (mc.player.moveForward > 0.0f) {
            f2 = 0.5f;
        }
        if (mc.player.moveStrafing > 0.0f) {
            f -= 90.0f * f2;
        }
        if (mc.player.moveStrafing < 0.0f) {
            f += 90.0f * f2;
        }
        return Math.toRadians(f);
    }

    public static double[] Method732(double d) {
        if (!isMovingClient()) {
            return null;
        }
        double d2 = Method718();
        double d3 = -Math.sin(d2) * d;
        double d4 = Math.cos(d2) * d;
        return new double[]{d3, d4};
    }
}
