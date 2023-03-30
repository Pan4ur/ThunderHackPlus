package com.mrzak34.thunderhack.modules.funnygame;

import com.mrzak34.thunderhack.events.PacketEvent;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ConsoleLogger extends Module {
    public ConsoleLogger() {
        super("ConsoleLogger", "ConsoleLogger", Category.FUNNYGAME);
    }

    public Setting<Boolean> clear = register(new Setting<>("clear", false));


    public static List<String> cache = new ArrayList<>();


    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive e){
        if(e.getPacket() instanceof SPacketChat){
            SPacketChat pac = e.getPacket();
            if(pac.getChatComponent().getFormattedText().contains("GameConsole")){
                cache.add(pac.getChatComponent().getFormattedText());
                if(clear.getValue())
                    e.setCanceled(true);
            }
        }
    }

    @Override
    public void onUpdate(){
        if(mc.player.ticksExisted % 400 == 0){
            load();

            File file = new File("ThunderHack/misc/gameconsole.txt");

            try {
                file.createNewFile();
            } catch (Exception ignored) {}

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                for (String str : cache) {
                    writer.write(str + "\n");
                }
                cache.clear();
            } catch (Exception ignored) {
            }
        }
    }

    public static void load() {
        try {
            File file = new File("ThunderHack/misc/gameconsole.txt");

            if (file.exists()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    while (reader.ready()) {
                        cache.add(reader.readLine());
                    }
                }
            }
        } catch (Exception ignored) {
        }
    }
}


