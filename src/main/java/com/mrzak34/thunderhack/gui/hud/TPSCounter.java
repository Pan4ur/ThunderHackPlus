package com.mrzak34.thunderhack.gui.hud;

import com.mojang.realmsclient.gui.ChatFormatting;
import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.events.PacketEvent;
import com.mrzak34.thunderhack.events.Render2DEvent;
import com.mrzak34.thunderhack.gui.fontstuff.FontRender;
import com.mrzak34.thunderhack.gui.thundergui2.ThunderGui2;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.ColorSetting;
import com.mrzak34.thunderhack.setting.PositionSetting;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.Timer;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.network.play.server.SPacketTimeUpdate;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Mouse;

public class TPSCounter extends Module {
    public final Setting<ColorSetting> color = this.register(new Setting<>("Color", new ColorSetting(0x8800FF00)));
    private final Setting<PositionSetting> pos = this.register(new Setting<>("Position", new PositionSetting(0.5f, 0.5f)));
    public Setting<mode> Mode = register(new Setting("Mode", mode.New));
    Timer tpscounter = new Timer();
    float x1 = 0;
    float y1 = 0;
    float timeDifference = 0;
    long abobka = 1;
    int dragX, dragY = 0;
    boolean mousestate = false;
    private long timeOfLastPacket = -1L;

    public TPSCounter() {
        super("TPS", "trps", Module.Category.HUD);
    }

    @SubscribeEvent
    public void onRender2D(Render2DEvent e) {
        String str = "TPS " + ChatFormatting.WHITE + (Thunderhack.serverManager.getTPS());

        y1 = e.scaledResolution.getScaledHeight() * pos.getValue().getY();
        x1 = e.scaledResolution.getScaledWidth() * pos.getValue().getX();

        FontRender.drawString6(str, x1, y1, color.getValue().getRawColor(), false);
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

    @Override
    public void onTick() {
        abobka = tpscounter.getPassedTimeMs();
        tpscounter.reset();
    }

    public int normaliseX() {
        return (int) ((Mouse.getX() / 2f));
    }

    public int normaliseY() {
        ScaledResolution sr = new ScaledResolution(mc);
        return (((-Mouse.getY() + sr.getScaledHeight()) + sr.getScaledHeight()) / 2);
    }

    public boolean isHovering() {
        return normaliseX() > x1 - 10 && normaliseX() < x1 + 50 && normaliseY() > y1 && normaliseY() < y1 + 10;
    }

    public enum mode {
        Old, New
    }


}
