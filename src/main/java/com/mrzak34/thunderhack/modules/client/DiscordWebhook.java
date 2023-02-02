package com.mrzak34.thunderhack.modules.client;

import com.mrzak34.thunderhack.events.ConnectToServerEvent;
import com.mrzak34.thunderhack.events.TotemPopEvent;
import com.mrzak34.thunderhack.command.Command;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.modules.combat.Aura;
import com.mrzak34.thunderhack.modules.funnygame.AutoPot;
import com.mrzak34.thunderhack.modules.funnygame.C4Aura;
import com.mrzak34.thunderhack.setting.Setting;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.ScreenshotEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.net.URL;


import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;

import net.minecraft.client.gui.GuiChat;
import org.apache.commons.lang3.RandomUtils;

public class DiscordWebhook extends Module {

    public DiscordWebhook() {
        super("DiscordWebhook", "DiscordWebhook", Category.CLIENT);
    }

    public Setting<Boolean> ochat = register(new Setting<>("OpenChat", false));
    public Setting<Boolean> sendToDiscord = register(new Setting<>("SendToDiscord", true));
    public Setting<Boolean> SDescr = register(new Setting<>("ScreenDescription", true));

    public ByteArrayOutputStream a;
    public ExecutorService b;


    @Override
    public void onEnable(){

        Command.sendMessage("Использовние: ");
        Command.sendMessage("1. Введи ссылку на вебхук (со своего дискорд сервера) с помощью команды .whook <ссылка> ");
        Command.sendMessage("2. Выбери действия в настройках модуля (по умолчанию отправляет скрины, килы ауры, килы лука, заходы на сервера) ");


        Command.sendMessage("Using: ");
        Command.sendMessage("1. Enter the link to the webhook (from your discord server) using the command .whook <link>");
        Command.sendMessage("2. Choose actions in the module settings (by default sends screenshots, aura kills, bow kills, server joins)");
    }


