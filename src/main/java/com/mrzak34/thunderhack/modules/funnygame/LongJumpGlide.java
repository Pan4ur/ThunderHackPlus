package com.mrzak34.thunderhack.modules.funnygame;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.event.events.EventMove;
import com.mrzak34.thunderhack.event.events.EventPreMotion;
import com.mrzak34.thunderhack.event.events.PacketEvent;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import net.minecraft.init.MobEffects;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.MovementInput;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;

public class LongJumpGlide extends Module {

    public LongJumpGlide() {
        super("LongJumpGlide", "Лонг джамп-с бустом в конце-(нужно каппучино) ", Category.FUNNYGAME, true, false, false);
    }
    public Setting<Boolean> timer = this.register ( new Setting <> ( "timer", false));
    private  Setting<Float> speed = this.register(new Setting<>("Speed", 16.7f, 5f, 30f));
    private  Setting<Float> speed2 = this.register(new Setting<>("timerSpeed", 1f, 0.5f, 3f));


    public Setting<Boolean> usver = this.register ( new Setting <> ( "use", false));


    public double speedXZ;
    public double distance;
    public int stage = 0;
    public int ticks = 2;

    @SubscribeEvent
    public void onMove(EventMove f4p2) {
        block22: {
            block23: {
                if (f4p2.getStage() != 0) return;
                if (mc.player.collidedHorizontally || !isMovingClient()) {
                    stage = 0;
                    ticks = 2;
                    f4p2.set_z(0.0);
                    f4p2.set_x(0.0);
                    f4p2.setCanceled(true);
                    return;
                }
                if (timer.getValue()) {
                    Thunderhack.TICK_TIMER = speed2.getValue();
                }
                if (ticks > 0 && isMovingClient()) {
                    speedXZ = 0.09;
                    --ticks;
                } else if (stage == 1 && mc.player.collidedVertically && isMovingClient()) {
                    speedXZ = 1.0 + getBaseMoveSpeed() - 0.05;
                } else if (stage == 2 && mc.player.collidedVertically && isMovingClient()) {
                    mc.player.motionY = 0.415 + isJumpBoost() ;
                    f4p2.set_y(0.415 + isJumpBoost());
                    speedXZ *=(speed.getValue()/10f);
                    jumped = true;
                } else if (stage == 3) {
                    double d = 0.66 * (distance - getBaseMoveSpeed());
                    speedXZ = distance - d;
                } else {
                    speedXZ = distance - distance / 159.0;
                }


                f4p2.setCanceled(true);
                Method744(f4p2, speedXZ);


                List list = mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(0.0, mc.player.motionY, 0.0));
                List list2 = mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(0.0, -0.4, 0.0));
                if (mc.player.collidedVertically) break block22;
                if (list.size() > 0) break block23;
                if (list2.size() <= 0) break block22;
            }
            if (stage > 10) {
                if (stage >= 98) {
                    mc.player.motionY = -0.4;
                    f4p2.set_y(-0.4);
                    stage = 0;
                    ticks = 5;
                } else {
                    mc.player.motionY = -0.001;
                    f4p2.set_y(-0.001);
                }
            }
        }
        if (ticks <= 0 && isMovingClient()) {
            ++stage;
        }
    }

    private boolean isMovingClient(){
        return (mc.player.moveForward != 0.0f || mc.player.moveStrafing != 0.0f);
    }

    public double isJumpBoost(){
        if (mc.player.isPotionActive(MobEffects.JUMP_BOOST)) {
            return 0.2;
        } else {
            return 0;
        }
    }

    public double getBaseMoveSpeed() {
        if(mc.player == null || mc.world == null){
            return 0.2873;
        }

        int n;
        double d = 0.2873;
        if (mc.player.isPotionActive(MobEffects.SPEED)) {
            n = mc.player.getActivePotionEffect(MobEffects.SPEED).getAmplifier();
            d *= 1.0 + 0.2 * (double)(n + 1);
        }
        if (mc.player.isPotionActive(MobEffects.JUMP_BOOST) && usver.getValue()) {
            n = mc.player.getActivePotionEffect(MobEffects.JUMP_BOOST).getAmplifier();
            d /= 1.0 + 0.2 * (double)(n + 1);
        }
        return d;
    }

    public void Method744( EventMove event,double d) {
        MovementInput movementInput = mc.player.movementInput;
        double d2 = movementInput.moveForward;
        double d3 = movementInput.moveStrafe;

        float f = mc.player.rotationYaw;
        if (d2 == 0.0 && d3 == 0.0) {
            event.set_x(0);
            event.set_z(0);
        } else {
            if (d2 != 0.0) {
                if (d3 > 0.0) {
                    f += (float)(d2 > 0.0 ? -45 : 45);
                } else if (d3 < 0.0) {
                    f += (float)(d2 > 0.0 ? 45 : -45);
                }
                d3 = 0.0;
                if (d2 > 0.0) {
                    d2 = 1.0;
                } else if (d2 < 0.0) {
                    d2 = -1.0;
                }
            }
            event.set_x(d2 * d * Math.cos(Math.toRadians(f + 90.0f)) + d3 * d * Math.sin(Math.toRadians(f + 90.0f)));
            event.set_z(d2 * d * Math.sin(Math.toRadians(f + 90.0f)) - d3 * d * Math.cos(Math.toRadians(f + 90.0f)));
        }
    }



    boolean jumped = false;
    @Override
    public void onUpdate(){
        if(mc.player == null || mc.world == null){
            return;
        }
        if(mc.player.onGround && jumped){
            Thunderhack.TICK_TIMER = 1f;
            toggle();
        }
    }


    @Override
    public void onDisable(){
        speedXZ = 0;
        distance = 0;
        stage = 0;
        ticks = 2;
        jumped = false;
    }

    private float startY = 0;

    @Override
    public void onEnable(){
        startY = (float) mc.player.posY;
    }

    @SubscribeEvent
    public void onPacketRecive(PacketEvent.Receive e){
        if(mc.player == null || mc.world == null){
            return;
        }
        if(e.getPacket() instanceof SPacketPlayerPosLook){
            Thunderhack.TICK_TIMER = 1f;
            toggle();
        }
    }

    @SubscribeEvent
    public void onUpdateWalkingPlayerPost(EventPreMotion f4u2) {

        if(startY > mc.player.posY){
            Thunderhack.TICK_TIMER = 1f;
            toggle();
        }
        double d = mc.player.posX - mc.player.prevPosX;
        double d2 = mc.player.posZ - mc.player.prevPosZ;
        distance = Math.sqrt(d * d + d2 * d2);
    }

}
