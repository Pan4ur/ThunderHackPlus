package com.mrzak34.thunderhack.modules.movement;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.event.events.PacketEvent;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.notification.NotificationManager;
import com.mrzak34.thunderhack.notification.NotificationType;
import com.mrzak34.thunderhack.util.Timer;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static com.mrzak34.thunderhack.util.MovementUtil.isMoving;

public class MatrixZoom extends Module {

    public MatrixZoom() {
        super("MatrixZoom", "MatrixZoom", Category.MOVEMENT, true, false, false);
    }


    private int boostMotion = 0;
    public Setting<Float> speed = register(new Setting("Speed", 2.0f, 0.0f, 3.0f)); //радиус круга компасса ежжи
    public Setting<Float> jumpTimer = register(new Setting("JumpTimer", 0.1f, 0.1f, 2f)); //радиус круга компасса ежжи
    public Setting<Float> spd = register(new Setting("Speed2", 1.5f, 0.1f, 2f)); //радиус круга компасса ежжи


    public  Setting<Boolean> dmgkick = this.register(new Setting<>("DmgKickProtection", true));

    public  Setting<Boolean> noGround = this.register(new Setting<>("Ground", true));
    public  Setting<Boolean> dad = this.register(new Setting<>("ada", true));

    @Override
    public void onEnable() {
        boostMotion = 0;
    }

    static void strafe(float speed) {
        if (!isMoving()) return;
        double yaw = direction();
        mc.player.motionX = -Math.sin(yaw) * speed;
        mc.player.motionZ = Math.cos(yaw) * speed;
    }


    static double direction(){
        double rotationYaw = mc.player.rotationYaw;
        if (mc.player.moveForward < 0f) rotationYaw += 180f;
        double forward = 1f;
        if (mc.player.moveForward < 0f){
            forward = -0.5f;
        } else if (mc.player.moveForward > 0f) forward = 0.5f;

        if (mc.player.moveStrafing > 0f) rotationYaw -= 90f * forward;
        if (mc.player.moveStrafing < 0f) rotationYaw += 90f * forward;
        return Math.toRadians(rotationYaw);
    }

    @Override
    public void onUpdate() {
        if(mc.player.hurtTime > 0 && dmgkick.getValue()){
            NotificationManager.publicity("LongJump","Kick Protection", 2, NotificationType.ERROR);
            toggle();
        }

        if(mc.player.onGround){
            flag = true;
            return;
        }

        if (boostMotion == 0) {
            double yaw = Math.toRadians(mc.player.rotationYaw);

            if(!noGround.getValue())
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, true));

            if(dad.getValue()) {
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX + -Math.sin(yaw) * spd.getValue(), mc.player.posY + 0.42f, mc.player.posZ + Math.cos(yaw) * spd.getValue(), false));
            } else {
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX + -Math.sin(yaw) * spd.getValue(), mc.player.posY, mc.player.posZ + Math.cos(yaw) * spd.getValue(), false));
            }
            boostMotion = 1;
            Thunderhack.TICK_TIMER = jumpTimer.getValue();
            flag = false;

        } else if (boostMotion == 2) {
            strafe(speed.getValue());

            mc.player.motionY = 0.42f;
            boostMotion = 3;
        } else if (boostMotion < 5) {
            boostMotion++;
        } else {
            Thunderhack.TICK_TIMER = 1f;
            if(flag)
                boostMotion = 0;

        }
    }

    boolean flag = false;

    @Override
    public void onDisable() {
        Thunderhack.TICK_TIMER = 1f;
    }

    Timer timer = new Timer();

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive e){
        if(mc.currentScreen == null && e.getPacket() instanceof SPacketPlayerPosLook){
            final SPacketPlayerPosLook packet = (SPacketPlayerPosLook) e.getPacket();
            packet.yaw = mc.player.rotationYaw;
            packet.pitch = mc.player.rotationPitch;
        }
    }
}


