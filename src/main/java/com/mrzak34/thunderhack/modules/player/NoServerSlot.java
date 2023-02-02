package com.mrzak34.thunderhack.modules.player;

import com.mrzak34.thunderhack.command.Command;
import com.mrzak34.thunderhack.events.PacketEvent;
import com.mrzak34.thunderhack.modules.Module;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.server.SPacketHeldItemChange;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class NoServerSlot extends Module {
    public NoServerSlot() {
        super("NoServerSlot", "не дает серверу свапать слоты", Category.PLAYER);
    }


    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event){
        if(event.getPacket() instanceof SPacketHeldItemChange){
            event.setCanceled(true);
            mc.player.connection.sendPacket(new CPacketHeldItemChange(mc.player.inventory.currentItem));
        }
    }
}
