package com.mrzak34.thunderhack.util.DeadCodeUtils;

import net.minecraft.network.play.client.CPacketPlayer;

import java.lang.reflect.Field;

import static com.mrzak34.thunderhack.util.Util.mc;


public class ef {
    private static double e;
    private static double f;
    private static double g;
    public static boolean a;
    private static double[] h;
    private static float i;
    private static float j;
    public static boolean b;
    private static float[] k;
    private static boolean l;
    public static boolean c;
    public static boolean d;


    public static boolean a(Object object) {
        if (object instanceof CPacketPlayer) {
            CPacketPlayer cPacketPlayer = (CPacketPlayer)object;
            try {

                double field = cPacketPlayer.x;
                double field2 = cPacketPlayer.y;
                double field3 = cPacketPlayer.z;
                boolean field4 = cPacketPlayer.moving;
                boolean field5 = cPacketPlayer.onGround;
                double field6 = cPacketPlayer.yaw;
                double field7 = cPacketPlayer.pitch;
                boolean field8 = cPacketPlayer.rotating;

                if (a) {
                    field = e;
                    field2 = f;
                    field3 = g;
                    field4 = true;
                }
                if (b) {
                    field6 = i;
                    field7 = j;
                    field8 = true;
                }
                if (c) {
                    field5=l;
                }
                d = field5;
                if (field4) {
                    h = new double[]{field, field2, field3};
                }
                if (field8) {
                    k = new float[]{(float) field6, (float) field7};
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        return true;
    }

    public static double a() {
        return h[0];
    }

    public static double b() {
        return h[1];
    }

    public static double c() {
        return h[2];
    }

    public static float d() {
        return k[0];
    }

    public static float e() {
        return k[1];
    }

    public static float[] f() {
        return k;
    }

    public static double[] g() {
        return h;
    }

    public static boolean h() {
        return d;
    }

    public static void a(boolean bl) {
        l = bl;
        c = true;
    }

    public static void a(double d2, double d3, double d4, float f2, float f3) {
        ef.a(d2, d3, d4);
        ef.a(f2, f3);
    }

    public static void a(float[] fArray) {
        ef.a(fArray[0], fArray[1]);
        mc.player.renderYawOffset = fArray[0];
        mc.player.rotationYawHead = fArray[0];
    }

    public static void a(float f2, float f3) {
        if (Double.isNaN(f2) || Double.isNaN(f3)) {
            return;
        }
        i = f2;
        j = f3;
        b = true;
    }

    public static void a(double d2, double d3, double d4) {
        if (Double.isNaN(d2) || Double.isNaN(d3) || Double.isNaN(d4)) {
            return;
        }
        e = d2;
        f = d3;
        g = d4;
        a = true;
    }

    public static void i() {
        ef.j();
        ef.k();
        ef.l();
    }

    public static void j() {
        b = false;
        i = 0.0f;
        j = 0.0f;
    }

    public static void k() {
        a = false;
        e = 0.0;
        f = 0.0;
        g = 0.0;
    }

    public static void l() {
        c = false;
        l = mc.player.onGround;
    }

    static {
        h = new double[]{0.0, 0.0, 0.0};
        k = new float[]{0.0f, 0.0f};
    }
}