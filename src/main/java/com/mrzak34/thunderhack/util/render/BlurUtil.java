package com.mrzak34.thunderhack.util.render;


import net.minecraft.client.renderer.*;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import java.nio.FloatBuffer;

import static com.mrzak34.thunderhack.util.Util.mc;


public class BlurUtil {
    public static ShaderUtil blurShader = new ShaderUtil("blurShader");
    public static Framebuffer framebuffer = new Framebuffer(1, 1, false);


    public static void uninitStencilBuffer() {
        GL11.glDisable(2960);
    }
    
    public static void drawBlur(float radius, Runnable data) {
        BlurUtil.initStencilToWrite();
        data.run();
        BlurUtil.readStencilBuffer(1);
        BlurUtil.renderBlur(radius);
        BlurUtil.uninitStencilBuffer();
    }
    public static void renderBlur(float radius) {
        GlStateManager.enableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        framebuffer = BlurUtil.createFrameBuffer(framebuffer);
        framebuffer.framebufferClear();
        framebuffer.bindFramebuffer(true);
        blurShader.init();
        setupUniforms(1.0F, 0.0F, radius);
        BlurUtil.bindTexture(mc.getFramebuffer().framebufferTexture);
        ShaderUtil.drawQuads();
        framebuffer.unbindFramebuffer();
        blurShader.unload();
        mc.getFramebuffer().bindFramebuffer(true);
        blurShader.init();
        setupUniforms(0.0F, 1.0F, radius);
        BlurUtil.bindTexture(framebuffer.framebufferTexture);
        ShaderUtil.drawQuads();
        blurShader.unload();
        GlStateManager.resetColor();
        GlStateManager.bindTexture(0);
    }
    public static void bindTexture(int texture) {
        GL11.glBindTexture(3553, texture);
    }
    public static void setupUniforms(float dir1, float dir2, float radius) {
        blurShader.setUniformi("textureIn", new int[]{0});
        blurShader.setUniformf("texelSize", new float[]{1.0F / (float)mc.displayWidth, 1.0F / (float)mc.displayHeight});
        blurShader.setUniformf("direction", new float[]{dir1, dir2});
        blurShader.setUniformf("radius", new float[]{radius});
        FloatBuffer weightBuffer = BufferUtils.createFloatBuffer(256);

        for(int i = 0; (float)i <= radius; ++i) {
            weightBuffer.put(calculateGaussianValue((float)i, radius / 2.0F));
        }

        weightBuffer.rewind();
        GL20.glUniform1(blurShader.getUniform("weights"), weightBuffer);
    }

    public static float calculateGaussianValue(float x, float sigma) {
        double PI = 3.141592653;
        double output = 1.0 / Math.sqrt(2.0 * PI * (double)(sigma * sigma));
        return (float)(output * Math.exp((double)(-(x * x)) / (2.0 * (double)(sigma * sigma))));
    }

    public static Framebuffer createFrameBuffer(Framebuffer framebuffer) {
        if (framebuffer != null && framebuffer.framebufferWidth == mc.displayWidth && framebuffer.framebufferHeight == mc.displayHeight) {
            return framebuffer;
        } else {
            if (framebuffer != null) {
                framebuffer.deleteFramebuffer();
            }

            return new Framebuffer(mc.displayWidth, mc.displayHeight, true);
        }
    }
    public static void initStencilToWrite() {
        mc.getFramebuffer().bindFramebuffer(false);
        checkSetupFBO(mc.getFramebuffer());
        GL11.glClear(1024);
        GL11.glEnable(2960);
        GL11.glStencilFunc(519, 1, 1);
        GL11.glStencilOp(7681, 7681, 7681);
        GL11.glColorMask(false, false, false, false);
    }

    public static void checkSetupFBO(Framebuffer framebuffer) {
        if (framebuffer != null && framebuffer.depthBuffer > -1) {
            setupFBO(framebuffer);
            framebuffer.depthBuffer = -1;
        }
    }
    public static void setupFBO(Framebuffer framebuffer) {
        EXTFramebufferObject.glDeleteRenderbuffersEXT(framebuffer.depthBuffer);
        int stencilDepthBufferID = EXTFramebufferObject.glGenRenderbuffersEXT();
        EXTFramebufferObject.glBindRenderbufferEXT(36161, stencilDepthBufferID);
        EXTFramebufferObject.glRenderbufferStorageEXT(36161, 34041, mc.displayWidth, mc.displayHeight);
        EXTFramebufferObject.glFramebufferRenderbufferEXT(36160, 36128, 36161, stencilDepthBufferID);
        EXTFramebufferObject.glFramebufferRenderbufferEXT(36160, 36096, 36161, stencilDepthBufferID);
    }


    public static void readStencilBuffer(int ref) {
        GL11.glColorMask(true, true, true, true);
        GL11.glStencilFunc(514, ref, 1);
        GL11.glStencilOp(7680, 7680, 7680);
    }
}