package com.mrzak34.thunderhack.modules.misc;

import com.mojang.realmsclient.gui.ChatFormatting;
import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.command.Command;
import com.mrzak34.thunderhack.events.PacketEvent;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.notification.Notification;
import com.mrzak34.thunderhack.notification.NotificationManager;
import com.mrzak34.thunderhack.setting.Setting;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketChunkData;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;

public class StashLogger extends Module {

    public Setting<Boolean> chests = this.register(new Setting<>("Chests", true));
    public Setting<Integer> chestsAmount = this.register(new Setting<>("ChestsAmount", 5, 1, 100));
    public Setting<Boolean> shulker = this.register(new Setting<>("Shulkers", true));
    public Setting<Integer> shulkersAmount = this.register(new Setting<>("ShulkersAmount", 5, 1, 100));
    public Setting<Boolean> saveCoords = this.register(new Setting<>("SaveCoords", true));
    public StashLogger() {
        super("StashFinder", "ищет стеши в зоне-прогрузки", Module.Category.MISC);
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (fullNullCheck()) {
            return;
        }
        if (event.getPacket() instanceof SPacketChunkData) {
            SPacketChunkData l_Packet = event.getPacket();
            int l_ChestsCount = 0;
            int shulkers = 0;
            for (NBTTagCompound l_Tag : l_Packet.getTileEntityTags()) {
                String l_Id = l_Tag.getString("id");
                if (l_Id.equals("minecraft:chest") && this.chests.getValue()) {
                    ++l_ChestsCount;
                    continue;
                }
                if (!l_Id.equals("minecraft:shulker_box") || !shulker.getValue()) continue;
                ++shulkers;
            }
            if (l_ChestsCount >= chestsAmount.getValue()) {
                this.SendMessage(String.format("%s chests located at X: %s, Z: %s", l_ChestsCount, l_Packet.getChunkX() * 16, l_Packet.getChunkZ() * 16), true);
                if (Thunderhack.moduleManager.getModuleByClass(NotificationManager.class).isOn()) {
                    NotificationManager.publicity(String.format("%s chests located at X: %s, Z: %s", l_ChestsCount, l_Packet.getChunkX() * 16, l_Packet.getChunkZ() * 16), 5, Notification.Type.SUCCESS);
                }
            }
            if (shulkers >= shulkersAmount.getValue()) {
                this.SendMessage(String.format("%s shulker boxes at X: %s, Z: %s", shulkers, l_Packet.getChunkX() * 16, l_Packet.getChunkZ() * 16), true);
                if (Thunderhack.moduleManager.getModuleByClass(NotificationManager.class).isOn()) {
                    NotificationManager.publicity(String.format("%s shulker boxes at X: %s, Z: %s", shulkers, l_Packet.getChunkX() * 16, l_Packet.getChunkZ() * 16), 5, Notification.Type.SUCCESS);
                }
            }
        }
    }

    private void SendMessage(String message, boolean save) {
        String server;
        server = mc.isSingleplayer() ? "SINGLEPLAYER" : Objects.requireNonNull(mc.getCurrentServerData()).serverIP;
        if (saveCoords.getValue() && save) {
            try {
                FileWriter writer = new FileWriter("ThunderHack/misc/stashlogger.txt", true);
                writer.write("[" + server + "]: " + message + "\n");
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        mc.getSoundHandler().playSound(PositionedSoundRecord.getRecord(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f));
        Command.sendMessage(ChatFormatting.GREEN + message);
    }


}
