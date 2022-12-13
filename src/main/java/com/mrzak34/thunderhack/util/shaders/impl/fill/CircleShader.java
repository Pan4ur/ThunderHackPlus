package com.mrzak34.thunderhack.util.shaders.impl.fill;


import com.mrzak34.thunderhack.util.shaders.FramebufferShader;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.awt.*;
import java.util.HashMap;

public class CircleShader extends FramebufferShader {

    public static final CircleShader INSTANCE;
    public float time;

    public CircleShader( ) {
        super( "circle.frag" );
    }

    @Override public void setupUniforms ( ) {
        this.setupUniform( "resolution" );
        this.setupUniform( "time" );
        this.setupUniform( "colors" );
        this.setupUniform( "PI" );
        this.setupUniform( "rad" );
    }

    public void updateUniforms (float duplicate, Color color, Float PI, Float rad) {
        GL20.glUniform2f( getUniform( "resolution" ), new ScaledResolution( mc ).getScaledWidth( ) / duplicate, new ScaledResolution( mc ).getScaledHeight( ) / duplicate );
        GL20.glUniform1f( getUniform( "time" ), this.time );
        GL20.glUniform1f( getUniform( "PI" ), PI.floatValue() );
        GL20.glUniform1f( getUniform( "rad" ), rad.floatValue() );
        GL20.glUniform4f( getUniform( "colors" ), color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f );
    }

    public void stopDraw(float duplicate, Color color, Float PI, Float rad) {
        mc.gameSettings.entityShadows = entityShadows;
        framebuffer.unbindFramebuffer( );
        GL11.glEnable( 3042 );
        GL11.glBlendFunc( 770, 771 );
        mc.getFramebuffer( ).bindFramebuffer( true );
        mc.entityRenderer.disableLightmap( );
        RenderHelper.disableStandardItemLighting( );
        startShader(duplicate, color, PI, rad);
        mc.entityRenderer.setupOverlayRendering( );
        drawFramebuffer( framebuffer );
        stopShader( );
        mc.entityRenderer.disableLightmap( );
        GlStateManager.popMatrix( );
        GlStateManager.popAttrib( );
    }

    public void startShader(float duplicate, Color color, Float PI, Float rad) {
        GL11.glPushMatrix();
        GL20.glUseProgram(this.program);
        if (this.uniformsMap == null) {
            this.uniformsMap = new HashMap<String, Integer>();
            this.setupUniforms();
        }
        this.updateUniforms(duplicate, color, PI, rad);
    }


    static {
        INSTANCE = new CircleShader();
    }

    public void update(double speed) {
        this.time += speed;
    }
}
