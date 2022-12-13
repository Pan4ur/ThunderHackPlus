//
// Decompiled by Procyon v0.5.36
//

package com.mrzak34.thunderhack.command.commands;

import com.mojang.realmsclient.gui.ChatFormatting;
import com.mrzak34.thunderhack.command.Command;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.audio.SoundManager;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class ReloadSoundCommand extends Command {
    public ReloadSoundCommand() {
        super("sound", new String[0]);
    }

    @Override
    public void execute(final String[] commands) {
        try {
            final SoundManager sndManager = (SoundManager) ObfuscationReflectionHelper.getPrivateValue(SoundHandler.class, ReloadSoundCommand.mc.getSoundHandler(), new String[]{"sndManager", "sndManager"});
            sndManager.reloadSoundSystem();
            Command.sendMessage(ChatFormatting.GREEN + "Reloaded Sound System.");
        } catch (Exception e) {
            System.out.println(ChatFormatting.RED + "Could not restart sound manager: " + e.toString());
            e.printStackTrace();
            Command.sendMessage(ChatFormatting.RED + "Couldnt Reload Sound System!");
        }
    }
}
