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
            for(Module m : Thunderhack.moduleManager.modules) {
                if(Objects.equals(m.getName(), commands[0])) {
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