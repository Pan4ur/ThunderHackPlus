package com.mrzak34.thunderhack.modules.misc;

import com.mrzak34.thunderhack.command.Command;
import com.mrzak34.thunderhack.events.PacketEvent;
import com.mrzak34.thunderhack.events.Render2DEvent;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.PNGtoResourceLocation;
import com.mrzak34.thunderhack.util.Timer;
import com.mrzak34.thunderhack.util.Util;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Mouse;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import static com.mrzak34.thunderhack.modules.player.ElytraSwap.drawCompleteImage;

public class DiscordEmbeds extends Module {
    public static boolean nado = false;
    public static boolean once = false;
    public static float nigw = 0;
    public static float nigh = 0;
    public static Timer timer = new Timer();
    static String lasturl = "";
    static String filename = "";
    static String formatfila = "";
    public Setting<Integer> multip = this.register(new Setting<>("Scale", 200, 50, 1280));
    public Setting<Integer> posY = this.register(new Setting<>("PosY", 0, 0, 1000));
    public Setting<Integer> posX = this.register(new Setting<>("PosX", 0, -1000, 1000));
    public Setting<Boolean> fgbypass = register(new Setting<>("FunnyGame", false));
    public ResourceLocation logo = PNGtoResourceLocation.getTexture(filename, formatfila);
    String discord = "";
    String last = "";
    int xvalue = 0;
    int yvalue = 0;

    public DiscordEmbeds() {
        super("DiscordEmbeds", "DiscordEmbeds", Category.MISC);
    }

    public static String codeFGBypass(String raw) {
        String final_string;
        final_string = raw.replace("https://cdn.discordapp.com/attachments/", "THCRYPT");
        final_string = final_string.replace("0", "а");
        final_string = final_string.replace("1", "б");
        final_string = final_string.replace("2", "в");
        final_string = final_string.replace("3", "г");
        final_string = final_string.replace("4", "д");
        final_string = final_string.replace("5", "е");
        final_string = final_string.replace("6", "ж");
        final_string = final_string.replace("7", "з");
        final_string = final_string.replace("8", "и");
        final_string = final_string.replace("9", "й");
        final_string = final_string.replace("/", "к");
        final_string = final_string.replace(".png", "о");
        final_string = final_string.replace(".", "л");
        final_string = final_string.replace("-", "м");
        final_string = final_string.replace("_", "н");
        return final_string;
    }

    public static void saveDickPick(String s, String format) {
        if (Objects.equals(lasturl, s) && nado) {
            return;
        }
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
                    int niggermod = (int) (Math.random() * 10000);
                    ImageIO.write(img, format, new File("ThunderHack/temp/embeds/" + niggermod + "." + format));
                    filename = String.valueOf(niggermod);
                    formatfila = format;
                    once = true;
                } catch (IOException e) {
                    System.out.println("Couldn't create/send the output image.");
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {

        }
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send e) {
        if (e.getPacket() instanceof CPacketChatMessage) {
            if (((CPacketChatMessage) e.getPacket()).message.contains("https://cdn.discordapp.com/attachments/") && fgbypass.getValue()) {
                ((CPacketChatMessage) e.getPacket()).message = "!" + codeFGBypass(((CPacketChatMessage) e.getPacket()).message.replace("!", ""));
            }
        }
    }

    public String decodeFGBypass(String coded) {
        String final_string;
        final_string = coded.split("THCRYPT")[1];
        final_string = final_string.replace("а", "0");
        final_string = final_string.replace("б", "1");
        final_string = final_string.replace("в", "2");
        final_string = final_string.replace("г", "3");
        final_string = final_string.replace("д", "4");
        final_string = final_string.replace("е", "5");
        final_string = final_string.replace("ж", "6");
        final_string = final_string.replace("з", "7");
        final_string = final_string.replace("и", "8");
        final_string = final_string.replace("й", "9");
        final_string = final_string.replace("к", "/");
        final_string = final_string.replace("о", ".png");
        final_string = final_string.replace("л", ".");
        final_string = final_string.replace("м", "-");
        final_string = final_string.replace("н", "_");
        Command.sendMessage("https://cdn.discordapp.com/attachments/" + final_string);
        return "https://cdn.discordapp.com/attachments/" + final_string;
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (fullNullCheck()) {
            return;
        }
        if (event.getPacket() instanceof SPacketChat) {
            final SPacketChat packet = event.getPacket();
            if (packet.getType() != ChatType.GAME_INFO && check(packet.getChatComponent().getFormattedText())) {

            }
        }
    }

    private boolean check(String message) {
        if (message.contains("THCRYPT")) {
            check(decodeFGBypass(message));
        }

        if (message.contains("discordapp") && message.contains(".png")) {
            discord = message;
            try {
                String[] splitted = discord.split("https://");
                String url = "https://" + splitted[1];
                String[] splitted1 = url.split(".png");
                last = splitted1[0] + ".png";
                ITextComponent cancel2 = new TextComponentString(last);

                if (Objects.equals(solvename(message), "err")) {
                    ITextComponent cancel = new TextComponentString("Получена картинка через THCRYPT [ПОКАЗАТЬ]");
                    cancel.setStyle(cancel.getStyle().setColor(TextFormatting.AQUA).setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, cancel2)));
                    Command.sendIText(cancel);
                } else {
                    ITextComponent cancel = new TextComponentString("<" + solvename(message) + "> Отправил картинку [Show Discord Image]");
                    cancel.setStyle(cancel.getStyle().setColor(TextFormatting.AQUA).setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, cancel2)));
                    Command.sendIText(cancel);
                }

            } catch (Exception ignored) {
            }
        }
        if (message.contains("discordapp") && message.contains(".jpg")) {
            discord = message;
            try {
                String[] splitted = discord.split("https://");
                String url = "https://" + splitted[1];
                String[] splitted1 = url.split(".jpg");
                last = splitted1[0] + ".jpg";
                ITextComponent cancel2 = new TextComponentString(last);
                ITextComponent cancel = new TextComponentString("<" + solvename(message) + "> Отправил картинку [Show Discord Image]");
                cancel.setStyle(cancel.getStyle().setColor(TextFormatting.AQUA).setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, cancel2)));
                Command.sendIText(cancel);
            } catch (Exception ignored) {
            }
        }
        return true;
    }

    @SubscribeEvent
    public void onRender2D(Render2DEvent e) {
        GuiScreen guiscreen = mc.currentScreen;
        if (nado && guiscreen instanceof GuiChat && !Objects.equals(filename, "") && !Objects.equals(formatfila, "")) {
            if (once) {
                this.logo = PNGtoResourceLocation.getTexture(filename, formatfila);
                once = false;
            }

            if (this.logo != null) {
                Util.mc.getTextureManager().bindTexture(this.logo);
                drawCompleteImage(xvalue - (nigw / 2) + posX.getValue(), yvalue - (nigh / 2) + posY.getValue(), (int) nigw, (int) nigh);
            }
        }
        if (!nado) {
            this.logo = null;
        }
        if (timer.passedMs(500)) {
            nado = false;
        }


        ScaledResolution sr = new ScaledResolution(mc);

        xvalue = Mouse.getX() / 2;
        yvalue = (sr.getScaledHeight() - Mouse.getY()) / 2;

    }

    public String solvename(String notsolved) {
        AtomicReference<String> mb = new AtomicReference<>("err");
        Objects.requireNonNull(Util.mc.getConnection()).getPlayerInfoMap().forEach(player -> {
            if (notsolved.contains(player.getGameProfile().getName())) {
                mb.set(player.getGameProfile().getName());
            }
        });
        return mb.get();
    }

}
