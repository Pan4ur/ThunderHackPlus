package com.mrzak34.thunderhack.command.commands;


import com.mrzak34.thunderhack.command.Command;
import com.mrzak34.thunderhack.manager.ConfigManager;

import java.awt.*;
import java.io.File;
import java.util.Objects;


public class ConfigCommand extends Command {
    public ConfigCommand() {
        super("config");
    }

    public void execute(String[] commands) {
        if (commands.length == 1) {
            sendMessage("Конфиги сохраняются в  ThunderHack/configs/");
            return;
        }
        if (commands.length == 2)
            if ("list".equals(commands[0])) {
                StringBuilder configs = new StringBuilder("Configs: ");
                for(String str : Objects.requireNonNull(ConfigManager.getConfigList())){
                    configs.append("\n- ").append(str);
                }
                sendMessage(configs.toString());
            } else if( "dir".equals(commands[0]) ){
                try {
                    Desktop.getDesktop().browse(new File("ThunderHack/configs/").toURI());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                sendMessage("Нет такой команды!... Может list ?");
            }
        if (commands.length >= 3) {
            switch (commands[0]) {
                case "save":
                case "create":
                    ConfigManager.save(commands[1]);
                    return;
                case "set":
                case "load":
                    ConfigManager.load(commands[1]);
                    return;
            }
            sendMessage("Нет такой команды! Пример использования: <save/load>");
        }
    }

}
