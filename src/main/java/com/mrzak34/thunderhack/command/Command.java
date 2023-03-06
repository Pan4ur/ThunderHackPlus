package com.mrzak34.thunderhack.command;

import com.mojang.realmsclient.gui.ChatFormatting;
import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.modules.Feature;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentBase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Command extends Feature {
    protected String name;


    public Command(String name) {
        super(name);
        this.name = name;
    }

    public static void sendMessage(String message) {
        Command.sendSilentMessage(Thunderhack.commandManager.getClientMessage() + " " + ChatFormatting.GRAY + message);
    }

    public static void sendMessageWithoutTH(String message) {
        Command.sendSilentMessage(ChatFormatting.GRAY + message);
    }

    public static void sendSilentMessage(String message) {
        if (Command.nullCheck()) {
            return;
        }
        Command.mc.player.sendMessage(new ChatMessage(message));
    }

    public static void sendIText(ITextComponent message) {
        if (Command.nullCheck()) {
            return;
        }
        Command.mc.player.sendMessage(message);
    }
    public static String getCommandPrefix() {
        return Thunderhack.commandManager.getPrefix();
    }


    public abstract void execute(String[] var1);

    @Override
    public String getName() {
        return this.name;
    }



    public static class ChatMessage
            extends TextComponentBase {
        private final String text;

        public ChatMessage(String text) {
            Pattern pattern = Pattern.compile("&[0123456789abcdefrlosmk]");
            Matcher matcher = pattern.matcher(text);
            StringBuffer stringBuffer = new StringBuffer();
            while (matcher.find()) {
                String replacement = matcher.group().substring(1);
                matcher.appendReplacement(stringBuffer, replacement);
            }
            matcher.appendTail(stringBuffer);
            this.text = stringBuffer.toString();
        }

        public String getUnformattedComponentText() {
            return this.text;
        }

        public ITextComponent createCopy() {
            return null;
        }

        public ITextComponent shallowCopy() {
            return new ChatMessage(this.text);
        }
    }
}

