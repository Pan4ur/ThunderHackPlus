package com.mrzak34.thunderhack.gui.hud;

import com.jhlabs.image.GaussianFilter;
import com.mojang.realmsclient.gui.ChatFormatting;
import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.event.events.Render2DEvent;
import com.mrzak34.thunderhack.util.*;
import com.mrzak34.thunderhack.gui.thundergui.fontstuff.FontRender;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.ColorSetting;
import com.mrzak34.thunderhack.setting.Setting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import static org.lwjgl.opengl.GL11.*;

public class WaterMark extends Module {
    public WaterMark() {
        super("WaterMark", "WaterMark", Module.Category.HUD, true, false, false);
    }


    private Setting<mode> Mode = register(new Setting("Mode", mode.Classic));
    public enum mode {
        Cringe, Classic;
    }

    private final Setting<ColorSetting> cc = this.register(new Setting<>("Color", new ColorSetting(0x8800FF00)));
    private final Setting<ColorSetting> cs = this.register(new Setting<>("Color2", new ColorSetting(0x8800FF00)));
    private final Setting<ColorSetting> cs16 = this.register(new Setting<>("Color3", new ColorSetting(0x8800FF00)));

    int i = 0;
    public Timer timer = new Timer();

    @SubscribeEvent
    public void onRender2D(Render2DEvent e){

            if(Mode.getValue() == mode.Classic) {
                RenderUtil.drawSmoothRect(4f, 4f, Util.fr.getStringWidth("ThunderHack") + 4 + Util.fr.getStringWidth(mc.player.getName()) + Util.fr.getStringWidth(" 9999 мс ") + 140, 18f, new Color(35, 35, 40, 230).getRGB());

                if (timer.passedMs(350)) {
                    ++i;
                    timer.reset();
                }

                if (i == 24) {
                    i = 0;
                }

                String w1 = "_";
                String w2 = "T_";
                String w3 = "Th_";
                String w4 = "Thu_";
                String w5 = "Thun_";
                String w6 = "Thund_";
                String w7 = "Thunde_";
                String w8 = "Thunder_";
                String w9 = "ThunderH_";
                String w10 = "ThunderHa_";
                String w11 = "ThunderHac_";
                String w12 = "ThunderHack";
                String w13 = "ThunderHack";
                String w14 = "ThunderHack";
                String w15 = "ThunderHac_";
                String w16 = "ThunderHa_";
                String w17 = "ThunderH_";
                String w18 = "Thunder_";
                String w19 = "Thunde_";
                String w20 = "Thund_";
                String w21 = "Thun_";
                String w22 = "Thu_";
                String w23 = "Th_";
                String w24 = "T_";
                String w25 = "_";
                String text = "";
                if (i == 0) {
                    text = w1;
                }
                if (i == 1) {
                    text = w2;
                }
                if (i == 2) {
                    text = w3;
                }
                if (i == 3) {
                    text = w4;
                }
                if (i == 4) {
                    text = w5;
                }
                if (i == 5) {
                    text = w6;
                }
                if (i == 6) {
                    text = w7;
                }
                if (i == 7) {
                    text = w8;
                }
                if (i == 8) {
                    text = w9;
                }
                if (i == 9) {
                    text = w10;
                }
                if (i == 10) {
                    text = w11;
                }
                if (i == 11) {
                    text = w12;
                }
                if (i == 12) {
                    text = w13;
                }
                if (i == 13) {
                    text = w14;
                }
                if (i == 14) {
                    text = w15;
                }
                if (i == 15) {
                    text = w16;
                }
                if (i == 16) {
                    text = w17;
                }
                if (i == 17) {
                    text = w18;
                }
                if (i == 18) {
                    text = w19;
                }
                if (i == 19) {
                    text = w20;
                }
                if (i == 20) {
                    text = w21;
                }
                if (i == 21) {
                    text = w22;
                }
                if (i == 22) {
                    text = w23;
                }
                if (i == 23) {
                    text = w24;
                }
                if (i == 23) {
                    text = w25;
                }


                Util.fr.drawStringWithShadow(text, 9f, 7, -1);
                Util.fr.drawStringWithShadow("|  " + mc.player.getName(), Util.fr.getStringWidth(w13) + 20, 7, -1);
                Util.fr.drawStringWithShadow("|  " + Thunderhack.serverManager.getPing() + " мс", Util.fr.getStringWidth(w13) + 35 + Util.fr.getStringWidth(mc.player.getName()), 7, -1);
                try {
                    Util.fr.drawStringWithShadow("|  " + (Minecraft.getMinecraft().currentServerData.serverIP), Util.fr.getStringWidth(w13) + 38 + Util.fr.getStringWidth(mc.player.getName()) + Util.fr.getStringWidth(" 9999 мс "), 7, -1);
                } catch (Exception ew) {
                    Util.fr.drawStringWithShadow("|  " + ("SinglePlayer"), Util.fr.getStringWidth(w13) + 38 + Util.fr.getStringWidth(mc.player.getName()) + Util.fr.getStringWidth(" 9999 мс "), 7, -1);
                }
            } else {
                GlStateManager.pushMatrix();

                drawBlurredShadow(20.0f, 10.0f, 90.0f, 22.0f, 10, new Color(0, 0, 0, 180));
                RoundedShader.drawGradientRound(20.0f, 10.0f, 90.0f, 27.0f, 4.0f,cc.getValue().getColorObject(), cs.getValue().getColorObject(),  cs16.getValue().getColorObject(),  cs16.getValue().getColorObject());
                drawShadow(10, 1, () -> {
                    RoundedShader.drawGradientRound(8.0f, 7.0f, 24.3f, 30.3f, 12.1f,cc.getValue().getColorObject(), cs.getValue().getColorObject(), cs16.getValue().getColorObject(),  cs16.getValue().getColorObject());
                });
                RoundedShader.drawGradientRound(7.0f, 7.0f, 25.3f, 25.3f, 12.1f, cc.getValue().getColorObject(), cs.getValue().getColorObject(),  cs16.getValue().getColorObject(),  cs16.getValue().getColorObject());
                FontRender.drawString6("ThunderHack+", 36.0f, 13.0f, new Color(255, 255, 255).getRGB(),true);
                FontRender.drawString6(ChatFormatting.WHITE + mc.player.getName() + "  " + ChatFormatting.GRAY + Thunderhack.serverManager.getPing() + " мс ", 36.0f, 23.0f, -1,true);
                drawImage(new ResourceLocation("textures/suka.png"), 9.0f, 9.0f, 22.0f, 22.0f, new Color(0xFFFFFF));
                drawImage(new ResourceLocation("textures/lightning.png"), 9.0f, 9.0f, 22.0f, 22.0f, new Color(0xFFFFFF));
                GL11.glEnable(GL_BLEND);
                GlStateManager.popMatrix();
       }
    }

