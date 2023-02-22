package com.mrzak34.thunderhack.modules.player;

import com.mrzak34.thunderhack.events.PacketEvent;
import com.mrzak34.thunderhack.modules.Module;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PearlBlockThrow extends Module {
    public PearlBlockThrow() {
        super("PearlBlockThrow", "PearlBlockThrow", Category.PLAYER);
    }


    @SubscribeEvent
    public void onPackerSend(PacketEvent.Send event){
        if(fullNullCheck())
            return;
        if(mc.player.getHeldItemMainhand().getItem() == Items.ENDER_PEARL){
            if(event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock){
                CPacketPlayerTryUseItemOnBlock pac = event.getPacket();
                pac.hand = EnumHand.OFF_HAND; //Але, не пастить сука
            }
        }
    }

}
