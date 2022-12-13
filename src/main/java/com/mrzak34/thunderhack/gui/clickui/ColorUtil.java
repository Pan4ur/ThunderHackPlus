package com.mrzak34.thunderhack.gui.clickui;


import java.awt.Color;
import java.awt.image.BufferedImage;

import com.mrzak34.thunderhack.util.MathUtil;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public class ColorUtil {

    public static Color healthColor(float health, float maxHealth) {
        float[] fractions = { 0.0F, 0.5F, 1.0F };
        Color[] colors = { new Color(0xFFFF0C0C), new Color(0xFFFFE90C), new Color(0xFF0CFF2B) };
        float progress = health / maxHealth;
        return blendColors(fractions, colors, progress).brighter();
    }



    public static int fade(Color color, int delay) {
        float[] hsb = new float[3];
        Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsb);
        float brightness = Math.abs(((float) (System.currentTimeMillis() % 2000L + delay) / 1000.0F) % 2F - 1.0F);
        brightness = 0.5F + 0.5F * brightness;
        hsb[2] = brightness % 2.0F;
        return Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]);
    }

    public static int astolfoRainbow2(int counter, int alpha) {
        final int width = 110;

        double rainbowState = Math.ceil(System.currentTimeMillis() - (long) counter * width) / 11;
        rainbowState %= 360;
        final float hue = (float) (rainbowState / 360) < 0.5 ? -((float) (rainbowState / 360))
                : (float) (rainbowState / 360);

        Color color = Color.getHSBColor(hue, 0.7f, 1);
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha).getRGB();
    }


    public static Color astolfoRainbow2(int offset, float distance, float speedl) {
        float speed = 30 * 100;
        float hue = (System.currentTimeMillis() % (int) speed) + (distance - offset) * speedl;
        while (hue > speed) {
            hue -= speed;
        }
        hue /= speed;
        if (hue > 0.5)
            hue = 0.5F - (hue - 0.5f);

        hue += 0.5F;
        return Color.getHSBColor(hue, 0.4F, 1F);
    }

    public static Color astolfoRainbow(int offset) {
        float speed = 30 * 100;
        float hue = (System.currentTimeMillis() % (int) speed) + offset;
        while (hue > speed) {
            hue -= speed;
        }
        hue /= speed;
        if (hue > 0.5)
            hue = 0.5F - (hue - 0.5f);

        hue += 0.5F;
        return Color.getHSBColor(hue, 0.4F, 1F);
    }

    public static Color skyRainbow(int speed, int index) {
        int angle = (int) ((System.currentTimeMillis() / speed + index) % 360);
        float hue = angle / 360f;
        return Color.getHSBColor((double) ((float) ((angle %= 360.0) / 360.0)) < 0.5 ? -((float) (angle / 360.0))
                : (float) (angle / 360.0), 0.5F, 1.0F);
    }

    public static int rainbow(int delay, double speed) {
        double rainbow = Math.ceil((System.currentTimeMillis() + delay) / speed);
        rainbow %= 360.0D;
        return Color.getHSBColor((float) -((rainbow / 360.0F)), 0.9F, 1.0F).getRGB();
    }

    public static int getBrightness(Color color, float brightness) {
        float[] hsb = new float[3];
        Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsb);
        brightness = 0.5F + 0.5F * brightness;
        hsb[2] = brightness % 2.0F;
        return Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]);
    }

    public static void glColor(final Color color) {
        final float red = color.getRed() / 255F;
        final float green = color.getGreen() / 255F;
        final float blue = color.getBlue() / 255F;
        final float alpha = color.getAlpha() / 255F;

        GlStateManager.color(red, green, blue, alpha);
    }

    public static void glColor(final int hex, float alpha) {
        final float red = (hex >> 16 & 0xFF) / 255F;
        final float green = (hex >> 8 & 0xFF) / 255F;
        final float blue = (hex & 0xFF) / 255F;

        GlStateManager.color(red, green, blue, alpha / 255f);
    }

    public static void glColor(final int hex) {
        final float alpha = (hex >> 24 & 0xFF) / 255F;
        final float red = (hex >> 16 & 0xFF) / 255F;
        final float green = (hex >> 8 & 0xFF) / 255F;
        final float blue = (hex & 0xFF) / 255F;

        GlStateManager.color(red, green, blue, alpha);
    }

    public static Color getColor(int hex, int alpha) {
        float f1 = (float) (hex >> 16 & 255) / 255.0F;
        float f2 = (float) (hex >> 8 & 255) / 255.0F;
        float f3 = (float) (hex & 255) / 255.0F;
        return new Color((int) (f1 * 255f), (int) (f2 * 255f), (int) (f3 * 255f), alpha);
    }

    public static Color getColor(Color color, int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
    }

    public static Color blendColors(float[] fractions, Color[] colors, float progress) {
        if (fractions.length == colors.length) {
            int[] indices = getFractionIndices(fractions, progress);
            float[] range = { fractions[indices[0]], fractions[indices[1]] };
            Color[] colorRange = { colors[indices[0]], colors[indices[1]] };
            float max = range[1] - range[0];
            float value = progress - range[0];
            float weight = value / max;
            return blend(colorRange[0], colorRange[1], (1.0F - weight));
        }
        throw new IllegalArgumentException("Fractions and colours must have equal number of elements");
    }

    public static int[] getFractionIndices(float[] fractions, float progress) {
        int[] range = new int[2];
        int startPoint;
        for (startPoint = 0; startPoint < fractions.length && fractions[startPoint] <= progress; startPoint++)
            ;
        if (startPoint >= fractions.length)
            startPoint = fractions.length - 1;
        range[0] = startPoint - 1;
        range[1] = startPoint;
        return range;
    }

    public static Color blend(Color acolor, Color bcolor, double ratio) {
        float r = (float) ratio;
        float ir = (float) 1.0 - r;

        float[] rgb1 = new float[3];
        float[] rgb2 = new float[3];

        acolor.getColorComponents(rgb1);
        bcolor.getColorComponents(rgb2);

        float red = rgb1[0] * r + rgb2[0] * ir;
        float green = rgb1[1] * r + rgb2[1] * ir;
        float blue = rgb1[2] * r + rgb2[2] * ir;

        if (red < 0) {
            red = 0;
        } else if (red > 255) {
            red = 255;
        }
        if (green < 0) {
            green = 0;
        } else if (green > 255) {
            green = 255;
        }
        if (blue < 0) {
            blue = 0;
        } else if (blue > 255) {
            blue = 255;
        }

        Color color = null;
        try {
            color = new Color(red, green, blue);
        } catch (IllegalArgumentException ignored) {
        }
        return color;
    }

    public static int evaluate(float fraction, int startValue, int endValue) {
        float startA = ((startValue >> 24) & 0xff) / 255.0f;
        float startR = ((startValue >> 16) & 0xff) / 255.0f;
        float startG = ((startValue >> 8) & 0xff) / 255.0f;
        float startB = (startValue & 0xff) / 255.0f;

        float endA = ((endValue >> 24) & 0xff) / 255.0f;
        float endR = ((endValue >> 16) & 0xff) / 255.0f;
        float endG = ((endValue >> 8) & 0xff) / 255.0f;
        float endB = (endValue & 0xff) / 255.0f;

        startR = (float) Math.pow(startR, 2.2);
        startG = (float) Math.pow(startG, 2.2);
        startB = (float) Math.pow(startB, 2.2);

        endR = (float) Math.pow(endR, 2.2);
        endG = (float) Math.pow(endG, 2.2);
        endB = (float) Math.pow(endB, 2.2);

        float a = MathUtil.lerp(fraction, startA, endA);
        float r = MathUtil.lerp(fraction, startR, endR);
        float g = MathUtil.lerp(fraction, startG, endG);
        float b = MathUtil.lerp(fraction, startB, endB);

        a = a * 255.0f;
        r = (float) Math.pow(r, 1.0 / 2.2) * 255.0f;
        g = (float) Math.pow(g, 1.0 / 2.2) * 255.0f;
        b = (float) Math.pow(b, 1.0 / 2.2) * 255.0f;

        return Math.round(a) << 24 | Math.round(r) << 16 | Math.round(g) << 8 | Math.round(b);
    }

    public static Color[] getAnalogousColor(Color color) {
        Color[] colors = new Color[2];
        float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);

        float degree = 30 / 360f;

        float newHueAdded = hsb[0] + degree;
        colors[0] = new Color(Color.HSBtoRGB(newHueAdded, hsb[1], hsb[2]));

        float newHueSubtracted = hsb[0] - degree;

        colors[1] = new Color(Color.HSBtoRGB(newHueSubtracted, hsb[1], hsb[2]));

        return colors;
    }

    public static int[] rgbToHsv(int rgb) {
        // Source: en.wikipedia.org/wiki/HSV_and_HSL#Formal_derivation
        float r = (float) ((rgb & 0xff0000) >> 16) / 255;
        float g = (float) ((rgb & 0x00ff00) >> 8) / 255;
        float b = (float) (rgb & 0x0000ff) / 255;
        float M = r > g ? (r > b ? r : b) : (g > b ? g : b);
        float m = r < g ? (r < b ? r : b) : (g < b ? g : b);
        float c = M - m;
        float h;
        if (M == r) {
            h = ((g - b) / c);
            while (h < 0)
                h += 6;
            h %= 6;
        } else if (M == g) {
            h = ((b - r) / c) + 2;
        } else {
            h = ((r - g) / c) + 4;
        }
        h *= 60;
        float s = c / M;
        return new int[] { c == 0 ? -1 : (int) h, (int) (s * 100), (int) (M * 100) };
    }

    // RGB TO HSL AND HSL TO RGB FOUND HERE:
    // https://gist.github.com/mjackson/5311256
    public static Color hslToRGB(float[] hsl) {
        float red, green, blue;

        if (hsl[1] == 0) {
            red = green = blue = 1;
        } else {
            float q = hsl[2] < .5 ? hsl[2] * (1 + hsl[1]) : hsl[2] + hsl[1] - hsl[2] * hsl[1];
            float p = 2 * hsl[2] - q;

            red = hueToRGB(p, q, hsl[0] + 1 / 3f);
            green = hueToRGB(p, q, hsl[0]);
            blue = hueToRGB(p, q, hsl[0] - 1 / 3f);
        }

        red *= 255;
        green *= 255;
        blue *= 255;

        return new Color((int) red, (int) green, (int) blue);
    }

    public static float hueToRGB(float p, float q, float t) {
        float newT = t;
        if (newT < 0)
            newT += 1;
        if (newT > 1)
            newT -= 1;
        if (newT < 1 / 6f)
            return p + (q - p) * 6 * newT;
        if (newT < .5f)
            return q;
        if (newT < 2 / 3f)
            return p + (q - p) * (2 / 3f - newT) * 6;
        return p;
    }

    public static int hsvToRgb(int hue, int saturation, int value) {
        // Source: en.wikipedia.org/wiki/HSL_and_HSV#Converting_to_RGB#From_HSV
        hue %= 360;
        float s = (float) saturation / 100;
        float v = (float) value / 100;
        float c = v * s;
        float h = (float) hue / 60;
        float x = c * (1 - Math.abs(h % 2 - 1));
        float r, g, b;
        switch (hue / 60) {
            case 0:
                r = c;
                g = x;
                b = 0;
                break;
            case 1:
                r = x;
                g = c;
                b = 0;
                break;
            case 2:
                r = 0;
                g = c;
                b = x;
                break;
            case 3:
                r = 0;
                g = x;
                b = c;
                break;
            case 4:
                r = x;
                g = 0;
                b = c;
                break;
            case 5:
                r = c;
                g = 0;
                b = x;
                break;
            default:
                return 0;
        }
        float m = v - c;
        return ((int) ((r + m) * 255) << 16) | ((int) ((g + m) * 255) << 8) | ((int) ((b + m) * 255));
    }

    public static float[] rgbToHSL(Color rgb) {
        float red = rgb.getRed() / 255f;
        float green = rgb.getGreen() / 255f;
        float blue = rgb.getBlue() / 255f;

        float max = Math.max(Math.max(red, green), blue);
        float min = Math.min(Math.min(red, green), blue);
        float c = (max + min) / 2f;
        float[] hsl = new float[] { c, c, c };

        if (max == min) {
            hsl[0] = hsl[1] = 0;
        } else {
            float d = max - min;
            hsl[1] = hsl[2] > .5 ? d / (2 - max - min) : d / (max + min);

            if (max == red) {
                hsl[0] = (green - blue) / d + (green < blue ? 6 : 0);
            } else if (max == blue) {
                hsl[0] = (blue - red) / d + 2;
            } else if (max == green) {
                hsl[0] = (red - green) / d + 4;
            }
            hsl[0] /= 6;
        }
        return hsl;
    }

    public static Color imitateTransparency(Color backgroundColor, Color accentColor, float percentage) {
        return new Color(ColorUtil.interpolateColor(backgroundColor, accentColor, (255 * percentage) / 255));
    }

    public static int applyOpacity(int color, float opacity) {
        Color old = new Color(color);
        return applyOpacity(old, opacity).getRGB();
    }

    // Opacity value ranges from 0-1
    public static Color applyOpacity(Color color, float opacity) {
        opacity = Math.min(1, Math.max(0, opacity));
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) (color.getAlpha() * opacity));
    }

    public static Color darker(Color color, float FACTOR) {
        return new Color(Math.max((int) (color.getRed() * FACTOR), 0), Math.max((int) (color.getGreen() * FACTOR), 0), Math.max((int) (color.getBlue() * FACTOR), 0), color.getAlpha());
    }

    public static Color brighter(Color color, float FACTOR) {
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();
        int alpha = color.getAlpha();

        /*
         * From 2D group: 1. black.brighter() should return grey 2. applying brighter to
         * blue will always return blue, brighter 3. non pure color (non zero rgb) will
         * eventually return white
         */
        int i = (int) (1.0 / (1.0 - FACTOR));
        if (r == 0 && g == 0 && b == 0) {
            return new Color(i, i, i, alpha);
        }
        if (r > 0 && r < i)
            r = i;
        if (g > 0 && g < i)
            g = i;
        if (b > 0 && b < i)
            b = i;

        return new Color(Math.min((int) (r / FACTOR), 255), Math.min((int) (g / FACTOR), 255),
                Math.min((int) (b / FACTOR), 255), alpha);
    }

    /**
     * This method gets the average color of an image performance of this goes as
     * O((width * height) / step)
     */
    public static Color averageColor(BufferedImage bi, int width, int height, int pixelStep) {
        int[] color = new int[3];
        for (int x = 0; x < width; x += pixelStep) {
            for (int y = 0; y < height; y += pixelStep) {
                Color pixel = new Color(bi.getRGB(x, y));
                color[0] += pixel.getRed();
                color[1] += pixel.getGreen();
                color[2] += pixel.getBlue();
            }
        }
        int num = (width * height) / (pixelStep * pixelStep);
        return new Color(color[0] / num, color[1] / num, color[2] / num);
    }

    public static Color rainbow(int speed, int index, float saturation, float brightness, float opacity) {
        int angle = (int) ((System.currentTimeMillis() / speed + index) % 360);
        float hue = angle / 360f;
        Color color = new Color(Color.HSBtoRGB(hue, saturation, brightness));
        return new Color(color.getRed(), color.getGreen(), color.getBlue(),
                Math.max(0, Math.min(255, (int) (opacity * 255))));
    }

    public static Color interpolateColorsBackAndForth(int speed, int index, Color start, Color end, boolean trueColor) {
        int angle = (int) (((System.currentTimeMillis()) / speed + index) % 360);
        angle = (angle >= 180 ? 360 - angle : angle) * 2;
        return trueColor ? ColorUtil.interpolateColorHue(start, end, angle / 360f)
                : ColorUtil.interpolateColorC(start, end, angle / 360f);
    }

    // The next few methods are for interpolating colors
    public static int interpolateColor(Color color1, Color color2, float amount) {
        amount = Math.min(1, Math.max(0, amount));
        return interpolateColorC(color1, color2, amount).getRGB();
    }

    public static int interpolateColor(int color1, int color2, float amount) {
        amount = Math.min(1, Math.max(0, amount));
        Color cColor1 = new Color(color1);
        Color cColor2 = new Color(color2);
        return interpolateColorC(cColor1, cColor2, amount).getRGB();
    }

    public static Color interpolateColorC(Color color1, Color color2, float amount) {
        amount = Math.min(1, Math.max(0, amount));
        return new Color(interpolateInt(color1.getRed(), color2.getRed(), amount),
                interpolateInt(color1.getGreen(), color2.getGreen(), amount),
                interpolateInt(color1.getBlue(), color2.getBlue(), amount),
                interpolateInt(color1.getAlpha(), color2.getAlpha(), amount));
    }

    public static Color interpolateColorHue(Color color1, Color color2, float amount) {
        amount = Math.min(1, Math.max(0, amount));

        float[] color1HSB = Color.RGBtoHSB(color1.getRed(), color1.getGreen(), color1.getBlue(), null);
        float[] color2HSB = Color.RGBtoHSB(color2.getRed(), color2.getGreen(), color2.getBlue(), null);

        Color resultColor = Color.getHSBColor(interpolateFloat(color1HSB[0], color2HSB[0], amount),
                interpolateFloat(color1HSB[1], color2HSB[1], amount),
                interpolateFloat(color1HSB[2], color2HSB[2], amount));

        return new Color(resultColor.getRed(), resultColor.getGreen(), resultColor.getBlue(),
                interpolateInt(color1.getAlpha(), color2.getAlpha(), amount));
    }

    // Fade a color in and out with a specified alpha value ranging from 0-1
    public static Color fade(int speed, int index, Color color, float alpha) {
        float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        int angle = (int) ((System.currentTimeMillis() / speed + index) % 360);
        angle = (angle > 180 ? 360 - angle : angle) + 180;

        Color colorHSB = new Color(Color.HSBtoRGB(hsb[0], hsb[1], angle / 360f));

        return new Color(colorHSB.getRed(), colorHSB.getGreen(), colorHSB.getBlue(),
                Math.max(0, Math.min(255, (int) (alpha * 255))));
    }

    private static float getAnimationEquation(int index, int speed) {
        int angle = (int) ((System.currentTimeMillis() / speed + index) % 360);
        return ((angle > 180 ? 360 - angle : angle) + 180) / 360f;
    }

    public static int[] createColorArray(int color) {
        return new int[] { bitChangeColor(color, 16), bitChangeColor(color, 8), bitChangeColor(color, 0),
                bitChangeColor(color, 24) };
    }

    public static int getOppositeColor(int color) {
        int R = bitChangeColor(color, 0);
        int G = bitChangeColor(color, 8);
        int B = bitChangeColor(color, 16);
        int A = bitChangeColor(color, 24);
        R = 255 - R;
        G = 255 - G;
        B = 255 - B;
        return R + (G << 8) + (B << 16) + (A << 24);
    }

    private static int bitChangeColor(int color, int bitChange) {
        return (color >> bitChange) & 255;
    }
    public static Double interpolate(double oldValue, double newValue, double interpolationValue) {
        return (oldValue + (newValue - oldValue) * interpolationValue);
    }

    public static float interpolateFloat(float oldValue, float newValue, double interpolationValue) {
        return interpolate(oldValue, newValue, (float) interpolationValue).floatValue();
    }

    public static int interpolateInt(int oldValue, int newValue, double interpolationValue) {
        return interpolate(oldValue, newValue, (float) interpolationValue).intValue();
    }
}