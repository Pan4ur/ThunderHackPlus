package com.mrzak34.thunderhack.modules.movement;

import com.mrzak34.thunderhack.events.EventMove;
import com.mrzak34.thunderhack.events.EventPreMotion;
import com.mrzak34.thunderhack.events.PacketEvent;
import com.mrzak34.thunderhack.modules.Module;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class DMGFly extends Module {

    public static long lastVelocityTime;
    public static double velocityXZ, velocityY;
    public DMGFly() {
        super("DMGFly", "DMGFly", Category.MOVEMENT);
    }

    public static double[] getSpeed(double speed) {
        float yaw = mc.player.rotationYaw;
        float forward = mc.player.movementInput.moveForward;
        float strafe = mc.player.movementInput.moveStrafe;
        if (forward != 0) {
            if (strafe > 0) {
                yaw += (forward > 0 ? -45 : 45);
            } else if (strafe < 0) {
                yaw += (forward > 0 ? 45 : -45);
            }
            strafe = 0;
            if (forward > 0) {
                forward = 1;
            } else if (forward < 0) {
                forward = -1;
            }
        }
        return new double[]{
                (forward * speed * Math.cos(Math.toRadians(yaw + 90))
                        + strafe * speed * Math.sin(Math.toRadians(yaw + 90))),
                (forward * speed * Math.sin(Math.toRadians(yaw + 90))
                        - strafe * speed * Math.cos(Math.toRadians(yaw + 90))),
                yaw};
    }

    public static double getProgress() {
        return System.currentTimeMillis() - lastVelocityTime > 1350 ? 0
                : 1 - ((System.currentTimeMillis() - lastVelocityTime) / 1350.);
    }

    @SubscribeEvent
    public void onPyroMove(EventMove e) {
        if (System.currentTimeMillis() - lastVelocityTime < 1350) {
            double speed = Math.hypot(e.get_x(), e.get_z()) + velocityXZ - 0.25;
            double[] brain = getSpeed(speed);
            e.set_x(brain[0]);
            e.set_z(brain[1]);
            if (velocityY > 0)
                e.set_y(velocityY);
            e.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onUpdateWalkingPlayer(EventPreMotion e) {
        if (System.currentTimeMillis() - lastVelocityTime < 1350) {
            mc.player.setSprinting(!mc.player.isSprinting());
        }
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketEntityVelocity) {
            SPacketEntityVelocity packet = event.getPacket();
            if (packet.getEntityID() == mc.player.getEntityId()
                    && System.currentTimeMillis() - lastVelocityTime > 1350) {
                double vX = Math.abs(packet.getMotionX() / 8000d),
                        vY = packet.getMotionY() / 8000d,
                        vZ = Math.abs(packet.getMotionZ() / 8000d);
                if (vX + vZ > 0.3) {
                    velocityXZ = vX + vZ;
                    lastVelocityTime = System.currentTimeMillis();
                    velocityY = vY;
                } else {
                    velocityXZ = 0;
                    velocityY = 0;
                }
            }
        }
    }
}
