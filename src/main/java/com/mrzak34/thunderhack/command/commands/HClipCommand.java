package com.mrzak34.thunderhack.command.commands;

import com.mrzak34.thunderhack.command.Command;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;

public class HClipCommand extends Command {

        public HClipCommand() {
            super("hclip");
        }

        @Override
        public void execute(String[] commands) {
            if (commands.length == 1) {
                Command.sendMessage("Попробуй .hclip <число>");
                return;
            }
            if (commands.length == 2) {
                try {
                    Command.sendMessage((Object)TextFormatting.GREEN + "клипаемся на  " + Double.valueOf(commands[0]) + " blocks.");
                    float f = this.mc.player.rotationYaw * ((float)Math.PI / 180);
                    double speed = Double.valueOf(commands[0]);
                    double x = -((double) MathHelper.sin((float)f) * speed);
                    double z = (double)MathHelper.cos((float)f) * speed;
                    this.mc.player.setPosition(this.mc.player.posX + x, this.mc.player.posY, this.mc.player.posZ + z);
                }
                catch (Exception exception) {}

                return;
            }
        }
}
