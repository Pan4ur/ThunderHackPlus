package com.mrzak34.thunderhack.command.commands;

import com.mojang.realmsclient.gui.ChatFormatting;
import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.command.Command;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Bind;
import org.lwjgl.input.Keyboard;

public class BindCommand extends Command {
    public BindCommand() {
        super("bind");
    }

    @Override
    public void execute(String[] commands) {
        if (commands.length < 2) {
            sendMessage("Usage: .bind <module> <key>");
            return;
        }

        String moduleName = commands[0];
        String rawKey = commands[1];

        Module module = Thunderhack.moduleManager.getModuleByName(moduleName);
        if (module == null) {
            sendMessage("Unknown module '" + moduleName + "'!");
            return;
        }

        int key = getKeyFromName(rawKey.toUpperCase());
        if (key == 0) {
            sendMessage("Unknown key '" + rawKey + "'!");
            return;
        }

        module.bind.setValue(new Bind(key));
        sendMessage("Bind for " + ChatFormatting.GREEN + module.getName() + ChatFormatting.WHITE + " set to " + ChatFormatting.GRAY + rawKey.toUpperCase());
    }

    private int getKeyFromName(String keyName) {
        if (keyName.equalsIgnoreCase("none")) {
            return -1;
        }

        return Keyboard.getKeyIndex(keyName);
    }
}
