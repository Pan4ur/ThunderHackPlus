package com.mrzak34.thunderhack.modules.client;

import com.mrzak34.thunderhack.event.events.PacketEvent;
import com.mrzak34.thunderhack.command.Command;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.util.text.ChatType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Date;

public class ThunderChat extends Module {

    public ThunderChat() {
        super("ThunderChat", "ThunderChat", Category.CLIENT, true, true, false);
    }

    public Setting<Boolean> crypt = register(new Setting<>("Crypt", true));



    String string;
    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive e){
        if(e.getPacket() instanceof SPacketChat){
            SPacketChat sp = e.getPacket();
            if (sp.getType() != ChatType.GAME_INFO) {
                string = sp.getChatComponent().getFormattedText();
                if(string.contains("thndrh4k")){
                   string = "[ThunderChat]" + convertBack(string);
                }
                Command.sendMessageWithoutTH(string);
            }
        }
    }


    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send e){
        if(fullNullCheck()){
            return;
        }
        if(!crypt.getValue()){
            return;
        }
        if(e.getPacket() instanceof CPacketChatMessage){
            if (((CPacketChatMessage) e.getPacket()).getMessage().startsWith("/") || ((CPacketChatMessage) e.getPacket()).getMessage().startsWith(Command.getCommandPrefix())) return;

            ((CPacketChatMessage) e.getPacket()).message = "!" + convert(((CPacketChatMessage) e.getPacket()).getMessage().toLowerCase()).replace("!", "");
        }
    }


    static Date date = new Date(System.currentTimeMillis());

    public static String convert(String raw){
        StringBuilder coded = new StringBuilder();

        raw = Rot(raw,false);
        char[] chars = raw.toCharArray();
        coded.append("thndrh4k");


        for (char aChar : chars) {
            String chr = String.valueOf(aChar);
            if (chr.equals(" ")) {
                coded.append("0f");
            }
            if (chr.equals("а")) {
                coded.append("1f");
            }
            if (chr.equals("б")) {
                coded.append("2g");
            }
            if (chr.equals("в")) {
                coded.append("3d");
            }
            if (chr.equals("г")) {
                coded.append("5o");
            }
            if (chr.equals("д")) {
                coded.append("6y");
            }
            if (chr.equals("е")) {
                coded.append("7q");
            }
            if (chr.equals("ё")) {
                coded.append("8i");
            }
            if (chr.equals("ж")) {
                coded.append("9l");
            }
            if (chr.equals("з")) {
                coded.append("1c");
            }
            if (chr.equals("и")) {
                coded.append("2k");
            }
            if (chr.equals("й")) {
                coded.append("3a");
            }
            if (chr.equals("к")) {
                coded.append("4j");
            }
            if (chr.equals("л")) {
                coded.append("6l");
            }
            if (chr.equals("м")) {
                coded.append("7h");
            }
            if (chr.equals("н")) {
                coded.append("8b");
            }
            if (chr.equals("о")) {
                coded.append("9b");
            }
            if (chr.equals("п")) {
                coded.append("1t");
            }
            if (chr.equals("р")) {
                coded.append("2q");
            }
            if (chr.equals("с")) {
                coded.append("3w");
            }
            if (chr.equals("т")) {
                coded.append("4n");
            }
            if (chr.equals("у")) {
                coded.append("5z");
            }
            if (chr.equals("ф")) {
                coded.append("6c");
            }
            if (chr.equals("х")) {
                coded.append("7v");
            }
            if (chr.equals("ч")) {
                coded.append("8u");
            }
            if (chr.equals("щ")) {
                coded.append("9a");
            }
            if (chr.equals("ш")) {
                coded.append("1s");
            }
            if (chr.equals("ъ")) {
                coded.append("2d");
            }
            if (chr.equals("ь")) {
                coded.append("3h");
            }
            if (chr.equals("э")) {
                coded.append("4l");
            }
            if (chr.equals("ю")) {
                coded.append("5n");
            }
            if (chr.equals("я")) {
                coded.append("6x");
            }
            if (chr.equals("ц")) {
                coded.append("7f");
            }
            if (chr.equals("ы")) {
                coded.append("8c");
            }
        }
        return coded.toString();
    }

    public static String convertBack(String raw){

        if (raw.contains("thndrh4k")) {
            raw = raw.replace("thndrh4k","");
        }
        if (raw.contains("0f")) {
            raw = raw.replace("0f"," ");
        }
        if (raw.contains("1f")) {
            raw = raw.replace("1f","а");
        }
        if (raw.contains("2g")) {
            raw = raw.replace("2g","б");
        }
        if (raw.contains("3d")) {
            raw = raw.replace("3d","в");
        }
        if (raw.contains("5o")) {
            raw = raw.replace("5o","г");
        }
        if (raw.contains("6y")) {
            raw = raw.replace("6y","д");
        }
        if (raw.contains("7q")) {
            raw = raw.replace("7q","е");
        }
        if (raw.contains("8i")) {
            raw = raw.replace("8i","ё");
        }
        if (raw.contains("9l")) {
            raw = raw.replace("9l","ж");
        }
        if (raw.contains("1c")) {
            raw = raw.replace("1c","з");
        }
        if (raw.contains("2k")) {
            raw = raw.replace("2k","и");
        }
        if (raw.contains("3a")) {
            raw = raw.replace("3a","й");
        }
        if (raw.contains("4j")) {
            raw = raw.replace("4j","к");
        }
        if (raw.contains("6l")) {
            raw = raw.replace("6l","л");
        }
        if (raw.contains("7h")) {
            raw = raw.replace("7h","м");
        }
        if (raw.contains("8b")) {
            raw = raw.replace("8b","н");
        }
        if (raw.contains("9b")) {
            raw = raw.replace("9b","о");
        }
        if (raw.contains("1t")) {
            raw = raw.replace("1t","п");
        }
        if (raw.contains("2q")) {
            raw = raw.replace("2q","р");
        }
        if (raw.contains("3w")) {
            raw = raw.replace("3w","с");
        }
        if (raw.contains("4n")) {
            raw = raw.replace("4n","т");
        }
        if (raw.contains("5z")) {
            raw = raw.replace("5z","у");
        }
        if (raw.contains("6c")) {
            raw = raw.replace("6c","ф");
        }
        if (raw.contains("7v")) {
            raw = raw.replace("7v","х");
        }
        if (raw.contains("8u")) {
            raw = raw.replace("8u","ч");
        }
        if (raw.contains("9a")) {
            raw = raw.replace("9a","щ");
        }
        if (raw.contains("1s")) {
            raw = raw.replace("1s","ш");
        }
        if (raw.contains("2d")) {
            raw = raw.replace("2d","ъ");
        }
        if (raw.contains("3h")) {
            raw = raw.replace("3h","ь");
        }
        if (raw.contains("4l")) {
            raw = raw.replace("4l","э");
        }
        if (raw.contains("5n")) {
            raw = raw.replace("5n","ю");
        }
        if (raw.contains("6x")) {
            raw = raw.replace("6x","я");
        }
        if (raw.contains("7f")) {
            raw = raw.replace("7f","ц");
        }
        if (raw.contains("8c")) {
            raw = raw.replace("8c","ы");
        }
        raw = Rot(raw,true);
        return raw;
    }


    public static String Rot(String a,boolean back){
        StringBuilder res = new StringBuilder();
        char[] textChar = a.toCharArray();
        char[] lCh = { 'а', 'б', 'в', 'г', 'д', 'е', 'ё', 'ж', 'з', 'и','й', 'к', 'л', 'м', 'н', 'о', 'п', 'р', 'с','т', 'у', 'ф', 'х', 'ц', 'ч', 'ш', 'щ', 'ъ', 'ы', 'ь', 'э','ю', 'я' };
        for (int i=0; i < textChar.length;i++){
            int index =0;

            if (Character.isLowerCase(textChar[i])){
                while (textChar[i] != lCh[index]) index++;

                if(!back){
                    index += date.getMinutes() / 10;
                } else if ((date.getMinutes() / 10) == 0){
                    index += 33;
                }else if ((date.getMinutes() / 10) == 1){
                    index += 32;
                }else if ((date.getMinutes() / 10) == 2){
                    index += 31;
                }else if ((date.getMinutes() / 10) == 3){
                    index += 30;
                }else if ((date.getMinutes() / 10) == 4){
                    index += 29;
                }else if ((date.getMinutes() / 10) == 5){
                    index += 28;
                }
                if (index > 32){index -= 33;}
                textChar[i] = lCh[index];
            }
        }
        for (char c:textChar) {
            res.append(c);
        }
        return res.toString();
    }
    //абвгдеёжзийклмнопрстуфхцчшщъыьэюя

    //6 27
    //5 28
    //4 29
    //3 30
    //2 31
    //1 32
    //0 33

    //200 цифр - макс
}
