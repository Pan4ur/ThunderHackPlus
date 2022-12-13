package com.mrzak34.thunderhack.modules.misc;

import com.mrzak34.thunderhack.event.events.PacketEvent;
import com.mrzak34.thunderhack.modules.Module;
import net.minecraft.network.play.client.CPacketUpdateSign;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AutoSign extends Module {
    public AutoSign() {
        super("AutoSign", "AutoSign", Category.MISC, true, false, false);
    }


    //搁攁
    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send e){
        if(e.getPacket() instanceof CPacketUpdateSign){
            CPacketUpdateSign pac = e.getPacket();
            pac.lines[0] = "\u6401\u6501";
        }
    }
}
