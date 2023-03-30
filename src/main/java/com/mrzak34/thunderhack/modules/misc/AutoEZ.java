package com.mrzak34.thunderhack.modules.misc;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.command.Command;
import com.mrzak34.thunderhack.events.DeathEvent;
import com.mrzak34.thunderhack.events.PacketEvent;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.modules.combat.Aura;
import com.mrzak34.thunderhack.modules.combat.AutoCrystal;
import com.mrzak34.thunderhack.modules.funnygame.C4Aura;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.ThunderUtils;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.util.text.ChatType;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Random;

public class AutoEZ extends Module {
    public static ArrayList<String> EZWORDS = new ArrayList<>();
    public Setting<Boolean> global = this.register(new Setting<>("global", true));
    String a = "";
    String b = "";
    String c = "";
    String[] EZ = new String[]{
            "%player% БЫЛ ПОПУЩЕН",
            "%player% БЫЛ ПРИХЛОПНУТ ТАПКОМ",
            "%player% EZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZ",
            "%player% ПОСЫПАЛСЯ EZZZZZZZZZ",
            "%player% ТЫ БЫ ХОТЬ КИЛЛКУ ВКЛЮЧИЛ",
            "%player% СЛОЖИЛСЯ ПОПОЛАМ",
            "%player% ТУДААААААААААААА",
            "%player% ИЗИ БЛЯТЬ ХАХАХААХХАХАХА",
            "%player% УЛЕТЕЛ НА ТОТ СВЕТ",
            "%player% ПОПУЩЕННННННН",
            "%player% ВЫТАЩИ ЗАЛУПУ ИЗО РТА",
            "%player% БОЖЕ ЕЗЗКА",
            "%player% ИЗИИИИИИИИИИИИ",
            "%player% ЧЕ ТАК ЛЕГКО????",
            "RAGE OWNS %player% AND ALL",
            "RAGE ЗАОВНИЛ %player%",
            "%player% АХАХАХААХАХАХАХХААХАХ",
            "%player% GET GOOD JOIN RAGE",
            "%player% ЛЕГЧАЙШАЯ",
            "%player% GG EZ",
            "%player% НУЛИНА",
            "%player% ЛЕЖАТЬ ПЛЮС СОСАТЬ",
            "%player% ИЗИ БОТЯРА"
    };
    private final Setting<ModeEn> Mode = register(new Setting("Mode", ModeEn.Basic));
    private final Setting<ServerMode> server = register(new Setting("Sever", ServerMode.Universal));


    public AutoEZ() {
        super("AutoEZ", "Пишет изи убил убил - после килла", "only for-mcfunny.su", Category.MISC);
        loadEZ();
    }

    public static void loadEZ() {
        try {
            File file = new File("ThunderHack/misc/AutoEZ.txt");
            if (!file.exists()) file.createNewFile();

            new Thread(() -> {
                try {

                    FileInputStream fis = new FileInputStream(file);
                    InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
                    BufferedReader reader = new BufferedReader(isr);


                    ArrayList<String> lines = new ArrayList<>();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        lines.add(line);
                    }

                    boolean newline = false;

                    for (String l : lines) {
                        if (l.equals("")) {
                            newline = true;
                            break;
                        }
                    }

                    EZWORDS.clear();
                    ArrayList<String> spamList = new ArrayList<>();

                    if (newline) {
                        StringBuilder spamChunk = new StringBuilder();

                        for (String l : lines) {
                            if (l.equals("")) {
                                if (spamChunk.length() > 0) {
                                    spamList.add(spamChunk.toString());
                                    spamChunk = new StringBuilder();
                                }
                            } else {
                                spamChunk.append(l).append(" ");
                            }
                        }
                        spamList.add(spamChunk.toString());
                    } else {
                        spamList.addAll(lines);
                    }

                    EZWORDS = spamList;
                } catch (Exception e) {
                    System.err.println("Could not load file ");
                }
            }).start();
        } catch (IOException e) {
            System.err.println("Could not load file ");
        }

    }

    @Override
    public void onEnable() {
        loadEZ();
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPacketReceive(PacketEvent.Receive e) {
        if (fullNullCheck()) return;
        if(server.getValue() == ServerMode.Universal) return;
        if (e.getPacket() instanceof SPacketChat) {
            final SPacketChat packet = e.getPacket();
            if (packet.getType() != ChatType.GAME_INFO) {
                a = packet.getChatComponent().getFormattedText();
                if (a.contains("Вы убили игрока")) {
                    b = ThunderUtils.solvename(a);

                    if (Mode.getValue() == ModeEn.Basic) {
                        int n;
                        n = (int) Math.floor(Math.random() * EZ.length);
                        c = EZ[n].replace("%player%", b);
                    } else {
                        if (EZWORDS.isEmpty()) {
                            Command.sendMessage("Файл с AutoEZ пустой!");
                            return;
                        }
                        c = EZWORDS.get(new Random().nextInt(EZWORDS.size()));
                        c = c.replaceAll("%player%", b);
                    }

                    mc.player.sendChatMessage(global.getValue() ? "!" + c : c);
                }
            }
        }
    }




    @SubscribeEvent
    public void onPlayerDeath(DeathEvent e){
        if(server.getValue() != ServerMode.Universal) return;
        if(Aura.target != null && Aura.target == e.player){
            sayEZ(e.player.getName());
            return;
        }
        if(C4Aura.target != null && C4Aura.target == e.player){
            sayEZ(e.player.getName());
            return;
        }
        if(Thunderhack.moduleManager.getModuleByClass(AutoCrystal.class).target != null && Thunderhack.moduleManager.getModuleByClass(AutoCrystal.class).target == e.player){
            sayEZ(e.player.getName());
        }
    }

    public void sayEZ(String pn){
        if (Mode.getValue() == ModeEn.Basic) {
            int n;
            n = (int) Math.floor(Math.random() * EZ.length);
            c = EZ[n].replace("%player%", pn);
        } else {
            if (EZWORDS.isEmpty()) {
                Command.sendMessage("Файл с AutoEZ пустой!");
                return;
            }
            c = EZWORDS.get(new Random().nextInt(EZWORDS.size()));
            c = c.replaceAll("%player%", pn);
        }

        mc.player.sendChatMessage(global.getValue() ? "!" + c : c);
    }


    public enum ModeEn {
        Custom,
        Basic
    }
    public enum ServerMode {
        Universal,
        FunnyGame,
        NexusGrief
    }

    // Вы убили игрока Ken257 и забрали у него 802.06$ (нексус ебучий) лооооол и на фанике такое пишет
}
