package com.mrzak34.thunderhack.util.shaders.impl.fill;


import com.mrzak34.thunderhack.util.shaders.FramebufferShader;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.awt.*;
import java.util.HashMap;

public class FillShader extends FramebufferShader {

    public static final FillShader INSTANCE;

    static {
        INSTANCE = new FillShader();
    }

    public float time;

    public FillShader() {
        super("fill.frag");
    }

    @Override
    public void setupUniforms() {
        this.setupUniform("color");
    }

    public void updateUniforms(float red, float green, float blue, float alpha) {
        GL20.glUniform4f(getUniform("color"), red, green, blue, alpha);
    }

    public void stopDraw(Color color) {
        mc.gameSettings.entityShadows = entityShadows;
        framebuffer.unbindFramebuffer();
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        mc.getFramebuffer().bindFramebuffer(true);
        mc.entityRenderer.disableLightmap();
        RenderHelper.disableStandardItemLighting();
        startShader(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f);
        mc.entityRenderer.setupOverlayRendering();
        drawFramebuffer(framebuffer);
        stopShader();
        mc.entityRenderer.disableLightmap();
        GlStateManager.popMatrix();
        GlStateManager.popAttrib();
    }

    public void startShader(float red, float green, float blue, float alpha) {
        GL11.glPushMatrix();
        GL20.glUseProgram(this.program);
        if (this.uniformsMap == null) {
            this.uniformsMap = new HashMap<String, Integer>();
            this.setupUniforms();
        }
        this.updateUniforms(red, green, blue, alpha);
    }

    public void update(double speed) {
        this.time += speed;
    }
}
