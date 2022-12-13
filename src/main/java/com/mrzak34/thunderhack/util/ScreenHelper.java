package com.mrzak34.thunderhack.util;

import net.minecraft.client.Minecraft;
import com.mrzak34.thunderhack.util.AnimationHelper;


public class ScreenHelper {

    private float x;
    private float y;
    private long lastMS;

    public ScreenHelper(float x, float y) {
        this.x = x;
        this.y = y;
        this.lastMS = System.currentTimeMillis();
    }

    public void calculateCompensation(float targetX, float targetY, float xSpeed, float ySpeed) {
        int deltaX = (int) (Math.abs(targetX - x) * xSpeed);
        int deltaY = (int) (Math.abs(targetY - y) * ySpeed);
        x = AnimationHelper.calculateCompensation(targetX, this.x, (long) 1/Minecraft.getDebugFPS(), deltaX);
        y = AnimationHelper.calculateCompensation(targetY, this.y, (long) 1/Minecraft.getDebugFPS(), deltaY);
    }

    public float getX() {
        return this.x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return this.y;
    }

    public void setY(float y) {
        this.y = y;
    }
}
