package com.mrzak34.thunderhack.modules.funnygame;

import com.mrzak34.thunderhack.events.PacketEvent;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.Timer;
import com.mrzak34.thunderhack.util.Util;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.util.text.ChatType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Objects;
import java.util.regex.*;
public class PhotoMath extends Module {
    public PhotoMath() {
        super("PhotoMath", "Решает чат игру автоматом", Category.FUNNYGAME);
    }
    //[Чат игра] Решите: 289 + 336 кто первый решит получит: 3000$
    public Setting<String> autoez = this.register(new Setting<String>("Custom", "EZZZZZZZZZ boosted by ThunderHack"));
    public Setting<Boolean> spam = this.register(new Setting<>("Spam", false));

    public Timer delay = new Timer();
    int nigger = 0;
    boolean ez = false;
    boolean check = false;

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event){
        if (event.getPacket() instanceof SPacketChat) {
            final SPacketChat packet = (SPacketChat)event.getPacket();
            if (packet.getType() != ChatType.GAME_INFO && this.Solve(packet.getChatComponent().getFormattedText())) {
                sendEZ();
            }
        }
    }

    private boolean Solve(String message) {
        String out = message;
        if (Util.mc.player == null && Util.mc.world == null) {
            return false;
        }
        if(out.contains("Решите")){
            if(!out.contains("+")){
                return false;
            }
            Pattern pat=Pattern.compile("[-]?[0-9]+(.[0-9]+)?");
            Matcher matcher=pat.matcher(out);
            while (matcher.find()) {
                if(Objects.equals(matcher.group(), "3000")){

                    if(spam.getValue()) {
                        for (int i = 0; i <= 8; i++) {
                            mc.player.sendChatMessage(String.valueOf(nigger));
                        }
                    } else {
                        mc.player.sendChatMessage(String.valueOf(nigger));
                    }

                    check = true;
                    delay.reset();
                } else {
                    nigger = nigger + Integer.parseInt(matcher.group());
                }

            }
        }
        if(out.contains("победил!") && check){
            nigger = 0;
            ez = true;
            check = false;
        }
        return true;
    }





    public void sendEZ(){
        if (ez && delay.passedMs(3000)){
            mc.player.sendChatMessage(autoez.getValue());
            ez = false;
        }
    }


}
