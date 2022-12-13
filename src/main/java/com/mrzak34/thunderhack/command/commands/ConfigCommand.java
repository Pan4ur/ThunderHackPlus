package com.mrzak34.thunderhack.command.commands;

import com.mojang.realmsclient.gui.ChatFormatting;
import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.command.Command;

import java.awt.*;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ConfigCommand extends Command {
    public ConfigCommand() {
        super("config", new String[]{"<save/load/dir>"});
    }

    public void execute(String[] commands) {
        File dir = new File("ThunderHack/");
        if (commands.length == 1) {
            sendMessage("Конфиги сохраняются в  ThunderHack/config");
            return;
        }
        if (commands.length == 2)
            if ("list".equals(commands[0])) {
                String configs = "Configs: ";
                File file = new File("ThunderHack/");
                List<File> directories = Arrays.stream(file.listFiles()).filter(File::isDirectory).filter(f -> !f.getName().equals("util")).collect(Collectors.toList());
                StringBuilder builder = new StringBuilder(configs);
                for (File file1 : directories)
                    builder.append(file1.getName() + ", ");
                configs = builder.toString();
                sendMessage(configs);
            } else if( "dir".equals(commands[0]) ){
                try {
                    Desktop.getDesktop().browse(dir.toURI());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                sendMessage("Нет такой команды!... Может list ?");
            }
        if (commands.length >= 3) {
            switch (commands[0]) {
                case "save":
                    Thunderhack.configManager.saveConfig(commands[1]);
                    sendMessage(ChatFormatting.GREEN + "Конфиг '" + commands[1] + "' сохранен");
                    return;
                case "load":
                    if (Thunderhack.configManager.configExists(commands[1])) {
                        Thunderhack.configManager.loadConfig(commands[1],false);
                        sendMessage(ChatFormatting.GREEN + "Загружен конфиг '" + commands[1]);
                    } else {
                        sendMessage(ChatFormatting.RED + "Конфиг '" + commands[1] + "' не существует");
                    }
                    return;
            }
            sendMessage("Нет такой команды! Пример использования: <save/load>");
        }
    }
}
