package com.mrzak34.thunderhack.gui.hud;

import com.mrzak34.thunderhack.events.PacketEvent;
import com.mrzak34.thunderhack.events.Render2DEvent;
import com.mrzak34.thunderhack.gui.fontstuff.FontRender;
import com.mrzak34.thunderhack.gui.thundergui2.ThunderGui2;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.ColorSetting;
import com.mrzak34.thunderhack.setting.PositionSetting;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.RoundedShader;
import com.mrzak34.thunderhack.util.render.RenderUtil;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Mouse;


public class PacketStatistics extends Module {
    public PacketStatistics() {
        super("PacketStatistics", "PacketStatistics", "PacketStatistics", Category.HUD);
    }

    private final Setting<PositionSetting> pos = this.register(new Setting<>("Position", new PositionSetting(0.5f, 0.5f)));
    public final Setting<ColorSetting> shadowColor = this.register(new Setting<>("ShadowColor", new ColorSetting(0xFF101010)));
    public final Setting<ColorSetting> color2 = this.register(new Setting<>("Color", new ColorSetting(0xFF101010)));
    public final Setting<ColorSetting> textColor = this.register(new Setting<>("TextColor", new ColorSetting(0xBEBEBE)));
    public final Setting<ColorSetting> color3 = this.register(new Setting<>("Color2", new ColorSetting(0xC59B9B9B)));


    float x1 = 0;
    float y1 = 0;
    int dragX, dragY = 0;
    boolean mousestate = false;



    @SubscribeEvent
    public void onRender2D(Render2DEvent e) {
        y1 = e.scaledResolution.getScaledHeight() * pos.getValue().getY();
        x1 = e.scaledResolution.getScaledWidth() * pos.getValue().getX();
        GlStateManager.pushMatrix();
        RenderUtil.drawBlurredShadow(x1, y1, 80, 40, 20, shadowColor.getValue().getColorObject());
        RoundedShader.drawRound(x1, y1, 80, 40, 7f, color2.getValue().getColorObject());
        RoundedShader.drawRound(x1 + 2, y1 + 13, 76, 1, 0.5f, color3.getValue().getColorObject());
        FontRender.drawCentString6("PacketStatistics", x1 + 40, y1 + 5, textColor.getValue().getColor());
        FontRender.drawString5("In: " + packets_in * 4 + " p/s", x1 + 3, y1 + 20, textColor.getValue().getColor());
        FontRender.drawString5("Out: " + packets_out * 4 + " p/s", x1 + 3, y1 + 30, textColor.getValue().getColor());
        GlStateManager.popMatrix();
        if (mc.currentScreen instanceof GuiChat || mc.currentScreen instanceof HudEditorGui || mc.currentScreen instanceof ThunderGui2) {
            if (isHovering()) {
                if (Mouse.isButtonDown(0) && mousestate) {
                    pos.getValue().setX((float) (normaliseX() - dragX) / e.scaledResolution.getScaledWidth());
                    pos.getValue().setY((float) (normaliseY() - dragY) / e.scaledResolution.getScaledHeight());
                }
            }
        }
        if (Mouse.isButtonDown(0) && isHovering()) {
            if (!mousestate) {
                dragX = (int) (normaliseX() - (pos.getValue().getX() * e.scaledResolution.getScaledWidth()));
                dragY = (int) (normaliseY() - (pos.getValue().getY() * e.scaledResolution.getScaledHeight()));
            }
            mousestate = true;
        } else {
            mousestate = false;
        }
    }

    int counter_in;
    int counter_out;

    int packets_in;
    int packets_out;

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive e){
        counter_in++;
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send e){
        counter_out++;
    }

    @Override
    public void onUpdate(){
        //4 раза в секунду
        if(mc.player.ticksExisted % 5 == 0){
            packets_in = counter_in;
            packets_out = counter_out;
            counter_out = 0;
            counter_in = 0;
        }
    }

    public int normaliseX() {
        return (int) ((Mouse.getX() / 2f));
    }

    public int normaliseY() {
        ScaledResolution sr = new ScaledResolution(mc);
        return (((-Mouse.getY() + sr.getScaledHeight()) + sr.getScaledHeight()) / 2);
    }

    public boolean isHovering() {
        return normaliseX() > x1 - 10 && normaliseX() < x1 + 100 && normaliseY() > y1 && normaliseY() < y1 + 100;
    }
}
