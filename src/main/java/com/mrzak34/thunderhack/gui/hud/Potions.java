package com.mrzak34.thunderhack.gui.hud;

import com.mrzak34.thunderhack.events.Render2DEvent;
import com.mrzak34.thunderhack.gui.fontstuff.FontRender;
import com.mrzak34.thunderhack.gui.thundergui2.ThunderGui2;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.ColorSetting;
import com.mrzak34.thunderhack.setting.PositionSetting;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.render.DrawHelper;
import com.mrzak34.thunderhack.util.render.RenderUtil;
import com.mrzak34.thunderhack.util.RoundedShader;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.StringUtils;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.util.ArrayList;

public class Potions extends Module{
    public Potions() {
        super("Potions", "Potions", Module.Category.HUD);
    }

    private Setting<Modes> mode = register(new Setting("Mode", Modes.New));

    public enum Modes {
        Wexside, New
    }

    public final Setting<ColorSetting> color = this.register(new Setting<>("WexColor", new ColorSetting(0x8800FF00), v-> mode.getValue() != Modes.New));
    private final Setting<PositionSetting> pos = this.register(new Setting<>("Position", new PositionSetting(0.5f,0.5f)));





    public Setting<Float> grange = register(new Setting("GlowRange", 3.6f, 0.0f, 10.0f, v-> mode.getValue() == Modes.Wexside));
    public Setting<Float> gmult = register(new Setting("GlowMultiplier", 3.6f, 0.0f, 10.0f, v-> mode.getValue() == Modes.Wexside));



    public final Setting<ColorSetting> shadowColor = this.register(new Setting<>("ShadowColor", new ColorSetting(0xFF101010), v-> mode.getValue() == Modes.New));
    public final Setting<ColorSetting> color2 = this.register(new Setting<>("Color", new ColorSetting(0xFF101010), v-> mode.getValue() == Modes.New));
    public final Setting<ColorSetting> color3 = this.register(new Setting<>("Color2",  new ColorSetting(0xC59B9B9B), v-> mode.getValue() == Modes.New));
    public final Setting<ColorSetting> textColor = this.register(new Setting<>("TextColor", new ColorSetting(0xBEBEBE), v-> mode.getValue() == Modes.New));
    public final Setting<ColorSetting> oncolor = this.register(new Setting<>("TextColor", new ColorSetting(0xBEBEBE), v-> mode.getValue() == Modes.New));


    float x1 =0;
    float y1= 0;

    @SubscribeEvent
    public void onRender2D(Render2DEvent e){
        y1 = e.scaledResolution.getScaledHeight() * pos.getValue().getY();
        x1 = e.scaledResolution.getScaledWidth() * pos.getValue().getX();




        if(mode.getValue() == Modes.New){
            drawNew();
        } else{
            drawWexside(e);
        }

        if(mc.currentScreen instanceof GuiChat || mc.currentScreen instanceof HudEditorGui || mc.currentScreen instanceof ThunderGui2){
            if(isHovering()){
                if(Mouse.isButtonDown(0) && mousestate){
                    pos.getValue().setX( (float) (normaliseX() - dragX) /  e.scaledResolution.getScaledWidth());
                    pos.getValue().setY( (float) (normaliseY() - dragY) / e.scaledResolution.getScaledHeight());
                }
            }
        }

        if(Mouse.isButtonDown(0) && isHovering()){
            if(!mousestate){
                dragX = (int) (normaliseX() - (pos.getValue().getX() * e.scaledResolution.getScaledWidth()));
                dragY = (int) (normaliseY() - (pos.getValue().getY() * e.scaledResolution.getScaledHeight()));
            }
            mousestate = true;
        } else {
            mousestate = false;
        }
    }



    private void drawNew(){
        int y_offset1 = 0;
        java.util.ArrayList<PotionEffect> effects = new ArrayList<>();

        for (PotionEffect potionEffect : mc.player.getActivePotionEffects()) {
            if (potionEffect.getDuration() != 0 && !potionEffect.getPotion().getName().contains("effect.nightVision")) {
                effects.add(potionEffect);
                y_offset1 += 10;
            }
        }


        GlStateManager.pushMatrix();

        RenderUtil.drawBlurredShadow(x1,y1,100,20 + y_offset1, 20, shadowColor.getValue().getColorObject());


        RoundedShader.drawRound(x1,y1,100,20 + y_offset1, 7f, color2.getValue().getColorObject());
        FontRender.drawCentString6("Potions", x1 + 50, y1 + 5, textColor.getValue().getColor());
        RoundedShader.drawRound(x1 + 2,y1 + 13,96,1, 0.5f, color3.getValue().getColorObject());

        int y_offset = 0;


        for (PotionEffect potionEffect : effects) {
            Potion potion = potionEffect.getPotion();
            String power = "";
            if (potionEffect.getAmplifier() == 0) {
                power = "I";
            } else if (potionEffect.getAmplifier() == 1) {
                power = "II";
            } else if (potionEffect.getAmplifier() == 2) {
                power = "III";
            } else if (potionEffect.getAmplifier() == 3) {
                power = "IV";
            } else if (potionEffect.getAmplifier() == 4) {
                power = "V";
            }
            String s = potionEffect.getPotion().getName().replace("effect.", "") + " " + power;
            String s2 = getDuration(potionEffect) + "";


            GlStateManager.pushMatrix();
            GlStateManager.resetColor();
            FontRender.drawString6(s + "  " + s2, x1 + 5, y1 + 20 + y_offset,  oncolor.getValue().getColor(),false);
            GlStateManager.resetColor();
            GlStateManager.popMatrix();
            y_offset+= 10;
        }


        GlStateManager.popMatrix();

    }


