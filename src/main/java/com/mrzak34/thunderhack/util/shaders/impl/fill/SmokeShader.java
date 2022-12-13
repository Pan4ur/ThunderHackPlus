package com.mrzak34.thunderhack.util.shaders.impl.fill;


import com.mrzak34.thunderhack.util.shaders.FramebufferShader;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.awt.*;
import java.util.HashMap;

public class SmokeShader extends FramebufferShader {

    public static final SmokeShader INSTANCE;
    public float time;

    public SmokeShader( ) {
        super( "smoke.frag" );
    }

    public void setupUniforms ( ) {
        this.setupUniform( "resolution" );
        this.setupUniform( "time" );
        this.setupUniform("first");
        this.setupUniform("second");
        this.setupUniform("third");
        this.setupUniform("oct");
    }

    public void updateUniforms ( float duplicate, final Color first, final Color second, final Color third, int oct ) {
        GL20.glUniform2f( getUniform( "resolution" ), new ScaledResolution( mc ).getScaledWidth( )/duplicate, new ScaledResolution( mc ).getScaledHeight( )/duplicate );
        GL20.glUniform1f( getUniform( "time" ), time );
        GL20.glUniform4f(getUniform("first"), first.getRed() / 255.0f * 5, first.getGreen() / 255.0f * 5, first.getBlue() / 255.0f * 5, first.getAlpha() / 255.0f );
        GL20.glUniform3f(getUniform("second"), second.getRed() / 255.0f * 5, second.getGreen() / 255.0f * 5, second.getBlue() / 255.0f * 5 );
        GL20.glUniform3f(getUniform("third"), third.getRed() / 255.0f * 5, third.getGreen() / 255.0f * 5, third.getBlue() / 255.0f * 5 );
        GL20.glUniform1i(getUniform("oct"), oct);
    }
    static {
        INSTANCE = new SmokeShader();
    }

    public void stopDraw(final Color color, final float radius, final float quality, float duplicate, final Color first, final Color second, final Color third, int oct ) {
        mc.gameSettings.entityShadows = entityShadows;
        framebuffer.unbindFramebuffer( );
        GL11.glEnable( 3042 );
        GL11.glBlendFunc( 770, 771 );
        mc.getFramebuffer( ).bindFramebuffer( true );
        red = color.getRed( ) / 255.0f;
        green = color.getGreen( ) / 255.0f;
        blue = color.getBlue( ) / 255.0f;
        alpha = color.getAlpha( ) / 255.0f;
        this.radius = radius;
        this.quality = quality;
        mc.entityRenderer.disableLightmap( );
        RenderHelper.disableStandardItemLighting( );
        startShader(duplicate, first, second, third, oct);
        mc.entityRenderer.setupOverlayRendering( );
        drawFramebuffer( framebuffer );
        stopShader( );
        mc.entityRenderer.disableLightmap( );
        GlStateManager.popMatrix( );
        GlStateManager.popAttrib( );
    }

    public void startShader(float duplicate, final Color first, final Color second, final Color third, int oct) {
        GL11.glPushMatrix();
        GL20.glUseProgram(this.program);
        if (this.uniformsMap == null) {
            this.uniformsMap = new HashMap<String, Integer>();
            this.setupUniforms();
        }
        this.updateUniforms(duplicate, first, second, third, oct);
    }

    public void update(double speed) {
        this.time += speed;
    }
}
