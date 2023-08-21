package com.mrzak34.thunderhack.modules.movement;

import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import net.minecraft.network.play.client.CPacketPlayer;

public class NoFall extends Module {
    public Setting<Mode> mode = register(new Setting("Mode", Mode.RUBBERBAND));


    public NoFall() {
        super("NoFall", "рубербендит если ты-упал", Category.MOVEMENT);
    }

    @Override
    public void onUpdate() {
        if (fullNullCheck()) {
            return;
        }

        if (mode.getValue() == Mode.RUBBERBAND) {
            if (mc.player.fallDistance > 3 && !mc.player.isSneaking()) {
                mc.player.motionY -= 0.1;
                mc.player.onGround = true;
                mc.player.capabilities.disableDamage = true;
            }
        } else if (mode.getValue() == Mode.DEFAULT) {
            if ((double) mc.player.fallDistance > 2.5) {
                mc.player.connection.sendPacket(new CPacketPlayer(true));
            }
        }
    }

    public enum Mode {
        RUBBERBAND, DEFAULT
    }


}
