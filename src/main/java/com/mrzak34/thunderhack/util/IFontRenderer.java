package com.mrzak34.thunderhack.util;

public interface IFontRenderer {

    int drawString(String text, float x, float y, int color);

    int drawStringWithShadow(String text, float x, float y, int color);

    int drawCenteredString(String text, float x, float y, int color);

    float getStringWidth(String text);

    int getFontHeight();

    float getStringHeight(String text);
}
