package com.mrzak34.thunderhack.command.commands;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.command.Command;
import com.mrzak34.thunderhack.modules.Module;

import java.util.Objects;

public class ToggleCommand extends Command {
    public ToggleCommand() {
        super("Toggle");
    }

    @Override
    public void execute(String[] commands) {
        if (commands.length != 1 || commands[0].isEmpty()) {
            Command.sendMessage("Usage: .toggle <moduleName>");
            return;
        }

        String moduleName = commands[0].toLowerCase();
        boolean foundModule = false;

        for (Module module : Thunderhack.moduleManager.modules) {
            if (Objects.equals(module.getName().toLowerCase(), moduleName)) {
                module.toggle();
                Command.sendMessage("Toggled module: " + module.getName());
                foundModule = true;
                break;
            }
        }

        if (!foundModule) {
            Command.sendMessage("Couldn't find module: " + moduleName);
        }
    }
}
