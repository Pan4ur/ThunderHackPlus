package com.mrzak34.thunderhack.command.commands;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.command.Command;
import com.mrzak34.thunderhack.manager.ConfigManager;
import com.mrzak34.thunderhack.manager.EventManager;
import com.mrzak34.thunderhack.modules.client.MainSettings;

import java.awt.*;
import java.io.File;
import java.util.Objects;

public class CfgCommand extends Command {
    public CfgCommand() {
        super("cfg");
    }

    public void execute(String[] commands) {
        if (commands.length == 1) {

            if(Thunderhack.moduleManager.getModuleByClass(MainSettings.class).language.getValue() == MainSettings.Language.RU) {
                sendMessage("Конфиги сохраняются в  ThunderHack/configs/");
            } else {
                sendMessage("Configurations are saved in ThunderHack/configs/");
            }
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
                if(Thunderhack.moduleManager.getModuleByClass(MainSettings.class).language.getValue() == MainSettings.Language.RU) {
                    sendMessage("Нет такой команды!... Может list ?");
                } else {
                    sendMessage("Wrong command!... Maybe list?");
                }
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
            if(Thunderhack.moduleManager.getModuleByClass(MainSettings.class).language.getValue() == MainSettings.Language.RU) {
                sendMessage("Нет такой команды! Пример использования: <save/load>");
            } else {
                sendMessage("Wrong command! try: <save/load>");
            }
        }
    }
}
