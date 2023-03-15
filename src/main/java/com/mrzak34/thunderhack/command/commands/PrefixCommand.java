package com.mrzak34.thunderhack.command.commands;

import com.mojang.realmsclient.gui.ChatFormatting;
import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.command.Command;
import com.mrzak34.thunderhack.modules.client.MainSettings;

public class PrefixCommand
        extends Command {
    public PrefixCommand() {
        super("prefix");
    }

    @Override
    public void execute(String[] commands) {
        if (commands.length == 1) {
            if (Thunderhack.moduleManager.getModuleByClass(MainSettings.class).language.getValue() == MainSettings.Language.RU) {
                Command.sendMessage(ChatFormatting.GREEN + "Текущий префикс:" + Thunderhack.commandManager.getPrefix());
            } else {
                Command.sendMessage(ChatFormatting.GREEN + "current prefix:" + Thunderhack.commandManager.getPrefix());
            }
            return;
        }
        Thunderhack.commandManager.setPrefix(commands[0]);
        if (Thunderhack.moduleManager.getModuleByClass(MainSettings.class).language.getValue() == MainSettings.Language.RU) {
            Command.sendMessage("Префикс изменен на  " + ChatFormatting.GRAY + commands[0]);
        } else {
            Command.sendMessage("Prefix changed to  " + ChatFormatting.GRAY + commands[0]);
        }
    }
}

