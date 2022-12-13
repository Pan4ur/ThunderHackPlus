package com.mrzak34.thunderhack.util;


public class ColorShell {
    private int r, g, b, a;
    private boolean rainbow;

    public ColorShell() {
        this.r = 230;
        this.g = 230;
        this.b = 230;
        this.a = 255;
    }

    public static int HSBtoRGB(float hue) {
        int r = 0, g = 0, b = 0;
        float h = (hue - (float) Math.floor(hue)) * 6.0f;
        float f = h - (float) Math.floor(h);
        float p = 1 * (1.0f - 1);
        float q = 1 * (1.0f - 1 * f);
        float t = 1 * (1.0f - (1 * (1.0f - f)));
        switch ((int) h) {
            case 0:
                r = (int) (1 * 255.0f + 0.5f);
                g = (int) (t * 255.0f + 0.5f);
                b = (int) (p * 255.0f + 0.5f);
                break;
            case 1:
                r = (int) (q * 255.0f + 0.5f);
                g = (int) (1 * 255.0f + 0.5f);
                b = (int) (p * 255.0f + 0.5f);
                break;
            case 2:
                r = (int) (p * 255.0f + 0.5f);
                g = (int) (1 * 255.0f + 0.5f);
                b = (int) (t * 255.0f + 0.5f);
                break;
            case 3:
                r = (int) (p * 255.0f + 0.5f);
                g = (int) (q * 255.0f + 0.5f);
                b = (int) (1 * 255.0f + 0.5f);
                break;
            case 4:
                r = (int) (t * 255.0f + 0.5f);
                g = (int) (p * 255.0f + 0.5f);
                b = (int) (1 * 255.0f + 0.5f);
                break;
            case 5:
                r = (int) (1 * 255.0f + 0.5f);
                g = (int) (p * 255.0f + 0.5f);
                b = (int) (q * 255.0f + 0.5f);
                break;
        }
        return 0xff000000 | (r << 16) | (g << 8) | (b);
    }


    public int getRed() {
        if (this.isRainbow())
            this.updateRainbow();
        return this.r;
    }

    public int getGreen() {
        this.updateRainbow();
        return this.g;
    }

    public int getBlue() {
        this.updateRainbow();
        return this.b;
    }

    public int getAlpha() {
        return this.a;
    }

    public boolean isRainbow() {
        return this.rainbow;
    }

    public ColorShell setRainbow(boolean rainbow) {
        this.rainbow = rainbow;
        return this;
    }


    public void updateRainbow() {
        if (!isRainbow()) {
            return;
        }
        double rainbow = Math.ceil(System.currentTimeMillis() / 16f);
        rainbow %= 360;
        int color = HSBtoRGB((float) (rainbow / 360F));
        this.r = (color >> 16) & 255;
        this.g = (color >> 8) & 255;
        this.b = color & 255;
    }


    public static int rgba(int r, int g, int b, int a) {
        return a << 24 | r << 16 | g << 8 | b;
    }
}
