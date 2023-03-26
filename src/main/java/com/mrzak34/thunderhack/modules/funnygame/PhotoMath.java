package com.mrzak34.thunderhack.modules.funnygame;

import com.mrzak34.thunderhack.events.PacketEvent;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.ThunderUtils;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.util.text.ChatType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public class PhotoMath extends Module {

    public PhotoMath() {super("PhotoMath", "Решает чат игру автоматом", Category.FUNNYGAME);}

    public Setting<Boolean> spam = register(new Setting<>("Spam", false));

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketChat) {
            final SPacketChat packet = event.getPacket();
            if (packet.getType() != ChatType.GAME_INFO && packet.getChatComponent().getFormattedText().contains("Решите: ") && Objects.equals(ThunderUtils.solvename(packet.getChatComponent().getFormattedText()), "err")) {
                int solve = Integer.parseInt(StringUtils.substringBetween(packet.getChatComponent().getUnformattedText(), "Решите: ", " + ")) + Integer.parseInt(StringUtils.substringBetween(packet.getChatComponent().getUnformattedText(), " + ", " кто первый"));
                for (int i = 0; i < (spam.getValue() ? 9 : 1); i++) mc.player.sendChatMessage(String.valueOf(solve));
            }
        }
    }
}
