package com.mrzak34.thunderhack.modules.funnygame;

import com.mrzak34.thunderhack.event.events.ConnectToServerEvent;
import com.mrzak34.thunderhack.event.events.PacketEvent;
import com.mrzak34.thunderhack.command.Command;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.util.MathematicHelper;
import com.mrzak34.thunderhack.util.MapColor;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.util.text.ChatType;
import net.minecraft.world.storage.MapData;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

public class AutoCaptcha extends Module {

    public AutoCaptcha() {
        super("AutoCaptcha", "Автоматически решает-уебанскуюю капчу", Category.FUNNYGAME, true, false, false);
    }

    String API = "fdbfc81b78e9cbf1f4cd2d761db0bb5a";


    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPacketReceive(PacketEvent.Receive e){
        if(fullNullCheck()) return;
        if (e.getPacket() instanceof SPacketChat) {
            final SPacketChat packet = e.getPacket();
            if (packet.getType() != ChatType.GAME_INFO) {
                String a = packet.getChatComponent().getFormattedText();
                if(a.contains("Вы ввели капчу") || a.contains("Проверка пройдена")){
                    (new Thread(() -> {
                        try {
                            TimeUnit.MILLISECONDS.sleep(1000);
                        } catch (InterruptedException ex) {
                            throw new RuntimeException(ex);
                        }
                        handled = false;

                    })).start();
                }

                if(a.contains("Введите капчу с картинки") ){
                    (new Thread(() -> {
                        try {
                            TimeUnit.MILLISECONDS.sleep(1000);
                        } catch (InterruptedException ex) {
                            throw new RuntimeException(ex);
                        }
                        if (!handled)
                            handleMaps();
                        handled = true;

                    })).start();
                }
            }
        }
    }
    private boolean handled = false;
    public MapData currentmap;



    @SubscribeEvent
    public void onConnect(ConnectToServerEvent e){
        handled = false;
    }

    public void handleMaps() {
        Command.sendMessage("MapData handled!");

        (new Thread(() -> {

            Command.sendMessage("Starting thread...");
            final BufferedImage img = new BufferedImage(128, 128, 2);
            final byte[] data = currentmap.colors;
            for (int x = 0; x < 128; ++x) {
                for (int y = 0; y < 128; ++y) {
                    final byte input = data[x + y * 128];
                    final int colId = input >>> 2 & 0x1F;
                    final byte shader = (byte) (input & 0x3);
                    MapColor col = MapColor.colors.get(colId);
                    if (col == null) {
                        col = MapColor.TRANSPARENT;
                    }
                    img.setRGB(x, y, col.shaded(shader));
                }
            }
            final ByteArrayOutputStream os = new ByteArrayOutputStream();
            try {
                ImageIO.write(img, "png", os);
                sendPost(Base64.getEncoder().encodeToString(os.toByteArray()));
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                ImageIO.write(img, "png", new File("ThunderHack/" + (int) (MathematicHelper.randomizeFloat(1, 100)) + ".png"));
            } catch (IOException e) {
                Command.sendMessage("err");
            }
        })).start();
    }

    private void sendPost(final String capbase64img) throws IOException {
        URL url = new URL("https://api.anti-captcha.com/" + toCamelCase(ApiMethod.CREATE_TASK.toString()));
        HttpURLConnection con = (HttpURLConnection)url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Accept", "application/json");
        con.setDoOutput(true);
        String body = "{\n" +
                "    \"clientKey\": \"" + API + "\",\n" +
                "    \"task\": {\n" +
                "        \"type\": \"ImageToTextTask\",\n" +
                "        \"body\": \"" + capbase64img + "\",\n" +
                "        \"phrase\": false,\n" +
                "        \"case\": true,\n" +
                "        \"numeric\": 0,\n" +
                "        \"math\": false,\n" +
                "        \"minLength\": 0,\n" +
                "        \"maxLength\": 0\n" +
                "    }," +
                " \"languagePool\": \"null\"\n" +
                "}";
        try(OutputStream os = con.getOutputStream()) {
            byte[] input = body.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }
        try(BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            String taskId = StringUtils.substringBetween(response.toString(), "taskId\":", "}");

            body = "{\n" +
                    "    \"clientKey\": \"" + API + "\",\n" +
                    "    \"taskId\": \"" + taskId + "\"" +
                    "}";
            if(!fullNullCheck()) {
                TimeUnit.MILLISECONDS.sleep(2000);
                createTask(body);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void createTask(final String id) throws IOException {
        URL url = new URL("https://api.anti-captcha.com/" + toCamelCase(ApiMethod.GET_TASK_RESULT.toString()));
        HttpURLConnection con = (HttpURLConnection)url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Accept", "application/json");
        con.setDoOutput(true);
        try(OutputStream os = con.getOutputStream()) {
            byte[] input = id.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }
        try(BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while (((responseLine = br.readLine()) != null)) {
                response.append(responseLine.trim());
            }
            if(response.toString().contains("processing")){
                TimeUnit.MILLISECONDS.sleep(2000);
                createTask(id);
            }
            if(response.toString().contains("solution")) {
                String solution = StringUtils.substringBetween(response.toString(), "text\":", ",");
                solution = StringUtils.substringBetween(solution, "\"","\"");

                Command.sendMessage("Ответ был: " + solution);
                mc.player.sendChatMessage(solution);
            }
            } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    //[13:36:35] [Thread-16/INFO]: [CHAT] [ThunderHack+] §7{"errorId":0,"status":"ready","solution":{"text":"ifMP5","url":"http:\/\/69.39.235.40\/97\/166634856821391.png"},"cost":"0.00070","ip":"5.139.12.191","createTime":1666348568,"endTime":1666348575,"solveCount":0}


    public static String toCamelCase(String s) {
        String[] parts = s.split("_");
        StringBuilder camelCaseString = new StringBuilder();

        for (String part : parts) {
            camelCaseString.append(part.substring(0, 1).toUpperCase()).append(part.substring(1).toLowerCase());
        }

        return camelCaseString.substring(0, 1).toLowerCase() + camelCaseString.substring(1);
    }

    private enum ApiMethod {
        CREATE_TASK,
        GET_TASK_RESULT,
        GET_BALANCE
    }
}
