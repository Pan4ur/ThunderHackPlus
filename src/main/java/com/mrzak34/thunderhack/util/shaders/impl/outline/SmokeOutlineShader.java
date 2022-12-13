package com.mrzak34.thunderhack.util.shaders.impl.outline;


import com.mrzak34.thunderhack.util.shaders.FramebufferShader;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.awt.*;
import java.util.HashMap;

public final class SmokeOutlineShader extends FramebufferShader
{
    public static final SmokeOutlineShader INSTANCE;
    public float time = 0;

    public SmokeOutlineShader() {
        super("smokeOutline.frag");
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
        this.setupUniform("first");
        this.setupUniform("second");
        this.setupUniform("third");
        this.setupUniform("oct");
    }

    public void updateUniforms(final Color first, final float radius, final float quality, boolean gradientAlpha, int alphaOutline, float duplicate, final Color second, final Color third, int oct ) {
        GL20.glUniform1i(this.getUniform("texture"), 0);
        GL20.glUniform2f(this.getUniform("texelSize"), 1.0f / this.mc.displayWidth * (radius * quality), 1.0f / this.mc.displayHeight * (radius * quality));
        GL20.glUniform1f(this.getUniform("divider"), 140.0f);
        GL20.glUniform1f(this.getUniform("radius"), radius);
        GL20.glUniform1f(this.getUniform("maxSample"), 10.0f);
        GL20.glUniform1f(this.getUniform("alpha0"), gradientAlpha ? -1.0f : alphaOutline / 255.0f);
        GL20.glUniform2f( getUniform( "resolution" ), new ScaledResolution( mc ).getScaledWidth( )/duplicate, new ScaledResolution( mc ).getScaledHeight( )/duplicate );
        GL20.glUniform1f( getUniform( "time" ), time );
        GL20.glUniform4f(getUniform("first"), first.getRed() / 255.0f * 5, first.getGreen() / 255.0f * 5, first.getBlue() / 255.0f * 5, first.getAlpha() / 255.0f );
        GL20.glUniform3f(getUniform("second"), second.getRed() / 255.0f * 5, second.getGreen() / 255.0f * 5, second.getBlue() / 255.0f * 5 );
        GL20.glUniform3f(getUniform("third"), third.getRed() / 255.0f * 5, third.getGreen() / 255.0f * 5, third.getBlue() / 255.0f * 5 );
        GL20.glUniform1i(getUniform("oct"), oct);

    }

    public void stopDraw(final Color color, final float radius, final float quality, boolean gradientAlpha, int alphaOutline, float duplicate, final Color second, final Color third, int oct ) {
        mc.gameSettings.entityShadows = entityShadows;
        framebuffer.unbindFramebuffer( );
        GL11.glEnable( 3042 );
        GL11.glBlendFunc( 770, 771 );
        mc.getFramebuffer( ).bindFramebuffer( true );
        mc.entityRenderer.disableLightmap( );
        RenderHelper.disableStandardItemLighting( );
        startShader(color, radius, quality, gradientAlpha, alphaOutline, duplicate, second, third, oct);
        mc.entityRenderer.setupOverlayRendering( );
        drawFramebuffer( framebuffer );
        stopShader( );
        mc.entityRenderer.disableLightmap( );
        GlStateManager.popMatrix( );
        GlStateManager.popAttrib( );
    }

    public void startShader(final Color color, final float radius, final float quality, boolean gradientAlpha, int alphaOutline, float duplicate, final Color second, final Color third, int oct ) {
        GL11.glPushMatrix();
        GL20.glUseProgram(this.program);
        if (this.uniformsMap == null) {
            this.uniformsMap = new HashMap<String, Integer>();
            this.setupUniforms();
        }
        this.updateUniforms(color, radius, quality, gradientAlpha, alphaOutline, duplicate, second, third, oct);
    }

    static {
        INSTANCE = new SmokeOutlineShader();
    }

    public void update(double speed) {
        this.time += speed;
    }
}