package com.mrzak34.thunderhack.command.commands;

import com.mojang.realmsclient.gui.ChatFormatting;
import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.command.Command;

public class HelpCommand
        extends Command {
    public HelpCommand() {
        super("help");
    }

    @Override
    public void execute(String[] commands) {
        HelpCommand.sendMessage("Commands: ");
        for (Command command : Thunderhack.commandManager.getCommands()) {
            HelpCommand.sendMessage(ChatFormatting.GRAY + Thunderhack.commandManager.getPrefix() + command.getName());
        }
    }
}

