package com.mrzak34.thunderhack.modules.funnygame;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.events.PacketEvent;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.modules.client.DiscordWebhook;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.ThunderUtils;
import com.mrzak34.thunderhack.util.Timer;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.text.ChatType;
import net.minecraft.world.GameType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class ClanInvite extends Module {


    public ClanInvite() {
        super("ClanInvite", "Автоматически приглашает-в клан", Category.FUNNYGAME);
    }
    private static final Ordering<NetworkPlayerInfo> ENTRY_ORDERING = Ordering.from(new PlayerComparator());

    Timer timer = new Timer();

    public Setting<Integer> delay = this.register ( new Setting <> ( "Delay", 10, 1, 30) );

    ArrayList<String> playersNames = new ArrayList<String>();

    public  Setting<Mode> b = this.register(new Setting<>("Donate", Mode.Creativ));
    private enum Mode {
        ALL, Vip, Premium, Creativ, Admin, Lord,glAdmin,Sozdatel,Osnovatel,Vladelec,Cesar,President,Bog,Vlastelin,Pravitel,Baron,Vladika,Sultan,Major,Gospod,Sponsor
    }

    @Override
    public void onUpdate(){
        if(timer.passedS(delay.getValue())){
            NetHandlerPlayClient nethandlerplayclient = mc.player.connection;
            List<NetworkPlayerInfo> list = ENTRY_ORDERING.sortedCopy(nethandlerplayclient.getPlayerInfoMap());
            for (NetworkPlayerInfo networkplayerinfo : list) {
                if(!(resolveDonate(getPlayerName(networkplayerinfo))>= resolveMode())){
                    continue;
                }
                playersNames.add(getPlayerName(networkplayerinfo));
            }
            if(playersNames.size() > 1) {
                int randomName = (int) Math.floor(Math.random() * playersNames.size());
                mc.player.sendChatMessage("/c invite " + playersNames.get(randomName));
                playersNames.clear();
                timer.reset();
            }
        }
    }

    public String getPlayerName2(NetworkPlayerInfo networkPlayerInfoIn) {
        return networkPlayerInfoIn.getDisplayName() != null ? networkPlayerInfoIn.getDisplayName().getFormattedText() : ScorePlayerTeam.formatPlayerName(networkPlayerInfoIn.getPlayerTeam(), networkPlayerInfoIn.getGameProfile().getName());
    }

    public String getPlayerName(NetworkPlayerInfo networkPlayerInfoIn) {
        return networkPlayerInfoIn.getGameProfile().getName();
    }

    @SideOnly(Side.CLIENT)
    static class PlayerComparator implements Comparator<NetworkPlayerInfo> {
        private PlayerComparator() {
        }

        public int compare(NetworkPlayerInfo p_compare_1_, NetworkPlayerInfo p_compare_2_) {
            ScorePlayerTeam scoreplayerteam = p_compare_1_.getPlayerTeam();
            ScorePlayerTeam scoreplayerteam1 = p_compare_2_.getPlayerTeam();
            return ComparisonChain.start().compareTrueFirst(p_compare_1_.getGameType() != GameType.SPECTATOR, p_compare_2_.getGameType() != GameType.SPECTATOR).compare(scoreplayerteam != null ? scoreplayerteam.getName() : "", scoreplayerteam1 != null ? scoreplayerteam1.getName() : "").compare(p_compare_1_.getGameProfile().getName(), p_compare_2_.getGameProfile().getName()).result();
        }
    }

    /*
     0 - игрок
     1 - Вип
     2 - Премиум
     3 - Креатив
     4 - Админ
     5 - Лорд
     6 - Гл.Админ
     7 - Создатель
     8 - Основатель
     9 - Владелец
     10 - Цезарь
     11 - Президент
     12 - БОГ
     13 - Властелин
     14 - ПРАВИТЕЛЬ
     15 - БАРОН
     16 - Владыка
     17 - Султан
     18 - МАЖОР
     19 - ГОСПОДЬ
     20 - СПОНСОР
     */

    public int resolveMode(){
        return b.getValue().ordinal();
    }


    public int resolveDonate(String nick){
        String donate = "null";
        NetHandlerPlayClient nethandlerplayclient = this.mc.player.connection;
        List<NetworkPlayerInfo> list = ENTRY_ORDERING.sortedCopy(nethandlerplayclient.getPlayerInfoMap());

        for (NetworkPlayerInfo networkplayerinfo : list) {
            if (getPlayerName2(networkplayerinfo).contains(nick)) {
                String raw = getPlayerName2(networkplayerinfo);
                if(raw.contains("Вип")){
                    return 1;
                }
                if(raw.contains("Премиум")){
                    return 2;
                }
                if(raw.contains("Креатив")){
                    return 3;
                }
                if(raw.contains("Админ")){
                    return 4;
                }
                if(raw.contains("Лорд")){
                    return 5;
                }
                if(raw.contains("Гл.Админ")){
                    return 6;
                }
                if(raw.contains("Создатель")){
                    return 7;
                }
                if(raw.contains("Основатель")){
                    return 8;
                }
                if(raw.contains("Владелец")){
                    return 9;
                }
                if(raw.contains("Цезарь")){
                    return 10;
                }
                if(raw.contains("Президент")){
                    return 11;
                }
                if(raw.contains("БОГ")){
                    return 12;
                }
                if(raw.contains("Властелин")){
                    return 13;
                }
                if(raw.contains("ПРАВИТЕЛЬ")){
                    return 14;
                }
                if(raw.contains("БАРОН")){
                    return 15;
                }
                if(raw.contains("Владыка")){
                    return 16;
                }
                if(raw.contains("Султан")){
                    return 17;
                }
                if(raw.contains("МАЖОР")){
                    return 18;
                }
                if(raw.contains("ГОСПОДЬ")){
                    return 19;
                }
                if(raw.contains("СПОНСОР")){
                    return 20;
                }
            }
        }
        return 0;
    }

    public boolean helper(String nick){
        NetHandlerPlayClient nethandlerplayclient = this.mc.player.connection;
        List<NetworkPlayerInfo> list = ENTRY_ORDERING.sortedCopy(nethandlerplayclient.getPlayerInfoMap());
        for (NetworkPlayerInfo networkplayerinfo : list) {
            if (getPlayerName2(networkplayerinfo).contains(nick)) {
                String raw = getPlayerName2(networkplayerinfo);
                if(check(raw.toLowerCase())){
                    return true;
                }
            }
        }
        return false;
    }

    public boolean check(String name) {
        return name.contains("helper") || name.contains("moder") || name.contains("бог") || name.contains("admin") || name.contains("owner") || name.contains("curator") || name.contains("хелпер") || name.contains("модер") || name.contains("админ") || name.contains("куратор");
    }

    int aboba = 0;
    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive e){
        if(fullNullCheck()){
            return;
        }
        if(!Thunderhack.moduleManager.getModuleByClass(DiscordWebhook.class).isEnabled()){
            return;
        }
        if(e.getPacket() instanceof SPacketChat) {
            SPacketChat packet = (SPacketChat) e.getPacket();
            if (packet.getType() != ChatType.GAME_INFO) {

                if (packet.getChatComponent().getFormattedText().contains("принял ваше")) {
                    aboba++;
                    String finalmsg  = "```"+"Игрок " + ThunderUtils.solvename(packet.getChatComponent().getFormattedText()) + " принял приглашение в клан!" + "\n" + "Приглашено за сегодня " + aboba + "```";
                    DiscordWebhook.sendMsg(finalmsg,DiscordWebhook.readurl());
                }
            }
        }
    }



}
