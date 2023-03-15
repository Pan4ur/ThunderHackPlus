package com.mrzak34.thunderhack.util.shaders.impl.fill;


import com.mrzak34.thunderhack.util.shaders.FramebufferShader;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.awt.*;
import java.util.HashMap;

public class AquaShader extends FramebufferShader {

    public static final AquaShader INSTANCE;

    static {
        INSTANCE = new AquaShader();
    }

    public float time;

    public AquaShader() {
        super("aqua.frag");
    }

    @Override
    public void setupUniforms() {
        this.setupUniform("resolution");
        this.setupUniform("time");
        this.setupUniform("rgba");
        this.setupUniform("lines");
        this.setupUniform("tau");
    }

    public void updateUniforms(float duplicate, Color color, int lines, double tau) {
        GL20.glUniform2f(getUniform("resolution"), new ScaledResolution(mc).getScaledWidth() / duplicate, new ScaledResolution(mc).getScaledHeight() / duplicate);
        GL20.glUniform1f(getUniform("time"), this.time);
        GL20.glUniform4f(getUniform("rgba"), color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f);
        GL20.glUniform1i(getUniform("lines"), lines);
        GL20.glUniform1f(getUniform("tau"), (float) tau);
    }

    public void stopDraw(final Color color, final float radius, final float quality, float duplicate, final int lines, final double tau) {
        mc.gameSettings.entityShadows = entityShadows;
        framebuffer.unbindFramebuffer();
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        mc.getFramebuffer().bindFramebuffer(true);
        red = color.getRed() / 255.0f;
        green = color.getGreen() / 255.0f;
        blue = color.getBlue() / 255.0f;
        alpha = color.getAlpha() / 255.0f;
        this.radius = radius;
        this.quality = quality;
        mc.entityRenderer.disableLightmap();
        RenderHelper.disableStandardItemLighting();
        startShader(duplicate, color, lines, tau);
        mc.entityRenderer.setupOverlayRendering();
        drawFramebuffer(framebuffer);
        stopShader();
        mc.entityRenderer.disableLightmap();
        GlStateManager.popMatrix();
        GlStateManager.popAttrib();
    }

    public void startShader(float duplicate, Color color, int lines, double tau) {
        GL11.glPushMatrix();
        GL20.glUseProgram(this.program);
        if (this.uniformsMap == null) {
            this.uniformsMap = new HashMap<String, Integer>();
            this.setupUniforms();
        }
        this.updateUniforms(duplicate, color, lines, tau);
    }

    public void update(double speed) {
        this.time += speed;
    }
}
