package com.mrzak34.thunderhack.util.render;

import com.mrzak34.thunderhack.mixin.mixins.IRenderManager;
import com.mrzak34.thunderhack.util.EntityUtil;
import com.mrzak34.thunderhack.util.Util;
import com.mrzak34.thunderhack.util.gaussianblur.GaussianFilter;
import com.mrzak34.thunderhack.util.math.MathUtil;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import javax.vecmath.Vector3d;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Objects;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glEnd;

public class RenderUtil implements Util {

    public static RenderItem itemRender;
    public static ICamera camera = new Frustum();
    public static Tessellator tessellator = Tessellator.getInstance();
    public static BufferBuilder bufferbuilder = tessellator.getBuffer();
    private static final HashMap<Integer, Integer> shadowCache = new HashMap<Integer, Integer>();

    static {
        itemRender = mc.getRenderItem();
        camera = new Frustum();
    }

    public static double interpolate(double current, double old, double scale) {
        return old + (current - old) * scale;
    }

    public static void bindTexture(int texture) {
        glBindTexture(GL_TEXTURE_2D, texture);
    }

    public static javax.vecmath.Vector3d vectorTo2D(int scaleFactor, double x, double y, double z) {
        float xPos = (float) x;
        float yPos = (float) y;
        float zPos = (float) z;
        IntBuffer viewport = GLAllocation.createDirectIntBuffer(16);
        FloatBuffer modelview = GLAllocation.createDirectFloatBuffer(16);
        FloatBuffer projection = GLAllocation.createDirectFloatBuffer(16);
        FloatBuffer vector = GLAllocation.createDirectFloatBuffer(4);
        GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, modelview);
        GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, projection);
        GL11.glGetInteger(GL11.GL_VIEWPORT, viewport);
        if (GLU.gluProject(xPos, yPos, zPos, modelview, projection, viewport, vector))
            return new Vector3d((vector.get(0) / scaleFactor), ((Display.getHeight() - vector.get(1)) / scaleFactor),
                    vector.get(2));
        return null;
    }

    public static float scrollAnimate(float endPoint, float current, float speed) {
        boolean shouldContinueAnimation = endPoint > current;
        if (speed < 0.0f) {
            speed = 0.0f;
        } else if (speed > 1.0f) {
            speed = 1.0f;
        }

        float dif = Math.max(endPoint, current) - Math.min(endPoint, current);
        float factor = dif * speed;
        return current + (shouldContinueAnimation ? factor : -factor);
    }

    public static void glBillboard(float x, float y, float z) {
        float scale = 0.016666668f * 1.6f;
        GlStateManager.translate(x - ((IRenderManager) mc.getRenderManager()).getRenderPosX(), y - ((IRenderManager) mc.getRenderManager()).getRenderPosY(), z - ((IRenderManager) mc.getRenderManager()).getRenderPosZ());
        GlStateManager.glNormal3f(0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(-Minecraft.getMinecraft().player.rotationYaw, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(Minecraft.getMinecraft().player.rotationPitch, Minecraft.getMinecraft().gameSettings.thirdPersonView == 2 ? -1.0f : 1.0f, 0.0f, 0.0f);
        GlStateManager.scale(-scale, -scale, scale);
    }

    public static void glBillboardDistanceScaled(float x, float y, float z, EntityPlayer player, float scale) {
        glBillboard(x, y, z);
        int distance = (int) player.getDistance(x, y, z);
        float scaleDistance = (distance / 2.0f) / (2.0f + (2.0f - scale));
        if (scaleDistance < 1f)
            scaleDistance = 1;
        GlStateManager.scale(scaleDistance, scaleDistance, scaleDistance);
    }

    public static void scale() {
        switch (mc.gameSettings.guiScale) {
            case 0:
                GlStateManager.scale(0.5, 0.5, 0.5);
                break;
            case 1:
                GlStateManager.scale(2, 2, 2);
                break;
            case 3:
                GlStateManager.scale(0.6666666666666667, 0.6666666666666667, 0.6666666666666667);
                break;
        }
    }

    public static float lerp(float a, float b, float f) {
        return a + f * (b - a);
    }

    public static Color TwoColoreffect(Color cl1, Color cl2, double speed) {
        double thing = speed / 4.0 % 1.0;
        float val = MathHelper.clamp((float) Math.sin(Math.PI * 6 * thing) / 2.0f + 0.5f, 0.0f, 1.0f);
        return new Color(lerp((float) cl1.getRed() / 255.0f, (float) cl2.getRed() / 255.0f, val),
                lerp((float) cl1.getGreen() / 255.0f, (float) cl2.getGreen() / 255.0f, val),
                lerp((float) cl1.getBlue() / 255.0f, (float) cl2.getBlue() / 255.0f, val));
    }

    public static void drawFilledCircleNoGL(final int x, final int y, final double r, final int c, final int quality) {
        final float f = ((c >> 24) & 0xff) / 255F;
        final float f1 = ((c >> 16) & 0xff) / 255F;
        final float f2 = ((c >> 8) & 0xff) / 255F;
        final float f3 = (c & 0xff) / 255F;

        GL11.glColor4f(f1, f2, f3, f);
        GL11.glBegin(GL11.GL_TRIANGLE_FAN);

        for (int i = 0; i <= 360 / quality; i++) {
            final double x2 = Math.sin(((i * quality * Math.PI) / 180)) * r;
            final double y2 = Math.cos(((i * quality * Math.PI) / 180)) * r;
            GL11.glVertex2d(x + x2, y + y2);
        }

        GL11.glEnd();
    }

    public static void drawCircleWithTexture(float cX, float cY, int start, int end, float radius, ResourceLocation res, int color) {
        double radian, x, y, tx, ty, xsin, ycos;
        GL11.glPushMatrix();
        // GL11.glEnable(GL11.GL_TEXTURE_2D);
        mc.getTextureManager().bindTexture(res);
        GL11.glEnable(GL11.GL_POLYGON_SMOOTH);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        colorflux(color);
        GL11.glBegin(GL11.GL_POLYGON);
        for (int i = start; i < end; ++i) {
            radian = i * (Math.PI / 180.0f);
            xsin = Math.sin(radian);
            ycos = Math.cos(radian);

            x = xsin * radius;
            y = ycos * radius;

            tx = xsin * 0.5 + 0.5;
            ty = ycos * 0.5 + 0.5;

            GL11.glTexCoord2d(cX + tx, cY + ty);
            GL11.glVertex2d(cX + x, cY + y);
        }
        GL11.glEnd();
        GL11.glDisable(GL11.GL_POLYGON_SMOOTH);
        // GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glPopMatrix();
    }

    public static void glColor(int hex) {
        float alpha = (hex >> 24 & 0xFF) / 255.0F;
        float red = (hex >> 16 & 0xFF) / 255.0F;
        float green = (hex >> 8 & 0xFF) / 255.0F;
        float blue = (hex & 0xFF) / 255.0F;
        GL11.glColor4f(red, green, blue, alpha);
    }

    public static void glColor(final Color color) {
        final float red = color.getRed() / 255F;
        final float green = color.getGreen() / 255F;
        final float blue = color.getBlue() / 255F;
        final float alpha = color.getAlpha() / 255F;

        GlStateManager.color(red, green, blue, alpha);
    }

    public static void drawRect(float x, float y, float w, float h, int color) {

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
        bufferBuilder.pos(x, h, 0.0D).endVertex(); // top left
        bufferBuilder.pos(w, h, 0.0D).endVertex(); // top right
        bufferBuilder.pos(w, y, 0.0D).endVertex(); // bottom right
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

    public static void draw2DGradientRect(float left, float top, float right, float bottom, int leftBottomColor, int leftTopColor, int rightBottomColor, int rightTopColor) {
        float lba = (float) (leftBottomColor >> 24 & 255) / 255.0F;
        float lbr = (float) (leftBottomColor >> 16 & 255) / 255.0F;
        float lbg = (float) (leftBottomColor >> 8 & 255) / 255.0F;
        float lbb = (float) (leftBottomColor & 255) / 255.0F;
        float rba = (float) (rightBottomColor >> 24 & 255) / 255.0F;
        float rbr = (float) (rightBottomColor >> 16 & 255) / 255.0F;
        float rbg = (float) (rightBottomColor >> 8 & 255) / 255.0F;
        float rbb = (float) (rightBottomColor & 255) / 255.0F;
        float lta = (float) (leftTopColor >> 24 & 255) / 255.0F;
        float ltr = (float) (leftTopColor >> 16 & 255) / 255.0F;
        float ltg = (float) (leftTopColor >> 8 & 255) / 255.0F;
        float ltb = (float) (leftTopColor & 255) / 255.0F;
        float rta = (float) (rightTopColor >> 24 & 255) / 255.0F;
        float rtr = (float) (rightTopColor >> 16 & 255) / 255.0F;
        float rtg = (float) (rightTopColor >> 8 & 255) / 255.0F;
        float rtb = (float) (rightTopColor & 255) / 255.0F;

        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(right, top, 0).color(rtr, rtg, rtb, rta).endVertex();
        bufferbuilder.pos(left, top, 0).color(ltr, ltg, ltb, lta).endVertex();
        bufferbuilder.pos(left, bottom, 0).color(lbr, lbg, lbb, lba).endVertex();
        bufferbuilder.pos(right, bottom, 0).color(rbr, rbg, rbb, rba).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    public static void draw1DGradientRect(float left, float top, float right, float bottom, int leftColor, int rightColor) {
        float la = (float) (leftColor >> 24 & 255) / 255.0F;
        float lr = (float) (leftColor >> 16 & 255) / 255.0F;
        float lg = (float) (leftColor >> 8 & 255) / 255.0F;
        float lb = (float) (leftColor & 255) / 255.0F;
        float ra = (float) (rightColor >> 24 & 255) / 255.0F;
        float rr = (float) (rightColor >> 16 & 255) / 255.0F;
        float rg = (float) (rightColor >> 8 & 255) / 255.0F;
        float rb = (float) (rightColor & 255) / 255.0F;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(right, top, 0).color(rr, rg, rb, ra).endVertex();
        bufferbuilder.pos(left, top, 0).color(lr, lg, lb, la).endVertex();
        bufferbuilder.pos(left, bottom, 0).color(lr, lg, lb, la).endVertex();
        bufferbuilder.pos(right, bottom, 0).color(rr, rg, rb, ra).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    public static void beginRender() {
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.enableBlend();
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        //GlStateManager.color(1, 1, 1, 1);
    }

    public static void endRender() {
        //GlStateManager.resetColor();
        GlStateManager.enableCull();
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
    }

    public static void colorflux(int color) {
        float f = (float) (color >> 24 & 255) / 255.0f;
        float f1 = (float) (color >> 16 & 255) / 255.0f;
        float f2 = (float) (color >> 8 & 255) / 255.0f;
        float f3 = (float) (color & 255) / 255.0f;
        GL11.glColor4f(f1, f2, f3, f);
    }

    public static void renderCrosses(BlockPos pos, Color color, float lineWidth) {
        AxisAlignedBB bb = new AxisAlignedBB((double) pos.getX() - Util.mc.getRenderManager().viewerPosX, (double) pos.getY() - Util.mc.getRenderManager().viewerPosY, (double) pos.getZ() - Util.mc.getRenderManager().viewerPosZ, (double) (pos.getX() + 1) - Util.mc.getRenderManager().viewerPosX, (double) (pos.getY() + 1) - Util.mc.getRenderManager().viewerPosY, (double) (pos.getZ() + 1) - Util.mc.getRenderManager().viewerPosZ);
        camera.setPosition(Objects.requireNonNull(Util.mc.getRenderViewEntity()).posX, Util.mc.getRenderViewEntity().posY, Util.mc.getRenderViewEntity().posZ);
        if (camera.isBoundingBoxInFrustum(new AxisAlignedBB(pos))) {
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.disableDepth();
            GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
            GlStateManager.disableTexture2D();
            GlStateManager.depthMask(false);
            GL11.glEnable(2848);
            GL11.glHint(3154, 4354);
            GL11.glLineWidth(lineWidth);
            renderCrosses(bb, color);
            GL11.glDisable(2848);
            GlStateManager.depthMask(true);
            GlStateManager.enableDepth();
            GlStateManager.enableTexture2D();
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }
    }

    public static void renderCrosses(AxisAlignedBB bb, Color color) {
        int hex = color.getRGB();
        float red = (float) (hex >> 16 & 0xFF) / 255.0f;
        float green = (float) (hex >> 8 & 0xFF) / 255.0f;
        float blue = (float) (hex & 0xFF) / 255.0f;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(1, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red, green, blue, 1.0f).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(red, green, blue, 1.0f).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red, green, blue, 1.0f).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(red, green, blue, 1.0f).endVertex();
        tessellator.draw();
    }

    public static void blockEspFrame(BlockPos blockPos, double red, double green, double blue) {
        double d = blockPos.getX();
        Minecraft.getMinecraft().getRenderManager();
        double x = d - ((IRenderManager)RenderUtil.mc.getRenderManager()).getRenderPosX();
        double d2 = blockPos.getY();
        Minecraft.getMinecraft().getRenderManager();
        double y = d2 - ((IRenderManager)RenderUtil.mc.getRenderManager()).getRenderPosY();
        double d3 = blockPos.getZ();
        Minecraft.getMinecraft().getRenderManager();
        double z = d3 - ((IRenderManager)RenderUtil.mc.getRenderManager()).getRenderPosZ();
        glBlendFunc(770, 771);
        GL11.glEnable(3042);
        GL11.glLineWidth(1.0f);
        GL11.glDisable(3553);
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        GL11.glColor4d(red, green, blue, 0.5);
        drawSelectionBoundingBox(new AxisAlignedBB(x, y, z, x + 1.0, y + 1.0, z + 1.0));
        GL11.glEnable(3553);
        GL11.glEnable(2929);
        GL11.glDepthMask(true);
        GL11.glDisable(3042);
    }

    public static void rectangleBordered(double x, double y, double x1, double y1, double width, int internalColor, int borderColor) {
        drawRect((float) (x + width), (float) (y + width), (float) (x1 - width), (float) (y1 - width), internalColor);
        drawRect((float) (x + width), (float) y, (float) (x1 - width), (float) (y + width), borderColor);
        drawRect((float) x, (float) y, (float) (x + width), (float) y1, borderColor);
        drawRect((float) (x1 - width), (float) y, (float) x1, (float) y1, borderColor);
        drawRect((float) (x + width), (float) (y1 - width), (float) (x1 - width), (float) y1, borderColor);
    }

    public static void rotationHelper(float xAngle, float yAngle, float zAngle) {
        GlStateManager.rotate(yAngle, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(zAngle, 0.0f, 0.0f, 1.0f);
        GlStateManager.rotate(xAngle, 1.0f, 0.0f, 0.0f);
    }

    public static AxisAlignedBB interpolateAxis(AxisAlignedBB bb) {
        return new AxisAlignedBB(bb.minX - RenderUtil.mc.getRenderManager().viewerPosX, bb.minY - RenderUtil.mc.getRenderManager().viewerPosY, bb.minZ - RenderUtil.mc.getRenderManager().viewerPosZ, bb.maxX - RenderUtil.mc.getRenderManager().viewerPosX, bb.maxY - RenderUtil.mc.getRenderManager().viewerPosY, bb.maxZ - RenderUtil.mc.getRenderManager().viewerPosZ);
    }

    public static void drawTexturedRect(int x, int y, int textureX, int textureY, int width, int height, int zLevel) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder BufferBuilder2 = tessellator.getBuffer();
        BufferBuilder2.begin(7, DefaultVertexFormats.POSITION_TEX);
        BufferBuilder2.pos(x, y + height, zLevel).tex((float) (textureX) * 0.00390625f, (float) (textureY + height) * 0.00390625f).endVertex();
        BufferBuilder2.pos(x + width, y + height, zLevel).tex((float) (textureX + width) * 0.00390625f, (float) (textureY + height) * 0.00390625f).endVertex();
        BufferBuilder2.pos(x + width, y, zLevel).tex((float) (textureX + width) * 0.00390625f, (float) (textureY) * 0.00390625f).endVertex();
        BufferBuilder2.pos(x, y, zLevel).tex((float) (textureX) * 0.00390625f, (float) (textureY) * 0.00390625f).endVertex();
        tessellator.draw();
    }

    public static void drawBoxESP(BlockPos pos, Color color, boolean secondC, Color secondColor, float lineWidth, boolean outline, boolean box, int boxAlpha, boolean air, int mode) {
        if (box) {
            drawBox(pos, new Color(color.getRed(), color.getGreen(), color.getBlue(), boxAlpha), mode);
        }
        if (outline) {
            drawBlockOutline(pos, secondC ? secondColor : color, lineWidth, air, mode);
        }
    }

    public static void glScissor(float x, float y, float x1, float y1, ScaledResolution sr) {
        GL11.glScissor((int) (x * (float) sr.getScaleFactor()), (int) ((float) RenderUtil.mc.displayHeight - y1 * (float) sr.getScaleFactor()), (int) ((x1 - x) * (float) sr.getScaleFactor()), (int) ((y1 - y) * (float) sr.getScaleFactor()));
    }

    public static void glScissor(float x, float y, float x1, float y1, ScaledResolution sr, double animation_factor) {
        float h = y + y1;
        float h2 = (float) (h * (1d - MathUtil.clamp(animation_factor, 0, 1.0025f)));

        float x3 = x;
        float y3 = y + h2;
        float x4 = x1;
        float y4 = y1 - h2;

        if (x4 < x3) {
            x4 = x3;
        }
        if (y4 < y3) {
            y4 = y3;
        }
        glScissor(x3, y3, x4, y4, sr);

    }

    public static Color blend(Color color1, Color color2, double ratio) {
        float r = (float) ratio;
        float ir = 1.0f - r;
        float[] rgb1 = new float[3];
        float[] rgb2 = new float[3];
        color1.getColorComponents(rgb1);
        color2.getColorComponents(rgb2);
        float red = rgb1[0] * r + rgb2[0] * ir;
        float green = rgb1[1] * r + rgb2[1] * ir;
        float blue = rgb1[2] * r + rgb2[2] * ir;
        if (red < 0.0f) {
            red = 0.0f;
        } else if (red > 255.0f) {
            red = 255.0f;
        }
        if (green < 0.0f) {
            green = 0.0f;
        } else if (green > 255.0f) {
            green = 255.0f;
        }
        if (blue < 0.0f) {
            blue = 0.0f;
        } else if (blue > 255.0f) {
            blue = 255.0f;
        }
        Color color = null;
        try {
            color = new Color(red, green, blue);
        } catch (IllegalArgumentException exp) {
            NumberFormat numberFormat = NumberFormat.getNumberInstance();
        }
        return color;
    }

    public static void drawSmoothRect(float left, float top, float right, float bottom, int color) {
        GL11.glEnable(3042);
        GL11.glEnable(2848);
        drawRect(left, top, right, bottom, color);
        GL11.glScalef(0.5f, 0.5f, 0.5f);
        drawRect(left * 2.0f - 1.0f, top * 2.0f, left * 2.0f, bottom * 2.0f - 1.0f, color);
        drawRect(left * 2.0f, top * 2.0f - 1.0f, right * 2.0f, top * 2.0f, color);
        drawRect(right * 2.0f, top * 2.0f, right * 2.0f + 1.0f, bottom * 2.0f - 1.0f, color);
        drawRect(left * 2.0f, bottom * 2.0f - 1.0f, right * 2.0f, bottom * 2.0f, color);
        GL11.glDisable(3042);
        GL11.glScalef(2.0f, 2.0f, 2.0f);
    }

    public static void drawBox(BlockPos pos, Color color, int mode) {

        IBlockState iblockstate = RenderUtil.mc.world.getBlockState(pos);
        Vec3d interp = EntityUtil.interpolateEntity(RenderUtil.mc.getRenderViewEntity(), mc.getRenderPartialTicks());

        AxisAlignedBB bb = null;
        switch (mode) {
            case 0:
                bb = iblockstate.getSelectedBoundingBox(RenderUtil.mc.world, pos).grow(0.002f).offset(-interp.x, -interp.y, -interp.z);
                break;
            case 1:
                bb = iblockstate.getSelectedBoundingBox(RenderUtil.mc.world, pos).grow(0.002f).expand(0.9, 0, 0).offset(-interp.x, -interp.y, -interp.z);
                break;
            case 2:
                bb = iblockstate.getSelectedBoundingBox(RenderUtil.mc.world, pos).grow(0.002f).expand(-0.9, 0, 0).offset(-interp.x, -interp.y, -interp.z);
                break;
            case 3:
                bb = iblockstate.getSelectedBoundingBox(RenderUtil.mc.world, pos).grow(0.002f).expand(0, 0, 0.9).offset(-interp.x, -interp.y, -interp.z);
                break;
            case 4:
                bb = iblockstate.getSelectedBoundingBox(RenderUtil.mc.world, pos).grow(0.002f).expand(0, 0, -0.9).offset(-interp.x, -interp.y, -interp.z);
                break;
        }

        camera.setPosition(Objects.requireNonNull(RenderUtil.mc.getRenderViewEntity()).posX, RenderUtil.mc.getRenderViewEntity().posY, RenderUtil.mc.getRenderViewEntity().posZ);
        if (camera.isBoundingBoxInFrustum(new AxisAlignedBB(bb.minX + RenderUtil.mc.getRenderManager().viewerPosX, bb.minY + RenderUtil.mc.getRenderManager().viewerPosY, bb.minZ + RenderUtil.mc.getRenderManager().viewerPosZ, bb.maxX + RenderUtil.mc.getRenderManager().viewerPosX, bb.maxY + RenderUtil.mc.getRenderManager().viewerPosY, bb.maxZ + RenderUtil.mc.getRenderManager().viewerPosZ))) {
            GlStateManager.pushMatrix();

            boolean texture = GL11.glIsEnabled(GL11.GL_TEXTURE_2D);
            boolean blend = GL11.glIsEnabled(GL11.GL_BLEND);
            boolean hz = GL11.glIsEnabled(2848);

            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
            GlStateManager.disableTexture2D();
            GL11.glEnable(2848);
            GL11.glHint(3154, 4354);

            RenderGlobal.renderFilledBox(bb, (float) color.getRed() / 255.0f, (float) color.getGreen() / 255.0f, (float) color.getBlue() / 255.0f, (float) color.getAlpha() / (mode == 0 ? 255f : 510.0f));

            if (!hz)
                GL11.glDisable(2848);
            if (texture)
                GlStateManager.enableTexture2D();
            if (!blend)
                GlStateManager.disableBlend();

            GlStateManager.popMatrix();
        }
    }

    public static void drawBlockOutline(BlockPos pos, Color color, float linewidth, boolean air, int mode) {
        IBlockState iblockstate = RenderUtil.mc.world.getBlockState(pos);
        if ((air || iblockstate.getMaterial() != Material.AIR) && RenderUtil.mc.world.getWorldBorder().contains(pos)) {
            assert (RenderUtil.mc.getRenderViewEntity() != null);
            Vec3d interp = EntityUtil.interpolateEntity(RenderUtil.mc.getRenderViewEntity(), mc.getRenderPartialTicks());
            drawBlockOutline(iblockstate.getSelectedBoundingBox(RenderUtil.mc.world, pos).grow(0.002f).offset(-interp.x, -interp.y, -interp.z), color, linewidth);


        }
    }

    public static void drawBlockOutline(AxisAlignedBB bb, Color color, float linewidth) {
        float red = (float) color.getRed() / 255.0f;
        float green = (float) color.getGreen() / 255.0f;
        float blue = (float) color.getBlue() / 255.0f;
        float alpha = (float) color.getAlpha() / 255.0f;
        GlStateManager.pushMatrix();

        boolean texture = GL11.glIsEnabled(GL11.GL_TEXTURE_2D);
        boolean blend = GL11.glIsEnabled(GL11.GL_BLEND);
        boolean DEPTH = GL11.glIsEnabled(GL_DEPTH_TEST);
        boolean HZ = GL11.glIsEnabled(2848);

        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);
        GL11.glEnable(2848);


        GL11.glHint(3154, 4354);
        GL11.glLineWidth(linewidth);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        tessellator.draw();

        if (!HZ)
            GL11.glDisable(2848);
        if (DEPTH) {
            GlStateManager.depthMask(true);
            GlStateManager.enableDepth();
        }
        if (texture) {
            GlStateManager.enableTexture2D();
        }
        if (blend) {
            GlStateManager.disableBlend();
        }


        GlStateManager.popMatrix();
    }

    public static void blockEsp(BlockPos blockPos, Color c, double length, double length2) {
        double x = (double) blockPos.getX() - ((IRenderManager)RenderUtil.mc.getRenderManager()).getRenderPosX();
        double y = (double) blockPos.getY() - ((IRenderManager)RenderUtil.mc.getRenderManager()).getRenderPosY();
        double z = (double) blockPos.getZ() - ((IRenderManager)RenderUtil.mc.getRenderManager()).getRenderPosZ();
        GL11.glPushMatrix();
        glBlendFunc(770, 771);
        GL11.glEnable(3042);
        GL11.glLineWidth(2.0f);
        GL11.glDisable(3553);
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        GL11.glColor4d((float) c.getRed() / 255.0f, (float) c.getGreen() / 255.0f, (float) c.getBlue() / 255.0f, 0.25);
        drawColorBox(new AxisAlignedBB(x, y, z, x + length2, y + 1.0, z + length), 0.0f, 0.0f, 0.0f, 0.0f);
        GL11.glColor4d(0.0, 0.0, 0.0, 0.5);
        drawSelectionBoundingBox(new AxisAlignedBB(x, y, z, x + length2, y + 1.0, z + length));
        GL11.glLineWidth(2.0f);
        GL11.glEnable(3553);
        GL11.glEnable(2929);
        GL11.glDepthMask(true);
        GL11.glDisable(3042);
        GL11.glPopMatrix();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
    }

    public static void drawRect2(double left, double top, double right, double bottom, int color) {
        GlStateManager.pushMatrix();
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
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.color(f, f1, f2, f3);
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
        bufferbuilder.pos(left, bottom, 0.0D).endVertex();
        bufferbuilder.pos(right, bottom, 0.0D).endVertex();
        bufferbuilder.pos(right, top, 0.0D).endVertex();
        bufferbuilder.pos(left, top, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public static void drawColorBox(AxisAlignedBB axisalignedbb, float red, float green, float blue, float alpha) {
        Tessellator ts = Tessellator.getInstance();
        BufferBuilder vb = ts.getBuffer();
        vb.begin(7, DefaultVertexFormats.POSITION_TEX);
        vb.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
        ts.draw();
        vb.begin(7, DefaultVertexFormats.POSITION_TEX);
        vb.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
        ts.draw();
        vb.begin(7, DefaultVertexFormats.POSITION_TEX);
        vb.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
        ts.draw();
        vb.begin(7, DefaultVertexFormats.POSITION_TEX);
        vb.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
        ts.draw();
        vb.begin(7, DefaultVertexFormats.POSITION_TEX);
        vb.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
        ts.draw();
        vb.begin(7, DefaultVertexFormats.POSITION_TEX);
        vb.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
        ts.draw();
    }

    public static void drawSelectionBoundingBox(AxisAlignedBB boundingBox) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vertexbuffer = tessellator.getBuffer();
        vertexbuffer.begin(3, DefaultVertexFormats.POSITION);
        vertexbuffer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).endVertex();
        vertexbuffer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).endVertex();
        vertexbuffer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).endVertex();
        vertexbuffer.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).endVertex();
        vertexbuffer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).endVertex();
        tessellator.draw();
        vertexbuffer.begin(3, DefaultVertexFormats.POSITION);
        vertexbuffer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).endVertex();
        vertexbuffer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).endVertex();
        vertexbuffer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).endVertex();
        vertexbuffer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).endVertex();
        vertexbuffer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).endVertex();
        tessellator.draw();
        vertexbuffer.begin(1, DefaultVertexFormats.POSITION);
        vertexbuffer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).endVertex();
        vertexbuffer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).endVertex();
        vertexbuffer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).endVertex();
        vertexbuffer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).endVertex();
        vertexbuffer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).endVertex();
        vertexbuffer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).endVertex();
        vertexbuffer.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).endVertex();
        vertexbuffer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).endVertex();
        tessellator.draw();
    }

    public static void glEnd() {
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glPopMatrix();
        GL11.glEnable(2929);
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
    }

    public static AxisAlignedBB getBoundingBox(BlockPos blockPos) {
        return RenderUtil.mc.world.getBlockState(blockPos).getBoundingBox(RenderUtil.mc.world, blockPos).offset(blockPos);
    }

    public static void drawFilledBox(AxisAlignedBB bb, int color) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);
        float alpha = (float) (color >> 24 & 0xFF) / 255.0f;
        float red = (float) (color >> 16 & 0xFF) / 255.0f;
        float green = (float) (color >> 8 & 0xFF) / 255.0f;
        float blue = (float) (color & 0xFF) / 255.0f;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        tessellator.draw();
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public static void setColor(Color color) {
        GL11.glColor4d((double) color.getRed() / 255.0, (double) color.getGreen() / 255.0, (double) color.getBlue() / 255.0, (double) color.getAlpha() / 255.0);
    }

    public static Entity getEntity() {
        return mc.getRenderViewEntity() == null ? mc.player : mc.getRenderViewEntity();
    }

    public static void prepare(float x, float y, float x1, float y1, int color, int color1) {
        startRender();
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f(x, y1);
        GL11.glVertex2f(x1, y1);
        GL11.glVertex2f(x1, y);
        GL11.glVertex2f(x, y);
        GL11.glEnd();
        GL11.glShadeModel(GL11.GL_FLAT);
        endRender2();
    }

    public static void startRender() {
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_FASTEST);
        GL11.glDisable(GL11.GL_LIGHTING);
    }

    public static void endRender2() {
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glDepthMask(true);
        GL11.glCullFace(GL11.GL_BACK);
        GL11.glPopMatrix();
        GL11.glPopAttrib();
    }

    public static void drawBlurredShadow(float x, float y, float width, float height, int blurRadius, Color color) {
        glPushMatrix();
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.01f);
        width = width + blurRadius * 2;
        height = height + blurRadius * 2;
        x = x - blurRadius;
        y = y - blurRadius;

        float _X = x - 0.25f;
        float _Y = y + 0.25f;

        int identifier = (int) (width * height + width + color.hashCode() * blurRadius + blurRadius);

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        glDisable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GlStateManager.enableBlend();

        int texId = -1;
        if (shadowCache.containsKey(identifier)) {
            texId = shadowCache.get(identifier);

            GlStateManager.bindTexture(texId);
        } else {
            if (width <= 0) width = 1;
            if (height <= 0) height = 1;
            BufferedImage original = new BufferedImage((int) width, (int) height, BufferedImage.TYPE_INT_ARGB_PRE);

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
        GL11.glVertex2f(_X + width, _Y + height);

        GL11.glTexCoord2f(1, 0); // top right
        GL11.glVertex2f(_X + width, _Y);
        GL11.glEnd();

        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.resetColor();

        glEnable(GL_CULL_FACE);
        glPopMatrix();
    }

    public static long delta = 0L;

    public static void drawRect(double left, double top, double right, double bottom, int color) {
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
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.color(f, f1, f2, f3);
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
        bufferbuilder.pos(left, bottom, 0.0D).endVertex();
        bufferbuilder.pos(right, bottom, 0.0D).endVertex();
        bufferbuilder.pos(right, top, 0.0D).endVertex();
        bufferbuilder.pos(left, top, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void drawBorderedRect(float left, float top, float right, float bottom, float borderWidth, int insideColor, int borderColor, boolean borderIncludedInBounds) {
        drawRect(left - (!borderIncludedInBounds ? borderWidth : 0), top - (!borderIncludedInBounds ? borderWidth : 0), right + (!borderIncludedInBounds ? borderWidth : 0), bottom + (!borderIncludedInBounds ? borderWidth : 0), borderColor);
        drawRect(left + (borderIncludedInBounds ? borderWidth : 0), top + (borderIncludedInBounds ? borderWidth : 0), right - ((borderIncludedInBounds ? borderWidth : 0)), bottom - ((borderIncludedInBounds ? borderWidth : 0)), insideColor);
    }


    public static void renderBox(AxisAlignedBB bb, Color color, Color outLineColor, float lineWidth) {
        glPushMatrix();
        drawOutline(bb, lineWidth, outLineColor);
        drawBox(bb, color);
        glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        glPopMatrix();
    }


    public static void drawBox(AxisAlignedBB bb, Color color) {
        glPushMatrix();
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glDisable(GL_LIGHTING);
        glDisable(GL_TEXTURE_2D);
        glEnable(GL_LINE_SMOOTH);
        glDisable(GL_DEPTH_TEST);
        glDepthMask(false);
        color(color);
        fillBox(bb);
        glDisable(GL_LINE_SMOOTH);
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_LIGHTING);
        glEnable(GL_DEPTH_TEST);
        glDepthMask(true);
        glDisable(GL_BLEND);
        glPopMatrix();
    }

    public static void drawOutline(AxisAlignedBB bb, float lineWidth, Color color) {
        glPushMatrix();
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glDisable(GL_LIGHTING);
        glDisable(GL_TEXTURE_2D);
        glEnable(GL_LINE_SMOOTH);
        glDisable(GL_DEPTH_TEST);
        glDepthMask(false);
        glLineWidth(lineWidth);
        color(color);
        fillOutline(bb);
        glLineWidth(1.0f);
        glDisable(GL_LINE_SMOOTH);
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_LIGHTING);
        glEnable(GL_DEPTH_TEST);
        glDepthMask(true);
        glDisable(GL_BLEND);
        glPopMatrix();
    }


    public static void fillOutline(AxisAlignedBB bb) {
        if (bb != null) {
            glBegin(GL_LINES);
            {
                glVertex3d(bb.minX, bb.minY, bb.minZ);
                glVertex3d(bb.maxX, bb.minY, bb.minZ);

                glVertex3d(bb.maxX, bb.minY, bb.minZ);
                glVertex3d(bb.maxX, bb.minY, bb.maxZ);

                glVertex3d(bb.maxX, bb.minY, bb.maxZ);
                glVertex3d(bb.minX, bb.minY, bb.maxZ);

                glVertex3d(bb.minX, bb.minY, bb.maxZ);
                glVertex3d(bb.minX, bb.minY, bb.minZ);

                glVertex3d(bb.minX, bb.minY, bb.minZ);
                glVertex3d(bb.minX, bb.maxY, bb.minZ);

                glVertex3d(bb.maxX, bb.minY, bb.minZ);
                glVertex3d(bb.maxX, bb.maxY, bb.minZ);

                glVertex3d(bb.maxX, bb.minY, bb.maxZ);
                glVertex3d(bb.maxX, bb.maxY, bb.maxZ);

                glVertex3d(bb.minX, bb.minY, bb.maxZ);
                glVertex3d(bb.minX, bb.maxY, bb.maxZ);

                glVertex3d(bb.minX, bb.maxY, bb.minZ);
                glVertex3d(bb.maxX, bb.maxY, bb.minZ);

                glVertex3d(bb.maxX, bb.maxY, bb.minZ);
                glVertex3d(bb.maxX, bb.maxY, bb.maxZ);

                glVertex3d(bb.maxX, bb.maxY, bb.maxZ);
                glVertex3d(bb.minX, bb.maxY, bb.maxZ);

                glVertex3d(bb.minX, bb.maxY, bb.maxZ);
                glVertex3d(bb.minX, bb.maxY, bb.minZ);
            }
            glEnd();
        }
    }

    public static void fillBox(AxisAlignedBB boundingBox) {
        if (boundingBox != null) {
            glBegin(GL_QUADS);
            glVertex3d((float) boundingBox.minX, (float) boundingBox.minY, (float) boundingBox.maxZ);
            glVertex3d((float) boundingBox.maxX, (float) boundingBox.minY, (float) boundingBox.maxZ);
            glVertex3d((float) boundingBox.maxX, (float) boundingBox.maxY, (float) boundingBox.maxZ);
            glVertex3d((float) boundingBox.minX, (float) boundingBox.maxY, (float) boundingBox.maxZ);
            glEnd();

            glBegin(GL_QUADS);
            glVertex3d((float) boundingBox.maxX, (float) boundingBox.minY, (float) boundingBox.maxZ);
            glVertex3d((float) boundingBox.minX, (float) boundingBox.minY, (float) boundingBox.maxZ);
            glVertex3d((float) boundingBox.minX, (float) boundingBox.maxY, (float) boundingBox.maxZ);
            glVertex3d((float) boundingBox.maxX, (float) boundingBox.maxY, (float) boundingBox.maxZ);
            glEnd();

            glBegin(GL_QUADS);
            glVertex3d((float) boundingBox.minX, (float) boundingBox.minY, (float) boundingBox.minZ);
            glVertex3d((float) boundingBox.minX, (float) boundingBox.minY, (float) boundingBox.maxZ);
            glVertex3d((float) boundingBox.minX, (float) boundingBox.maxY, (float) boundingBox.maxZ);
            glVertex3d((float) boundingBox.minX, (float) boundingBox.maxY, (float) boundingBox.minZ);
            glEnd();

            glBegin(GL_QUADS);
            glVertex3d((float) boundingBox.minX, (float) boundingBox.minY, (float) boundingBox.maxZ);
            glVertex3d((float) boundingBox.minX, (float) boundingBox.minY, (float) boundingBox.minZ);
            glVertex3d((float) boundingBox.minX, (float) boundingBox.maxY, (float) boundingBox.minZ);
            glVertex3d((float) boundingBox.minX, (float) boundingBox.maxY, (float) boundingBox.maxZ);
            glEnd();

            glBegin(GL_QUADS);
            glVertex3d((float) boundingBox.maxX, (float) boundingBox.minY, (float) boundingBox.maxZ);
            glVertex3d((float) boundingBox.maxX, (float) boundingBox.minY, (float) boundingBox.minZ);
            glVertex3d((float) boundingBox.maxX, (float) boundingBox.maxY, (float) boundingBox.minZ);
            glVertex3d((float) boundingBox.maxX, (float) boundingBox.maxY, (float) boundingBox.maxZ);
            glEnd();

            glBegin(GL_QUADS);
            glVertex3d((float) boundingBox.maxX, (float) boundingBox.minY, (float) boundingBox.minZ);
            glVertex3d((float) boundingBox.maxX, (float) boundingBox.minY, (float) boundingBox.maxZ);
            glVertex3d((float) boundingBox.maxX, (float) boundingBox.maxY, (float) boundingBox.maxZ);
            glVertex3d((float) boundingBox.maxX, (float) boundingBox.maxY, (float) boundingBox.minZ);
            glEnd();

            glBegin(GL_QUADS);
            glVertex3d((float) boundingBox.minX, (float) boundingBox.minY, (float) boundingBox.minZ);
            glVertex3d((float) boundingBox.maxX, (float) boundingBox.minY, (float) boundingBox.minZ);
            glVertex3d((float) boundingBox.maxX, (float) boundingBox.maxY, (float) boundingBox.minZ);
            glVertex3d((float) boundingBox.minX, (float) boundingBox.maxY, (float) boundingBox.minZ);
            glEnd();

            glBegin(GL_QUADS);
            glVertex3d((float) boundingBox.maxX, (float) boundingBox.minY, (float) boundingBox.minZ);
            glVertex3d((float) boundingBox.minX, (float) boundingBox.minY, (float) boundingBox.minZ);
            glVertex3d((float) boundingBox.minX, (float) boundingBox.maxY, (float) boundingBox.minZ);
            glVertex3d((float) boundingBox.maxX, (float) boundingBox.maxY, (float) boundingBox.minZ);
            glEnd();

            glBegin(GL_QUADS);
            glVertex3d((float) boundingBox.minX, (float) boundingBox.maxY, (float) boundingBox.minZ);
            glVertex3d((float) boundingBox.maxX, (float) boundingBox.maxY, (float) boundingBox.minZ);
            glVertex3d((float) boundingBox.maxX, (float) boundingBox.maxY, (float) boundingBox.maxZ);
            glVertex3d((float) boundingBox.minX, (float) boundingBox.maxY, (float) boundingBox.maxZ);
            glEnd();

            glBegin(GL_QUADS);
            glVertex3d((float) boundingBox.maxX, (float) boundingBox.maxY, (float) boundingBox.minZ);
            glVertex3d((float) boundingBox.minX, (float) boundingBox.maxY, (float) boundingBox.minZ);
            glVertex3d((float) boundingBox.minX, (float) boundingBox.maxY, (float) boundingBox.maxZ);
            glVertex3d((float) boundingBox.maxX, (float) boundingBox.maxY, (float) boundingBox.maxZ);
            glEnd();

            glBegin(GL_QUADS);
            glVertex3d((float) boundingBox.minX, (float) boundingBox.minY, (float) boundingBox.minZ);
            glVertex3d((float) boundingBox.maxX, (float) boundingBox.minY, (float) boundingBox.minZ);
            glVertex3d((float) boundingBox.maxX, (float) boundingBox.minY, (float) boundingBox.maxZ);
            glVertex3d((float) boundingBox.minX, (float) boundingBox.minY, (float) boundingBox.maxZ);
            glEnd();

            glBegin(GL_QUADS);
            glVertex3d((float) boundingBox.maxX, (float) boundingBox.minY, (float) boundingBox.minZ);
            glVertex3d((float) boundingBox.minX, (float) boundingBox.minY, (float) boundingBox.minZ);
            glVertex3d((float) boundingBox.minX, (float) boundingBox.minY, (float) boundingBox.maxZ);
            glVertex3d((float) boundingBox.maxX, (float) boundingBox.minY, (float) boundingBox.maxZ);
            glEnd();
        }
    }

    public static void color(Color color) {
        glColor4f(color.getRed() / 255.0f,
                color.getGreen() / 255.0f,
                color.getBlue() / 255.0f,
                color.getAlpha() / 255.0f);
    }

    public static void color(int color) {
        Color colord = new Color(color);

        glColor4f(colord.getRed() / 255f, colord.getGreen() / 255f, colord.getBlue() / 255f, colord.getAlpha() / 255f);
    }

    public static void color(float r, float g, float b, float a) {
        glColor4f(r, g, b, a);
    }
}