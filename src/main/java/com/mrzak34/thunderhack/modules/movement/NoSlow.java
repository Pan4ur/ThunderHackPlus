package com.mrzak34.thunderhack.modules.movement;

import com.mrzak34.thunderhack.events.EventSync;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import net.minecraft.item.ItemFood;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class NoSlow extends Module {

    public Setting<Integer> speed = this.register(new Setting<>("Speed", 100, 1, 100));
    private final Setting<mode> Mode = register(new Setting("Mode", mode.NCP));

    public NoSlow() {
        super("NoSlow", "NoSlow", Category.MOVEMENT);
    }

    @SubscribeEvent
    public void onInput(InputUpdateEvent e) {
        if (!(Mode.getValue() == mode.StrictNCP && Mode.getValue() == mode.NCP) && mc.player.isHandActive() && !mc.player.isRiding()) {
            mc.player.movementInput.moveForward *= (5f * (speed.getValue() / 100f));
            mc.player.movementInput.moveStrafe *= (5f * (speed.getValue() / 100f));
        }

        if (Mode.getValue() == mode.StrictNCP || Mode.getValue() == mode.NCP) {
            if (mc.player.isHandActive() && !mc.player.isRiding() && !mc.player.isSneaking()) {
                if (Mode.getValue() == mode.StrictNCP && (mc.player.getHeldItemMainhand().getItem() instanceof ItemFood || mc.player.getHeldItemOffhand().getItem() instanceof ItemFood))
                    mc.player.connection.sendPacket(new CPacketHeldItemChange(mc.player.inventory.currentItem));
                mc.player.movementInput.moveForward /= 0.2;
                mc.player.movementInput.moveStrafe /= 0.2;
            }
        }
    }

    @SubscribeEvent
    public void onPreMotion(EventSync event) {
        if (mc.player.isHandActive()) {
            if (mc.player.onGround) {
                if (mc.player.ticksExisted % 2 == 0) {
                    if (Mode.getValue() == mode.Matrix) {
                        mc.player.motionX *= mc.player.moveStrafing == 0 ? 0.55 : 0.5;
                        mc.player.motionZ *= mc.player.moveStrafing == 0 ? 0.55 : 0.5;
                    } else if (Mode.getValue() == mode.SunRise) {
                        mc.player.motionX *= 0.47;
                        mc.player.motionZ *= 0.47;
                    } else if (Mode.getValue() == mode.Matrix2) {
                        mc.player.motionX *= 0.5;
                        mc.player.motionZ *= 0.5;
                    }
                }
            } else if (Mode.getValue() == mode.Matrix2) {
                mc.player.motionX *= 0.95;
                mc.player.motionZ *= 0.95;
            } else if (mc.player.fallDistance > (Mode.getValue() == mode.Matrix ? 0.7 : 0.2)) {
                if (Mode.getValue() == mode.Matrix) {
                    mc.player.motionX *= 0.93;
                    mc.player.motionZ *= 0.93;
                } else if (Mode.getValue() == mode.SunRise) {
                    mc.player.motionX *= 0.91;
                    mc.player.motionZ *= 0.91;
                }
            }


        }


    }

    public enum mode {
        NCP, StrictNCP, Matrix, Matrix2, SunRise
    }
}
