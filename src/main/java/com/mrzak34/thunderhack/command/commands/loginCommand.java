package com.mrzak34.thunderhack.command.commands;

import com.mrzak34.thunderhack.command.Command;
import com.mrzak34.thunderhack.util.AccountAuthenticator;
import com.mrzak34.thunderhack.util.Util;

public class loginCommand extends Command {

    public loginCommand() {
        super("login");
    }

    @Override
    public void execute(String[] var1) {
        try {
            if (var1.length > 2 || var1[0].contains(":")) {
                String[] object;
                String string2 = "";
                String string3 = "";
                if (var1[0].contains(":")) {
                    object = var1[0].split(":", 2);
                    string2 = object[0];
                    string3 = object[1];
                } else {
                    string2 = var1[0];
                    string3 = var1[1];
                }
                Command.sendMessage(AccountAuthenticator.a(string2, string3));
            } else {
                AccountAuthenticator.a(var1[0]);
                Command.sendMessage("Logged [Cracked]: " + Util.mc.getSession().getUsername());
            }
        }
        catch (Exception exception) {
            Command.sendMessage("Usage: .login nick / .login email password");
        }
    }
}
