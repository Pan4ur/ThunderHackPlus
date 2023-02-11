package com.mrzak34.thunderhack.command.commands;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.command.Command;
import com.mrzak34.thunderhack.modules.client.MainSettings;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.text.TextFormatting;

public class VClipCommand extends Command {

    public VClipCommand() {
        super("vclip");
    }

    @Override
    public void execute(String[] commands) {
        if (commands.length == 1) {
            if(Thunderhack.moduleManager.getModuleByClass(MainSettings.class).language.getValue() == MainSettings.Language.RU) {
                Command.sendMessage("Попробуй .vclip <число>");
            } else {
                Command.sendMessage("Try .vclip <number>");
            }
            return;
        }
        if (commands.length == 2) {
            try {
                int i;

                if(Thunderhack.moduleManager.getModuleByClass(MainSettings.class).language.getValue() == MainSettings.Language.RU) {
                    Command.sendMessage((Object) TextFormatting.GREEN + "Клипаемся на " + Double.valueOf(commands[0]) + " блоков");
                } else {
                    Command.sendMessage((Object)TextFormatting.GREEN + "clipping to  " + Double.valueOf(commands[0]) + " blocks.");
                }

                for (i = 0; i < 10; ++i) {
                    this.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(this.mc.player.posX, this.mc.player.posY, this.mc.player.posZ, false));
                }
                for (i = 0; i < 10; ++i) {
                    this.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(this.mc.player.posX, this.mc.player.posY + Double.parseDouble(commands[0]), this.mc.player.posZ, false));
                }
                this.mc.player.setPosition(this.mc.player.posX, this.mc.player.posY + Double.parseDouble(commands[0]), this.mc.player.posZ);
            }
            catch (Exception i) {
                // empty catch block
            }

            return;
        }
    }
}
