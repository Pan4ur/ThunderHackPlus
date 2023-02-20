package com.mrzak34.thunderhack.modules.misc;

import com.mojang.authlib.GameProfile;
import com.mrzak34.thunderhack.command.Command;
import com.mrzak34.thunderhack.events.PacketEvent;
import com.mrzak34.thunderhack.modules.Module;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketPlayerListItem;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.LinkedHashMap;
import java.util.UUID;

public class StaffAlert extends Module {
    public StaffAlert() {
        super("StaffAlert", "StaffAlert", Category.MISC);
    }

    private LinkedHashMap<UUID, String> nameMap = new LinkedHashMap<>();


    @Override
    public void onDisable() {
        nameMap.clear();
    }

/*
    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive e) {
        if (e.getPacket() instanceof SPacketPlayerListItem) {
            SPacketPlayerListItem pck = e.getPacket();


            for (SPacketPlayerListItem.AddPlayerData item : pck.getEntries()) {
                switch (pck.getAction()) {
                    case REMOVE_PLAYER:
                        if(item.getProfile().getId() != null) {
                            EntityPlayer player = mc.world.getPlayerEntityByUUID(item.getProfile().getId());
                            if (player != null) {
                                Command.sendMessage(player.getDisplayName().getFormattedText() + " vanish");
                            }
                        }
                        if (!nameMap.containsKey(item.getProfile().getId())) {
                            return;
                        }
                        if(antiBot(nameMap.get(item.getProfile().getId()))){
                            return;
                        }

                        Command.sendMessage(nameMap.get(item.getProfile().getId()));



                        nameMap.remove(item.getProfile().getId());
                        break;
                    case ADD_PLAYER:
                        if(antiBot(item.getProfile().getName())){
                            return;
                        }


                        EntityPlayer player = mc.world.getPlayerEntityByUUID(item.getProfile().getId());
                        if(player != null)
                            Command.sendMessage(player.getDisplayName().getFormattedText());

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
    }*/


    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketPlayerListItem) {
            SPacketPlayerListItem pac = (SPacketPlayerListItem) event.getPacket();
            for (SPacketPlayerListItem.AddPlayerData item : pac.getEntries()) {
                SPacketPlayerListItem.Action action = pac.getAction();
                if (item != null && item.getDisplayName() != null && item.getDisplayName().getFormattedText() != null && item.getProfile() != null && item.getProfile().getName() != null) {
                    String displayName = item.getDisplayName().getFormattedText();
                    boolean havePerm = havePermission(item.getProfile(), displayName);
                    if (havePerm) {
                        if (action == SPacketPlayerListItem.Action.ADD_PLAYER) {
                            Command.sendMessage("Администратор " + displayName + " зашел на сервер/вышел из ваниша.");
                        } else if (action == SPacketPlayerListItem.Action.REMOVE_PLAYER) {
                            Command.sendMessage("Администратор " + displayName + " вышел с сервера/зашел в ваниш.");
                        }
                    }
                }
            }
        }
    }

    public boolean havePermission(GameProfile gameProfile, String displayName) {
        StringBuilder builder = new StringBuilder();
        char[] buffer = displayName.toCharArray();
        for (int i = 0; i < buffer.length; i++) {
            char c = buffer[i];
            if (c == '§') {
                i++;
            } else {
                builder.append(c);
            }
        }
        return havePermissionFixed(builder.toString().toLowerCase().replace(gameProfile.getName().toLowerCase(), ""));
    }

    private boolean havePermissionFixed(String displayName) {
        return displayName.contains("helper") || displayName.contains("хелпер") || displayName.contains("модер")
                || displayName.contains("moder") || displayName.contains("куратор") || displayName.contains("админ")
                || displayName.contains("admin") || displayName.contains("yt") || displayName.contains("yt+") || displayName.contains("бог");
    }

    // 1. заходим на сервер гетаем лист по пакету
    // 2. через пару секунд прочекиваем с табом
}
