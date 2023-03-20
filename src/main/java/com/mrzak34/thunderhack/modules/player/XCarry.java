package com.mrzak34.thunderhack.modules.player;

import com.mrzak34.thunderhack.events.PacketEvent;
import com.mrzak34.thunderhack.modules.Module;
import net.minecraft.network.play.client.CPacketCloseWindow;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class XCarry extends Module {
    public XCarry() {
        super("XCarry","позволяет хранить-предметы в мышке", Category.PLAYER);
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send e) {
        if(fullNullCheck()) return;
        if(e.getPacket() instanceof CPacketCloseWindow){
            e.setCanceled(true);
        }
    }
}
