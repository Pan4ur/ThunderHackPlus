package com.mrzak34.thunderhack.util;

import java.util.HashMap;
import java.util.Map;

public class MapColor {
    public static final MapColor TRANSPARENT;
    public static Map<Integer, MapColor> colors;
    final int r;
    final int g;
    final int b;

    private static MapColor toCol(final int r, final int g, final int b) {
        return new MapColor(r, g, b);
    }

    public MapColor(final int r, final int g, final int b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public int shaded(final byte shaderB) {
        double shader = 0.0;
        switch (shaderB) {
            case 0: {
                shader = 0.71;
                break;
            }
            case 1: {
                shader = 0.85;
                break;
            }
            case 2: {
                shader = 1.0;
                break;
            }
            case 3: {
                shader = 0.53;
                break;
            }
        }
        return 0xFF000000 | this.toInt(this.r, shader) << 16 | this.toInt(this.g, shader) << 8 | this.toInt(this.b, shader);
    }

    private int toInt(final int c, final double shader) {
        return (int)Math.round(c * shader);
    }

    static {
        TRANSPARENT = new MapColor(0, 0, 0) {
            @Override
            public int shaded(final byte b) {
                return 0;
            }
        };
        (MapColor.colors = new HashMap<Integer, MapColor>()).put(0, MapColor.TRANSPARENT);
        MapColor.colors.put(1, toCol(127, 178, 56));
        MapColor.colors.put(2, toCol(247, 233, 163));
        MapColor.colors.put(3, toCol(199, 199, 199));
        MapColor.colors.put(4, toCol(255, 0, 0));
        MapColor.colors.put(5, toCol(160, 160, 255));
        MapColor.colors.put(6, toCol(167, 167, 167));
        MapColor.colors.put(7, toCol(0, 124, 0));
        MapColor.colors.put(8, toCol(255, 255, 255));
        MapColor.colors.put(9, toCol(164, 168, 184));
        MapColor.colors.put(10, toCol(151, 109, 77));
        MapColor.colors.put(11, toCol(112, 112, 112));
        MapColor.colors.put(12, toCol(64, 64, 255));
        MapColor.colors.put(13, toCol(143, 119, 72));
        MapColor.colors.put(14, toCol(255, 252, 245));
        MapColor.colors.put(15, toCol(216, 127, 51));
        MapColor.colors.put(16, toCol(178, 76, 216));
        MapColor.colors.put(17, toCol(102, 153, 216));
        MapColor.colors.put(18, toCol(229, 229, 51));
        MapColor.colors.put(19, toCol(127, 204, 25));
        MapColor.colors.put(20, toCol(242, 127, 165));
        MapColor.colors.put(21, toCol(76, 76, 76));
        MapColor.colors.put(22, toCol(153, 153, 153));
        MapColor.colors.put(23, toCol(76, 127, 153));
        MapColor.colors.put(24, toCol(127, 63, 178));
        MapColor.colors.put(25, toCol(51, 76, 178));
        MapColor.colors.put(26, toCol(102, 76, 51));
        MapColor.colors.put(27, toCol(102, 127, 51));
        MapColor.colors.put(28, toCol(153, 51, 51));
        MapColor.colors.put(29, toCol(25, 25, 25));
        MapColor.colors.put(30, toCol(250, 238, 77));
        MapColor.colors.put(31, toCol(92, 219, 213));
        MapColor.colors.put(32, toCol(74, 128, 255));
        MapColor.colors.put(33, toCol(0, 217, 58));
        MapColor.colors.put(34, toCol(129, 86, 49));
        MapColor.colors.put(35, toCol(112, 2, 0));
        MapColor.colors.put(36, toCol(209, 177, 161));
        MapColor.colors.put(37, toCol(159, 82, 36));
        MapColor.colors.put(38, toCol(149, 87, 108));
        MapColor.colors.put(39, toCol(112, 108, 138));
        MapColor.colors.put(40, toCol(186, 133, 36));
        MapColor.colors.put(41, toCol(103, 117, 53));
        MapColor.colors.put(42, toCol(160, 77, 78));
        MapColor.colors.put(43, toCol(57, 41, 35));
        MapColor.colors.put(44, toCol(135, 107, 98));
        MapColor.colors.put(45, toCol(87, 92, 92));
        MapColor.colors.put(46, toCol(122, 73, 88));
        MapColor.colors.put(47, toCol(76, 62, 92));
        MapColor.colors.put(48, toCol(76, 50, 35));
        MapColor.colors.put(49, toCol(76, 82, 42));
        MapColor.colors.put(50, toCol(142, 60, 46));
        MapColor.colors.put(51, toCol(37, 22, 16));
        MapColor.colors.put(52, toCol(189, 48, 49));
        MapColor.colors.put(53, toCol(148, 63, 97));
        MapColor.colors.put(54, toCol(92, 25, 29));
        MapColor.colors.put(55, toCol(22, 126, 134));
        MapColor.colors.put(56, toCol(58, 142, 140));
        MapColor.colors.put(57, toCol(86, 44, 62));
        MapColor.colors.put(58, toCol(20, 180, 133));
    }
}
