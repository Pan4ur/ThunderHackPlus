package com.mrzak34.thunderhack.util.shaders.impl.outline;


import com.mrzak34.thunderhack.util.shaders.FramebufferShader;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.awt.*;
import java.util.HashMap;

public final class RainbowCubeOutlineShader extends FramebufferShader {
    public static final RainbowCubeOutlineShader INSTANCE;

    static {
        INSTANCE = new RainbowCubeOutlineShader();
    }

    public float time = 0;

    public RainbowCubeOutlineShader() {
        super("rainbowCubeOutline.frag");
    }

    @Override
    public void setupUniforms() {
        this.setupUniform("texture");
        this.setupUniform("texelSize");
        this.setupUniform("color");
        this.setupUniform("divider");
        this.setupUniform("radius");
        this.setupUniform("maxSample");
        this.setupUniform("alpha0");
        this.setupUniform("resolution");
        this.setupUniform("time");
        this.setupUniform("WAVELENGTH");
        this.setupUniform("R");
        this.setupUniform("G");
        this.setupUniform("B");
        this.setupUniform("RSTART");
        this.setupUniform("GSTART");
        this.setupUniform("BSTART");
        this.setupUniform("alpha");
    }

    public void updateUniforms(final Color color, final float radius, final float quality, boolean gradientAlpha, int alphaOutline, float duplicate, Color start, int wave, int rStart, int gStart, int bStart) {
        GL20.glUniform1i(this.getUniform("texture"), 0);
        GL20.glUniform2f(this.getUniform("texelSize"), 1.0f / this.mc.displayWidth * (radius * quality), 1.0f / this.mc.displayHeight * (radius * quality));
        GL20.glUniform1f(this.getUniform("divider"), 140.0f);
        GL20.glUniform1f(this.getUniform("radius"), radius);
        GL20.glUniform1f(this.getUniform("maxSample"), 10.0f);
        GL20.glUniform1f(this.getUniform("alpha0"), gradientAlpha ? -1.0f : alphaOutline / 255.0f);
        GL20.glUniform2f(getUniform("resolution"), new ScaledResolution(mc).getScaledWidth() / duplicate, new ScaledResolution(mc).getScaledHeight() / duplicate);
        GL20.glUniform1f(getUniform("time"), time);
        GL20.glUniform1f(getUniform("alpha"), start.getAlpha() / 255.0f);
        GL20.glUniform1f(getUniform("WAVELENGTH"), (float) wave);
        GL20.glUniform1i(getUniform("R"), start.getRed());
        GL20.glUniform1i(getUniform("G"), start.getGreen());
        GL20.glUniform1i(getUniform("B"), start.getBlue());
        GL20.glUniform1i(getUniform("RSTART"), rStart);
        GL20.glUniform1i(getUniform("GSTART"), gStart);
        GL20.glUniform1i(getUniform("BSTART"), bStart);
    }

    public void stopDraw(final Color color, final float radius, final float quality, boolean gradientAlpha, int alphaOutline, float duplicate, Color start, int wave, int rStart, int gStart, int bStart) {
        mc.gameSettings.entityShadows = entityShadows;
        framebuffer.unbindFramebuffer();
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        mc.getFramebuffer().bindFramebuffer(true);
        mc.entityRenderer.disableLightmap();
        RenderHelper.disableStandardItemLighting();
        startShader(color, radius, quality, gradientAlpha, alphaOutline, duplicate, start, wave, rStart, gStart, bStart);
        mc.entityRenderer.setupOverlayRendering();
        drawFramebuffer(framebuffer);
        stopShader();
        mc.entityRenderer.disableLightmap();
        GlStateManager.popMatrix();
        GlStateManager.popAttrib();
    }

    public void startShader(final Color color, final float radius, final float quality, boolean gradientAlpha, int alphaOutline, float duplicate, Color start, int wave, int rStart, int gStart, int bStart) {
        GL11.glPushMatrix();
        GL20.glUseProgram(this.program);
        if (this.uniformsMap == null) {
            this.uniformsMap = new HashMap<String, Integer>();
            this.setupUniforms();
        }
        this.updateUniforms(color, radius, quality, gradientAlpha, alphaOutline, duplicate, start, wave, rStart, gStart, bStart);
    }

    public void update(double speed) {
        this.time += speed;
    }
}