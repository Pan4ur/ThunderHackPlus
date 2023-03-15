package com.mrzak34.thunderhack.modules.movement;

import com.mrzak34.thunderhack.events.PacketEvent;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;


public class MSTSpeed extends Module {

    static int ticks = 0;
    static int maxticks2 = 1;
    public Setting<Integer> maxticks = this.register(new Setting<>("Ticks", 20, 1, 100));
    public Setting<Float> speed = this.register(new Setting<>("Speed", 0.7f, 0.1f, 2f));
    public Setting<Float> airspeed = this.register(new Setting<>("AirSpeed", 0.7f, 0.1f, 2f));
    public Setting<Boolean> onlyGround = register(new Setting<>("onlyGround", true));
    public MSTSpeed() {
        super("DMGSpeed", "Matrix moment", Category.MOVEMENT);
    }

    public static double getProgress() {
        return (double) ticks / (double) maxticks2;
    }

    @Override
    public void onUpdate() {
        if (ticks > 0) {
            ticks--;
        }
        if (mc.player.moveForward == 0 && mc.player.moveStrafing == 0) {
            return;
        }
        maxticks2 = maxticks.getValue();
        if (ticks > 0) {
            mc.player.setSprinting(true);
            if (mc.player.onGround) {
                mc.player.motionX = (double) (-MathHelper.sin(get_rotation_yaw())) * speed.getValue();
                mc.player.motionZ = (double) MathHelper.cos(get_rotation_yaw()) * speed.getValue();
            } else if (onlyGround.getValue() && !mc.player.onGround) {
                mc.player.motionX = (double) (-MathHelper.sin(get_rotation_yaw())) * airspeed.getValue();
                mc.player.motionZ = (double) MathHelper.cos(get_rotation_yaw()) * airspeed.getValue();
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketEntityVelocity) {
            if (((SPacketEntityVelocity) event.getPacket()).getEntityID() == mc.player.getEntityId()) {
                ticks = maxticks.getValue();
            }
        }
        if (event.getPacket() instanceof SPacketPlayerPosLook) {
            ticks = 0;
        }
    }

    private float get_rotation_yaw() {
        float rotation_yaw = mc.player.rotationYaw;
        if (mc.player.moveForward < 0.0f) {
            rotation_yaw += 180.0f;
        }
        float n = 1.0f;
        if (mc.player.moveForward < 0.0f) {
            n = -0.5f;
        } else if (mc.player.moveForward > 0.0f) {
            n = 0.5f;
        }
        if (mc.player.moveStrafing > 0.0f) {
            rotation_yaw -= 90.0f * n;
        }
        if (mc.player.moveStrafing < 0.0f) {
            rotation_yaw += 90.0f * n;
        }
        return rotation_yaw * 0.017453292f;
    }
}


