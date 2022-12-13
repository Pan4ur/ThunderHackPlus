package com.mrzak34.thunderhack.modules.funnygame;

import com.mrzak34.thunderhack.event.events.PacketEvent;
import com.mrzak34.thunderhack.command.Command;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.ThunderUtils;
import com.mrzak34.thunderhack.util.Timer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Objects;


public class AntiTPhere extends Module {

    public AntiTPhere() {
        super("AntiTPhere", "AntiTPhere", Category.FUNNYGAME, true, false, false);
    }

    private Setting<Modes> mode = register(new Setting("Mode", Modes.Back));

    public enum Modes {
        Back, Home, RTP, Spawn
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event){
        if(event.getPacket() instanceof SPacketChat){
            SPacketChat packet = event.getPacket();
            if(packet.getChatComponent().getFormattedText().contains("Телепортирование...") && check(packet.getChatComponent().getFormattedText())){

                StringBuilder log = new StringBuilder("Тебя телепортировали в X: " + (int)mc.player.posX + " Z: " + (int) mc.player.posZ +
                        ". Ближайшие игроки : ");

                for(Entity entity : mc.world.loadedEntityList){
                    if(entity instanceof EntityPlayer){
                        if(entity == mc.player){
                            continue;
                        }
                        log.append(entity.getName()).append(" ");
                    }
                }
                Command.sendMessage(String.valueOf(log));
                switch (mode.getValue()){
                    case RTP: {
                        mc.player.sendChatMessage("/rtp");
                        break;
                    }
                    case Back: {
                        mc.player.sendChatMessage("/back");
                        break;
                    }
                    case Home: {
                        mc.player.sendChatMessage("/home");
                        break;
                    }
                    case Spawn: {
                        mc.player.sendChatMessage("/spawn");
                        break;
                    }

                }
            }
        }
    }

    public boolean check(String checkstring){
        return checktimer.passedMs(3000) && (Objects.equals(ThunderUtils.solvename(checkstring), "err"));
    }

    Timer checktimer = new Timer();
    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send e){
        if(e.getPacket() instanceof CPacketChatMessage){
            checktimer.reset();
        }
    }
}
