package com.mrzak34.thunderhack.util;

import com.mrzak34.thunderhack.util.math.AnimationMode;
import net.minecraft.util.math.MathHelper;


public class TimeAnimation {
    private final long length;
    private final double start;
    private final double end;
    private double current;
    private double progress;
    private boolean playing;
    private boolean backwards;
    private boolean reverseOnEnd;
    private final long startTime;
    private long lastTime;
    private double per;
    private final long dif;
    private boolean flag;

    private AnimationMode mode;

    public TimeAnimation(long length, double start, double end, boolean backwards, AnimationMode mode) {
        this.length = length;
        this.start = start;
        current = start;
        this.end = end;
        this.mode = mode;
        this.backwards = backwards;
        startTime = System.currentTimeMillis();
        playing = true;
        dif = (System.currentTimeMillis() - startTime);
        switch (mode) {
            case LINEAR:
                per = (end - start) / length;
                break;
            case EXPONENTIAL:
                double dif = end - start;
                flag = dif < 0;
                if (flag) dif *= -1;
                for (int i = 0; i < length; i++) {
                    dif = Math.sqrt(dif);
                }
                per = dif;
                break;
        }
        lastTime = System.currentTimeMillis();
    }


    public void add(float partialTicks) {
        if (playing) {
            if (mode == AnimationMode.LINEAR) {
                current = start + progress;
                progress += per * (System.currentTimeMillis() - lastTime);
            } else if (mode == AnimationMode.EXPONENTIAL) {

            }
            current = MathHelper.clamp(current, start, end);
            if (current >= end || (backwards && current <= start)) {
                if (reverseOnEnd) {
                    reverse();
                    reverseOnEnd = false;
                } else {
                    playing = false;
                }
            }
        }
        lastTime = System.currentTimeMillis();
    }

    public long getLength() {
        return length;
    }

    public double getStart() {
        return start;
    }

    public double getEnd() {
        return end;
    }

    public double getCurrent() {
        return current;
    }

    public void setCurrent(double current) {
        this.current = current;
    }

    public AnimationMode getMode() {
        return mode;
    }

    public void setMode(AnimationMode mode) {
        this.mode = mode;
    }

    public void play() {
        playing = true;
    }

    public void stop() {
        playing = false;
    }

    public void reverse() {
        backwards = !backwards;
        per *= -1;
    }

}