    public static void drawBlurredShadow(float x, float y, float width, float height, int blurRadius, Color color) {
        glPushMatrix();
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.01f);

        width = width + blurRadius * 2;
        height = height + blurRadius * 2;
        x = x - blurRadius;
        y = y - blurRadius;

        float _X = x - 0.25f;
        float _Y = y + 0.25f;

        int identifier = (int) (width * height + width + color.hashCode() * blurRadius + blurRadius);

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        glDisable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GlStateManager.enableBlend();

        int texId = -1;
        if (shadowCache.containsKey(identifier)) {
            texId = shadowCache.get(identifier);

            GlStateManager.bindTexture(texId);
        } else {
            if (width <= 0) width = 1;
            if (height <= 0) height = 1;
            BufferedImage original = new BufferedImage((int) width, (int) height, BufferedImage.TYPE_INT_ARGB_PRE);

            Graphics g = original.getGraphics();
            g.setColor(color);
            g.fillRect(blurRadius, blurRadius, (int) (width - blurRadius * 2), (int) (height - blurRadius * 2));
            g.dispose();

            GaussianFilter op = new GaussianFilter(blurRadius);

            BufferedImage blurred = op.filter(original, null);


            texId = TextureUtil.uploadTextureImageAllocate(TextureUtil.glGenTextures(), blurred, true, false);

            shadowCache.put(identifier, texId);
        }

        GL11.glColor4f(1f, 1f, 1f, 1f);

        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(0, 0); // top left
        GL11.glVertex2f(_X, _Y);

        GL11.glTexCoord2f(0, 1); // bottom left
        GL11.glVertex2f(_X, _Y + height);

        GL11.glTexCoord2f(1, 1); // bottom right
        GL11.glVertex2f(_X + width, _Y + height);

        GL11.glTexCoord2f(1, 0); // top right
        GL11.glVertex2f(_X + width, _Y);
        GL11.glEnd();

        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.resetColor();

        glEnable(GL_CULL_FACE);
        glPopMatrix();
    }

    private static Framebuffer bloomFramebuffer = new Framebuffer(1, 1, false);
    public static Framebuffer createFrameBuffer(Framebuffer framebuffer) {
        if (framebuffer == null || framebuffer.framebufferWidth != mc.displayWidth || framebuffer.framebufferHeight != mc.displayHeight) {
            if (framebuffer != null) {
                framebuffer.deleteFramebuffer();
            }
            return new Framebuffer(mc.displayWidth, mc.displayHeight, true);
        }
        return framebuffer;
    }

    public static void stuffToBlur(boolean bloom) {

        // Gui.drawRect2(40, 40, 400, 40, -1);

    }
    public static void setColor(int color) {
        GL11.glColor4ub((byte) (color >> 16 & 0xFF), (byte) (color >> 8 & 0xFF), (byte) (color & 0xFF), (byte) (color >> 24 & 0xFF));
    }
    public static void drawImage(ResourceLocation resourceLocation, float x, float y, float width, float height, Color color) {
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDepthMask(false);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        setColor(color.getRGB());
        Minecraft.getMinecraft().getTextureManager().bindTexture(resourceLocation);
        Gui.drawModalRectWithCustomSizedTexture((int)x, (int) y, 0, 0, (int)width, (int)height, width, height);
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }

    public static void drawShadow(float radius, float offset, Runnable data) {
        bloomFramebuffer = createFrameBuffer(bloomFramebuffer);
        bloomFramebuffer.framebufferClear();
        bloomFramebuffer.bindFramebuffer(true);
        data.run();
        stuffToBlur(true);
        bloomFramebuffer.unbindFramebuffer();
        BloomUtil.renderBlur(bloomFramebuffer.framebufferTexture, (int) radius, (int) offset);

    }
    private static HashMap<Integer, Integer> shadowCache = new HashMap<Integer, Integer>();



}
