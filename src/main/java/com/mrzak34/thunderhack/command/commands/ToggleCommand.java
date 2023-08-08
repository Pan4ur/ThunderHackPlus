package com.mrzak34.thunderhack.command.commands;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.command.Command;
import com.mrzak34.thunderhack.modules.Module;

import java.util.Objects;

public class ToggleCommand extends Command {
    public ToggleCommand() {
        super("Toggle");
    }

    public boolean foundModule = false;

    @Override
    public void execute(String[] commands) {
        if (commands.length == 1) {
            if(commands[0].isEmpty()) {
                Command.sendMessage("Incomplete command, must be .toggle <moduleName>");
                return;
            }
            for(Module m : Thunderhack.moduleManager.modules) {
                if(Objects.equals(m.getName().toLowerCase(), commands[0].toLowerCase())) {
                    m.toggle();
                    Command.sendMessage("Toggled module: " + m.getName());
                    foundModule = true;
                    break;
                }
            }

            Command.sendMessage("Couldn't find module: " + commands[0]);
        }
    }
}