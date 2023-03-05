package com.mrzak34.thunderhack.gui.hud;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.events.Render2DEvent;
import com.mrzak34.thunderhack.gui.fontstuff.FontRender;
import com.mrzak34.thunderhack.setting.ColorSetting;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.*;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.util.render.RenderUtil;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

public class WaterMark extends Module {
    public WaterMark() {
        super("WaterMark", "WaterMark", Module.Category.HUD);
    }

    int i = 0;
    public Timer timer = new Timer();

    public final Setting<ColorSetting> color1 = this.register(new Setting<>("TextColor", new ColorSetting(-1)));
    public final Setting<ColorSetting> color2 = this.register(new Setting<>("Color", new ColorSetting(0xFF101010)));
    public final Setting<ColorSetting> shadowColor = this.register(new Setting<>("ShadowColor", new ColorSetting(0xFF101010)));

    @SubscribeEvent
    public void onRender2D(Render2DEvent e){

        RenderUtil.drawBlurredShadow(4,4,FontRender.getStringWidth6("ThunderHack" + "  |  " +  mc.player.getName() + "  |  " + Thunderhack.serverManager.getPing() + " ms  |  " + (mc.currentServerData == null ? "SinglePlayer" : mc.currentServerData.serverIP)) + 29,12, 10, shadowColor.getValue().getColorObject());
        RoundedShader.drawRound(4,4,FontRender.getStringWidth6("ThunderHack" + "  |  " +  mc.player.getName() + "  |  " + Thunderhack.serverManager.getPing() + " ms  |  " + (mc.currentServerData == null ? "SinglePlayer" : mc.currentServerData.serverIP)) + 30,13, 2f, color2.getValue().getColorObject());

        if (timer.passedMs(350)) {
            ++i;
            timer.reset();
        }

        if (i == 24) {i = 0;}

        String w1 = "_";String w2 = "T_";String w3 = "Th_";String w4 = "Thu_";String w5 = "Thun_";String w6 = "Thund_";String w7 = "Thunde_";String w8 = "Thunder_";String w9 = "ThunderH_";String w10 = "ThunderHa_";String w11 = "ThunderHac_";String w12 = "ThunderHack";String w13 = "ThunderHack";String w14 = "ThunderHack";String w15 = "ThunderHac_";String w16 = "ThunderHa_";String w17 = "ThunderH_";String w18 = "Thunder_";String w19 = "Thunde_";String w20 = "Thund_";String w21 = "Thun_";String w22 = "Thu_";String w23 = "Th_";String w24 = "T_";String w25 = "_";String text = "";

        if (i == 0) {text = w1;}
        if (i == 1) {text = w2;}
        if (i == 2) {text = w3;}
        if (i == 3) {text = w4;}
        if (i == 4) {text = w5;}
        if (i == 5) {text = w6;}
        if (i == 6) {text = w7;}
        if (i == 7) {text = w8;}
        if (i == 8) {text = w9;}
        if (i == 9) {text = w10;}
        if (i == 10) {text = w11;}
        if (i == 11) {text = w12;}
        if (i == 12) {text = w13;}
        if (i == 13) {text = w14;}
        if (i == 14) {text = w15;}
        if (i == 15) {text = w16;}
        if (i == 16) {text = w17;}
        if (i == 17) {text = w18;}
        if (i == 18) {text = w19;}
        if (i == 19) {text = w20;}
        if (i == 20) {text = w21;}
        if (i == 21) {text = w22;}
        if (i == 22) {text = w23;}
        if (i == 23) {text = w24;}
        if (i == 23) {text = w25;}


        FontRender.drawString6(text, 7,9,-1,false);
        FontRender.drawString6( "  |  " +  mc.player.getName() + "  |  " + Thunderhack.serverManager.getPing() + " ms  |  " + (mc.currentServerData == null ? "SinglePlayer" : mc.currentServerData.serverIP), FontRender.getStringWidth6("ThunderHack") + 10,9,color1.getValue().getColor(),false);

    }



    public static void setColor(int color) {
        GL11.glColor4ub((byte) (color >> 16 & 0xFF), (byte) (color >> 8 & 0xFF), (byte) (color & 0xFF), (byte) (color >> 24 & 0xFF));
    }


}
