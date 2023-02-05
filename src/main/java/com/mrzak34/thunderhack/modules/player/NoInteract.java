package com.mrzak34.thunderhack.modules.player;

import com.mrzak34.thunderhack.events.PacketEvent;
import com.mrzak34.thunderhack.modules.Module;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class NoInteract extends Module {
    public NoInteract() {
        super("NoInteract", "не посылать пакеты использования-блоков", Category.PLAYER);
    }


    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event){
        if(fullNullCheck()) return;
        if(mc.player.getHeldItemMainhand().getItem() instanceof ItemSword || mc.player.getHeldItemMainhand().getItem() instanceof ItemAxe ||  mc.player.getHeldItemMainhand().getItem() instanceof ItemBow) {
            if (event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock) {
                event.setCanceled(true);
            }
            if (event.getPacket() instanceof CPacketUseEntity) {
                CPacketUseEntity var2 = (CPacketUseEntity) event.getPacket();
                if (var2.getAction() == CPacketUseEntity.Action.INTERACT || var2.getAction() == CPacketUseEntity.Action.INTERACT_AT) {
                    event.setCanceled(true);
                }
            }
        }
    }
}
