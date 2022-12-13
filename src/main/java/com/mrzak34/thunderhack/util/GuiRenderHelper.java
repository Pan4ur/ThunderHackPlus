package com.mrzak34.thunderhack.util;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL11.GL_LINES;
import static org.lwjgl.opengl.GL11.GL_LINE_SMOOTH;

public final class GuiRenderHelper {

    public static void drawRect(float x, float y, float w, float h, int color) {
        float right = x + w;
        float bottom = y + h;

        float alpha = (color >> 24 & 0xFF) / 255.0F;
        float red = (color >> 16 & 0xFF) / 255.0F;
        float green = (color >> 8 & 0xFF) / 255.0F;
        float blue = (color & 0xFF) / 255.0F;

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();

        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

        GlStateManager.color(red, green, blue, alpha);

        bufferBuilder.begin(7, DefaultVertexFormats.POSITION);
        bufferBuilder.pos(x, bottom, 0.0D).endVertex(); // top left
        bufferBuilder.pos(right, bottom, 0.0D).endVertex(); // top right
        bufferBuilder.pos(right, y, 0.0D).endVertex(); // bottom right
        bufferBuilder.pos(x, y, 0.0D).endVertex(); // bottom left
        tessellator.draw();

        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void drawOutlineRect(float x, float y, float w, float h, float lineWidth, int color) {
        float right = x + w;
        float bottom = y + h;

        float alpha = (color >> 24 & 0xFF) / 255.0F;
        float red = (color >> 16 & 0xFF) / 255.0F;
        float green = (color >> 8 & 0xFF) / 255.0F;
        float blue = (color & 0xFF) / 255.0F;

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();

        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

        GlStateManager.color(red, green, blue, alpha);

        GL11.glEnable(GL_LINE_SMOOTH);

        GlStateManager.glLineWidth(lineWidth);
        bufferBuilder.begin(GL_LINES, DefaultVertexFormats.POSITION);
        bufferBuilder.pos(x, bottom, 0.0D).endVertex(); // top left
        bufferBuilder.pos(right, bottom, 0.0D).endVertex(); // top right
        bufferBuilder.pos(right, bottom, 0.0D).endVertex(); // top right
        bufferBuilder.pos(right, y, 0.0D).endVertex(); // bottom right
        bufferBuilder.pos(right, y, 0.0D).endVertex(); // bottom right
        bufferBuilder.pos(x, y, 0.0D).endVertex(); // bottom left
        bufferBuilder.pos(x, y, 0.0D).endVertex(); // bottom left
        bufferBuilder.pos(x, bottom, 0.0D).endVertex(); // top left
        tessellator.draw();

        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }
}