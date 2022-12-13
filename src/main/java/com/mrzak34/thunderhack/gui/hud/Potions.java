package com.mrzak34.thunderhack.gui.hud;

import com.mrzak34.thunderhack.event.events.Render2DEvent;
import com.mrzak34.thunderhack.gui.thundergui.fontstuff.FontRender;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.ColorSetting;
import com.mrzak34.thunderhack.setting.PositionSetting;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.DrawHelper;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.BufferBuilder;
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
        super("Potions", "Potions", Module.Category.HUD, true, false, false);
    }

    public final Setting<ColorSetting> color = this.register(new Setting<>("Color", new ColorSetting(0x8800FF00)));

    private final Setting<PositionSetting> pos = this.register(new Setting<>("Position", new PositionSetting(0.5f,0.5f)));

    public Setting<Float> grange = register(new Setting("GlowRange", 3.6f, 0.0f, 10.0f));
    public Setting<Float> gmult = register(new Setting("GlowMultiplier", 3.6f, 0.0f, 10.0f));

    float x1 =0;
    float y1= 0;

    @SubscribeEvent
    public void onRender2D(Render2DEvent e){
        ScaledResolution sr = new ScaledResolution(mc);
        y1 = sr.getScaledHeight() * pos.getValue().getY();
        x1 = sr.getScaledWidth() * pos.getValue().getX();

      //  Util.fr.drawStringWithShadow(str,x1,y1, color.getValue().getRawColor());


        int i = 0;


        java.util.ArrayList<PotionEffect> effects = new ArrayList();
        for (PotionEffect potionEffect : mc.player.getActivePotionEffects()) {
            if (potionEffect.getDuration() != 0
                    && !potionEffect.getPotion().getName().contains("effect.nightVision")) {
                effects.add(potionEffect);
            }
        }
        int j = sr.getScaledHeight() / 2 - (effects.size() * 24) / 2;
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


        if(mc.currentScreen instanceof GuiChat || mc.currentScreen instanceof HudEditorGui){
            if(isHovering()){
                if(Mouse.isButtonDown(0) && mousestate){
                    pos.getValue().setX( (float) (normaliseX() - dragX) /  sr.getScaledWidth());
                    pos.getValue().setY( (float) (normaliseY() - dragY) / sr.getScaledHeight());
                }
            }
        }

        if(Mouse.isButtonDown(0) && isHovering()){
            if(!mousestate){
                dragX = (int) (normaliseX() - (pos.getValue().getX() * sr.getScaledWidth()));
                dragY = (int) (normaliseY() - (pos.getValue().getY() * sr.getScaledHeight()));
            }
            mousestate = true;
        } else {
            mousestate = false;
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
        return normaliseX() > x1 - 10 && normaliseX()< x1 + 50 && normaliseY() > y1 &&  normaliseY() < y1 + 10;
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
