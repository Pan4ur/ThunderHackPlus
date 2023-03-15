package com.mrzak34.thunderhack.command.commands;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.command.Command;
import com.mrzak34.thunderhack.manager.EventManager;
import com.mrzak34.thunderhack.modules.client.MainSettings;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.BlockPos;

public class BackCommand extends Command {
    public BackCommand() {
        super("back");
    }


    @Override
    public void execute(String[] var1) {
        if (EventManager.backX == 0 && EventManager.backY == 0 && EventManager.backZ == 0) {
            return;
        }
        BlockPos pos = new BlockPos(EventManager.backX, EventManager.backY, EventManager.backZ);

        for (int i = 0; i < 10; ++i) {
            this.mc.player.connection.sendPacket(new CPacketPlayer.Position(pos.x, 1 + pos.y, pos.z, false));
        }
        mc.player.setPosition(pos.getX(), pos.getY() + 1, pos.getZ());

        if (Thunderhack.moduleManager.getModuleByClass(MainSettings.class).language.getValue() == MainSettings.Language.RU) {
            Command.sendMessage("Телепортируемся на координаты X: " + EventManager.backX + " Y: " + EventManager.backY + " Z: " + EventManager.backZ);
        } else {
            Command.sendMessage("Teleporting to X: " + EventManager.backX + " Y: " + EventManager.backY + " Z: " + EventManager.backZ);
        }
    }
}
