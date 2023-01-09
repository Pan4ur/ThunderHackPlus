package com.mrzak34.thunderhack.modules.movement;

import com.mrzak34.thunderhack.command.Command;
import com.mrzak34.thunderhack.event.events.*;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.modules.combat.Aura;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.*;
import com.mrzak34.thunderhack.util.DeadCodeUtils.MathUtils;
import net.minecraft.block.BlockAir;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.MoverType;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;


import net.minecraftforge.client.event.InputUpdateEvent;

import java.util.List;

import static com.mrzak34.thunderhack.modules.movement.Jesus.isInLiquid;
import static com.mrzak34.thunderhack.util.MovementUtil.isMoving;


public class Strafe extends Module {
    public Strafe() {
        super("Strafe", "matrix only!!!", Category.MOVEMENT, true, false, false);
    }

    /*

    private Setting<Boolean> bypass = this.register(new Setting<>("Bypass", true));
    private Setting<Boolean> random = this.register(new Setting<>("Randomise", true));
    private Setting<Boolean> pauseOnAura = this.register(new Setting<>("PauseOnAura", true));
    public Setting<Float> reduction  = this.register(new Setting<>("reduction ", 2.5f, 2f, 3f));
    public Setting<Float> reduction2  = this.register(new Setting<>("reduction2 ", 1f, 1f, 2f));



    @SubscribeEvent
    public void onUpdateWP(EventPreMotion e) {
        if (Aura.target != null && pauseOnAura.getValue()) {
            return;
        }
        if (isMoving() && bypass.getValue()) {
            float angle = getAngle() + (random.getValue() ? MathUtils.randomFloat(-1.75f, 1.75f) : 0f);
            mc.player.rotationYaw = angle;
            mc.player.renderYawOffset = angle;
            mc.player.rotationYawHead = angle;
        }


    }


    public static float getAngle() {
        double forward = mc.player.movementInput.moveForward;
        double strafe = mc.player.movementInput.moveStrafe;
        float yaw = mc.player.rotationYaw;
        if (forward < 0f)
            yaw += 180f;

        float forwardS = 1f;
        if (forward < 0f)
            forwardS = -0.5f;
        else if (forward > 0f)
            forwardS = 0.5f;

        if (strafe > 0)
            yaw -= 90 * forwardS;

        if (strafe < 0)
            yaw += 90 * forwardS;

        return yaw;
    }
     */

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
            if (Aura.hitTick) {
                Aura.hitTick = false;
                return;
            }
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
