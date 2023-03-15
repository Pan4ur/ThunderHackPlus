package com.mrzak34.thunderhack.modules.misc;

import com.mrzak34.thunderhack.command.Command;
import com.mrzak34.thunderhack.events.PacketEvent;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.Timer;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.network.play.server.SPacketPlayerListItem;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.LinkedHashMap;
import java.util.UUID;

public class Welcomer extends Module {
    public final Setting<Boolean> serverside = register(new Setting<>("ServerSide", false));
    public final Setting<Boolean> global = register(new Setting<>("Global", false, v -> serverside.getValue()));
    private final String[] bb = new String[]{"See you later, ", "Catch ya later, ", "See you next time, ", "Farewell, ", "Bye, ", "Good bye, ", "Later, "};
    private final String[] qq = new String[]{"Good to see you, ", "Greetings, ", "Hello, ", "Howdy, ", "Hey, ", "Good evening, ", "Welcome to SERVERIP1D5A9E, "};
    private final Timer timer = new Timer();
    private String string1 = "server";
    private final LinkedHashMap<UUID, String> nameMap = new LinkedHashMap<>();


    public Welcomer() {
        super("Welcomer", "Приветствует игроков", Category.MISC);
    }

    @Override
    public void onDisable() {
        nameMap.clear();
    }

    @Override
    public void onUpdate() {
        if (timer.passedMs(15000)) {
            for (NetworkPlayerInfo b : mc.player.connection.getPlayerInfoMap()) {
                if (!nameMap.containsKey(b.getGameProfile().getId())) {
                    nameMap.put(b.getGameProfile().getId(), b.getGameProfile().getName());
                }
            }
            timer.reset();
        }
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive e) {
        if (e.getPacket() instanceof SPacketPlayerListItem) {
            SPacketPlayerListItem pck = e.getPacket();

            int n = (int) Math.floor(Math.random() * bb.length);
            int n2 = (int) Math.floor(Math.random() * qq.length);
            if (mc.currentServerData != null) {
                string1 = qq[n2].replace("SERVERIP1D5A9E", mc.currentServerData.serverIP);
            } else {
                string1 = "server";
            }

            for (SPacketPlayerListItem.AddPlayerData item : pck.getEntries()) {
                switch (pck.getAction()) {
                    case REMOVE_PLAYER:
                        if (!nameMap.containsKey(item.getProfile().getId())) {
                            return;
                        }
                        if (antiBot(nameMap.get(item.getProfile().getId()))) {
                            return;
                        }
                        if (serverside.getValue()) {
                            mc.player.sendChatMessage((global.getValue() ? "!" : "") + bb[n] + nameMap.get(item.getProfile().getId()));
                        } else {
                            Command.sendMessage(bb[n] + nameMap.get(item.getProfile().getId()));
                        }


                        nameMap.remove(item.getProfile().getId());
                        break;
                    case ADD_PLAYER:
                        if (antiBot(item.getProfile().getName())) {
                            return;
                        }
                        if (serverside.getValue()) {
                            mc.player.sendChatMessage((global.getValue() ? "!" : "") + string1 + item.getProfile().getName());
                        } else {
                            Command.sendMessage(string1 + item.getProfile().getName());
                        }
                        nameMap.put(item.getProfile().getId(), item.getProfile().getName());
                        break;
                    default:
                        return;
                }
            }
        }
    }

    public boolean antiBot(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (Character.UnicodeBlock.of(s.charAt(i)).equals(Character.UnicodeBlock.CYRILLIC)) {
                return true;
            }
        }
        return false;
    }


}
