package com.mrzak34.thunderhack.util;

import net.minecraft.client.Minecraft;

public class DefaultFontRenderer implements IFontRenderer {

    public static DefaultFontRenderer INSTANCE = new DefaultFontRenderer();

    private DefaultFontRenderer() {}

    @Override
    public int drawString(String text, float x, float y, int color) {
        text = text.replaceAll("§", String.valueOf(ChatColor.COLOR_CHAR));
        return Minecraft.getMinecraft().fontRenderer.drawString(text, x, y, color, false);
    }

    @Override
    public int drawStringWithShadow(String text, float x, float y, int color) {
        text = text.replaceAll("§", String.valueOf(ChatColor.COLOR_CHAR));
        return Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(text, x, y, color);
    }

    @Override
    public int drawCenteredString(String text, float x, float y, int color) {
        text = text.replaceAll("§", String.valueOf(ChatColor.COLOR_CHAR));
        return Minecraft.getMinecraft().fontRenderer.drawString(text, x - Minecraft.getMinecraft().fontRenderer.getStringWidth(text) / 2F, y, color, false);
    }

    @Override
    public float getStringWidth(String text) {
        return Minecraft.getMinecraft().fontRenderer.getStringWidth(text);
    }

    @Override
    public int getFontHeight() {
        return Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT;
    }

    @Override
    public float getStringHeight(String text) {
        return Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT;
    }


}