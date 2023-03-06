package com.mrzak34.thunderhack.modules.misc;

import com.mojang.authlib.GameProfile;
import com.mojang.realmsclient.gui.ChatFormatting;
import com.mrzak34.thunderhack.events.Render2DEvent;
import com.mrzak34.thunderhack.gui.fontstuff.FontRender;
import com.mrzak34.thunderhack.modules.Module;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.world.GameType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.*;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class StaffAlert extends Module {
    public StaffAlert() {
        super("StaffAlert", "StaffAlert", Category.MISC);
    }

    private LinkedHashMap<UUID, String> nameMap = new LinkedHashMap<>();


    @Override
    public void onDisable() {
        nameMap.clear();
    }


    private static final Pattern validUserPattern = Pattern.compile("^\\w{3,16}$");
    List<String> players = new ArrayList<>();
    List<String> notSpec = new ArrayList<>();


    @Override
    public void onUpdate(){
        if (mc.player.ticksExisted % 10 == 0) {
            players = getVanish();
            notSpec = getOnlinePlayerD();
            players.sort(String::compareTo);
            notSpec.sort(String::compareTo);
        }
    }

    @SubscribeEvent
    public void onRender2D(Render2DEvent er){
        if (players.isEmpty() && notSpec.isEmpty()) return;
        List<String> all = new ArrayList<>();
        all.addAll(players);
        all.addAll(notSpec);



        int staffY = 11;
        for (String player : all) {
            String a = player.split(":")[1].equalsIgnoreCase("vanish") ? ChatFormatting.RED + "VANISH" : player.split(":")[1].equalsIgnoreCase("gm3") ? ChatFormatting.RED + "VANISH " + ChatFormatting.YELLOW + "(GM 3)" : ChatFormatting.GREEN + "ACTIVE";
            FontRender.drawString6(player.split(":")[0] + " " + a, 10 + 3, 200 + 4 + staffY, -1,false);
            staffY += 13;
        }
    }



    public static List<String> getOnlinePlayer() {
        return mc.player.connection.getPlayerInfoMap().stream()
                .map(NetworkPlayerInfo::getGameProfile)
                .map(GameProfile::getName)
                .filter(profileName -> validUserPattern.matcher(profileName).matches())
                .collect(Collectors.toList());
    }

    // StaffCommand equals
    public static List<String> getOnlinePlayerD() {
        List<String> S = new ArrayList<>();
        for (NetworkPlayerInfo player : mc.player.connection.getPlayerInfoMap()) {
            if (mc.isSingleplayer() || player.getPlayerTeam() == null) break;
            String prefix = player.getPlayerTeam().getPrefix();

            if (check(ChatFormatting.stripFormatting(prefix).toLowerCase())
                   // || CommandStaff.staffNames.toString().toLowerCase().contains(player.getGameProfile().getName().toLowerCase())
                    || player.getGameProfile().getName().toLowerCase().contains("1danil_mansoru1") || player.getPlayerTeam().getPrefix().contains("YT")) {
                String name = Arrays.asList(player.getPlayerTeam().getMembershipCollection().stream().toArray()).toString().replace("[", "").replace("]", "");

                if (player.getGameType() == GameType.SPECTATOR) {
                    S.add(player.getPlayerTeam().getPrefix() + name + ":gm3");
                    continue;
                }
                S.add(player.getPlayerTeam().getPrefix() + name + ":active");
            }
        }
        return S;
    }

    public List<String> getVanish() {
        List<String> list = new ArrayList<>();
        for (ScorePlayerTeam s : mc.world.getScoreboard().getTeams()) {
            if (s.getPrefix().length() == 0 || mc.isSingleplayer()) continue;
            String name = Arrays.asList(s.getMembershipCollection().stream().toArray()).toString().replace("[", "").replace("]", "");

            if (getOnlinePlayer().contains(name) || name.isEmpty())
                continue;
           // if (CommandStaff.staffNames.toString().toLowerCase().contains(name.toLowerCase()) && check(s.getPrefix().toLowerCase()) || check(s.getPrefix().toLowerCase()) || name.toLowerCase().contains("1danil_mansoru1") || s.getColorPrefix().contains("YT"))
           //     list.add(s.getPrefix() + name + ":vanish");
            if (check(s.getPrefix().toLowerCase()) || name.toLowerCase().contains("1danil_mansoru1") || s.getPrefix().contains("YT"))
                list.add(s.getPrefix() + name + ":vanish");
        }
        return list;
    }

    public static boolean check(String name) {
        return name.contains("helper") || name.contains("moder") || name.contains("admin") || name.contains("owner") || name.contains("curator") || name.contains("������") || name.contains("�����") || name.contains("�����") || name.contains("�������");
    }





    // 1. заходим на сервер гетаем лист по пакету
    // 2. через пару секунд прочекиваем с табом
}
