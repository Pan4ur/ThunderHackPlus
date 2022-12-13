package com.mrzak34.thunderhack.gui.thundergui;


import com.mrzak34.thunderhack.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class BlurUtil {

    private void setInstance() {
        INSTANCE = this;
    }
    private static BlurUtil INSTANCE = new BlurUtil();
    protected Minecraft mc = Util.mc;
    private final ResourceLocation resourceLocation = new ResourceLocation("shaders/post/blur.json");
    private ShaderGroup shaderGroup;
    private Framebuffer framebuffer;

    private int lastFactor;
    private int lastWidth;
    private int lastHeight;

    public void init() {
        try {
            this.setInstance();
            shaderGroup = new ShaderGroup(mc.getTextureManager(), mc.getResourceManager(), mc.getFramebuffer(), resourceLocation);
            shaderGroup.createBindFramebuffers(mc.displayWidth, mc.displayHeight);
            framebuffer = shaderGroup.mainFramebuffer;

        } catch (Exception e) {
            System.out.println("Fuck Blur");
            e.printStackTrace();
        }
    }

    public static BlurUtil getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new BlurUtil();
        }
        return INSTANCE;
    }

    public void blur(float xBlur, float yBlur, float widthBlur, float heightBlur, int strength) {
        ScaledResolution scaledResolution = new ScaledResolution(mc);

        int scaleFactor = scaledResolution.getScaleFactor();
        int width = scaledResolution.getScaledWidth();
        int height = scaledResolution.getScaledHeight();

        if (sizeHasChanged(scaleFactor, width, height) || framebuffer == null || shaderGroup == null) {
            init();
        }

        lastFactor = scaleFactor;
        lastWidth = width;
        lastHeight = height;

        GL11.glPushMatrix();

        GL11.glEnable(GL11.GL_SCISSOR_TEST);

        scissorRect(xBlur, yBlur, widthBlur, heightBlur);

        framebuffer.bindFramebuffer(true);
        shaderGroup.render(mc.timer.renderPartialTicks);

        for (int i = 0; i < 1; i++) {
            shaderGroup.listShaders.get(i).getShaderManager().getShaderUniform("Radius").set(strength);
        }

        mc.getFramebuffer().bindFramebuffer(false);

        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        GL11.glPopMatrix();
    }

    private boolean sizeHasChanged(int scaleFactor, int width, int height) {
        return (lastFactor != scaleFactor || lastWidth != width || lastHeight != height);
    }

    public static void scissorRect(float x, float y, float width, double height) {
        ScaledResolution sr = new ScaledResolution(Util.mc);
        int factor = sr.getScaleFactor();
        GL11.glScissor((int) (x * (float) factor), (int) (((float) sr.getScaledHeight() - height) * (float) factor), (int) ((width - x) * (float) factor), (int) ((height - y) * (float) factor));
    }
}