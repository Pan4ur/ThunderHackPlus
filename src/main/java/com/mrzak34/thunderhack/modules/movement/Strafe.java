package com.mrzak34.thunderhack.modules.movement;

import com.mrzak34.thunderhack.events.*;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.util.math.MatrixStrafeMovement;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.server.SPacketPlayerPosLook;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;


import static com.mrzak34.thunderhack.modules.movement.Jesus.isInLiquid;
import static com.mrzak34.thunderhack.util.MovementUtil.isMoving;


public class Strafe extends Module {
    public Strafe() {
        super("Strafe", "matrix only!!!", Category.MOVEMENT);
    }

    public static boolean serversprint = false;
    public static boolean needSprintState;
    int waterTicks;

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send e){
        if(e.getPacket() instanceof CPacketEntityAction){
            CPacketEntityAction ent = e.getPacket();
            if(ent.getAction() == CPacketEntityAction.Action.START_SPRINTING) {
                serversprint = true;
            }
            if(ent.getAction() == CPacketEntityAction.Action.STOP_SPRINTING) {
                serversprint = false;
            }
        }
    }


    @SubscribeEvent
    public void onSprint(EventSprint e){
        MatrixStrafeMovement.actionEvent(e);
        if (strafes()) {
            if (serversprint != needSprintState) {
                e.setSprintState(!serversprint);
            }
        }
    }
    @SubscribeEvent
    public void onMove(MatrixMove move){
        if (isInLiquid()) {
            waterTicks = 10;
        } else {
            waterTicks--;
        }
        if (strafes()) {
            double forward = mc.player.movementInput.moveForward;
            double strafe = mc.player.movementInput.moveStrafe;
            float yaw = mc.player.rotationYaw;
            if (forward == 0.0 && strafe == 0.0) {
                MatrixStrafeMovement.oldSpeed = 0;
                move.setMotionX(0);
                move.setMotionZ(0);
            } else {
                if (forward != 0.0) {
                    if (strafe > 0.0) {
                        yaw += ((forward > 0.0) ? -45 : 45);
                    } else if (strafe < 0.0) {
                        yaw += ((forward > 0.0) ? 45 : -45);
                    }
                    strafe = 0.0;
                    if (forward > 0.0) {
                        forward = 1.0;
                    } else if (forward < 0.0) {
                        forward = -1.0;
                    }
                }
                double speed = MatrixStrafeMovement.calculateSpeed(move);
                move.setMotionX(forward * speed * Math.cos(Math.toRadians(yaw + 90.0f)) + strafe * speed * Math.sin(Math.toRadians(yaw + 90.0f)));
                move.setMotionZ(forward * speed * Math.sin(Math.toRadians(yaw + 90.0f)) - strafe * speed * Math.cos(Math.toRadians(yaw + 90.0f)));
            }
        } else {
            MatrixStrafeMovement.oldSpeed = 0;
        }
        move.setCanceled(true);
    }

    @SubscribeEvent
    public void onPostMove(EventPostMove move){
        MatrixStrafeMovement.postMove(move.getHorizontalMove());
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive e){
        if(e.getPacket() instanceof  SPacketPlayerPosLook){
            MatrixStrafeMovement.oldSpeed = 0;
        }
    }


    public boolean strafes() {
        if (mc.player.isSneaking()) {
            return false;
        }
        if (mc.player.isInLava()) {
            return false;
        }
        if (mc.player.isInWater() || waterTicks > 0) {
            return false;
        }
        if (mc.player.isInWeb) {
            return false;
        }
        return !mc.player.capabilities.isFlying;
    }

}
