package com.mrzak34.thunderhack.util;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;

import static com.mrzak34.thunderhack.util.Util.mc;

public class dV {
    public static boolean a(Entity entity) {
        return entity.motionX != 0.0 || entity.motionZ != 0.0 || entity.motionY != 0.0;
    }

    public static boolean a() {
        return mc.player.moveForward != 0.0f || mc.player.moveStrafing != 0.0f;
    }

    public static void a(int n2) {
        mc.player.setPosition(mc.player.posX, mc.player.posY + (double)n2, mc.player.posZ);
    }

    public static float b() {
        return (float)Math.sqrt(mc.player.motionX * mc.player.motionX + mc.player.motionZ * mc.player.motionZ);
    }

    public static void c() {
        dV.a(dV.b());
    }

    public static void a(float f2) {
        float f3 = 0;
        float f4 = mc.player.moveForward > 0.0f ? 1.0f : (f3 = mc.player.moveForward < 0.0f ? -1.0f : 0.0f);
        float f5 = mc.player.moveStrafing > 0.0f ? 1.0f : (mc.player.moveStrafing < 0.0f ? -1.0f : 0.0f);
        float f6 = mc.player.rotationYaw;
        float f7 = 90.0f * f5;
        float f8 = f6 - (f7 *= f3 != 0.0f ? f3 * 0.5f : 1.0f);
        f8 -= (float)(f3 < 0.0f ? 180 : 0);
        f8 = (float)Math.toRadians(f8);
        float f9 = mc.gameSettings.mouseSensitivity;
        float f10 = f9 * 0.6f + 0.2f;
        float f11 = f10 * f10 * f10 * 1.2f;
        f8 -= f8 % f11;
        double d2 = -Math.sin(f8) * (double)f2;
        double d3 = Math.cos(f8) * (double)f2;
        mc.player.motionX = d2;
        mc.player.motionZ = d3;
    }

    public static void a(float f2, double d2) {
        if (dV.a()) {
            dV.b(f2, d2);
        }
    }

    public static void b(float f2, double d2) {
        double d3 = -Math.sin(f2) * d2;
        double d4 = Math.cos(f2) * d2;
        mc.player.motionX = d3;
        mc.player.motionZ = d4;
    }

    public static float b(float f2) {
        float f3 = dV.d();
        float f4 = dV.e();
        float f5 = f2;
        float f6 = 90.0f * f4;
        float f7 = f5 - (f6 *= f3 != 0.0f ? f3 * 0.5f : 1.0f);
        f7 -= (float)(f3 < 0.0f ? 180 : 0);
        f7 = (float)Math.toRadians(f7);
        float f8 = mc.gameSettings.mouseSensitivity;
        float f9 = f8 * 0.6f + 0.2f;
        float f10 = f9 * f9 * f9 * 1.2f;
        f7 -= f7 % f10;
        return f7;
    }

    public static float d() {
        return dV.c(mc.player.movementInput.moveForward);
    }

    public static float e() {
        return dV.c(mc.player.movementInput.moveStrafe);
    }

    private static float c(float f2) {
        return f2 > 0.0f ? 1.0f : (f2 < 0.0f ? -1.0f : 0.0f);
    }
}