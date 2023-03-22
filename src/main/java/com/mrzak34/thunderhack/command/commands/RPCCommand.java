package com.mrzak34.thunderhack.command.commands;

import com.mrzak34.thunderhack.command.Command;
import com.mrzak34.thunderhack.modules.client.RPC;

public class RPCCommand extends Command {


    public RPCCommand() {
        super("rpc");
    }

    @Override
    public void execute(String[] args) {

        if (args.length == 1) {
            ModuleCommand.sendMessage(".rpc url or .rpc url url");
            return;

        }
        if (args.length == 2) {
            RPC.WriteFile(args[0], "none");
            Command.sendMessage("Большая картинка RPC изменена на " + args[0]);
            return;
        }
        if (args.length >= 2) {
            RPC.WriteFile(args[0], args[1]);
            Command.sendMessage("Большая картинка RPC изменена на " + args[0]);
            Command.sendMessage("Маленькая картинка RPC изменена на " + args[1]);
        }

    }
}
