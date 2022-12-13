package com.mrzak34.thunderhack.util.DeadCodeUtils;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class feh {
    public BlockPos Field472;
    public EnumFacing Field473;
    public Vec3d Field474;
    public fex Field475 = null;

    public feh(BlockPos blockPos, EnumFacing enumFacing) {
        this.Field472 = blockPos;
        this.Field473 = enumFacing;
        this.Field474 = new Vec3d((double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5);
    }

    public feh(BlockPos blockPos, EnumFacing enumFacing, Vec3d vec3d, fex fex2) {
        this.Field472 = blockPos;
        this.Field473 = enumFacing;
        this.Field474 = vec3d;
        this.Field475 = fex2;
    }

    public BlockPos Method782() {
        return this.Field472;
    }

    public EnumFacing Method783() {
        return this.Field473;
    }

    /*
    public static feh Method784(BlockPos blockPos) {
        if (feg.Method691(blockPos.add(0, -1, 0))) {
            return new feh(blockPos.add(0, -1, 0), EnumFacing.UP);
        }
        if (feg.Method691(blockPos.add(-1, 0, 0))) {
            return new feh(blockPos.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (feg.Method691(blockPos.add(1, 0, 0))) {
            return new feh(blockPos.add(1, 0, 0), EnumFacing.WEST);
        }
        if (feg.Method691(blockPos.add(0, 0, -1))) {
            return new feh(blockPos.add(0, 0, -1), EnumFacing.SOUTH);
        }
        if (feg.Method691(blockPos.add(0, 0, 1))) {
            return new feh(blockPos.add(0, 0, 1), EnumFacing.NORTH);
        }
        return null;
    }


     */
    public Vec3d Method785() {
        return this.Field474;
    }

    public feh(BlockPos blockPos, EnumFacing enumFacing, Vec3d vec3d) {
        this.Field472 = blockPos;
        this.Field473 = enumFacing;
        this.Field474 = vec3d;
    }

    public fex Method786() {
        return this.Field475;
    }
}