package com.mrzak34.thunderhack.modules.movement;


import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.events.EventMove;
import com.mrzak34.thunderhack.events.EventPostMotion;
import com.mrzak34.thunderhack.events.PacketEvent;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.notification.NotificationManager;
import com.mrzak34.thunderhack.notification.NotificationType;
import com.mrzak34.thunderhack.setting.Setting;
import net.minecraft.init.MobEffects;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.MovementInput;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;

import static com.mrzak34.thunderhack.util.MovementUtil.isMoving;

public class LongJump extends Module{
    public LongJump() {
        super("LongJump", "Догонять попусков-на ez", Category.MOVEMENT);
    }

    private Setting<ModeEn> Mode = register(new Setting("Mode", ModeEn.FunnyGame));

    public enum ModeEn {
        FunnyGame,
        Default,
        NexusGrief,
        MatrixCustom
    }

    private Setting<Float> timr = this.register(new Setting("TimerSpeed", 1.0F, 0.5F, 3.0F,v-> Mode.getValue() == ModeEn.Default || Mode.getValue() == ModeEn.FunnyGame));
    private Setting<Float> speed = this.register(new Setting("Speed", 16.7F, 5.0F, 30.0F,v-> Mode.getValue() == ModeEn.Default || Mode.getValue() == ModeEn.FunnyGame));
    public Setting<Boolean> usetimer = this.register(new Setting("Timer", true,v-> Mode.getValue() == ModeEn.Default || Mode.getValue() == ModeEn.FunnyGame));
    public Setting<Boolean> usver = this.register ( new Setting <> ( "JumpBoost", false,v-> Mode.getValue() == ModeEn.Default || Mode.getValue() == ModeEn.FunnyGame));
    public Setting<Boolean> ongr = this.register ( new Setting <> ( "groundSpoof", false,v-> Mode.getValue() == ModeEn.Default || Mode.getValue() == ModeEn.FunnyGame));
    public Setting<Boolean> ongr2 = this.register ( new Setting <> ( "groundSpoofVal", false,v-> Mode.getValue() == ModeEn.Default || Mode.getValue() == ModeEn.FunnyGame));

    public Setting<Float> speed2 = register(new Setting("Speed", 1.44f, 0.0f, 3.0f,v-> Mode.getValue() == ModeEn.MatrixCustom));
    public Setting<Float> jumpTimer = register(new Setting("JumpTimer", 0.60f, 0.1f, 2f,v-> Mode.getValue() == ModeEn.MatrixCustom));
    public Setting<Float> spd = register(new Setting("Speed2", 1.49f, 0.1f, 2f,v-> Mode.getValue() == ModeEn.MatrixCustom));
    public  Setting<Boolean> dmgkick = this.register(new Setting<>("DmgKickProtection", true,v-> Mode.getValue() == ModeEn.MatrixCustom));
    public  Setting<Boolean> noGround = this.register(new Setting<>("Ground", true,v-> Mode.getValue() == ModeEn.MatrixCustom));
    public  Setting<Boolean> YSpoof = this.register(new Setting<>("YSpoof", true,v-> Mode.getValue() == ModeEn.MatrixCustom));




    public double Field1990;
    public double Field1991;
    public int Field1992 = 0;
    public int Field1993 = 0;
    public boolean jumped = false;

    private int boostMotion = 0;
    boolean flag = false;
    private float startY = 0;
    public double speedXZ;
    public double distance;
    public int stage = 0;
    public int ticks = 2;




    @SubscribeEvent
    public void onMove(EventMove f4p2) {
        if(Mode.getValue() == ModeEn.Default){
            DefaultOnMove(f4p2);
        } else if (Mode.getValue() == ModeEn.FunnyGame){
            FunnyGameOnMove(f4p2);
        }
    }

    @SubscribeEvent
    public void onPacketRecive(PacketEvent.Receive e) {
        if (mc.world != null && mc.player != null && (Mode.getValue() == ModeEn.Default || Mode.getValue() == ModeEn.FunnyGame)) {
            if (e.getPacket() instanceof SPacketPlayerPosLook) {
                this.toggle();
            }
        } else if(Mode.getValue() == ModeEn.NexusGrief || Mode.getValue() == ModeEn.MatrixCustom){
            if(mc.currentScreen == null && e.getPacket() instanceof SPacketPlayerPosLook){
                final SPacketPlayerPosLook packet = e.getPacket();
                packet.yaw = mc.player.rotationYaw;
                packet.pitch = mc.player.rotationPitch;
            }
        }
    }


