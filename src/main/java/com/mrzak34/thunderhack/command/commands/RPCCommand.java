package com.mrzak34.thunderhack.command.commands;

import com.mrzak34.thunderhack.Discord;
import com.mrzak34.thunderhack.command.Command;

public class RPCCommand extends Command {


    public RPCCommand() {
        super("rpc", new String[]{"<imageformat>",  "<url>"});
    }

    @Override
    public void execute(String[] args) {

        if (args.length == 1) {
            ModuleCommand.sendMessage(".rpc l/s url");
            return;

        }
        if (args.length == 2) {
            Discord.WriteFile(args[0],"none");
            Command.sendMessage("Большая картинка RPC изменена на " + args[0]);
            return;
        }
        if (commands.length >= 2) {
            Discord.WriteFile(args[0],args[1]);
            Command.sendMessage("Большая картинка RPC изменена на " + args[0]);
            Command.sendMessage("Маленькая картинка RPC изменена на " + args[1]);
        }

    }
}
