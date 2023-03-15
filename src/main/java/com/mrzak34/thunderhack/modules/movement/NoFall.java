package com.mrzak34.thunderhack.modules.movement;

import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import net.minecraft.network.play.client.CPacketPlayer;

public class NoFall extends Module {
    public Setting<rotmod> mod = register(new Setting("Mode", rotmod.Rubberband));


    public NoFall() {
        super("NoFall", "рубербендит если ты-упал", Category.MOVEMENT);
    }

    @Override
    public void onUpdate() {
        if (fullNullCheck()) return;
        if (mc.player.fallDistance > 3 && !mc.player.isSneaking() && mod.getValue() == rotmod.Rubberband) {
            mc.player.motionY -= 0.1;
            mc.player.onGround = true;
            mc.player.capabilities.disableDamage = true;
        }
        if (mod.getValue() == rotmod.Default) {
            if ((double) mc.player.fallDistance > 2.5) {
                mc.player.connection.sendPacket(new CPacketPlayer(true));
            }
        }
    }


    public enum rotmod {
        Rubberband, Default
    }


}
