package com.mrzak34.thunderhack.modules.movement;

import com.mrzak34.thunderhack.event.events.*;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class MatrixNoSlow extends Module {

    public MatrixNoSlow() {
        super("MatrixNoSlow", "MatrixNoSlow", Category.MOVEMENT, true, false, false);
    }
    public Setting<Mode> a = this.register(new Setting<Mode>("Mode: ", Mode.Matrix));
    public enum Mode {
        Vanilla,
        Matrix,
        SunRise

    }

    public final Setting<Integer> percentage = this.register(new Setting<Integer>("Speed", 2, 1, 100));

    @SubscribeEvent
    public void onInputUpdate(InputUpdateEvent e){
        if(mc.player.isHandActive() && !mc.player.isRiding()) {
            mc.player.movementInput.moveForward /= (float) (percentage.getValue() / 100d);
            mc.player.movementInput.moveStrafe /= (float) (percentage.getValue() / 100d);
        }
    }



    @SubscribeEvent
    public void EventMove(EventMove e) {
            if (mc.player.isHandActive()) {
                if (mc.player.onGround) {
                    if (mc.player.ticksExisted % 2 == 0) {
                        if (a.getValue() == Mode.Matrix) {

                           // mc.player.motionX *= mc.player.moveStrafing == 0 ? 0.55 : 0.5;
                          //  mc.player.motionZ *= mc.player.moveStrafing == 0 ? 0.55 : 0.5;

                            e.set_x(e.x * (mc.player.moveStrafing == 0 ? 0.55 : 0.5));
                            e.set_z(e.z * (mc.player.moveStrafing == 0 ? 0.55 : 0.5));


                        } else if (a.getValue() == Mode.SunRise) {
                          //  mc.player.motionX *= 0.47;
                          //  mc.player.motionZ *= 0.47;

                            e.set_x(e.x * (0.47));
                            e.set_z(e.z * (0.47));
                        }
                    }
                } else if (mc.player.fallDistance > (a.getValue() == Mode.Matrix ? 0.7 : 0.2)) {
                    if (a.getValue() == Mode.Matrix) {
                      //  mc.player.motionX *= 0.93;
                     //   mc.player.motionZ *= 0.93;
                        e.set_x(e.x * (0.93));
                        e.set_z(e.z * (0.93));
                    } else if (a.getValue() == Mode.SunRise) {
                     //   mc.player.motionX *= 0.91;
                      //  mc.player.motionZ *= 0.91;
                        e.set_x(e.x * (0.91));
                        e.set_z(e.z * (0.91));
                    }
                }
            }
            e.setCanceled(true);
    }


}
