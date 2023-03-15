//
// Decompiled by Procyon v0.5.36
//

package com.mrzak34.thunderhack.command.commands;

import com.mojang.realmsclient.gui.ChatFormatting;
import com.mrzak34.thunderhack.command.Command;

public class ReloadSoundCommand extends Command {
    public ReloadSoundCommand() {
        super("sound");
    }

    @Override
    public void execute(final String[] commands) {
        try {
            mc.getSoundHandler().sndManager.reloadSoundSystem();
            Command.sendMessage(ChatFormatting.GREEN + "Reloaded Sound System.");
        } catch (Exception e) {
            Command.sendMessage(ChatFormatting.RED + "Couldnt Reload Sound System!");
        }
    }
}
