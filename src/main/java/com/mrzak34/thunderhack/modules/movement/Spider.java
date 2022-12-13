package com.mrzak34.thunderhack.modules.movement;

import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import net.minecraft.client.entity.EntityPlayerSP;

public class Spider extends Module {

    public Spider() {
        super("Spider", "Spider", Category.MOVEMENT, true, false, false);
    }



    private Setting<mode> a = register(new Setting("Mode", mode.Matrix));
    public enum mode {
        Default, Matrix;
    }

    @Override
    public void onTick() {
        if (!mc.player.collidedHorizontally) {
            return;
        }
        if (this.a.getValue() == mode.Default) {
            mc.player.motionY = 0.2;
            mc.player.isAirBorne = false;
        } else if( this.a.getValue() == mode.Matrix) {
            if (mc.player.ticksExisted % 8 == 0) {
                mc.player.onGround = true;
                mc.player.isAirBorne = false;
            } else {
                mc.player.onGround = false;
            }
            mc.player.prevPosY -= 2.0E-232;
            if (mc.player.onGround) {
                mc.player.motionY = 0.42f;
            }
        }
    }

}
