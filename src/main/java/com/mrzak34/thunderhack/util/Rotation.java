package com.mrzak34.thunderhack.util;



public class Rotation{
    public float yaw;
    public float pitch;

    public float getPitch() {
        return this.pitch;
    }


    public Rotation c(float var1, float var2) {
        return new Rotation(var1, var2);
    }


    public static Rotation c(Rotation var0, float var1, float var2, int var3) {
        if ((var3 & 1) != 0) {
            var1 = var0.yaw;
        }

        if ((var3 & 2) != 0) {
            var2 = var0.pitch;
        }

        return var0.c(var1, var2);
    }

    public float meth2() {
        return this.yaw;
    }

    public Rotation(float var1, float var2) {
        this.yaw = var1;
        this.pitch = var2;
    }

    public void setPitch(float var1) {
        this.pitch = var1;
    }

    public boolean c(Rotation var1) {
        return var1.yaw == this.yaw && var1.pitch == this.pitch;
    }



    public int hashCode() {
        return Float.hashCode(this.yaw) * 31 + Float.hashCode(this.pitch);
    }




    public boolean equals(Object var1) {
        if (this != var1) {
            if (var1 instanceof Rotation) {
                Rotation var2 = (Rotation)var1;
                return Float.compare(this.yaw, var2.yaw) == 0 && Float.compare(this.pitch, var2.pitch) == 0;
            }

            return false;
        } else {
            return true;
        }
    }
}