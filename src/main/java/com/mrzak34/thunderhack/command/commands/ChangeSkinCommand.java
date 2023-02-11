package com.mrzak34.thunderhack.command.commands;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.command.Command;

import com.mrzak34.thunderhack.modules.client.MainSettings;
import com.mrzak34.thunderhack.util.ThunderUtils;

import java.util.ArrayList;

public class ChangeSkinCommand extends Command {
    public ChangeSkinCommand() {
        super("skinset");
        this.setInstance();
    }
    private void setInstance() {
        INSTANCE = this;
    }

    public ArrayList<String> changedplayers = new ArrayList<String>();


    private static ChangeSkinCommand INSTANCE = new ChangeSkinCommand();
    public static ChangeSkinCommand getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ChangeSkinCommand();
        }
        return INSTANCE;
    }

    @Override
    public void execute(String[] commands) {
        if (commands.length == 1) {
            if(Thunderhack.moduleManager.getModuleByClass(MainSettings.class).language.getValue() == MainSettings.Language.RU) {
                Command.sendMessage("skinset имяигрока имяскина");
            } else {
                Command.sendMessage("skinset playername skinname");
            }
            return;
        }
        if (commands.length == 2) {
            if(Thunderhack.moduleManager.getModuleByClass(MainSettings.class).language.getValue() == MainSettings.Language.RU) {
                Command.sendMessage("skinset имяигрока имяскина");
            } else {
                Command.sendMessage("skinset playername skinname");
            }
            return;
        }
        if (commands.length == 3) {
            ThunderUtils.savePlayerSkin("https://minotar.net/skin/" + commands[1],commands[0]);
            changedplayers.add(commands[0]);
            if(Thunderhack.moduleManager.getModuleByClass(MainSettings.class).language.getValue() == MainSettings.Language.RU) {
                Command.sendMessage("Скин игрока " + commands[0] + " изменен на " + commands[1]);
            } else {
                Command.sendMessage("Player " + commands[0] + "'s skin has been changed to " + commands[1]);
            }
        }
    }

}
