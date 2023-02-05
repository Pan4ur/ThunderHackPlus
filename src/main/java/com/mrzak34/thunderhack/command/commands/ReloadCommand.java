package com.mrzak34.thunderhack.command.commands;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.command.Command;

public class ReloadCommand
        extends Command {
    public ReloadCommand() {
        super("reload");
    }

    @Override
    public void execute(String[] commands) {
        Thunderhack.reload();
    }
}

