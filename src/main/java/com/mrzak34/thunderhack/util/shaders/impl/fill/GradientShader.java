package com.mrzak34.thunderhack.util.shaders.impl.fill;


import com.mrzak34.thunderhack.util.shaders.FramebufferShader;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.awt.*;
import java.util.HashMap;

public class GradientShader extends FramebufferShader {

    public static final GradientShader INSTANCE;

    static {
        INSTANCE = new GradientShader();
    }

    public float time;

    public GradientShader() {
        super("gradient.frag");
    }

    @Override
    public void setupUniforms() {
        this.setupUniform("resolution");
        this.setupUniform("time");
        this.setupUniform("moreGradient");
        this.setupUniform("Creepy");
        this.setupUniform("alpha");
        this.setupUniform("NUM_OCTAVES");
    }

    public void updateUniforms(float duplicate, float moreGradient, float creepy, float alpha, int numOctaves) {
        GL20.glUniform2f(getUniform("resolution"), new ScaledResolution(mc).getScaledWidth() / duplicate, new ScaledResolution(mc).getScaledHeight() / duplicate);
        GL20.glUniform1f(getUniform("time"), time);
        GL20.glUniform1f(getUniform("moreGradient"), moreGradient);
        GL20.glUniform1f(getUniform("Creepy"), creepy);
        GL20.glUniform1f(getUniform("alpha"), alpha);
        GL20.glUniform1i(getUniform("NUM_OCTAVES"), numOctaves);
    }

    public void stopDraw(final Color color, final float radius, final float quality, float duplicate, float moreGradient, float creepy, float alpha, int numOctaves) {
        mc.gameSettings.entityShadows = entityShadows;
        framebuffer.unbindFramebuffer();
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        mc.getFramebuffer().bindFramebuffer(true);
        red = color.getRed() / 255.0f;
        green = color.getGreen() / 255.0f;
        blue = color.getBlue() / 255.0f;
        this.radius = radius;
        this.quality = quality;
        mc.entityRenderer.disableLightmap();
        RenderHelper.disableStandardItemLighting();
        startShader(duplicate, moreGradient, creepy, alpha, numOctaves);
        mc.entityRenderer.setupOverlayRendering();
        drawFramebuffer(framebuffer);
        stopShader();
        mc.entityRenderer.disableLightmap();
        GlStateManager.popMatrix();
        GlStateManager.popAttrib();
    }

    public void startShader(float duplicate, float moreGradient, float creepy, float alpha, int numOctaves) {
        GL11.glPushMatrix();
        GL20.glUseProgram(this.program);
        if (this.uniformsMap == null) {
            this.uniformsMap = new HashMap<String, Integer>();
            this.setupUniforms();
        }
        this.updateUniforms(duplicate, moreGradient, creepy, alpha, numOctaves);
    }

    public void update(double speed) {
        this.time += speed;
    }
}
