package com.mrzak34.thunderhack.gui.hud;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.event.events.Render2DEvent;
import com.mrzak34.thunderhack.event.events.RenderAttackIndicatorEvent;
import com.mrzak34.thunderhack.util.RenderUtil;
import com.mrzak34.thunderhack.util.RoundedShader;
import com.mrzak34.thunderhack.gui.thundergui.fontstuff.FontRender;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.modules.combat.EZbowPOP;
import com.mrzak34.thunderhack.setting.ColorSetting;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.PaletteHelper;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.init.Items;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;

public class CoolCrosshair extends Module {
    public CoolCrosshair() {
        super("CoolCrosshair", "CoolCrosshair", Category.HUD, true, false, false);
    }

    public final Setting<ColorSetting> color = this.register(new Setting<>("Color", new ColorSetting(0x8800FF00)));




    public Setting<Float> car = this.register(new Setting<Float>("otstup", Float.valueOf(0.0f), Float.valueOf(0.1f), Float.valueOf(1.0f)));
    private final Setting<Boolean> smt = this.register(new Setting<Boolean>("smooth", Boolean.FALSE));
    public Setting<Float> lwid = this.register(new Setting<Float>("otstup", Float.valueOf(0.0f), Float.valueOf(0.1f), Float.valueOf(1.0f)));
    public Setting<Float> rounded2 = this.register(new Setting<Float>("Round2", Float.valueOf(0.0f), Float.valueOf(0.5f), Float.valueOf(20.0f)));











    @SubscribeEvent
    public void onRenderAttackIndicator(RenderAttackIndicatorEvent event) {
        event.setCanceled(true);
    }

    int status = 0;


    int santi = 0;


    @Override
    public void onUpdate(){

        if(!EZbowPOP.Ready){
            if(animation < 20){
                animation += 1f;
            }
        }

        if(santi<status){
            santi = santi + 60;
        }
        if(status<santi){
            santi = santi-360;
        }
        if(santi<0){
            santi = 0;
        }

    }

    float animation = 0;
    @SubscribeEvent
    public void onRender2D(Render2DEvent event) {
        ScaledResolution sr = new ScaledResolution(mc);
        float x1 = (float) (sr.getScaledWidth_double() / 2F);
        float y1 = (float) (sr.getScaledHeight_double() / 2F);


      // Command.sendMessage(String.valueOf(mc.itemRenderer.equippedProgressMainHand));



        boolean blend = GL11.glIsEnabled(GL_BLEND);
        GL11.glEnable(GL_BLEND);

        if(EZbowPOP.Ready || mc.player.getHeldItemMainhand().getItem() != Items.BOW) {
            animation = 0f;
            status = (int) (360 / (1f / mc.itemRenderer.equippedProgressMainHand));
            drawPartialCircle(x1, y1, rounded2.getValue(), 0, 360, lwid.getValue(), color.getValue().withAlpha(color.getValue().getAlpha() > 210 ? color.getValue().getAlpha() : color.getValue().getAlpha() + 40).getColorObject(), smt.getValue());
            drawPartialCircle(x1, y1, rounded2.getValue() - car.getValue(), 0, 360, lwid.getValue(), color.getValue().withAlpha(color.getValue().getAlpha() > 210 ? color.getValue().getAlpha() : color.getValue().getAlpha() + 40).getColorObject(), smt.getValue());
            drawPartialCircle(x1, y1, rounded2.getValue() + car.getValue(), 0, 360, lwid.getValue(), color.getValue().withAlpha(color.getValue().getAlpha() > 210 ? color.getValue().getAlpha() : color.getValue().getAlpha() + 40).getColorObject(), smt.getValue());
            drawPartialCircle(x1, y1, rounded2.getValue(), 0, status, lwid.getValue(), PaletteHelper.astolfo(false, 1), smt.getValue());
            drawPartialCircle(x1, y1, rounded2.getValue() - car.getValue(), 0, status, lwid.getValue(), PaletteHelper.astolfo(false, 1), smt.getValue());
            drawPartialCircle(x1, y1, rounded2.getValue() + car.getValue(), 0, status, lwid.getValue(), PaletteHelper.astolfo(false, 1), smt.getValue());
        } else {
            if(animation < 20){
                animation += 1f;
            }
            RoundedShader.drawRound(x1 - animation, y1 - 3f, animation*2, 6, 4,  new Color(0x0A0A0A));
            RenderUtil.glScissor(x1 - animation, y1 - 3f, x1 + animation*2, x1 + 6, sr);
            GL11.glEnable(GL11.GL_SCISSOR_TEST);

            if(EZbowPOP.ticks > (float)Thunderhack.moduleManager.getModuleByClass(EZbowPOP.class).getMaxDelay() * 0.666f){
                FontRender.drawCentString5("charging.  ",x1,y1 - 0.5f,-1);
            } else if(EZbowPOP.ticks > Thunderhack.moduleManager.getModuleByClass(EZbowPOP.class).getMaxDelay()/3f){
                FontRender.drawCentString5("charging.. ",x1,y1 - 0.5f,-1);
            } else{
                FontRender.drawCentString5("charging...",x1,y1 - 0.5f,-1);
            }
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
        }



        if(!blend){
            GL11.glDisable(GL_BLEND);
        }
    }


    public static void drawPartialCircle(float x, float y, float radius, int startAngle, int endAngle, float thickness, Color colour, boolean smooth) {
        preRender();
        if (startAngle > endAngle) {
            int temp = startAngle;
            startAngle = endAngle;
            endAngle = temp;
        }
        if (startAngle < 0)
            startAngle = 0;
        if (endAngle > 360)
            endAngle = 360;
        if (smooth) {
            GL11.glEnable(GL_LINE_SMOOTH);
        } else {
            GL11.glDisable(GL_LINE_SMOOTH);
        }
        GL11.glLineWidth(thickness);
        GL11.glColor4f(colour.getRed() / 255.0F, colour.getGreen() / 255.0F, colour.getBlue() / 255.0F, colour.getAlpha() / 255.0F);
        GL11.glBegin(GL_LINE_STRIP);
        float ratio = 0.01745328F;
        for (int i = startAngle; i <= endAngle; i = i + 1) {
            float radians = (i - 90) * ratio;
            GL11.glVertex2f(x + (float)Math.cos(radians) * radius, y + (float)Math.sin(radians) * radius);
        }
        GL11.glEnd();
        postRender();
    }



    public static void preRender() {
        GL11.glDisable(GL_TEXTURE_2D);
        GL11.glBlendFunc(770, 771);
    }

    public static void postRender() {
        GL11.glEnable(GL_TEXTURE_2D);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }
}
