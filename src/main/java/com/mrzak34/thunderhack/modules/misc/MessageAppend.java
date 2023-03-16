package com.mrzak34.thunderhack.modules.misc;

import com.mrzak34.thunderhack.command.Command;
import com.mrzak34.thunderhack.events.PacketEvent;
import com.mrzak34.thunderhack.mixin.mixins.ICPacketChatMessage;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class MessageAppend extends Module {

    public Setting<String> word = this.register(new Setting<String>("word", "   RAGE"));

    public MessageAppend() {
        super("MessageAppend", "добавляет фразу-в конце сообщения", Category.MISC);
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send e) {
        if (fullNullCheck()) {
            return;
        }

        if (e.getPacket() instanceof CPacketChatMessage) {
            if (((CPacketChatMessage) e.getPacket()).getMessage().startsWith("/") || ((CPacketChatMessage) e.getPacket()).getMessage().startsWith(Command.getCommandPrefix()))
                return;

            CPacketChatMessage pac = e.getPacket();
            ((ICPacketChatMessage) pac).setMessage(((CPacketChatMessage) e.getPacket()).getMessage() + word.getValue());
        }
    }
}
