package com.mrzak34.thunderhack.command.commands;

import com.mrzak34.thunderhack.command.Command;
import com.mrzak34.thunderhack.modules.funnygame.ChatBot;

public class BullingCommand extends Command {

    public BullingCommand() {
        super("bulling");
    }

    @Override
    public void execute(String[] var1) {
        ChatBot.target = var1[0];
        Command.sendMessage("Задана цель - " + var1[0]);
    }
}