    public static String readurl(){
        try {
            File file = new File("ThunderHack/misc/WHOOK.txt");

            if (file.exists()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    if (reader.ready()) {
                        return  reader.readLine();
                    }

                }
            } else {
                return "none";
            }
        } catch (Exception ignored) {}
        return "none";
    }



    public static void saveurl(String rat) {
        File file = new File("ThunderHack/misc/WHOOK.txt");
        try {
            new File("ThunderHack").mkdirs();
            file.createNewFile();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write(rat + '\n');
            } catch (Exception ignored){}
        } catch (Exception ignored){}
    }


    @SubscribeEvent
    public void onScreenshotEvent(ScreenshotEvent screenshotEvent) {
        Command.sendMessage("SS Getted!");
        this.a = new ByteArrayOutputStream();
        if (this.b == null) {
            this.b = Executors.newCachedThreadPool();
        }
        this.a(screenshotEvent.getImage());
    }

    @SubscribeEvent
    public void onConnectionEvent(ConnectToServerEvent e) {
        (new Thread(() -> {
            try {
                TimeUnit.MILLISECONDS.sleep(6000);
            } catch (InterruptedException var2) {
                var2.printStackTrace();
            }
            String msg = "```" + mc.player.getName() + " зашёл на сервер " + e.getIp() + "```";
            sendMsg(msg,readurl());
        })).start();
    }

    private final static String CLIENT_ID = "efce6070269a7f1";


    public static void sendMsg(String message, String webhook) {
        PrintWriter out = null;
        BufferedReader in = null;
        StringBuilder result = new StringBuilder();
        try {
            String line;
            URL realUrl = new URL(webhook);
            URLConnection conn = realUrl.openConnection();
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            out = new PrintWriter(conn.getOutputStream());
            String postData = URLEncoder.encode("content", "UTF-8") + "=" + URLEncoder.encode(message, "UTF-8");
            out.print(postData);
            out.flush();
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while ((line = in.readLine()) != null) {
                result.append("/n").append(line);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        System.out.println(result.toString());
    }


    public void a(BufferedImage bufferedImage) {

        this.b.execute(() -> {
            Thread.currentThread().setName("Imgur Image Uploading");
            try {
                String string;
                URL uRL = new URL("https://api.imgur.com/3/image");
                HttpURLConnection httpURLConnection = (HttpURLConnection)uRL.openConnection();
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("Authorization", "Client-ID " + CLIENT_ID);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                httpURLConnection.connect();
                ImageIO.write((RenderedImage)bufferedImage, "png", this.a);
                this.a.flush();
                byte[] byArray = this.a.toByteArray();
                String string2 = Base64.getEncoder().encodeToString(byArray);
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(httpURLConnection.getOutputStream());
                String string3 = URLEncoder.encode("image", "UTF-8") + "=" + URLEncoder.encode(string2, "UTF-8");
                outputStreamWriter.write(string3);
                outputStreamWriter.flush();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                while ((string = bufferedReader.readLine()) != null) {
                    stringBuilder.append(string).append("\n");
                }
                outputStreamWriter.close();
                bufferedReader.close();
                JsonObject jsonObject = new JsonParser().parse(stringBuilder.toString()).getAsJsonObject();
                String string4 = jsonObject.get("data").getAsJsonObject().get("link").getAsString();
                if(ochat.getValue()) {
                    mc.displayGuiScreen(new GuiChat(string4));
                }

                String ip = "ошибка";
                try{
                    ip = Minecraft.getMinecraft().currentServerData.serverIP;
                } catch (Exception ignored){

                }

                Date date = new Date(System.currentTimeMillis());
                String description = "```Скрин сделан игроком " + mc.player.getName() +
                                     "\n" + date +"\n" + "на сервере " + ip + "```";

                if(sendToDiscord.getValue()){
                    sendMsg(string4,readurl());
                    if(SDescr.getValue()) {
                        sendMsg(description, readurl());
                    }

                }
            }
            catch (Exception exception) {
                Command.sendMessage(exception.getMessage());
            }
        });
    }

    @SubscribeEvent
    public void onTotemPop(TotemPopEvent e){
        if(Aura.target == e.getEntity() || C4Aura.target == e.getEntity() || getEntityUnderMouse(100) == e.getEntity()) {
            if (this.b == null) {
                this.b = Executors.newCachedThreadPool();
            }
            this.b.execute(() -> {
                Thread.currentThread().setName("TotemPop");
                String str = "```" + mc.player.getName() + " " + getWord() + e.getEntity().getName() + "```";
                sendMsg(str, readurl());
            });
        }
    }


    public String getWord() {
        int n2 = RandomUtils.nextInt(0,3);
        switch (n2) {
            case 0: {
                return " дал тотем ";
            }
            case 1: {
                return " снял тотем ";
            }
            case 2: {
                return " отжал тотем у ";
            }
            case 3: {
                return " попнул ";
            }
        }
        return "";
    }

    public EntityPlayer getEntityUnderMouse(int range) {
        Entity entity = mc.getRenderViewEntity();

        if (entity != null) {
            Vec3d pos = mc.player.getPositionEyes(1F);
            for (float i = 0F; i < range; i += 0.5F) {
                pos = pos.add(mc.player.getLookVec().scale(0.5));
                for (EntityPlayer player : mc.world.playerEntities) {
                    if (player == mc.player) continue;
                    AxisAlignedBB bb = player.getEntityBoundingBox();
                    if (bb == null) continue;
                    if (player.getDistance(mc.player) > 6) {
                        bb = bb.grow(0.5);
                    }
                    if (bb.contains(pos)) return player;
                }
            }
        }

        return null;
    }




    public static void sendAuraMsg(EntityPlayer name,int killz){
        int caps = AutoPot.neededCap;
        (new Thread(() -> {
            sendMsg("```" + mc.player.getName() + getAuraWord() + name.getName() + " c помощью " + mc.player.getHeldItemMainhand().getDisplayName() +
                            "\n" + "Убийств за сегодня: " + killz +
                            "\n" + "Каппучино потребовалось: " + caps + "```", readurl());
        })).start();
        AutoPot.neededCap = 0;
    }






    public static String getAuraWord() {
        int n2 = RandomUtils.nextInt(0, 5);
        switch (n2) {
            case 0: {
                return " убил ";
            }
            case 1: {
                return " попустил ";
            }
            case 2: {
                return " кильнул ";
            }
            case 3: {
                return " дропнул ";
            }
            case 4: {
                return " отымел ";
            }
            case 5: {
                return " пропенил ";
            }

        }
        return "";
    }
}
