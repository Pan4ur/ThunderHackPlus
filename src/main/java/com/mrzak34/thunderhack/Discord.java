package com.mrzak34.thunderhack;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;

import com.mrzak34.thunderhack.modules.misc.ChatTweaks;
import com.mrzak34.thunderhack.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;


import com.mrzak34.thunderhack.modules.client.RPC;
import net.minecraft.client.gui.GuiScreenAddServer;
import net.minecraft.client.gui.GuiScreenServerList;

import java.io.*;
import java.util.Objects;

import static com.mrzak34.thunderhack.util.ItemUtil.mc;

public
class Discord {
    private static final DiscordRPC rpc;
    public static DiscordRichPresence presence;
    private static Thread thread;
    private static int index;
    public static boolean started;
    static {
        index = 1;
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
            Discord.presence.details = Util.mc.currentScreen instanceof GuiMainMenu ? "В главном меню" : "Играет " + (Minecraft.getMinecraft().currentServerData != null ? (RPC.INSTANCE.showIP.getValue() ? Minecraft.getMinecraft().currentServerData.serverIP.equals("localhost") ? "на " + "2bt2.org via 2bored2wait" : "на " + Minecraft.getMinecraft().currentServerData.serverIP : " НН сервер") : " Читерит в одиночке");
            Discord.presence.state = RPC.INSTANCE.state.getValue();
            Discord.presence.largeImageText = "v2.36";
            rpc.Discord_UpdatePresence(presence);

            thread = new Thread(() -> {
                while (!Thread.currentThread().isInterrupted()) {
                    rpc.Discord_RunCallbacks();

                    if (!RPC.inQ) {
                        Discord.presence.details = Minecraft.getMinecraft().currentScreen instanceof GuiMainMenu || Minecraft.getMinecraft().currentScreen instanceof GuiScreenServerList || Minecraft.getMinecraft().currentScreen instanceof GuiScreenAddServer ? "В главном меню" : (Minecraft.getMinecraft().currentServerData != null ? (RPC.INSTANCE.showIP.getValue() ? "Играет на " + Minecraft.getMinecraft().currentServerData.serverIP : "НН сервер") : "Выбирает сервер");
                    }
                    if (RPC.inQ) {
                        Discord.presence.state = "In queue: " + RPC.position;
                    } else {
                        Discord.presence.state = RPC.INSTANCE.state.getValue();
                    }


                    if (RPC.INSTANCE.nickname.getValue()) {
                        Discord.presence.smallImageText = "logged as - " + mc.session.getUsername();
                        Discord.presence.smallImageKey = "https://minotar.net/helm/" + mc.session.getUsername() + "/100.png";
                    }
                    if (RPC.INSTANCE.Mode.getValue() == RPC.mode.MegaCute) {
                        Discord.presence.largeImageKey = "https://media1.tenor.com/images/6bcbfcc0be97d029613b54f97845bc59/tenor.gif?itemid=26823781";
                    }

                    if (RPC.INSTANCE.Mode.getValue() == RPC.mode.Custom) {
                        readFile();
                        Discord.presence.largeImageKey = String1.split("SEPARATOR")[0];
                        if (!Objects.equals(String1.split("SEPARATOR")[1], "none")) {
                            Discord.presence.smallImageKey = String1.split("SEPARATOR")[1];
                        }
                    }
                    if (RPC.INSTANCE.Mode.getValue() == RPC.mode.thbeta) {
                        Discord.presence.largeImageKey = "nek";
                    }
                    if (RPC.INSTANCE.Mode.getValue() == RPC.mode.newver) {
                        Discord.presence.largeImageKey = "img23";
                    }
                    if (RPC.INSTANCE.Mode.getValue() == RPC.mode.pic) {
                        Discord.presence.largeImageKey = "pic";
                    }
                    if (RPC.INSTANCE.Mode.getValue() == RPC.mode.SlivSRC) {
                        Discord.presence.largeImageKey = "hhh";
                    }
                    if (RPC.INSTANCE.Mode.getValue() == RPC.mode.Astolfo) {
                        Discord.presence.largeImageKey = "astolf";
                    }
                    if (RPC.INSTANCE.Mode.getValue() == RPC.mode.cat) {
                        Discord.presence.largeImageKey = "caaat";
                    }
                    if (RPC.INSTANCE.Mode.getValue() == RPC.mode.Konas) {
                        Discord.presence.largeImageKey = "2213";
                    }
                    if (RPC.INSTANCE.Mode.getValue() == RPC.mode.Unknown) {
                        Discord.presence.largeImageKey = "th";
                    }
                    if (RPC.INSTANCE.Mode.getValue() == RPC.mode.minecraft) {
                        Discord.presence.largeImageKey = "minecraft";
                    }
                    if (RPC.INSTANCE.Mode.getValue() == RPC.mode.Thlogo) {
                        Discord.presence.largeImageKey = "aboba3";
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
            File file = new File("ThunderHack/RPC.txt");

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
        File file = new File("ThunderHack/RPC.txt");
        try {
            new File("ThunderHack").mkdirs();
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