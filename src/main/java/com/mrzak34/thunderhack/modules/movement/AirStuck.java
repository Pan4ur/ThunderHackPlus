package com.mrzak34.thunderhack.modules.movement;

import com.mrzak34.thunderhack.events.PacketEvent;
import com.mrzak34.thunderhack.modules.Module;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AirStuck extends Module {
    public AirStuck() {
        super("AirStuck", "позваляет застрять в воздухе", Category.MOVEMENT);
    }

    @Override
    public void onUpdate() {
        mc.player.motionX = 0;
        mc.player.motionY = 0;
        mc.player.motionZ = 0;
    }

    @SubscribeEvent
    public void onSendPacket(PacketEvent.Receive event) {
        if (event.getPacket() instanceof CPacketPlayer) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof CPacketPlayer.Position) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof CPacketPlayer.PositionRotation) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof CPacketEntityAction) {
            event.setCanceled(true);
        }
    }
}
