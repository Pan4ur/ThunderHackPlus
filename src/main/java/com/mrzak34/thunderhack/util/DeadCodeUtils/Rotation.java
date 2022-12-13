package com.mrzak34.thunderhack.util.DeadCodeUtils;

import net.minecraft.entity.player.EntityPlayer;

import static com.mrzak34.thunderhack.util.Util.mc;

public class Rotation {
    public float Field4808;
    public float Field4809;

    public float Method6933() {
        return this.Field4809;
    }

    public void Method6934(float f) {
        this.Field4808 = f;
    }

    
    public Rotation Method6935(float f, float f2) {
        return new Rotation(f, f2);
    }

    public float Method6936() {
        return this.Field4809;
    }

    public static Rotation Method6937(Rotation rotation, float f, float f2, int n, Object object) {
        if ((n & 1) != 0) {
            f = rotation.Field4808;
        }
        if ((n & 2) != 0) {
            f2 = rotation.Field4809;
        }
        return rotation.Method6935(f, f2);
    }

    public float Method6938() {
        return this.Field4808;
    }

    public Rotation(float f, float f2) {
        this.Field4808 = f;
        this.Field4809 = f2;
    }

    public void Method6939(float f) {
        this.Field4809 = f;
    }

    public boolean Method6940( Rotation rotation) {
        return rotation.Field4808 == this.Field4808 && rotation.Field4809 == this.Field4809;
    }

    public void Method6941( EntityPlayer entityPlayer) {
        block3: {
            block2: {
                float f = this.Field4808;
                boolean bl = false;
                if (Float.isNaN(f)) break block2;
                f = this.Field4809;
                bl = false;
                if (!Float.isNaN(f)) break block3;
            }
            return;
        }
        this.Method6943(mc.gameSettings.mouseSensitivity);
        entityPlayer.rotationYaw = this.Field4808;
        entityPlayer.rotationPitch = this.Field4809;
    }

    public int hashCode() {
        return Float.hashCode(this.Field4808) * 31 + Float.hashCode(this.Field4809);
    }

    public float Method6942() {
        return this.Field4808;
    }

    
    public String toString() {
        return "Rotation(yaw=" + this.Field4808 + ", pitch=" + this.Field4809 + ")";
    }

    public void Method6943(float f) {
        float f2 = f * 0.6f + 0.2f;
        float f3 = f2 * f2 * f2 * 1.2f;
        this.Field4808 -= this.Field4808 % f3;
        this.Field4809 -= this.Field4809 % f3;
    }

    public boolean equals( Object object) {
        block3: {
            block2: {
                if (this == object) break block2;
                if (!(object instanceof Rotation)) break block3;
                Rotation rotation = (Rotation)object;
                if (Float.compare(this.Field4808, rotation.Field4808) != 0 || Float.compare(this.Field4809, rotation.Field4809) != 0) break block3;
            }
            return true;
        }
        return false;
    }
}