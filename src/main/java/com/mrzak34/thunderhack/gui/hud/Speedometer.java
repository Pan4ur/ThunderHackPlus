package com.mrzak34.thunderhack.gui.hud;

import com.mojang.realmsclient.gui.ChatFormatting;
import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.events.EventPostMotion;
import com.mrzak34.thunderhack.events.EventPostMove;
import com.mrzak34.thunderhack.events.Render2DEvent;
import com.mrzak34.thunderhack.gui.thundergui.fontstuff.FontRender;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.ColorSetting;
import com.mrzak34.thunderhack.setting.PositionSetting;
import com.mrzak34.thunderhack.setting.Setting;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Mouse;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Speedometer extends Module{
    public Speedometer() {
        super("Speedometer", "Speedometer", Module.Category.HUD);
    }

    public final Setting<ColorSetting> color = this.register(new Setting<>("Color", new ColorSetting(0x8800FF00)));
    private final Setting<PositionSetting> pos = this.register(new Setting<>("Position", new PositionSetting(0.5f,0.5f)));
    private Setting<Boolean> bps = this.register(new Setting<>("BPS", false));



    float x1 =0;
    float y1= 0;

    @SubscribeEvent
    public void onRender2D(Render2DEvent e){
        ScaledResolution sr = new ScaledResolution(mc);

        double mx = mc.player.motionX;
        double mz = mc.player.motionZ;
        double motion = Math.hypot(mx, mz) * 20;
        String str ="";
        if(!bps.getValue()) {
            str = "Speed " + ChatFormatting.WHITE + getSpeedKpH() + " km/h";
        } else {
            str = String.format("Speed " + ChatFormatting.WHITE +  round(motion) + " b/s");
        }
        y1 = sr.getScaledHeight() * pos.getValue().getY();
        x1 = sr.getScaledWidth() * pos.getValue().getX();

        FontRender.drawString6(str,x1,y1, color.getValue().getRawColor(),true);
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

    public double speedometerCurrentSpeed = 0.0;

    private float round(double value) {
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(1, RoundingMode.HALF_UP);
        return bd.floatValue();
    }


    @SubscribeEvent
    public void onEventPostMove(EventPostMove e) {
        speedometerCurrentSpeed = e.getHorizontalMove();
    }

    public double getSpeedKpH() {
        return (double) Math.round(10.0 * (MathHelper.sqrt(speedometerCurrentSpeed) * 71.2729367892)) / 10.0;
    }
}
