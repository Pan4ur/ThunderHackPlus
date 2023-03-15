package com.mrzak34.thunderhack.modules.misc;

import com.mojang.realmsclient.gui.ChatFormatting;
import com.mrzak34.thunderhack.command.Command;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.Timer;
import net.minecraft.client.gui.GuiGameOver;

public class AutoRespawn extends Module {
    private final Timer timer;
    public Setting<Boolean> deathcoords = this.register(new Setting<>("deathcoords", true));
    public Setting<Boolean> autokit = this.register(new Setting<>("Auto Kit", false));
    public Setting<String> kit = this.register(new Setting<String>("kit name", "kitname", v -> autokit.getValue()));
    public Setting<Boolean> autohome = this.register(new Setting<>("Auto Home", false));


    public AutoRespawn() {
        super("AutoRespawn", "автореспавн с автокитом", Category.PLAYER);
        this.timer = new Timer();
    }

    @Override
    public void onTick() {
        if (nullCheck()) return;

        if (timer.passedMs(2100)) {
            timer.reset();
        }
        if (mc.currentScreen instanceof GuiGameOver) {
            mc.player.respawnPlayer();
            mc.displayGuiScreen(null);
        }
        if (mc.currentScreen instanceof GuiGameOver && this.timer.getPassedTimeMs() > 200) {
            if (autokit.getValue()) {
                mc.player.sendChatMessage("/kit " + kit.getValue());
            }
            if (deathcoords.getValue()) {
                Command.sendMessage(ChatFormatting.GOLD + "[PlayerDeath] " + ChatFormatting.YELLOW + (int) mc.player.posX + " " + (int) mc.player.posY + " " + (int) mc.player.posZ);
            }
            timer.reset();

        }
        if (mc.currentScreen instanceof GuiGameOver && this.timer.getPassedTimeMs() > 1000) {
            if (autohome.getValue()) {
                mc.player.sendChatMessage("/home");
            }
            if (deathcoords.getValue()) {
                Command.sendMessage(ChatFormatting.GOLD + "[PlayerDeath] " + ChatFormatting.YELLOW + (int) mc.player.posX + " " + (int) mc.player.posY + " " + (int) mc.player.posZ);
            }
            timer.reset();
        }
    }

}