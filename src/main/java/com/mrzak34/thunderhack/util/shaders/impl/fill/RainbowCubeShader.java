package com.mrzak34.thunderhack.util.shaders.impl.fill;


import com.mrzak34.thunderhack.util.shaders.FramebufferShader;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.awt.*;
import java.util.HashMap;

public class RainbowCubeShader extends FramebufferShader {

    public static final RainbowCubeShader INSTANCE;
    public float time;

    public RainbowCubeShader( ) {
        super( "rainbowCube.frag" );
    }

    @Override public void setupUniforms ( ) {
        this.setupUniform( "resolution" );
        this.setupUniform( "time" );
        this.setupUniform("alpha");
        this.setupUniform("WAVELENGTH");
        this.setupUniform("R");
        this.setupUniform("G");
        this.setupUniform("B");
        this.setupUniform("RSTART");
        this.setupUniform("GSTART");
        this.setupUniform("BSTART");
    }

    public void updateUniforms ( float duplicate, Color start, int wave, int rStart, int gStart, int bStart ) {
        GL20.glUniform2f( getUniform( "resolution" ), new ScaledResolution( mc ).getScaledWidth( )/duplicate, new ScaledResolution( mc ).getScaledHeight( )/duplicate );
        GL20.glUniform1f( getUniform( "time" ), time );
        GL20.glUniform1f(getUniform("alpha"), start.getAlpha() / 255.0f);
        GL20.glUniform1f(getUniform("WAVELENGTH"), (float) wave);
        GL20.glUniform1i(getUniform("R"), start.getRed());
        GL20.glUniform1i(getUniform("G"), start.getGreen());
        GL20.glUniform1i(getUniform("B"), start.getBlue());
        GL20.glUniform1i(getUniform("RSTART"), rStart);
        GL20.glUniform1i(getUniform("GSTART"), gStart);
        GL20.glUniform1i(getUniform("BSTART"), bStart);


    }
    static {
        INSTANCE = new RainbowCubeShader();
    }

    public void stopDraw(final Color color, final float radius, final float quality, float duplicate, Color start, int wave, int rStart, int gStart, int bStart ) {
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
        startShader(duplicate, start, wave, rStart, gStart, bStart);
        mc.entityRenderer.setupOverlayRendering( );
        drawFramebuffer( framebuffer );
        stopShader( );
        mc.entityRenderer.disableLightmap( );
        GlStateManager.popMatrix( );
        GlStateManager.popAttrib( );
    }

    public void startShader(float duplicate, Color start, int wave, int rStart, int gStart, int bStart) {
        GL11.glPushMatrix();
        GL20.glUseProgram(this.program);
        if (this.uniformsMap == null) {
            this.uniformsMap = new HashMap<String, Integer>();
            this.setupUniforms();
        }
        this.updateUniforms(duplicate, start, wave, rStart, gStart, bStart);
    }

    public void update(double speed) {
        this.time += speed;
    }
}
