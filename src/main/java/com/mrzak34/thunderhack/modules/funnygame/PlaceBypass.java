package com.mrzak34.thunderhack.modules.funnygame;

import com.mrzak34.thunderhack.event.events.PacketEvent;
import com.mrzak34.thunderhack.gui.classic.components.items.Item;
import com.mrzak34.thunderhack.modules.Module;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PlaceBypass extends Module {

    public PlaceBypass() {
        super("PlaceBypass", "PlaceBypass", Category.FUNNYGAME, true, false, false);
    }


    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send e){
        if(mc.player.getHeldItemMainhand().getItem() == Items.BOAT || mc.player.getHeldItemMainhand().getItem() == Items.LAVA_BUCKET ){
            if(e.getPacket() instanceof CPacketPlayerTryUseItemOnBlock){
                e.setCanceled(true);
            }
        }
    }
}
