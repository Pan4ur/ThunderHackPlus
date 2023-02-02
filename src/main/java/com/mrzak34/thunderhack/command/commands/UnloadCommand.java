package com.mrzak34.thunderhack.command.commands;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.command.Command;

public class UnloadCommand
        extends Command {
    public UnloadCommand() {
        super("unload");
    }

    @Override
    public void execute(String[] commands) {
        Thunderhack.unload(true);
    }
}

