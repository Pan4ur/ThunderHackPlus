package com.mrzak34.thunderhack.util.shaders.impl.outline;


import com.mrzak34.thunderhack.util.shaders.FramebufferShader;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.awt.*;
import java.util.HashMap;

public final class AquaOutlineShader extends FramebufferShader
{
    public static final AquaOutlineShader INSTANCE;
    public float time = 0;

    public AquaOutlineShader() {
        super("aquaOutline.frag");
    }

    @Override public void setupUniforms() {
        this.setupUniform("texture");
        this.setupUniform("texelSize");
        this.setupUniform("divider");
        this.setupUniform("radius");
        this.setupUniform("maxSample");
        this.setupUniform("alpha0");
        this.setupUniform( "resolution" );
        this.setupUniform( "time" );
        this.setupUniform("rgba");
        this.setupUniform("lines");
        this.setupUniform("tau");
    }

    public void updateUniforms(final Color color, final float radius, final float quality, boolean gradientAlpha, int alphaOutline, float duplicate, int lines, double tau) {
        GL20.glUniform1i(this.getUniform("texture"), 0);
        GL20.glUniform2f(this.getUniform("texelSize"), 1.0f / this.mc.displayWidth * (radius * quality), 1.0f / this.mc.displayHeight * (radius * quality));
        GL20.glUniform1f(this.getUniform("divider"), 140.0f);
        GL20.glUniform1f(this.getUniform("radius"), radius);
        GL20.glUniform1f(this.getUniform("maxSample"), 10.0f);
        GL20.glUniform1f(this.getUniform("alpha0"), gradientAlpha ? -1.0f : alphaOutline / 255.0f);
        GL20.glUniform2f( getUniform( "resolution" ), new ScaledResolution( mc ).getScaledWidth( )/duplicate, new ScaledResolution( mc ).getScaledHeight( )/duplicate );
        GL20.glUniform1f( getUniform( "time" ), time );
        GL20.glUniform4f(getUniform("rgba"), color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f );
        GL20.glUniform1i(getUniform("lines"), lines);
        GL20.glUniform1f(getUniform("tau"), (float) tau);
    }

    public void stopDraw(final Color color, final float radius, final float quality, boolean gradientAlpha, int alphaOutline, float duplicate, int lines, double tau) {
        mc.gameSettings.entityShadows = entityShadows;
        framebuffer.unbindFramebuffer( );
        GL11.glEnable( 3042 );
        GL11.glBlendFunc( 770, 771 );
        mc.getFramebuffer( ).bindFramebuffer( true );
        mc.entityRenderer.disableLightmap( );
        RenderHelper.disableStandardItemLighting( );
        startShader(color, radius, quality, gradientAlpha, alphaOutline, duplicate, lines, tau);
        mc.entityRenderer.setupOverlayRendering( );
        drawFramebuffer( framebuffer );
        stopShader( );
        mc.entityRenderer.disableLightmap( );
        GlStateManager.popMatrix( );
        GlStateManager.popAttrib( );
    }

    public void startShader(final Color color, final float radius, final float quality, boolean gradientAlpha, int alphaOutline, float duplicate, int lines, double tau) {
        GL11.glPushMatrix();
        GL20.glUseProgram(this.program);
        if (this.uniformsMap == null) {
            this.uniformsMap = new HashMap<String, Integer>();
            this.setupUniforms();
        }
        this.updateUniforms(color, radius, quality, gradientAlpha, alphaOutline, duplicate, lines, tau);
    }

    static {
        INSTANCE = new AquaOutlineShader();
    }

    public void update(double speed) {
        this.time += speed;
    }
}