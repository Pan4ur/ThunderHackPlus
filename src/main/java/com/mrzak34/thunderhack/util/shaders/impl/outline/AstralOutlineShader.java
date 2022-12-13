package com.mrzak34.thunderhack.util.shaders.impl.outline;


import com.mrzak34.thunderhack.util.shaders.FramebufferShader;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.awt.*;
import java.util.HashMap;

public final class AstralOutlineShader extends FramebufferShader
{
    public static final AstralOutlineShader INSTANCE;
    public float time = 0;

    public AstralOutlineShader() {
        super("astralOutline.frag");
    }

    @Override public void setupUniforms() {
        this.setupUniform("texture");
        this.setupUniform("texelSize");
        this.setupUniform("color");
        this.setupUniform("divider");
        this.setupUniform("radius");
        this.setupUniform("maxSample");
        this.setupUniform("alpha0");
        this.setupUniform( "time" );
        this.setupUniform("iterations");
        this.setupUniform("formuparam2");
        this.setupUniform("stepsize");
        this.setupUniform("volsteps");
        this.setupUniform("zoom");
        this.setupUniform("tile");
        this.setupUniform("distfading");
        this.setupUniform("saturation");
        this.setupUniform("fadeBol");
        this.setupUniform( "resolution" );
    }

    public void updateUniforms(final Color color, final float radius, final float quality, boolean gradientAlpha, int alphaOutline, float duplicate, float red, float green, float blue, float alpha, int iteractions, float formuparam2,
                               float zoom, float volumSteps, float stepSize, float title, float distfading, float saturation, float cloud, int fade) {
        GL20.glUniform1i(this.getUniform("texture"), 0);
        GL20.glUniform2f(this.getUniform("texelSize"), 1.0f / this.mc.displayWidth * (radius * quality), 1.0f / this.mc.displayHeight * (radius * quality));
        GL20.glUniform1f(this.getUniform("divider"), 140.0f);
        GL20.glUniform1f(this.getUniform("radius"), radius);
        GL20.glUniform1f(this.getUniform("maxSample"), 10.0f);
        GL20.glUniform1f(this.getUniform("alpha0"), gradientAlpha ? -1.0f : alphaOutline / 255.0f);
        GL20.glUniform2f( getUniform( "resolution" ), new ScaledResolution( mc ).getScaledWidth( ) / duplicate, new ScaledResolution( mc ).getScaledHeight( ) / duplicate );
        GL20.glUniform1f( getUniform( "time" ), time );
        GL20.glUniform4f(getUniform("color"), red, green, blue, alpha);
        GL20.glUniform1i(getUniform("iterations"), iteractions);
        GL20.glUniform1f(getUniform("formuparam2"), formuparam2);
        GL20.glUniform1i(getUniform("volsteps"), (int) volumSteps);
        GL20.glUniform1f(getUniform("stepsize"), stepSize);
        GL20.glUniform1f(getUniform("zoom"), zoom);
        GL20.glUniform1f(getUniform("tile"), title);
        GL20.glUniform1f(getUniform("distfading"), distfading);
        GL20.glUniform1f(getUniform("saturation"), saturation);/*
        GL20.glUniform1f(getUniform("cloud"), cloud);*/
        GL20.glUniform1i(getUniform("fadeBol"), fade);
    }

    public void stopDraw(final Color color, final float radius, final float quality, boolean gradientAlpha, int alphaOutline, float duplicate, float red, float green, float blue, float alpha, int iteractions, float formuparam2,
                         float zoom, float volumSteps, float stepSize, float title, float distfading, float saturation, float cloud, int fade) {
        mc.gameSettings.entityShadows = entityShadows;
        framebuffer.unbindFramebuffer( );
        GL11.glEnable( 3042 );
        GL11.glBlendFunc( 770, 771 );
        mc.getFramebuffer( ).bindFramebuffer( true );
        mc.entityRenderer.disableLightmap( );
        RenderHelper.disableStandardItemLighting( );
        startShader(color, radius, quality, gradientAlpha, alphaOutline, duplicate, red, green, blue, alpha, iteractions, formuparam2, zoom, volumSteps, stepSize, title, distfading, saturation, cloud, fade);
        mc.entityRenderer.setupOverlayRendering( );
        drawFramebuffer( framebuffer );
        stopShader( );
        mc.entityRenderer.disableLightmap( );
        GlStateManager.popMatrix( );
        GlStateManager.popAttrib( );
    }

    public void startShader(final Color color, final float radius, final float quality, boolean gradientAlpha, int alphaOutline, float duplicate, float red, float green, float blue, float alpha, int iteractions, float formuparam2,
                            float zoom, float volumSteps, float stepSize, float title, float distfading, float saturation, float cloud, int fade) {
        GL11.glPushMatrix();
        GL20.glUseProgram(this.program);
        if (this.uniformsMap == null) {
            this.uniformsMap = new HashMap<String, Integer>();
            this.setupUniforms();
        }
        this.updateUniforms(color, radius, quality, gradientAlpha, alphaOutline, duplicate, red, green, blue, alpha, iteractions, formuparam2, zoom, volumSteps, stepSize, title, distfading, saturation, cloud, fade);
    }

    static {
        INSTANCE = new AstralOutlineShader();
    }

    public void update(double speed) {
        this.time += speed;
    }
}