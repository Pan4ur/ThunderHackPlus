package com.mrzak34.thunderhack.command.commands;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.command.Command;
import com.mrzak34.thunderhack.modules.client.MainSettings;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;

public class HClipCommand extends Command {

    public HClipCommand() {
        super("hclip");
    }

    @Override
    public void execute(String[] commands) {
        if (commands.length == 1) {
            if (Thunderhack.moduleManager.getModuleByClass(MainSettings.class).language.getValue() == MainSettings.Language.RU) {
                Command.sendMessage("Попробуй .hclip <число>");
            } else {
                Command.sendMessage("Try .hclip <number>");
            }
            return;
        }
        if (commands.length == 2) {
            try {
                if (Thunderhack.moduleManager.getModuleByClass(MainSettings.class).language.getValue() == MainSettings.Language.RU) {
                    Command.sendMessage(TextFormatting.GREEN + "клипаемся на  " + Double.valueOf(commands[0]) + " блоков.");
                } else {
                    Command.sendMessage(TextFormatting.GREEN + "clipping to  " + Double.valueOf(commands[0]) + " blocks.");
                }

                float f = this.mc.player.rotationYaw * ((float) Math.PI / 180);
                double speed = Double.valueOf(commands[0]);
                double x = -((double) MathHelper.sin(f) * speed);
                double z = (double) MathHelper.cos(f) * speed;
                this.mc.player.setPosition(this.mc.player.posX + x, this.mc.player.posY, this.mc.player.posZ + z);
            } catch (Exception exception) {
            }

        }
    }
}
