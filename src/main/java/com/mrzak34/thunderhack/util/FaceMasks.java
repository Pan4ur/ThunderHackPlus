package com.mrzak34.thunderhack.util;

import net.minecraft.util.EnumFacing;

import java.util.HashMap;

public final class FaceMasks {

    public static final HashMap<EnumFacing, Integer> FACEMAP = new HashMap<>();

    static {
        FACEMAP.put(EnumFacing.DOWN, Quad.DOWN);
        FACEMAP.put(EnumFacing.WEST, Quad.WEST);
        FACEMAP.put(EnumFacing.NORTH, Quad.NORTH);
        FACEMAP.put(EnumFacing.SOUTH, Quad.SOUTH);
        FACEMAP.put(EnumFacing.EAST, Quad.EAST);
        FACEMAP.put(EnumFacing.UP, Quad.UP);
    }

    public static final class Quad {
        public static final int DOWN = 0x01;
        public static final int UP = 0x02;
        public static final int NORTH = 0x04;
        public static final int SOUTH = 0x08;
        public static final int WEST = 0x10;
        public static final int EAST = 0x20;
        public static final int ALL = DOWN | UP | NORTH | SOUTH | WEST | EAST;
    }
}