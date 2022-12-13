package com.mrzak34.thunderhack.util.DeadCodeUtils;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;

public class fex {
    
    public Vec3d Field536;
    
    public Rotation Field537;
    
    public EnumFacing Field538;

    
    public EnumFacing Method889() {
        return this.Field538;
    }

    
    public Vec3d Method890() {
        return this.Field536;
    }
    /*
    public fex(Vec3d vec3d, Rotation rotation, EnumFacing enumFacing, int n) {
        if ((n & 4) != 0) {
            enumFacing = null;
        }
        this(vec3d, rotation, enumFacing);
    }


     */
    public fex( Vec3d vec3d,  Rotation rotation) {
        this(vec3d, rotation, null);
    }

    
    public Rotation Method891() {
        return this.Field537;
    }

    
    public Vec3d Method892() {
        return this.Field536;
    }

    public int hashCode() {
        Vec3d vec3d = this.Field536;
        Rotation rotation = this.Field537;
        EnumFacing enumFacing = this.Field538;
        return ((vec3d != null ? vec3d.hashCode() : 0) * 31 + (rotation != null ? ((Object)rotation).hashCode() : 0)) * 31 + (enumFacing != null ? enumFacing.hashCode() : 0);
    }

    /*
    public boolean equals( Object object) {
        block3: {
            block2: {
                if (this == object) break block2;
                if (!(object instanceof fex)) break block3;
                fex fex2 = (fex)object;
                if (!Intrinsics.Method6572((Object)this.Field536, (Object)fex2.Field536)) break block3;
                if (!Intrinsics.Method6572(this.Field537, fex2.Field537) || !Intrinsics.Method6572((Object)this.Field538, (Object)fex2.Field538)) break block3;
            }
            return true;
        }
        return false;
    }



     */
    public EnumFacing Method893() {
        return this.Field538;
    }

    
    public Rotation Method894() {
        return this.Field537;
    }

    
    public String toString() {
        return "VecRotation(vec=" + (Object)this.Field536 + ", rotation=" + this.Field537 + ", sideHit=" + (Object)this.Field538 + ")";
    }

    public static fex Method895(fex fex2, Vec3d vec3d, Rotation rotation, EnumFacing enumFacing, int n, Object object) {
        if ((n & 1) != 0) {
            vec3d = fex2.Field536;
        }
        if ((n & 2) != 0) {
            rotation = fex2.Field537;
        }
        if ((n & 4) != 0) {
            enumFacing = fex2.Field538;
        }
        return fex2.Method896(vec3d, rotation, enumFacing);
    }

    
    public fex Method896( Vec3d vec3d,  Rotation rotation,  EnumFacing enumFacing) {
        return new fex(vec3d, rotation, enumFacing);
    }

    public fex( Vec3d vec3d,  Rotation rotation,  EnumFacing enumFacing) {
        this.Field536 = vec3d;
        this.Field537 = rotation;
        this.Field538 = enumFacing;
    }
}