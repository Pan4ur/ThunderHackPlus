package com.mrzak34.thunderhack.util;

import net.minecraft.client.Minecraft;


public class ScreenHelper {

    private float x;
    private float y;

    public ScreenHelper(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void calculateCompensation(float targetX, float targetY, float xSpeed, float ySpeed) {
        int deltaX = (int) (Math.abs(targetX - x) * xSpeed);
        int deltaY = (int) (Math.abs(targetY - y) * ySpeed);
        x = calculateCompensation(targetX, this.x, (long) 1/Minecraft.getDebugFPS(), (double) deltaX);
        y = calculateCompensation(targetY, this.y, (long) 1/Minecraft.getDebugFPS(), (double) deltaY);
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



    public float animation(float animation, float target, float speedTarget) {
        float dif = (target - animation) / Math.max((float) Minecraft.getDebugFPS(), 5) * 15;

        if (dif > 0) {
            dif = Math.max(speedTarget, dif);
            dif = Math.min(target - animation, dif);
        } else if (dif < 0) {
            dif = Math.min(-speedTarget, dif);
            dif = Math.max(target - animation, dif);
        }
        return animation + dif;
    }

    public float calculateCompensation(float target, float current, long delta, double speed) {
        float diff = current - target;
        if (delta < 1) {
            delta = 1;
        }
        if (delta > 1000) {
            delta = 16;
        }
        double dif = (Math.max(speed * delta / (1000 / 60F), 0.5));
        if (diff > speed) {
            current -= dif;
            if (current < target) {
                current = target;
            }
        } else if (diff < -speed) {
            current += dif;
            if (current > target) {
                current = target;
            }
        } else {
            current = target;
        }
        return current;
    }
}
