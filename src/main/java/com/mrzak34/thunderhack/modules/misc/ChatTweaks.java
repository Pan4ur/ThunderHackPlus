package com.mrzak34.thunderhack.modules.misc;

import com.google.common.collect.Maps;
import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.event.events.ConnectionEvent;
import com.mrzak34.thunderhack.event.events.Render2DEvent;
import com.mrzak34.thunderhack.command.Command;
import com.mrzak34.thunderhack.gui.classic.components.items.buttons.ModuleButton;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.event.events.PacketEvent;
import com.mrzak34.thunderhack.modules.client.RPC;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.notification.NotificationManager;
import com.mrzak34.thunderhack.notification.NotificationType;
import com.mrzak34.thunderhack.util.PNGtoResourceLocation;
import com.mrzak34.thunderhack.util.Timer;
import com.mrzak34.thunderhack.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.*;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;
import org.lwjgl.input.Mouse;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class ChatTweaks
        extends Module {







    private static ChatTweaks INSTANCE = new ChatTweaks();
    public Setting<Boolean> clean = this.register(new Setting<Boolean>("NoChatBackground", Boolean.valueOf(false)));
    public Setting<Boolean> infinite = this.register(new Setting<Boolean>("InfiniteChat", Boolean.valueOf(false)));
    public Setting<Boolean> timestamps = this.register(new Setting<Boolean>("TimeStamps", Boolean.valueOf(false)));
    public Setting<Boolean> namehighlight = this.register(new Setting<Boolean>("NameHighLight", Boolean.valueOf(false)));
    public Setting<Boolean> welcomer = this.register(new Setting<Boolean>("WelcomerCS", Boolean.valueOf(false)));
    private final Map<String, String> uuidNameCache = Maps.newConcurrentMap();
    public Setting<Boolean> futils = this.register(new Setting<Boolean>("FunnyGam Utils", true));
    public Setting<Boolean> popusktome = this.register(new Setting<Boolean>("[Попуск -> Я] adds", true, v -> futils.getValue()));
    public Setting<Boolean> shittyclans = this.register(new Setting<Boolean>("nn clans", true, v -> futils.getValue()));
    public Setting<Boolean> serverads = this.register(new Setting<Boolean>("Server adds", true, v -> futils.getValue()));
    public Setting<Boolean> primer = this.register(new Setting<Boolean>("реши 2+2 если не даун", true, v -> futils.getValue()));
    public Setting<Boolean> donators = this.register(new Setting<Boolean>("fuck donators", true, v -> futils.getValue()));
    public Setting<Boolean> itemclear = this.register(new Setting<Boolean>("item clear", true, v -> futils.getValue()));
    public Setting<Boolean> chatmarks = this.register(new Setting<Boolean>("G |", true, v -> futils.getValue()));
    public Setting<Boolean> bans = this.register(new Setting<Boolean>("kick/mute/ban", true, v -> futils.getValue()));
    public Setting<Boolean> welcom = this.register(new Setting<Boolean>("NN JOINS", true, v -> futils.getValue()));
    public Setting<Boolean> privat = this.register(new Setting<Boolean>("Privates", true, v -> futils.getValue()));
    public boolean check;
    public String date = "";
    public String forrpc = "";
    public String clean1 = "";
    public String clean2 = "";
    public String clean3 = "";
    public String clean4 = "";


    public Setting<Boolean> autoQQ = this.register(new Setting<Boolean>("AutoQQ", true));
    public Setting<Boolean> allQQ = this.register(new Setting<Boolean>("AllQQ", true));

    public Setting<Integer> multip = this.register ( new Setting <> ( "Scale", 200, 50, 1280 ) );




    public ChatTweaks() {
        super("ChatTweaks", "изменяет чат", Module.Category.MISC, true, false, false);
        this.setInstance();
    }

    public static ChatTweaks getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ChatTweaks();
        }
        return INSTANCE;
    }
    public boolean inq = false;
    private void setInstance() {
        INSTANCE = this;
    }





    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event){
        if(ChatTweaks.fullNullCheck()){
            return;
        }
        if (event.getPacket() instanceof SPacketChat) {
            final SPacketChat packet = event.getPacket();
            if (packet.getType() != ChatType.GAME_INFO && this.tryProcessChat(packet.getChatComponent().getFormattedText(), packet.getChatComponent().getUnformattedText())) {
                event.setCanceled(true);
            }
        }
    }


    String welcmout = "server";
    String[] myString = new String[]{ "See you later, ", "Catch ya later, ", "See you next time, ", "Farewell, ", "Bye, ", "Good bye, ", "Later, " };
    String[] myString2 = new String[]{ "Good to see you, ", "Greetings, ", "Hello, ", "Howdy, ", "Hey, ", "Good evening, ", "Welcome to SERVERIP1D5A9E, " };;


    @SubscribeEvent
    public void onConnectionEvent(ConnectionEvent e){

        if(welcomer.getValue() && mc.player != null && mc.world != null /*&& !Objects.equals(Minecraft.getMinecraft().currentServerData.serverIP, "mcfunny.su")*/) {
            int n = (int) Math.floor(Math.random() * myString.length);
            int n2 = (int) Math.floor(Math.random() * myString2.length);
            String welcm = myString2[n2];
            if (mc.world != null) {
                welcmout = welcm.replace("SERVERIP1D5A9E", Minecraft.getMinecraft().currentServerData.serverIP);
            } else {
                welcmout = "server";
            }
            if (e.getStage() == 0) {
                Command.sendMessage(welcmout + e.getName());
                if(autoQQ.getValue()){
                    if(Thunderhack.friendManager.isFriend(e.getName())){
                        mc.player.sendChatMessage(e.getName() + " qq");
                    }
                }
                if(allQQ.getValue()){
                    mc.player.sendChatMessage("!" + welcmout + e.getName());
                }
            } else if (e.getStage() == 2) {
                Command.sendMessage(myString[n] + uuidtoname(e.getUuid().toString()));

            }
        }
    }


    String discord = "";

    String last = "";

    HashMap<String, String> pics = new HashMap<String, String>();
    HashMap<Integer, String> posts = new HashMap<Integer, String>();

    private boolean tryProcessChat(String message, final String unformatted) {
        String out = message;
        final String[] parts = out.split(" ");
        final String[] partsUnformatted = unformatted.split(" ");
        out = message;
        if( out.contains("discordapp") && out.contains("png")){
                discord = out;
                try {
                    String[] splitted = discord.toString().split("https://");
                    String url = "https://" + splitted[1];
                    String[] splitted1 = url.split(".png");
                    last = splitted1[0]+".png";
                    URL nigUrl = new URL(last);
                    String withoutlink = discord.replace(last, "");
                   // saveDickPick(nigUrl, withoutlink,"png");

                    ITextComponent cancel2 = new TextComponentString(last);
                    ITextComponent cancel = new TextComponentString("<" + solvename(out) + "> Отправил картинку [Show Discord Image]");
                    cancel.setStyle(cancel.getStyle().setColor(TextFormatting.AQUA).setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, cancel2)));
                    Command.sendIText(cancel);
                    out = "";
                } catch (Exception ec){
                    System.out.println(ec.getMessage());
                }
        }
        if( out.contains("discordapp" )&& out.contains("jpg")){
            discord = out;
            try {
                String[] splitted = discord.toString().split("https://");
                String url = "https://" + splitted[1];
                String[] splitted1 = url.split(".jpg");
                last = splitted1[0]+".jpg";
                URL nigUrl = new URL(last);
                String withoutlink = discord.replace(last, "");

                ITextComponent cancel2 = new TextComponentString(last);
                ITextComponent cancel = new TextComponentString("<" + solvename(out) + "> Отправил картинку [Show Discord Image]");
                cancel.setStyle(cancel.getStyle().setColor(TextFormatting.AQUA).setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, cancel2)));
                Command.sendIText(cancel);
                out = "";
            } catch (Exception ec){
                System.out.println(ec.getMessage());
            }
        }
        if(popusktome.getValue() && out.contains("§r§cЯ§r§6")){
            out = "";
        }
        if(serverads.getValue() &&  out.contains("донат")){
            out = "";
        }
        if(itemclear.getValue() &&  out.contains("Все очистится через")){
            out = "";
        }
        if(itemclear.getValue() &&  out.contains("успешно удалены")){
            out = "";
        }
        if(shittyclans.getValue() &&  out.contains("сlan")){
            out = "";
        }
        if(shittyclans.getValue() &&  out.contains("клан")){
            out = "";
        }
        if(donators.getValue() &&  out.contains("миллиардер")){
            out = "";
        }
        if(primer.getValue() &&  out.contains("решил пример")){
            out = "";
        }
        if(primer.getValue() &&  out.contains("Ответ был:")){
            out = "";
        }
        if(serverads.getValue() &&  out.contains("оплаты")){
            out = "";
        }
        if(serverads.getValue() &&  out.contains("Купить ключ")){
            out = "";
        }
        if(serverads.getValue() &&  out.contains("После вайпа")){
            out = "";
        }
        if(serverads.getValue() &&  out.contains("Открыть купленные")){
            out = "";
        }
        if(serverads.getValue() &&  out.contains("§7[§r§e§l+§r§7]")){
            out = "";
        }
        if(serverads.getValue() &&  out.contains("/prize")){
            out = "";
        }
        if(bans.getValue() && out.contains("*")){
            out = "";
        }
        if(serverads.getValue() &&  out.contains("*")){
            out = "";
        }
        if(serverads.getValue() &&  out.contains("награда")){
            out = "";
        }

        if(serverads.getValue() &&  out.contains("§a§l§m")){
            out = "";
        }
        if(serverads.getValue() &&  out.contains("§a§l[!]")){
            out = "";
        }
        if(serverads.getValue() &&  out.contains("выбил из бесплатного")){
            out = "";
        }
        if(privat.getValue() &&  out.contains("[!]")){
            out = "";
            NotificationManager.publicity("§cПриват!§r" ,"Чел, ты влез в приват", 1, NotificationType.ERROR);
        }
        if(serverads.getValue() &&  out.contains("выделиться на сервере")){
            out = "";
        }
        if(serverads.getValue() &&  out.contains("большие скидки")){
            out = "";
        }
        if(serverads.getValue() &&  out.contains("руб")){
            out = "";
        }
        if(serverads.getValue() &&  out.contains("привяжите свой")){
            out = "";
        }
        if(primer.getValue() &&  out.contains("кто первый решит получит")){
            out = "";
        }

        if (timestamps.getValue()) {

            date = new SimpleDateFormat("k:mm").format(new Date());
        }
        if (namehighlight.getValue()) {
            if (Util.mc.player == null) {
                return false;
            }

            if(out.contains(ChatTweaks.mc.player.getName())){
                NotificationManager.publicity("ChatTweaks", "Ты был упомянут в чате!", 5, NotificationType.WARNING);
            }
            if(RPC.INSTANCE.queue.getValue() && out.contains("Position in queue:")){ //§7§6Position in queue: §r§6§l45§r

                clean1 = out.replace("Position in queue:", "");
                clean2 = clean1.replace("§6", "");
                clean3 = clean2.replace("§r", "");
                clean4 = clean3.replace("§7", "");
                forrpc = clean4.replace("§", "");
                forrpc = forrpc.replace("l", "");
                inq = true;
            }
            if(!out.contains("Position in queue:")){
                inq = false;
            }
            out = out.replace(ChatTweaks.mc.player.getName(), "§c" + ChatTweaks.mc.player.getName() + "§r");

        }
        if(donators.getValue()){
            out = out.replace("§r§6§l[§r§b§lПРЕЗИДЕНТ§r§6§l]§r", "§r");
            out = out.replace("§r§d§l[§r§5§lАдмин§r§d§l]§r", "§r");
            out = out.replace("§r§b§l[§r§3§lГл.Админ§r§b§l]§r", "§r");
            out = out.replace("§8[§r§6Игрок§r§8]§r", "§r");
            out = out.replace("§r§5§l[§r§e§lБОГ§r§5§l]§r", "§r");
            out = out.replace("§r§a§l[§r§2§lКреатив§r§a§l]", "§r");
            out = out.replace("§r§4§l[§r§c§lВладелец§r§4§l]", "§r");
            out = out.replace("§r§5§l[§r§d§lОснователь§r§5§l]", "§r");
            out = out.replace("§r§b§l[§r§e§l?§r§d§lMORGENSHTERN§r§e§l?§r§b§l]", "§r");
            out = out.replace("§r§8[§r§4§l§oBlood§r§0§l§oRavens§r§8]", "§r");
            out = out.replace("§r§6§l[§r§e§lЛорд§r§6§l]", "§r");
            out = out.replace("§r§4§l[§r§2§lВЛАДЫКА§r§4§l]", "§r");
        }
        if(chatmarks.getValue()){
            out = out.replace("|", "§r");
            out = out.replace("§r§a§l§r", "§r");
            out = out.replace("§r§f§l", "§r");
            out = out.replace("§7§b?", "§r");
            out = out.replace("§r§4?", "§r");
            out = out.replace("§7§a? §r§f §r §r§r", "§r");
            out = out.replace("Ⓖ", "§r");
        }
        try {
            if (timestamps.getValue() && !out.equals("")) {
                Command.sendMessageWithoutTH("[" + date + "]" + "  " + out);
            } else {
                if(!out.equals("")){
                    Command.sendMessageWithoutTH(out);
                }

            }
        } catch (Exception exception){}
        return true;
    }






    public String uuidtoname(String uuid) {
        uuid = uuid.replace("-", "");
        if (uuidNameCache.containsKey(uuid)) {
            return uuidNameCache.get(uuid);
        }

        final String url = "https://api.mojang.com/user/profiles/" + uuid + "/names";
        try {
            final String nameJson = IOUtils.toString(new URL(url));
            if (nameJson != null && nameJson.length() > 0) {
                final JSONArray jsonArray = (JSONArray) JSONValue.parseWithException(nameJson);
                if (jsonArray != null) {
                    final JSONObject latestName = (JSONObject) jsonArray.get(jsonArray.size() - 1);
                    if (latestName != null) {
                        return latestName.get("name").toString();
                    }
                }
            }
        }
        catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        return null;
    }




    public boolean nado = false;

    String lasturl = "";

    public void saveDickPick(String s,String format){
        if(Objects.equals(lasturl, s) && nado){return;}
        try {
            lasturl = s;
            URL url = new URL(s);
            URLConnection openConnection = url.openConnection();
            boolean check = true;

            try {
                openConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
                openConnection.connect();

                if (openConnection.getContentLength() > 8000000) {
                    System.out.println(" file size is too big.");
                    check = false;
                }
            } catch (Exception e) {
                System.out.println("Couldn't create a connection to the link, please recheck the link.");
                check = false;
                e.printStackTrace();
            }
            if (check) {
                BufferedImage img = null;
                try {
                    InputStream in = new BufferedInputStream(openConnection.getInputStream());
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    byte[] buf = new byte[1024];
                    int n = 0;
                    while (-1 != (n = in.read(buf))) {
                        out.write(buf, 0, n);
                    }
                    out.close();
                    in.close();
                    byte[] response = out.toByteArray();
                    img = ImageIO.read(new ByteArrayInputStream(response));
                } catch (Exception e) {
                    System.out.println(" couldn't read an image from this link.");
                    e.printStackTrace();
                }
                try {
                    int niggermod = (int) (Math.random()*10000);
                    ImageIO.write(img, format, new File("ThunderHack/tmp/" + niggermod + "." + format));
                    filename = String.valueOf(niggermod);
                    formatfila = format;
                    once = true;
                } catch (IOException e) {
                    System.out.println("Couldn't create/send the output image.");
                    e.printStackTrace();
                }
            }
        } catch (Exception e){

        }
    }


    int postid = 0;

    String filename = "";
    String formatfila = "";

    public boolean once = false;

    public ResourceLocation logo = PNGtoResourceLocation.getTexture(filename,formatfila);

    public float nigw = 0;
    public float nigh = 0;

    public Timer timer = new Timer();

    int xvalue = 0;
    int yvalue = 0;

    @SubscribeEvent
    public void onRender2D(Render2DEvent e) {
        GuiScreen guiscreen = mc.currentScreen;
        if (nado && guiscreen instanceof GuiChat && !Objects.equals(this.filename, "") && !Objects.equals(this.formatfila, "")) {
            if (this.once) {
                this.logo = PNGtoResourceLocation.getTexture(this.filename, this.formatfila);
                this.once = false;
            }

            if (this.logo != null) {
                Util.mc.getTextureManager().bindTexture(this.logo);
                ModuleButton.drawCompleteImage(xvalue, yvalue - (nigh), (int) nigw, (int) nigh);
            }
        } if(!nado){
            this.logo = null;
        }
        if(timer.passedMs(1500)){
            nado = false;
        }


        ScaledResolution sr = new ScaledResolution(mc);

        xvalue = Mouse.getX();
        yvalue = sr.getScaledHeight() - Mouse.getY();

    }

    public String solvename(String notsolved){
        AtomicReference<String> mb = new AtomicReference<>("err");
        Objects.requireNonNull(Util.mc.getConnection()).getPlayerInfoMap().forEach(player -> {
            if(notsolved.contains(player.getGameProfile().getName())){
                mb.set(player.getGameProfile().getName());
            }
        });
        return mb.get();
    }





}

