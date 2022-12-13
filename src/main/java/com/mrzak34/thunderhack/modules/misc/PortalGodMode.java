package com.mrzak34.thunderhack.modules.misc;

import com.mrzak34.thunderhack.event.events.PacketEvent;
import com.mrzak34.thunderhack.modules.Module;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PortalGodMode extends Module {

    public PortalGodMode() {
        super("PortalGodMode", "бессмертие пока ты в -портале", Category.MISC, true, false, false);
    }


    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event){
        if (event.getPacket() instanceof CPacketConfirmTeleport) {
            event.setCanceled(true);
        }
    }

}
