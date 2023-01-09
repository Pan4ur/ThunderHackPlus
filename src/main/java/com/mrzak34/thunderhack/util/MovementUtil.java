package com.mrzak34.thunderhack.util;

import com.mrzak34.thunderhack.event.events.EventMove;
import net.minecraft.entity.player.*;
import net.minecraft.util.MovementInput;
import net.minecraft.util.math.*;
import net.minecraft.client.*;
import net.minecraft.entity.*;
import java.util.*;
import net.minecraft.potion.*;

public class MovementUtil implements Util
{

    public static boolean isMoving()
    {
        return mc.player.moveForward != 0.0 || mc.player.moveStrafing != 0.0;
    }
    public static void strafe(EventMove event, double speed)
    {
        if (isMoving())
        {
            double[] strafe = strafe(speed);
            event.set_x(strafe[0]);
            event.set_z(strafe[1]);
        }
        else
        {
            event.set_x(0.0);
            event.set_z(0.0);
        }
    }

    public static double getSpeed() {
        return Math.hypot(mc.player.motionX, mc.player.motionZ);
    }

    public static double[] strafe(double speed)
    {
        return strafe(mc.player, speed);
    }

    public static double[] strafe(Entity entity, double speed)
    {
        return strafe(entity, mc.player.movementInput, speed);
    }

    public static double[] strafe(Entity entity,
                                  MovementInput movementInput,
                                  double speed)
    {
        float moveForward = movementInput.moveForward;
        float moveStrafe  = movementInput.moveStrafe;
        float rotationYaw = entity.prevRotationYaw
                + (entity.rotationYaw - entity.prevRotationYaw)
                * mc.getRenderPartialTicks();

        if (moveForward != 0.0f)
        {
            if (moveStrafe > 0.0f)
            {
                rotationYaw += ((moveForward > 0.0f) ? -45 : 45);
            }
            else if (moveStrafe < 0.0f)
            {
                rotationYaw += ((moveForward > 0.0f) ? 45 : -45);
            }
            moveStrafe = 0.0f;
            if (moveForward > 0.0f)
            {
                moveForward = 1.0f;
            }
            else if (moveForward < 0.0f)
            {
                moveForward = -1.0f;
            }
        }

        double posX =
                moveForward * speed * -Math.sin(Math.toRadians(rotationYaw))
                        + moveStrafe * speed * Math.cos(Math.toRadians(rotationYaw));
        double posZ =
                moveForward * speed * Math.cos(Math.toRadians(rotationYaw))
                        - moveStrafe * speed * -Math.sin(Math.toRadians(rotationYaw));

        return new double[] {posX, posZ};
    }



    public static double[] forward(final double d) {
        float f = Minecraft.getMinecraft().player.movementInput.moveForward;
        float f2 = Minecraft.getMinecraft().player.movementInput.moveStrafe;
        float f3 = Minecraft.getMinecraft().player.prevRotationYaw + (Minecraft.getMinecraft().player.rotationYaw - Minecraft.getMinecraft().player.prevRotationYaw) * Minecraft.getMinecraft().getRenderPartialTicks();
        if (f != 0.0f) {
            if (f2 > 0.0f) {
                f3 += ((f > 0.0f) ? -45 : 45);
            }
            else if (f2 < 0.0f) {
                f3 += ((f > 0.0f) ? 45 : -45);
            }
            f2 = 0.0f;
            if (f > 0.0f) {
                f = 1.0f;
            }
            else if (f < 0.0f) {
                f = -1.0f;
            }
        }
        final double d2 = Math.sin(Math.toRadians(f3 + 90.0f));
        final double d3 = Math.cos(Math.toRadians(f3 + 90.0f));
        final double d4 = f * d * d3 + f2 * d * d2;
        final double d5 = f * d * d2 - f2 * d * d3;
        return new double[] { d4,  d5 };
    }

    public static boolean isMoving(final EntityLivingBase entityLivingBase) {
        return entityLivingBase.moveForward != 0.0f || entityLivingBase.moveStrafing != 0.0f;
    }

}