package com.mrzak34.thunderhack.command.commands;

import com.mrzak34.thunderhack.command.Command;
import com.mrzak34.thunderhack.modules.client.Optimization;

public class RamClearCommand extends Command {
    public RamClearCommand() {
        super("clearram");
    }

    @Override
    public void execute(String[] var1) {
        Optimization.cleanMemory();
    }
}
