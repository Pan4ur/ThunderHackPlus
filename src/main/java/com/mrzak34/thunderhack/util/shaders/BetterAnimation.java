package com.mrzak34.thunderhack.util.shaders;

import net.minecraft.client.Minecraft;
import net.minecraft.util.math.MathHelper;

public class BetterAnimation {
    private int prevTick, tick, maxTick;

    public BetterAnimation(int maxTick) {
        this.maxTick = maxTick;
    }

    public BetterAnimation() {
        this(10);
    }

    public void update(boolean update) {
        prevTick = tick;
        tick = MathHelper.clamp(tick + (update ? 1 : -1), 0, maxTick);
    }


    public double getAnimationd() {
        return dropAnimation((this.prevTick + (this.tick - this.prevTick) * Minecraft.getMinecraft().getRenderPartialTicks()) / maxTick);
    }

    public static double dropAnimation(double value) {
        double c1 = 1.70158;
        double c3 = c1 + 1;
        return 1 + c3 * Math.pow(value - 1, 3) + c1 * Math.pow(value - 1, 2);
    }
}
