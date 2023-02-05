package com.mrzak34.thunderhack.modules.player;

import com.mrzak34.thunderhack.events.PacketEvent;
import com.mrzak34.thunderhack.modules.Module;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PacketRender extends Module{
    public PacketRender() {
        super("PacketRender",  "рендерит пакеты-CPacketPlayerRotation",  Category.PLAYER);
    }
    private static float yaw = 0;
    private static float pitch = 0;

    private static float prevyaw = 0;
    private static float prevpitch = 0;

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {

    }

    public static float getYaw() {
        return yaw;
    }

    public static float getPitch() {
        return pitch;
    }

    public static void setYaw(float yaw) {
        PacketRender.yaw = yaw;
    }

    public static void setPitch(float pitch) {
        PacketRender.pitch = pitch;
    }



    public static float getPrevyaw() {
        return prevyaw;
    }

    public static float getPrevpitch() {
        return prevpitch;
    }

    public static void setPrevYaw(float yaw) {
        PacketRender.prevyaw = yaw;
    }

    public static void setPrevPitch(float pitch) {
        PacketRender.prevpitch = pitch;
    }
}
