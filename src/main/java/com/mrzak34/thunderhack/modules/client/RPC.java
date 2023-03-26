package com.mrzak34.thunderhack.modules.client;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;
import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.events.DeathEvent;
import com.mrzak34.thunderhack.events.PacketEvent;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.modules.combat.Aura;
import com.mrzak34.thunderhack.modules.combat.AutoCrystal;
import com.mrzak34.thunderhack.modules.funnygame.C4Aura;
import com.mrzak34.thunderhack.modules.misc.NameProtect;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreenAddServer;
import net.minecraft.client.gui.GuiScreenServerList;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.Objects;

public class RPC extends Module {

    public RPC() {
        super ( "DiscordRPC" , "крутая рпс" , Category.CLIENT );
    }

    public Setting<mode> Mode = register(new Setting("Picture", mode.MegaCute));
    public Setting < Boolean > showIP = this.register ( new Setting <> ( "ShowIP" , true  ) );
    public Setting < Boolean > queue = this.register ( new Setting <> ( "Queue" , true  ) );
    public Setting<smode> sMode = register(new Setting("StateMode", smode.Stats));

    public Setting < String > state = this.register ( new Setting <> ( "State" , "ThunderHack+" ));
    public Setting < Boolean > nickname = this.register ( new Setting <> ( "Nickname" , true  ) );

    public static boolean inQ = false;
    private static final DiscordRPC rpc = DiscordRPC.INSTANCE;
    public static DiscordRichPresence presence = new DiscordRichPresence ( );
    private static Thread thread;
    public static boolean started;
    static String String1 = "none";
    public static String position = "";
    private int kills = 0;

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive e){
        if(fullNullCheck())return;
        if(e.getPacket() instanceof SPacketChat && queue.getValue()){
            SPacketChat packchat = e.getPacket();
            String wtf = packchat.getChatComponent().getUnformattedText();
            position= StringUtils.substringBetween(wtf, "Position in queue: ", "\nYou can purchase");
            if(wtf.contains("Position in queue")) inQ = true;
        }
        if( mc.player.posY < 63f || mc.player.posY > 64f ) inQ = false;
    }

    @Override
    public void onLogout(){
        inQ = false;
        position = "";
    }


    @Override
    public void onDisable() {
        started = false;
        if (thread != null && !thread.isInterrupted()) {
            thread.interrupt();
        }
        rpc.Discord_Shutdown();
    }


    @SubscribeEvent
    public void onPlayerDeath(DeathEvent e){
        if(Aura.target != null && Aura.target == e.player){
            kills++;
            return;
        }
        if(C4Aura.target != null && C4Aura.target == e.player){
            kills++;
            return;
        }
        if(Thunderhack.moduleManager.getModuleByClass(AutoCrystal.class).target != null && Thunderhack.moduleManager.getModuleByClass(AutoCrystal.class).target == e.player){
            kills++;
        }
    }

    @Override
    public void onUpdate(){
        if(!started){
            started = true;
            DiscordEventHandlers handlers = new DiscordEventHandlers ( );
            rpc.Discord_Initialize("939112431488225280", handlers, true, "");
            presence.startTimestamp = (System.currentTimeMillis() / 1000L);
            presence.largeImageText = "v2.41 by Pan4ur#2144";
            rpc.Discord_UpdatePresence(presence);

            thread = new Thread(() -> {
                while (!Thread.currentThread().isInterrupted()) {

                    rpc.Discord_RunCallbacks();
                    if (!RPC.inQ)
                        presence.details = Minecraft.getMinecraft().currentScreen instanceof GuiMainMenu || Minecraft.getMinecraft().currentScreen instanceof GuiScreenServerList || Minecraft.getMinecraft().currentScreen instanceof GuiScreenAddServer ? "В главном меню" : (Minecraft.getMinecraft().getCurrentServerData() != null ? (showIP.getValue() ? "Играет на " + Minecraft.getMinecraft().getCurrentServerData().serverIP : "НН сервер") : "Выбирает сервер");
                    else
                        presence.details = "In queue: " + RPC.position;


                    presence.details = Util.mc.currentScreen instanceof GuiMainMenu ? "В главном меню" : "Играет " + (Minecraft.getMinecraft().getCurrentServerData() != null ? (showIP.getValue() ? Minecraft.getMinecraft().getCurrentServerData().serverIP.equals("localhost") ? "на " + "2bt2.org via 2bored2wait" : "на " + Minecraft.getMinecraft().getCurrentServerData().serverIP : " НН сервер") : " Читерит в одиночке");

                    if(sMode.getValue() == smode.Custom){
                        presence.state = state.getValue();
                    } else {
                        presence.state  = "Kills: " + kills  + " | Hacks: " + Thunderhack.moduleManager.getEnabledModules().size() + " / " + Thunderhack.moduleManager.modules.size();
                    }


                    if (nickname.getValue()) {
                        if(Thunderhack.moduleManager.getModuleByClass(NameProtect.class).isDisabled()) {
                            presence.smallImageText = "logged as - " + Util.mc.getSession().getUsername();
                        } else {
                            presence.smallImageText = "logged as - Protected";
                        }
                        presence.smallImageKey = "https://minotar.net/helm/" +  Util.mc.getSession().getUsername()+ "/100.png";
                    }
                    switch (Mode.getValue()){
                        case Thlogo:
                            presence.largeImageKey = "aboba3";
                            break;

                        case minecraft:
                            presence.largeImageKey = "minecraft";
                            break;

                        case Unknown:
                            presence.largeImageKey = "th";
                            break;

                        case Konas:
                            presence.largeImageKey = "2213";
                            break;

                        case Astolfo:
                            presence.largeImageKey = "astolf";
                            break;

                        case SlivSRC:
                            presence.largeImageKey = "hhh";
                            break;

                        case pic:
                            presence.largeImageKey = "pic";
                            break;

                        case MegaCute:
                            presence.largeImageKey = "https://media1.tenor.com/images/6bcbfcc0be97d029613b54f97845bc59/tenor.gif?itemid=26823781";
                            break;

                        case Hunger:
                            presence.largeImageKey = "https://media.tenor.com/nUNorsu3_RIAAAAd/cat-sweet.gif";
                            break;

                        case Custom:
                            readFile();
                            presence.largeImageKey = String1.split("SEPARATOR")[0];
                            if (!Objects.equals(String1.split("SEPARATOR")[1], "none")) {
                                presence.smallImageKey = String1.split("SEPARATOR")[1];
                            }
                            break;
                    }
                    rpc.Discord_UpdatePresence(presence);
                    try {Thread.sleep(2000L);} catch (InterruptedException ignored) {}
                }
            }, "RPC-Callback-Handler");
            thread.start();
        }
    }


    public static void readFile(){
        try {
            File file = new File("ThunderHack/misc/RPC.txt");
            if (file.exists()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    while (reader.ready()) {
                        String1 =  reader.readLine();
                    }
                }
            }
        } catch (Exception ignored) {}
    }


    public static void WriteFile(String url1, String url2) {
        File file = new File("ThunderHack/misc/RPC.txt");
        try {
            file.createNewFile();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write(url1 + "SEPARATOR"+ url2 + '\n');
            } catch (Exception ignored){}
        } catch (Exception ignored){}
    }

    public enum mode {
        Konas, Custom, Thlogo, Unknown, minecraft,pic,SlivSRC,Astolfo,MegaCute,Hunger;
    }

    public enum smode {
        Custom, Stats;
    }
}
