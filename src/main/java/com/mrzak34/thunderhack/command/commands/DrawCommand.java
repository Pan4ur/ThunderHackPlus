package com.mrzak34.thunderhack.command.commands;

import com.mojang.realmsclient.gui.ChatFormatting;
import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.command.Command;
import com.mrzak34.thunderhack.modules.Module;

public class DrawCommand extends Command {
    public DrawCommand() {
        super("draw");
    }

    @Override
    public void execute(String[] commands) {
        if (commands.length == 1) {
            Command.sendMessage("Напиши название модуля");
            return;
        }
        String moduleName = commands[0];
        Module module = Thunderhack.moduleManager.getModuleByName(moduleName);
        if (module == null) {
            Command.sendMessage("Неизвестный модуль'" + module + "'!");
            return;
        }

        module.setDrawn(!module.isDrawn());
        BindCommand.sendMessage("Модуль " + ChatFormatting.GREEN + module.getName() + ChatFormatting.WHITE + " теперь " + (module.isDrawn() ? "виден в ArrayList" : "не виден в ArrayList"));
    }
}
