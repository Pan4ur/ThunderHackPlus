package com.mrzak34.thunderhack.util;

import static com.mrzak34.thunderhack.util.ItemUtil.mc;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_LINE_SMOOTH;
import static org.lwjgl.opengl.GL11.GL_SMOOTH;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glLineWidth;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.HashMap;

import javax.imageio.ImageIO;

import org.lwjgl.opengl.GL11;

import com.jhlabs.image.GaussianFilter;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class Drawable {

    private static HashMap<Integer, Integer> shadowCache = new HashMap<Integer, Integer>();

    public static void drawTexture(ResourceLocation texture, double x, double y, double width, double height) {
        drawTexture(texture, x, y, width, height, Color.WHITE);
    }

    public static void drawTexture(ResourceLocation texture, double x, double y, double width, double height,
                                   Color color) {
        mc.getTextureManager().bindTexture(texture);

        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        bufferBuilder.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX_COLOR);
        bufferBuilder.pos(x + width, y, 0).tex(1, 0)
                .color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferBuilder.pos(x, y, 0).tex(0, 0).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha())
                .endVertex();
        bufferBuilder.pos(x, y + height, 0).tex(0, 1)
                .color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferBuilder.pos(x, y + height, 0).tex(0, 1)
                .color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferBuilder.pos(x + width, y + height, 0).tex(1, 1)
                .color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferBuilder.pos(x + width, y, 0).tex(1, 0)
                .color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        draw(true);

    }

    public static void draw(boolean texture) {
        GlStateManager.color(1, 1, 1, 1);

        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableAlpha();
        GlStateManager.disableLighting();

        GlStateManager.disableCull();
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);

        if (texture)
            GlStateManager.enableTexture2D();
        else
            GlStateManager.disableTexture2D();

        // actually draw
        Tessellator.getInstance().draw();

        GlStateManager.enableAlpha();
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
    }

    public static void drawRect(Rectangle r, Color color) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(2, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(r.x, (r.y + r.height), 0).color(color.getRed() / 255.0F, color.getGreen() / 255.0F,
                color.getBlue() / 255.0F, color.getAlpha() / 255.0F).endVertex();
        bufferbuilder.pos((r.x + r.width), (r.y + r.height), 0).color(color.getRed() / 255.0F,
                color.getGreen() / 255.0F, color.getBlue() / 255.0F, color.getAlpha() / 255.0F).endVertex();
        bufferbuilder.pos((r.x + r.width), r.y, 0).color(color.getRed() / 255.0F, color.getGreen() / 255.0F,
                color.getBlue() / 255.0F, color.getAlpha() / 255.0F).endVertex();
        bufferbuilder.pos(r.x, r.y, 0).color(color.getRed() / 255.0F, color.getGreen() / 255.0F,
                color.getBlue() / 255.0F, color.getAlpha() / 255.0F).endVertex();
        tessellator.draw();
    }

    public static void drawRect(int mode, double left, double top, double right, double bottom, int color) {
        if (left < right) {
            double i = left;
            left = right;
            right = i;
        }

        if (top < bottom) {
            double j = top;
            top = bottom;
            bottom = j;
        }

        float f3 = (float) (color >> 24 & 255) / 255.0F;
        float f = (float) (color >> 16 & 255) / 255.0F;
        float f1 = (float) (color >> 8 & 255) / 255.0F;
        float f2 = (float) (color & 255) / 255.0F;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE,
                GlStateManager.DestFactor.ZERO);
        GlStateManager.color(f, f1, f2, f3);
        bufferbuilder.begin(mode, DefaultVertexFormats.POSITION);
        bufferbuilder.pos((double) left, (double) bottom, 0.0D).endVertex();
        bufferbuilder.pos((double) right, (double) bottom, 0.0D).endVertex();
        bufferbuilder.pos((double) right, (double) top, 0.0D).endVertex();
        bufferbuilder.pos((double) left, (double) top, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void drawRect(double left, double top, double right, double bottom, int color) {
        drawRect(7, left, top, right, bottom, color);
    }

    public static void drawRectWH(double x, double y, double width, double height, int color) {
        drawRect(x, y, x + width, y + height, color);
    }

    public static void horizontalGradient(double x1, double y1, double x2, double y2, int startColor, int endColor) {
        float f = (float) (startColor >> 24 & 255) / 255.0F;
        float f1 = (float) (startColor >> 16 & 255) / 255.0F;
        float f2 = (float) (startColor >> 8 & 255) / 255.0F;
        float f3 = (float) (startColor & 255) / 255.0F;
        float f4 = (float) (endColor >> 24 & 255) / 255.0F;
        float f5 = (float) (endColor >> 16 & 255) / 255.0F;
        float f6 = (float) (endColor >> 8 & 255) / 255.0F;
        float f7 = (float) (endColor & 255) / 255.0F;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE,
                GlStateManager.DestFactor.ZERO);
        GlStateManager.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos((double) x1, (double) y1, 0).color(f1, f2, f3, f).endVertex();
        bufferbuilder.pos((double) x1, (double) y2, 0).color(f1, f2, f3, f).endVertex();
        bufferbuilder.pos((double) x2, (double) y2, 0).color(f5, f6, f7, f4).endVertex();
        bufferbuilder.pos((double) x2, (double) y1, 0).color(f5, f6, f7, f4).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    public static void verticalGradient(double left, double top, double right, double bottom, int startColor,
                                        int endColor) {
        float f = (float) (startColor >> 24 & 255) / 255.0F;
        float f1 = (float) (startColor >> 16 & 255) / 255.0F;
        float f2 = (float) (startColor >> 8 & 255) / 255.0F;
        float f3 = (float) (startColor & 255) / 255.0F;
        float f4 = (float) (endColor >> 24 & 255) / 255.0F;
        float f5 = (float) (endColor >> 16 & 255) / 255.0F;
        float f6 = (float) (endColor >> 8 & 255) / 255.0F;
        float f7 = (float) (endColor & 255) / 255.0F;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE,
                GlStateManager.DestFactor.ZERO);
        GlStateManager.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos((double) right, (double) top, 0).color(f1, f2, f3, f).endVertex();
        bufferbuilder.pos((double) left, (double) top, 0).color(f1, f2, f3, f).endVertex();
        bufferbuilder.pos((double) left, (double) bottom, 0).color(f5, f6, f7, f4).endVertex();
        bufferbuilder.pos((double) right, (double) bottom, 0).color(f5, f6, f7, f4).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    public static void horizontalRGradient(double x, double y, double width, double height, double radius, int color,
                                           int endColor) {
        float f = (float) (color >> 24 & 255) / 255.0F;
        float f1 = (float) (color >> 16 & 255) / 255.0F;
        float f2 = (float) (color >> 8 & 255) / 255.0F;
        float f3 = (float) (color & 255) / 255.0F;
        float f4 = (float) (endColor >> 24 & 255) / 255.0F;
        float f5 = (float) (endColor >> 16 & 255) / 255.0F;
        float f6 = (float) (endColor >> 8 & 255) / 255.0F;
        float f7 = (float) (endColor & 255) / 255.0F;

        double x1 = x + width;
        double y1 = y + height;

        GL11.glPushAttrib(0);
        GL11.glScaled(0.5D, 0.5D, 0.5D);
        GlStateManager.enableBlend();
        x *= 2.0D;
        y *= 2.0D;
        x1 *= 2.0D;
        y1 *= 2.0D;
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glPushMatrix();
        GL11.glColor4f(f1, f2, f3, f);
        GL11.glBegin(9);
        int i;
        for (i = 0; i <= 90; i += 3)
            GL11.glVertex2d(x + radius + Math.sin(i * Math.PI / 180.0D) * radius * -1.0D,
                    y + radius + Math.cos(i * Math.PI / 180.0D) * radius * -1.0D);
        for (i = 90; i <= 180; i += 3)
            GL11.glVertex2d(x + radius + Math.sin(i * Math.PI / 180.0D) * radius * -1.0D,
                    y1 - radius + Math.cos(i * Math.PI / 180.0D) * radius * -1.0D);
        GL11.glColor4f(f5, f6, f7, f4);
        for (i = 0; i <= 90; i += 3)
            GL11.glVertex2d(x1 - radius + Math.sin(i * Math.PI / 180.0D) * radius,
                    y1 - radius + Math.cos(i * Math.PI / 180.0D) * radius);
        for (i = 90; i <= 180; i += 3)
            GL11.glVertex2d(x1 - radius + Math.sin(i * Math.PI / 180.0D) * radius,
                    y + radius + Math.cos(i * Math.PI / 180.0D) * radius);
        GL11.glEnd();
        GL11.glPopMatrix();
        GlStateManager.disableBlend();
        GL11.glScaled(2.0D, 2.0D, 2.0D);
        GL11.glPopAttrib();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public static void drawBorderedRect(double x, double y, double width, double height, float lineWidth, int lineColor,
                                        int bgColor) {
        drawRectWH(x, y, width, height, bgColor);
        float f = (float) (lineColor >> 24 & 255) / 255.0F;
        float f1 = (float) (lineColor >> 16 & 255) / 255.0F;
        float f2 = (float) (lineColor >> 8 & 255) / 255.0F;
        float f3 = (float) (lineColor & 255) / 255.0F;
        GL11.glPushMatrix();
        GL11.glPushAttrib(1048575);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(f1, f2, f3, f);
        GL11.glLineWidth(lineWidth);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex2d((double) x, (double) y);
        GL11.glVertex2d((double) x + width, (double) y);
        GL11.glVertex2d((double) x + width, (double) y);
        GL11.glVertex2d((double) x + width, (double) y + height);
        GL11.glVertex2d((double) x + width, (double) y + height);
        GL11.glVertex2d((double) x, (double) y + height);
        GL11.glVertex2d((double) x, (double) y + height);
        GL11.glVertex2d((double) x, (double) y);
        GL11.glEnd();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }

    public static void drawCircle(double x, double y, float radius, int color) {
        float alpha = (float) (color >> 24 & 255) / 255.0F;
        float red = (float) (color >> 16 & 255) / 255.0F;
        float green = (float) (color >> 8 & 255) / 255.0F;
        float blue = (float) (color & 255) / 255.0F;
        GlStateManager.pushMatrix();
        glEnable(GL_LINE_SMOOTH);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
        GlStateManager.disableAlpha();
        GlStateManager.disableCull();
        GlStateManager.enableBlend();
        GlStateManager.depthMask(false);
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.shadeModel(GL_SMOOTH);
        GL11.glColor4d(red, green, blue, alpha);
        GL11.glBegin(GL11.GL_POLYGON);
        for (int i = 0; i <= 360; i++) {
            GL11.glVertex2d(x + (MathHelper.sin((i * 3.141526f / 180)) * radius),
                    y + (MathHelper.cos((i * 3.141526f / 180)) * radius));
        }
        GL11.glColor4d(1f, 1f, 1f, 1f);
        GL11.glEnd();
        GlStateManager.enableTexture2D();
        GlStateManager.enableDepth();
        GlStateManager.disableBlend();
        GlStateManager.enableCull();
        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.enableAlpha();
        GlStateManager.depthMask(true);
        glDisable(GL_LINE_SMOOTH);
        GlStateManager.popMatrix();
    }

    public static void drawTriangle(double x, double y, double width, float size, float theta, int color) {
        GL11.glPushMatrix();
        GL11.glTranslated(x, y, 0);
        GL11.glRotatef(180 + theta, 0F, 0F, 1.0F);

        float alpha = (float) (color >> 24 & 255) / 255.0F;
        float red = (float) (color >> 16 & 255) / 255.0F;
        float green = (float) (color >> 8 & 255) / 255.0F;
        float blue = (float) (color & 255) / 255.0F;

        GL11.glColor4f(red, green, blue, alpha);
        GlStateManager.enableBlend();
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glLineWidth(1);
        GL11.glBegin(GL11.GL_TRIANGLE_FAN);

        GL11.glVertex2d(0, (1.0F * size));
        GL11.glVertex2d((width * size), -(1.0F * size));
        GL11.glVertex2d(-(width * size), -(1.0F * size));

        GL11.glEnd();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GlStateManager.disableBlend();
        GL11.glRotatef(-180 - theta, 0F, 0F, 1.0F);
        GL11.glColor4f(1f, 1f, 1f, 1f);
        GL11.glTranslated(-x, -y, 0);
        GL11.glPopMatrix();
    }

    public static void drawTriangle(double x, double y, float size, float theta, int color) {
        drawTriangle(x, y, 1, size, theta, color);
    }

    public static void drawRoundedRect(double x, double y, double width, double height, double radius, int color) {
        double x1 = x + width;
        double y1 = y + height;
        float f = (color >> 24 & 0xFF) / 255.0F;
        float f1 = (color >> 16 & 0xFF) / 255.0F;
        float f2 = (color >> 8 & 0xFF) / 255.0F;
        float f3 = (color & 0xFF) / 255.0F;
        GL11.glPushAttrib(0);
        GL11.glScaled(0.5D, 0.5D, 0.5D);
        GlStateManager.enableBlend();
        x *= 2.0D;
        y *= 2.0D;
        x1 *= 2.0D;
        y1 *= 2.0D;
        GL11.glColor4f(f1, f2, f3, f);
        GL11.glDisable(3553);
        GL11.glEnable(2848);
        GL11.glBegin(9);
        int i;
        for (i = 0; i <= 90; i += 3)
            GL11.glVertex2d(x + radius + Math.sin(i * Math.PI / 180.0D) * radius * -1.0D,
                    y + radius + Math.cos(i * Math.PI / 180.0D) * radius * -1.0D);
        for (i = 90; i <= 180; i += 3)
            GL11.glVertex2d(x + radius + Math.sin(i * Math.PI / 180.0D) * radius * -1.0D,
                    y1 - radius + Math.cos(i * Math.PI / 180.0D) * radius * -1.0D);
        for (i = 0; i <= 90; i += 3)
            GL11.glVertex2d(x1 - radius + Math.sin(i * Math.PI / 180.0D) * radius,
                    y1 - radius + Math.cos(i * Math.PI / 180.0D) * radius);
        for (i = 90; i <= 180; i += 3)
            GL11.glVertex2d(x1 - radius + Math.sin(i * Math.PI / 180.0D) * radius,
                    y + radius + Math.cos(i * Math.PI / 180.0D) * radius);
        GL11.glEnd();
        GL11.glEnable(3553);
        GL11.glDisable(2848);
        GL11.glEnable(3553);
        GlStateManager.disableBlend();
        GL11.glScaled(2.0D, 2.0D, 2.0D);
        GL11.glPopAttrib();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public static void drawTexturedModalRect(int x, int y, int textureX, int textureY, int width, int height) {
        drawTexturedModalRect(x, y, textureX, textureY, width, height, 0);
    }

    public static void drawTexturedModalRect(int x, int y, int textureX, int textureY, int width, int height,
                                             float zlevel) {
        float f = 0.00390625F;
        float f1 = 0.00390625F;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos((double) (x), (double) (y + height), zlevel)
                .tex((double) ((float) (textureX) * 0.00390625F), (double) ((float) (textureY + height) * 0.00390625F))
                .endVertex();
        bufferbuilder.pos((double) (x + width), (double) (y + height), zlevel)
                .tex((double) ((float) (textureX + width) * 0.00390625F),
                        (double) ((float) (textureY + height) * 0.00390625F))
                .endVertex();
        bufferbuilder.pos((double) (x + width), (double) (y), zlevel)
                .tex((double) ((float) (textureX + width) * 0.00390625F), (double) ((float) (textureY) * 0.00390625F))
                .endVertex();
        bufferbuilder.pos((double) (x), (double) (y), zlevel)
                .tex((double) ((float) (textureX) * 0.00390625F), (double) ((float) (textureY) * 0.00390625F))
                .endVertex();
        tessellator.draw();
    }

    public static void downloadImage(String url, float x, float y, float width, float height) {
        try {
            glPushMatrix();
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            glDisable(GL11.GL_CULL_FACE);
            GL11.glEnable(GL11.GL_ALPHA_TEST);
            GlStateManager.enableBlend();
            int texId = -1;
            int identifier = (int) (width * height + width + url.hashCode());
            if (shadowCache2.containsKey(identifier)) {
                texId = shadowCache2.get(identifier);
                GlStateManager.bindTexture(texId);
            } else {
                URL stringURL = new URL(url);
                BufferedImage img = ImageIO.read(stringURL);

                texId = TextureUtil.uploadTextureImageAllocate(TextureUtil.glGenTextures(), img, true, false);
                shadowCache2.put(identifier, texId);
            }
            GL11.glColor4f(1f, 1f, 1f, 1f);

            GL11.glBegin(GL11.GL_QUADS);
            GL11.glTexCoord2f(0, 0); // top left
            GL11.glVertex2f(x, y);

            GL11.glTexCoord2f(0, 1); // bottom left
            GL11.glVertex2f(x, y + height);

            GL11.glTexCoord2f(1, 1); // bottom right
            GL11.glVertex2f(x + width, y + height);

            GL11.glTexCoord2f(1, 0); // top right
            GL11.glVertex2f(x + width, y);
            GL11.glEnd();

            GlStateManager.enableTexture2D();
            GlStateManager.disableBlend();
            glEnable(GL_CULL_FACE);
            glPopMatrix();
        } catch (Exception ex) {
            System.out.println("boom");
        }
    }

    private static HashMap<Integer, Integer> shadowCache2 = new HashMap<Integer, Integer>();

    public static boolean isHovered(double mouseX, double mouseY, double x, double y, double width, double height) {
        return mouseX >= x && mouseX - width <= x && mouseY >= y && mouseY - height <= y;
    }

    public static void startScissor(double x, double y, double width, double height) {
        startScissor(x, y, width, height, 1);
    }

    public static void startScissor(double x, double y, double width, double height, double factor) {
        ScaledResolution resolution = new ScaledResolution(mc);
        double scaleWidth = (double) mc.displayWidth / resolution.getScaledWidth_double();
        double scaleHeight = (double) mc.displayHeight / resolution.getScaledHeight_double();

        scaleWidth *= factor;
        scaleHeight *= factor;

        GL11.glScissor((int) (x * scaleWidth), (mc.displayHeight) - (int) ((y + height) * scaleHeight),
                (int) (width * scaleWidth), (int) (height * scaleHeight));
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
    }

    public static void stopScissor() {
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    public static void drawBlurredShadow(float x, float y, float width, float height, int blurRadius, Color color) {
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.01f);

        width = width + blurRadius * 2;
        height = height + blurRadius * 2;
        x = x - blurRadius;
        y = y - blurRadius;

        float _X = x - 0.25f;
        float _Y = y + 0.25f;

        int identifier = (int) (width * height + width + color.hashCode() * blurRadius + blurRadius);

        boolean text2d = GL11.glIsEnabled(GL11.GL_TEXTURE_2D);
        boolean cface = GL11.glIsEnabled(GL11.GL_CULL_FACE);
        boolean atest = GL11.glIsEnabled(GL11.GL_ALPHA_TEST);
        boolean blend = GL11.glIsEnabled(GL11.GL_BLEND);

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        glDisable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GlStateManager.enableBlend();

        int texId = -1;
        if (shadowCache.containsKey(identifier)) {
            texId = shadowCache.get(identifier);
            GlStateManager.bindTexture(texId);
        } else {
            BufferedImage original = new BufferedImage((int) width, (int) height, BufferedImage.TYPE_INT_ARGB);

            Graphics g = original.getGraphics();
            g.setColor(color);
            g.fillRect(blurRadius, blurRadius, (int) (width - blurRadius * 2), (int) (height - blurRadius * 2));
            g.dispose();

            GaussianFilter op = new GaussianFilter(blurRadius);

            BufferedImage blurred = op.filter(original, null);

            texId = TextureUtil.uploadTextureImageAllocate(TextureUtil.glGenTextures(), blurred, true, false);
            shadowCache.put(identifier, texId);
        }
        GL11.glColor4f(1f, 1f, 1f, 1f);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(0, 0); // top left
        GL11.glVertex2f(_X, _Y);
        GL11.glTexCoord2f(0, 1); // bottom left
        GL11.glVertex2f(_X, _Y + height);
        GL11.glTexCoord2f(1, 1); // bottom right
        GL11.glVertex2f((float) (_X + width), _Y + height);
        GL11.glTexCoord2f(1, 0); // top right
        GL11.glVertex2f((float) (_X + width), _Y);
        GL11.glEnd();
        GlStateManager.resetColor();
        if(!blend)
            GlStateManager.disableBlend();
        if(!atest)
            GL11.glDisable(GL11.GL_ALPHA_TEST);
        if(!cface)
            GL11.glEnable(GL11.GL_CULL_FACE);
        if(!text2d){
            GL11.glDisable(GL11.GL_TEXTURE_2D);
        }
    }

    public static void drawArcOutline(float cx, float cy, float r, float start_angle, float end_angle,
                                      int num_segments) {
        GL11.glBegin(GL11.GL_LINE_LOOP);

        for (int i = (int) (num_segments / (360 / start_angle)) + 1; i <= num_segments / (360 / end_angle); i++) {
            double angle = 2 * Math.PI * i / num_segments;
            GL11.glVertex2d(cx + Math.cos(angle) * r, cy + Math.sin(angle) * r);
        }

        GL11.glEnd();
    }

    public static void drawCircleOutline(int x, int y, int radius, float lineWidth, int color) {
        float f = (color >> 24 & 0xFF) / 255.0F;
        float f1 = (color >> 16 & 0xFF) / 255.0F;
        float f2 = (color >> 8 & 0xFF) / 255.0F;
        float f3 = (color & 0xFF) / 255.0F;
        GL11.glLineWidth(lineWidth);
        glEnable(GL_LINE_SMOOTH);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
        GlStateManager.disableAlpha();
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        GlStateManager.disableCull();
        GlStateManager.enableBlend();
        GlStateManager.depthMask(false);
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();

        GL11.glColor4f(f1, f2, f3, f);

        drawArcOutline(x, y, radius, 0, 360, 40);

        GL11.glColor4f(1f, 1f, 1f, 1f);

        GlStateManager.enableTexture2D();
        GlStateManager.enableDepth();
        GlStateManager.disableBlend();
        GlStateManager.enableCull();
        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.enableAlpha();
        GlStateManager.depthMask(true);
        glDisable(GL_LINE_SMOOTH);
        glLineWidth(1f);
    }

}