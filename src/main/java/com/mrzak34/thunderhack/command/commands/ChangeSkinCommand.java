package com.mrzak34.thunderhack.command.commands;

import com.mrzak34.thunderhack.command.Command;

import com.mrzak34.thunderhack.util.ThunderUtils;

import java.util.ArrayList;

public class ChangeSkinCommand extends Command {
    public ChangeSkinCommand() {
        super("skinset", new String[]{"<name>", "<skinname>"});
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
            Command.sendMessage("skinset имяигрока имяскина");
            return;
        }
        if (commands.length == 2) {
            Command.sendMessage("skinset имяигрока имяскина");
            return;
        }
        if (commands.length == 3) {
            ThunderUtils.savePlayerSkin("https://minotar.net/skin/" + commands[1],commands[0]);
            changedplayers.add(commands[0]);
            Command.sendMessage("Скин игрока " + commands[0] + " изменен на " + commands[1]);
        }
    }

}
