package com.mrzak34.thunderhack.gui.fontstuff;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.util.Util;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

import java.awt.*;


public class FontRender {


    public static float drawStringWithShadow(String text, float x, float y, int color) {
        return drawStringWithShadow(text, (int) x, (int) y, color);
    }


    public static float drawString(String text, float x, float y, int color) {
        return drawString(text, (int) x, (int) y, color);
    }

    // ints
    public static float drawStringWithShadow(String text, int x, int y, int color) {
        return Thunderhack.fontRenderer.drawStringWithShadow(text, x, y, color);
    }

    public static float drawString(String text, int x, int y, int color) {
        return Thunderhack.fontRenderer.drawString(text, x, y, color);
    }

    public static float drawString2(String text, int x, int y, int color) {
        return Thunderhack.fontRenderer2.drawString(text, x, y, color);
    }

    public static float drawIcon(String id, int x, int y, int color) {
        return Thunderhack.icons.drawString(id, x, y, color);
    }

    public static float drawIconF(String id, float x, float y, int color) {
        return Thunderhack.icons.drawString(id, x, y, color);
    }

    public static float drawMidIcon(String id, float x, float y, int color) {
        return Thunderhack.middleicons.drawString(id, x, y, color);
    }

    public static float drawMidIconF(String id, int x, int y, int color) {
        return Thunderhack.middleicons.drawString(id, x, y, color);
    }

    public static float drawBigIcon(String id, int x, int y, int color) {
        return Thunderhack.BIGicons.drawString(id, x, y, color);
    }

    public static float drawString3(String text, float x, float y, int color) {
        return Thunderhack.fontRenderer3.drawString(text, x, y, color);
    }

    public static float drawCentString3(String text, float x, float y, int color) {
        return Thunderhack.fontRenderer3.drawString(text, x - getStringWidth3(text) / 2f, y - getFontHeight3() / 2f, color);
    }
    public static float drawCentString4(String text, float x, float y, int color) {
        return Thunderhack.fontRenderer4.drawString(text, x - getStringWidth4(text) / 2f, y - getFontHeight4() / 2f, color);
    }

    public static float drawString8(String text, int x, int y, int color) {
        return Thunderhack.fontRenderer8.drawString(text, x, y, color);
    }

    public static float drawCentString8(String text, float x, float y, int color) {
        return Thunderhack.fontRenderer8.drawString(text, x - getStringWidth6(text) / 2f, y, color);

    }

    public static float drawString4(String text, int x, int y, int color) {
        return Thunderhack.fontRenderer4.drawString(text, x, y, color);
    }

    public static float drawString5(String text, float x, float y, int color) {
        return Thunderhack.fontRenderer5.drawString(text, x, y, color);
    }


    public static float drawString6(String text, float x, float y, int color, boolean shadow) {
        return Thunderhack.fontRenderer6.drawString(text, x, y, color);
    }

    public static float drawString7(String text, float x, float y, int color, boolean shadow) {
        return Thunderhack.fontRenderer7.drawString(text, x, y, color);
    }

    public static float drawCentString6(String text, float x, float y, int color) {
        return Thunderhack.fontRenderer6.drawString(text, x - getStringWidth6(text) / 2f, y, color);

    }

    public static float drawCentString5(String text, float x, float y, int color) {
        return Thunderhack.fontRenderer5.drawString(text, x - getStringWidth5(text) / 2f, y, color);

    }
    public static float drawCentString2(String text, float x, float y, int color) {
        return Thunderhack.fontRenderer2.drawString(text, x - getStringWidth2(text) / 2f, y, color);

    }
    public static int getStringWidth2(String str) {
        return Thunderhack.fontRenderer2.getStringWidth(str);
    }

    public static int getStringWidth(String str) {
        return Thunderhack.fontRenderer.getStringWidth(str);
    }

    public static int getStringWidth6(String str) {
        return Thunderhack.fontRenderer6.getStringWidth(str);
    }

    public static int getStringWidth5(String str) {
        return Thunderhack.fontRenderer5.getStringWidth(str);
    }

    public static int getStringWidth3(String str) {
        return Thunderhack.fontRenderer3.getStringWidth(str);
    }


    public static int getStringWidth4(String str) {
        return Thunderhack.fontRenderer4.getStringWidth(str);
    }

    public static int getFontHeight() {
        return Thunderhack.fontRenderer.getHeight() + 2;
    }

    public static int getFontHeight2() {
        return Thunderhack.fontRenderer2.getHeight() + 2;

    }

    public static int getFontHeight3() {
        return Thunderhack.fontRenderer3.getHeight();
    }


    public static int getFontHeight4() {
        return Thunderhack.fontRenderer3.getHeight();
    }

    public static int getFontHeight6() {
        return Thunderhack.fontRenderer6.getHeight();
    }

    public static int getFontHeight8() {
        return Thunderhack.fontRenderer8.getHeight();
    }

    public static int getFontHeight5() {
        return Thunderhack.fontRenderer5.getHeight() + 2;
    }


}