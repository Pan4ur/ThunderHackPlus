package com.mrzak34.thunderhack.gui.hud;

import com.mojang.realmsclient.gui.ChatFormatting;
import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.event.events.PacketEvent;
import com.mrzak34.thunderhack.event.events.Render2DEvent;
import com.mrzak34.thunderhack.gui.thundergui.fontstuff.FontRender;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.ColorSetting;
import com.mrzak34.thunderhack.setting.PositionSetting;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.RenderUtil;
import com.mrzak34.thunderhack.util.Timer;
import com.mrzak34.thunderhack.util.Util;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.network.play.server.SPacketTimeUpdate;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.text.DecimalFormat;

public class TPSCounter extends Module{
    public TPSCounter() {
        super("TPS", "trps", Module.Category.HUD, true, false, false);
    }

    public final Setting<ColorSetting> color = this.register(new Setting<>("Color", new ColorSetting(0x8800FF00)));
    private final Setting<PositionSetting> pos = this.register(new Setting<>("Position", new PositionSetting(0.5f,0.5f)));

    public Setting<mode> Mode = register(new Setting("Mode", mode.New));

    Timer tpscounter = new Timer();


    public enum mode{
        Old, New,NewNew
    }

    float x1 =0;
    float y1= 0;

    private static final DecimalFormat df = new DecimalFormat("0.00");



    @SubscribeEvent
    public void onRender2D(Render2DEvent e){
        String str;
        if(Mode.getValue() == mode.Old) {
            str = "TPS " + ChatFormatting.WHITE + String.valueOf(Double.parseDouble(String.valueOf(Thunderhack.serverManager.getTPS())));
        }else if(Mode.getValue() == mode.New ) {
            str = "TPS " + ChatFormatting.WHITE + df.format( 1000f / (timeDifference / 20));
        } else {
            str = "TPS " + ChatFormatting.WHITE + df.format( 20f / ((float) abobka / 50f));
        }

        y1 = e.scaledResolution.getScaledHeight() * pos.getValue().getY();
        x1 = e.scaledResolution.getScaledWidth() * pos.getValue().getX();

        FontRender.drawString6(str,x1,y1, color.getValue().getRawColor(),false);
        if(mc.currentScreen instanceof GuiChat || mc.currentScreen instanceof HudEditorGui){
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

    private long timeOfLastPacket = -1L;

    float timeDifference = 0;

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {

        if (event.getPacket() instanceof SPacketTimeUpdate) {

            if (timeOfLastPacket != -1L) {
                long currentTime = System.currentTimeMillis();

                timeDifference = (currentTime - timeOfLastPacket);

            }
            timeOfLastPacket = System.currentTimeMillis();

        }

    }


    long abobka = 1;

    @Override
    public  void onTick(){
        abobka = tpscounter.getPassedTimeMs();
        tpscounter.reset();
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


}