    private void drawWexside(Render2DEvent e){
        int i = 0;


        java.util.ArrayList<PotionEffect> effects = new ArrayList<>();
        for (PotionEffect potionEffect : mc.player.getActivePotionEffects()) {
            if (potionEffect.getDuration() != 0 && !potionEffect.getPotion().getName().contains("effect.nightVision")) {
                effects.add(potionEffect);
            }
        }
        int j = e.scaledResolution.getScaledHeight() / 2 - (effects.size() * 24) / 2;
        for (PotionEffect potionEffect : effects) {
            Potion potion = potionEffect.getPotion();
            String power = "";
            if (potionEffect.getAmplifier() == 0) {
                power = "I";
            } else if (potionEffect.getAmplifier() == 1) {
                power = "II";
            } else if (potionEffect.getAmplifier() == 2) {
                power = "III";
            } else if (potionEffect.getAmplifier() == 3) {
                power = "IV";
            } else if (potionEffect.getAmplifier() == 4) {
                power = "V";
            }
            String s = I18n.format(potionEffect.getPotion().getName()) + " " + power;
            String s2 = getDuration(potionEffect) + "";
            float maxWidth = Math.max(FontRender.getStringWidth6(s), FontRender.getStringWidth6(s2))
                    + 32;


            DrawHelper.drawRectWithGlow(i + 2, j + 5, maxWidth - 4 + i + 2, 18.5f + j + 5,grange.getValue(),gmult.getValue(),color.getValue().getColorObject());


            this.mc.getTextureManager().bindTexture(GuiContainer.INVENTORY_BACKGROUND);
            if (potion.hasStatusIcon()) {
                int i1 = potion.getStatusIconIndex();
                drawTexturedModalRect(i + 5, j + 7, i1 % 8 * 18, 198 + i1 / 8 * 18, 18, 18);
            }
            FontRender.drawString6(s, i + 28, j + 11.5f, new Color(205, 205, 205, 205).getRGB(),false);
            FontRender.drawString6(s2, i + 28, j + 18.5f, new Color(205, 205, 205, 205).getRGB(),false);
            j += 24;
        }
    }







    int dragX, dragY = 0;
    boolean mousestate = false;

    public int normaliseX(){
        return (int) ((Mouse.getX()/2f));
    }
    public int normaliseY(){
        ScaledResolution sr = new ScaledResolution(mc);
        return (((-Mouse.getY() + sr.getScaledHeight()) + sr.getScaledHeight())/2);
    }

    public boolean isHovering(){
        return normaliseX() > x1 && normaliseX()< x1 + 100 && normaliseY() > y1 &&  normaliseY() < y1 + 100;
    }
    int zLevel = 0;
    public void drawTexturedModalRect(int x, int y, int textureX, int textureY, int width, int height) {
        float f = 0.00390625F;
        float f1 = 0.00390625F;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos((double)(x + 0), (double)(y + height), (double)this.zLevel).tex((double)((float)(textureX + 0) * 0.00390625F), (double)((float)(textureY + height) * 0.00390625F)).endVertex();
        bufferbuilder.pos((double)(x + width), (double)(y + height), (double)this.zLevel).tex((double)((float)(textureX + width) * 0.00390625F), (double)((float)(textureY + height) * 0.00390625F)).endVertex();
        bufferbuilder.pos((double)(x + width), (double)(y + 0), (double)this.zLevel).tex((double)((float)(textureX + width) * 0.00390625F), (double)((float)(textureY + 0) * 0.00390625F)).endVertex();
        bufferbuilder.pos((double)(x + 0), (double)(y + 0), (double)this.zLevel).tex((double)((float)(textureX + 0) * 0.00390625F), (double)((float)(textureY + 0) * 0.00390625F)).endVertex();
        tessellator.draw();
    }
    public static String getDuration(PotionEffect potionEffect) {
        if (potionEffect.getIsPotionDurationMax()) {
            return "**:**";
        } else {
            return StringUtils.ticksToElapsedTime(potionEffect.getDuration());
        }
    }
}
