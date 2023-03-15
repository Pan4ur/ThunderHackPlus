package com.mrzak34.thunderhack.command.commands;

import com.mrzak34.thunderhack.command.Command;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.BlockPos;

public class TpCommand extends Command {

    public TpCommand() {
        super("tp");
    }

    @Override
    public void execute(String[] commands) {
        if (commands.length == 1) {
            Command.sendMessage("Попробуй .tp <число> <число> <число>");
            return;
        }
        if (commands.length > 2) {
            BlockPos pos = new BlockPos(Integer.parseInt(commands[0]), Integer.parseInt(commands[1]), Integer.parseInt(commands[2]));

            for (int i = 0; i < 10; ++i) {
                this.mc.player.connection.sendPacket(new CPacketPlayer.Position(pos.x, 1 + pos.y, pos.z, false));
            }
            mc.player.setPosition(pos.getX(), pos.getY() + 1, pos.getZ());

            Command.sendMessage("Телепортируемся на координаты X: " + pos.x + " Y: " + pos.y + " Z: " + pos.z);
        }
    }
}
