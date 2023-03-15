package com.mrzak34.thunderhack.util.math;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MathematicHelper {

    public static BigDecimal round(float f, int times) {
        BigDecimal bd = new BigDecimal(Float.toString(f));
        bd = bd.setScale(times, RoundingMode.HALF_UP);
        return bd;
    }


    public static float abs(float num) {
        return num < 0.0f ? 0.0f - num : num;
    }

    public static double round(double num, double increment) {
        double v = (double) Math.round(num / increment) * increment;
        BigDecimal bd = new BigDecimal(v);
        bd = bd.setScale(3, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }


    public static float clamp(float val, float min, float max) {
        if (val <= min) {
            val = min;
        }
        if (val >= max) {
            val = max;
        }
        return val;
    }

    public static float randomizeFloat(float startInclusive, float endInclusive) {
        if (startInclusive == endInclusive || endInclusive - startInclusive <= 0.0f) {
            return startInclusive;
        }
        return (float) ((double) startInclusive + (double) (endInclusive - startInclusive) * Math.random());
    }
}
