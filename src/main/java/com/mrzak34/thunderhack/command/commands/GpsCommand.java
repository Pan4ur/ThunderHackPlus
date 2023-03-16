package com.mrzak34.thunderhack.command.commands;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.command.Command;
import com.mrzak34.thunderhack.modules.client.MainSettings;
import net.minecraft.util.math.BlockPos;

import java.util.Objects;

public class GpsCommand extends Command {
    public GpsCommand() {
        super("gps");
    }

    @Override
    public void execute(String[] commands) {
        if (commands.length == 1) {
            if (Thunderhack.moduleManager.getModuleByClass(MainSettings.class).language.getValue() == MainSettings.Language.RU) {
                Command.sendMessage("Попробуй .gps off / .gps x y");
            } else {
                Command.sendMessage("Try .gps off / .gps x y");
            }
        } else if (commands.length == 2) {
            if (Objects.equals(commands[0], "off"))
                Thunderhack.gps_position = null;
        } else if (commands.length > 2) {
            BlockPos pos = new BlockPos(Integer.parseInt(commands[0]), 0, Integer.parseInt(commands[1]));
            Thunderhack.gps_position = pos;
            if (Thunderhack.moduleManager.getModuleByClass(MainSettings.class).language.getValue() == MainSettings.Language.RU) {
                Command.sendMessage("GPS настроен на X: " + pos.getX() + " Z: " + pos.getZ());
            } else {
                Command.sendMessage("GPS is set to X: " + pos.getX() + " Z: " + pos.getZ());
            }
        }
    }
}
