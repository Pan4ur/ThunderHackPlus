package com.mrzak34.thunderhack;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;
import com.mrzak34.thunderhack.modules.misc.NameProtect;
import com.mrzak34.thunderhack.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;


import com.mrzak34.thunderhack.modules.client.RPC;
import net.minecraft.client.gui.GuiScreenAddServer;
import net.minecraft.client.gui.GuiScreenServerList;

import java.io.*;
import java.util.Objects;


public
class Discord {
    private static final DiscordRPC rpc;
    public static DiscordRichPresence presence;
    private static Thread thread;
    public static boolean started;

    static {
        rpc = DiscordRPC.INSTANCE;
        presence = new DiscordRichPresence ( );
    }

    public String out;


    public static
    void start ( ) {
        started = true;
        DiscordEventHandlers handlers = new DiscordEventHandlers ( );
        rpc.Discord_Initialize("939112431488225280", handlers, true, "");
            Discord.presence.startTimestamp = (System.currentTimeMillis() / 1000L);
            Discord.presence.details = Util.mc.currentScreen instanceof GuiMainMenu ? "В главном меню" : "Играет " + (Minecraft.getMinecraft().getCurrentServerData() != null ? (RPC.INSTANCE.showIP.getValue() ? Minecraft.getMinecraft().getCurrentServerData().serverIP.equals("localhost") ? "на " + "2bt2.org via 2bored2wait" : "на " + Minecraft.getMinecraft().getCurrentServerData().serverIP : " НН сервер") : " Читерит в одиночке");
            Discord.presence.state = RPC.INSTANCE.state.getValue();
            Discord.presence.largeImageText = "v2.40";
            rpc.Discord_UpdatePresence(presence);

            thread = new Thread(() -> {
                while (!Thread.currentThread().isInterrupted()) {
                    rpc.Discord_RunCallbacks();

                    if (!RPC.inQ) {
                        Discord.presence.details = Minecraft.getMinecraft().currentScreen instanceof GuiMainMenu || Minecraft.getMinecraft().currentScreen instanceof GuiScreenServerList || Minecraft.getMinecraft().currentScreen instanceof GuiScreenAddServer ? "В главном меню" : (Minecraft.getMinecraft().getCurrentServerData() != null ? (RPC.INSTANCE.showIP.getValue() ? "Играет на " + Minecraft.getMinecraft().getCurrentServerData().serverIP : "НН сервер") : "Выбирает сервер");
                    } else {
                        Discord.presence.details = "In queue: " + RPC.position;
                    }


                    if (RPC.INSTANCE.nickname.getValue()) {
                        if(Thunderhack.moduleManager.getModuleByClass(NameProtect.class).isDisabled()) {
                            Discord.presence.smallImageText = "logged as - " + Util.mc.getSession().getUsername();
                        } else {
                            Discord.presence.smallImageText = "logged as - Protected";
                        }
                        Discord.presence.smallImageKey = "https://minotar.net/helm/" +  Util.mc.getSession().getUsername()+ "/100.png";
                    }



                    switch (RPC.INSTANCE.Mode.getValue()){
                        case Thlogo:
                            Discord.presence.largeImageKey = "aboba3";
                            break;

                        case minecraft:
                            Discord.presence.largeImageKey = "minecraft";
                            break;

                        case Unknown:
                            Discord.presence.largeImageKey = "th";
                            break;

                        case Konas:
                            Discord.presence.largeImageKey = "2213";
                            break;

                        case cat:
                            Discord.presence.largeImageKey = "caaat";
                            break;

                        case Astolfo:
                            Discord.presence.largeImageKey = "astolf";
                            break;

                        case SlivSRC:
                            Discord.presence.largeImageKey = "hhh";
                            break;

                        case pic:
                            Discord.presence.largeImageKey = "pic";
                            break;

                        case newver:
                            Discord.presence.largeImageKey = "img23";
                            break;

                        case thbeta:
                            Discord.presence.largeImageKey = "nek";
                            break;

                        case MegaCute:
                            Discord.presence.largeImageKey = "https://media1.tenor.com/images/6bcbfcc0be97d029613b54f97845bc59/tenor.gif?itemid=26823781";
                            break;

                        case Custom:
                            readFile();
                            Discord.presence.largeImageKey = String1.split("SEPARATOR")[0];
                            if (!Objects.equals(String1.split("SEPARATOR")[1], "none")) {
                                Discord.presence.smallImageKey = String1.split("SEPARATOR")[1];
                            }
                            break;
                    }


                    rpc.Discord_UpdatePresence(presence);
                    try {
                        Thread.sleep(2000L);
                    } catch (InterruptedException ignored) {
                    }
                }
            }, "RPC-Callback-Handler");
            thread.start();
    }

    static String String1 = "none";

    public static void readFile(){
        try {
            File file = new File("ThunderHack/misc/RPC.txt");
            if (file.exists()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    while (reader.ready()) {
                        String1 =  reader.readLine();
                    }
                }
            } else {
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

    public static void stop ( ) {
        started = false;
        if ( thread != null && ! thread.isInterrupted ( ) ) {
            thread.interrupt ( );
        }
        rpc.Discord_Shutdown ( );
    }

}