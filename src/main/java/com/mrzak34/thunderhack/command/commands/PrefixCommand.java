package com.mrzak34.thunderhack.command.commands;

import com.mojang.realmsclient.gui.ChatFormatting;
import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.command.Command;

public class PrefixCommand
        extends Command {
    public PrefixCommand() {
        super("prefix");
    }

    @Override
    public void execute(String[] commands) {
        if (commands.length == 1) {
            Command.sendMessage(ChatFormatting.GREEN + "Текущий префикс:" + Thunderhack.commandManager.getPrefix());
            return;
        }
        Thunderhack.commandManager.setPrefix(commands[0]);
        Command.sendMessage("Префикс изменен на  " + ChatFormatting.GRAY + commands[0]);
    }
}

