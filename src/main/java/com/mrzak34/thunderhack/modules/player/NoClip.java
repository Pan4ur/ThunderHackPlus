package com.mrzak34.thunderhack.modules.player;

import com.mrzak34.thunderhack.modules.Module;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class NoClip extends Module {

    public NoClip( ) {
        super("NoClip", "NoClip", Category.PLAYER);
    }


    /*
    @Override
    public void onUpdate() {
        if (mc.player != null) {
            mc.player.noClip = true;
            mc.player.motionY = 1.0E-5D;

            if (mc.gameSettings.keyBindJump.isKeyDown()) {
                mc.player.motionY = 0.4D;
            }
            if (mc.gameSettings.keyBindSneak.isKeyDown()) {
                mc.player.motionY = -0.4D;
            }

        }
    }

     */



    public void onDisable() {
        mc.player.noClip = false;
    }

}
