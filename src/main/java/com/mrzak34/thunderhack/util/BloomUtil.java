package com.mrzak34.thunderhack.util;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;

import java.nio.FloatBuffer;

import static com.mrzak34.thunderhack.util.RenderUtil.bindTexture;
import static com.mrzak34.thunderhack.gui.hud.WaterMark.createFrameBuffer;
import static com.mrzak34.thunderhack.util.ItemUtil.mc;
import static org.lwjgl.opengl.GL11.GL_GREATER;

public class BloomUtil {

    public static ShaderUtil gaussianBloom = new ShaderUtil("textures/bloom.frag");

    public static Framebuffer framebuffer = new Framebuffer(1, 1, false);


    public static void renderBlur(int sourceTexture, int radius, int offset) {
        framebuffer = createFrameBuffer(framebuffer);
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(516, 0.0f);
        GlStateManager.enableBlend();
        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);

        final FloatBuffer weightBuffer = BufferUtils.createFloatBuffer(256);
        for (int i = 0; i <= radius; i++) {
            weightBuffer.put(calculateGaussianValue(i, radius));
        }
        weightBuffer.rewind();

        setAlphaLimit(0.0F);

        framebuffer.framebufferClear();
        framebuffer.bindFramebuffer(true);
        gaussianBloom.init();
        setupUniforms(radius, offset, 0, weightBuffer);
        bindTexture(sourceTexture);
        ShaderUtil.drawQuads();
        gaussianBloom.unload();
        framebuffer.unbindFramebuffer();


        mc.getFramebuffer().bindFramebuffer(true);
        gaussianBloom.init();
        setupUniforms(radius, 0, offset, weightBuffer);
        GL13.glActiveTexture(GL13.GL_TEXTURE16);
        bindTexture(sourceTexture);
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        bindTexture(framebuffer.framebufferTexture);
        ShaderUtil.drawQuads();
        gaussianBloom.unload();

        GlStateManager.alphaFunc(516, 0.1f);
        GlStateManager.enableAlpha();

        GlStateManager.bindTexture(0);
    }
    public static void setAlphaLimit(float limit) {
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(GL_GREATER, (float) (limit * .01));
    }
    public static void setupUniforms(int radius, int directionX, int directionY, FloatBuffer weights) {
        gaussianBloom.setUniformi("inTexture", 0);
        gaussianBloom.setUniformi("textureToCheck", 16);
        gaussianBloom.setUniformf("radius", radius);
        gaussianBloom.setUniformf("texelSize", 1.0F / (float) mc.displayWidth, 1.0F / (float) mc.displayHeight);
        gaussianBloom.setUniformf("direction", directionX, directionY);
        GL20.glUniform1(gaussianBloom.getUniform("weights"), weights);
    }
    public static float calculateGaussianValue(float x, float sigma) {
        double PI = 3.141592653;
        double output = 1.0 / Math.sqrt(2.0 * PI * (sigma * sigma));
        return (float) (output * Math.exp(-(x * x) / (2.0 * (sigma * sigma))));
    }

}