package com.mrzak34.thunderhack.command.commands;


import com.mrzak34.thunderhack.command.Command;
import com.mrzak34.thunderhack.modules.misc.NoCom;

public class ScannerCommand extends Command {
    public ScannerCommand() {
        super("scanner");
    }

    @Override
    public void execute(String[] commands) {
        Command.sendMessage("scanner gui loaded");
        NoCom.getgui();
    }
}