    @Override
    public void onUpdate() {
        if (mc.world != null && mc.player != null && Mode.getValue() == ModeEn.Default) {
            if (mc.player.onGround && this.jumped) {
                this.toggle();
            }
        } else if (Mode.getValue() == ModeEn.MatrixCustom){
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
                if(YSpoof.getValue()) {
                    mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX + -Math.sin(yaw) * spd.getValue(), mc.player.posY + 0.42f, mc.player.posZ + Math.cos(yaw) * spd.getValue(), false));
                } else {
                    mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX + -Math.sin(yaw) * spd.getValue(), mc.player.posY, mc.player.posZ + Math.cos(yaw) * spd.getValue(), false));
                }
                boostMotion = 1;
                Thunderhack.TICK_TIMER = jumpTimer.getValue();
                flag = false;
            } else if (boostMotion == 2) {
                strafe(speed2.getValue());
                mc.player.motionY = 0.42f;
                boostMotion = 3;
            } else if (boostMotion < 5) {
                boostMotion++;
            } else {
                Thunderhack.TICK_TIMER = 1f;
                if(flag)
                    boostMotion = 0;
            }
        } else if (Mode.getValue() == ModeEn.NexusGrief){
            if(mc.player.hurtTime > 0){
                NotificationManager.publicity("LongJump","Kick Protection", 2, NotificationType.ERROR);
                toggle();
            }
            if(mc.player.onGround ){
                flag = true;
                return;
            }

            if (boostMotion == 0) {
                double yaw = Math.toRadians(mc.player.rotationYaw);
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX + -Math.sin(yaw) * 1.5f, mc.player.posY + 0.42f, mc.player.posZ + Math.cos(yaw) * 1.5f, false));
                boostMotion = 1;
                Thunderhack.TICK_TIMER = 0.6f;
                flag = false;
            } else if (boostMotion == 2) {
                strafe(1.44f);
                mc.player.motionY = 0.42f;
                boostMotion = 3;
            } else if (boostMotion < 5) {
                boostMotion++;
            } else {
                Thunderhack.TICK_TIMER = 1f;
                if(flag)
                    boostMotion = 0;
            }

        } else if(Mode.getValue() == ModeEn.FunnyGame){
            if(mc.player == null || mc.world == null){
                return;
            }
            if(mc.player.onGround && jumped){
                Thunderhack.TICK_TIMER = 1f;
                toggle();
            }
        }
    }

    @Override
    public void onEnable(){
        boostMotion = 0;
        startY = (float) mc.player.posY;

    }

    @Override
    public void onDisable() {
        this.Field1990 = 0.0;
        this.Field1991 = 0.0;
        this.Field1992 = 0;
        this.Field1993 = 0;
        Thunderhack.TICK_TIMER = 1f;
        speedXZ = 0;
        distance = 0;
        stage = 0;
        ticks = 2;
        jumped = false;
    }

    @SubscribeEvent
    public void onUpdateWalkingPlayerPost(EventPostMotion f4u2) {
        if(Mode.getValue() == ModeEn.Default){
            DefaultOnPreMotion(f4u2);
        } else if(Mode.getValue() == ModeEn.FunnyGame){
            FGPostMotion(f4u2);
        }
    }







    public void DefaultOnPreMotion(EventPostMotion f4u2){
        double d = mc.player.posX - mc.player.prevPosX;
        double d2 = mc.player.posZ - mc.player.prevPosZ;
        this.Field1991 = Math.sqrt(d * d + d2 * d2);
        if(ongr2.getValue())
            mc.player.onGround = ongr.getValue();
    }

    public void DefaultOnMove(EventMove f4p2){
            if (!mc.player.collidedHorizontally  && this.Field1993 <= 0 && (mc.player.moveForward != 0.0f || mc.player.moveStrafing != 0.0f)) {
                if (this.usetimer.getValue()) {
                    Thunderhack.TICK_TIMER = this.timr.getValue();
                } else {
                    Thunderhack.TICK_TIMER = 1.0F;
                }

                if (this.Field1992 == 1 && mc.player.collidedVertically) {
                    this.Field1990 = 1.0 + getBaseMoveSpeed() - 0.05;
                } else if (this.Field1992 == 2 && mc.player.collidedVertically) {
                    mc.player.motionY = 0.415;
                    f4p2.set_y(0.415);
                    this.jumped = true;
                    this.Field1990 *= this.speed.getValue() / 10.0F;
                } else if (this.Field1992 == 3) {
                    double d = 0.66 * (this.Field1991 - getBaseMoveSpeed());
                    this.Field1990 = this.Field1991 - d;
                } else {
                    this.Field1990 = this.Field1991 - this.Field1991 / 159.0;
                    if (mc.player.collidedVertically && this.Field1992 > 3) {
                        this.Field1993 = 10;
                        this.Field1992 = 1;
                    }
                }

                this.Field1990 = Math.max(this.Field1990, getBaseMoveSpeed());
                this.Method744(f4p2, this.Field1990);
                f4p2.setCanceled(true);
                ++this.Field1992;
            } else {
                if (this.Field1993 > 0) {
                    --this.Field1993;
                }

                this.Field1992 = 0;
                this.Field1990 = 0.0;
                this.Field1991 = 0.0;
                f4p2.set_z(0.0);
                f4p2.set_x(0.0);
                f4p2.setCanceled(true);
            }

    }



    public void Method744(EventMove event, double d) {
        MovementInput movementInput = mc.player.movementInput;
        double d2 = movementInput.moveForward;
        double d3 = movementInput.moveStrafe;
        float f = mc.player.rotationYaw;
        if (d2 == 0.0 && d3 == 0.0) {
            event.set_x(0.0);
            event.set_z(0.0);
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
            event.set_x(d2 * d * Math.cos(Math.toRadians(f + 90.0F)) + d3 * d * Math.sin(Math.toRadians(f + 90.0F)));
            event.set_z(d2 * d * Math.sin(Math.toRadians(f + 90.0F)) - d3 * d * Math.cos(Math.toRadians(f + 90.0F)));
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


    public static void strafe(float speed) {
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

    public void FunnyGameOnMove(EventMove f4p2) {
        block22: {
            block23: {
                if (mc.player.collidedHorizontally || !isMovingClient()) {
                    stage = 0;
                    ticks = 2;
                    f4p2.set_z(0.0);
                    f4p2.set_x(0.0);
                    f4p2.setCanceled(true);
                    return;
                }
                if (usetimer.getValue()) {
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


    public void FGPostMotion(EventPostMotion f4u2) {
        if(startY > mc.player.posY){
            Thunderhack.TICK_TIMER = 1f;
            toggle();
        }
        double d = mc.player.posX - mc.player.prevPosX;
        double d2 = mc.player.posZ - mc.player.prevPosZ;
        distance = Math.sqrt(d * d + d2 * d2);
    }

}
