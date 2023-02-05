package com.mrzak34.thunderhack.modules.funnygame;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.events.EventMove;
import com.mrzak34.thunderhack.events.EventPreMotion;
import com.mrzak34.thunderhack.events.PacketEvent;
import com.mrzak34.thunderhack.mixin.mixins.ICPacketPlayer;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;

import com.mrzak34.thunderhack.util.Util;
import net.minecraft.entity.Entity;
import net.minecraft.init.MobEffects;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static com.mrzak34.thunderhack.util.MovementUtil.isMoving;

public class GroundBoost extends Module {

    public GroundBoost() {
        super("GroundBoost", "Лютейшие спиды-(каппучино+плоскость)", Category.FUNNYGAME);
    }


    boolean hasact = false;


    @Override
    public void onDisable(){
        if(hasact){
            mc.gameSettings.viewBobbing = true;
        }
    }

    public Setting<Integer> ticks = this.register(new Setting<>("RbandDelay", 2, 2, 40));
    public Setting <Boolean> autoSprint = this.register ( new Setting <> ( "AutoSprint", true));

    public Setting <Integer> spddd = this.register (new Setting <>( "Speed", 2149, 50, 2149 ));



    private int rhh = 0;
    private int stage = 0;
    private double moveSpeed = 0;
    private double distance = 0;
    private float startY = 0;



    @Override
    public void onEnable( ) {
        try {
            if(mc.gameSettings.viewBobbing){
                hasact = true;
                mc.gameSettings.viewBobbing = false;
            }
            stage = 2;
            distance = 0;
            moveSpeed = getBaseMoveSpeed( );

            Thunderhack.TICK_TIMER = 1.0f;
            if ( autoSprint.getValue() && mc.player != null )
                mc.player.setSprinting( false );
            startY = (float) mc.player.posY;
        } catch ( Exception ignored) {

        }
    }




    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive e) {
        if ( e.getPacket( ) instanceof SPacketPlayerPosLook) {
            rhh = ticks.getValue();
            stage = 2;
            distance = 0;
            moveSpeed = getBaseMoveSpeed( );
        }
    }




    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send e) {
        if ( e.getPacket( ) instanceof CPacketPlayer) {
            if ( rhh > 0 ){
                return;
            }
            CPacketPlayer packet = ( CPacketPlayer ) e.getPacket( );
            if ( stage == 3 )
                ( (ICPacketPlayer) packet ).setY( packet.getY( 0 ) + (isBoxColliding() ? 0.2: 0.4) + getJumpSpeed() );
        }
    }

    public static boolean isBoxColliding() {
        return Util.mc.world.getCollisionBoxes((Entity)Util.mc.player, Util.mc.player.getEntityBoundingBox().offset(0.0, 0.21, 0.0)).size() > 0;
    }

    @SubscribeEvent
    public void onUpdateWalkingPlayer(EventPreMotion e) {
        if(startY > mc.player.posY){
            startY = 0;
            toggle();
        }
            double d3 = mc.player.posX - mc.player.prevPosX;
            double d4 = mc.player.posZ - mc.player.prevPosZ;

            distance = Math.sqrt( d3 * d3 + d4 * d4 );
    }

    @SubscribeEvent
    public void onMoveEvent(EventMove event) {
        if ( nullCheck( ) ) return;
        if ( mc.player.isElytraFlying( ) || mc.player.fallDistance >= 4.0f ) return;
        if ( mc.player.isInWater( ) || mc.player.isInLava( )  ) return;

        if ( rhh > 0 ){
            rhh--;
            return;
        }

        if ( autoSprint.getValue() )
            mc.player.setSprinting( true );


        if (!mc.player.collidedHorizontally || checkMove())
        {
            if (mc.player.onGround){
                if (stage == 2) {
                    if (rhh > 0)
                        moveSpeed = getBaseMoveSpeed();
                    moveSpeed *= ((float)spddd.getValue() / 1000f);
                    stage = 3;
                } else if (stage == 3) {
                        double var = 0.66 * (distance - getBaseMoveSpeed());
                        moveSpeed = distance - var;
                        stage = 2;
                }
            }
            moveSpeed = Math.max(moveSpeed, getBaseMoveSpeed());
            setVanilaSpeed(event, moveSpeed);
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
    public Setting<Boolean> usver = this.register ( new Setting <> ( "use", false));


    public float[] setYaw ( float yaw, double niggers ) {
        float moveForward = mc.player.movementInput.moveForward;
        float moveStrafe = mc.player.movementInput.moveStrafe;
        float rotationYaw = yaw;

        if ( moveForward == 0.0f && moveStrafe == 0.0f ) {
            float[] ret = new float[ 2 ];
            ret[ 0 ] = 0.0f;
            ret[ 1 ] = 0.0f;
            return ret;
        } else if ( moveForward != 0.0f ) {
            if ( moveStrafe >= 1.0f ) {
                rotationYaw += moveForward > 0.0f ? -45.0f : 45.0f;
                moveStrafe = 0.0f;
            } else if ( moveStrafe <= -1.0f ) {
                rotationYaw += moveForward > 0.0f ? 45.0f : -45.0f;
                moveStrafe = 0.0f;
            }

            if ( moveForward > 0.0f )
                moveForward = 1.0f;
            else if ( moveForward < 0.0f )
                moveForward = -1.0f;
        }

        double motionX = Math.cos( Math.toRadians( rotationYaw + 90.0f ) );
        double motionZ = Math.sin( Math.toRadians( rotationYaw + 90.0f ) );

        double newX = moveForward * niggers * motionX + moveStrafe * niggers * motionZ;
        double newZ = moveForward * niggers * motionZ - moveStrafe * niggers * motionX;

        float[] ret = new float[ 2 ];
        ret[ 0 ] = ( float ) newX;
        ret[ 1 ] = ( float ) newZ;
        return ret;
    }


    public float[] getYaw ( double niggers ) {
        float yaw = mc.player.prevRotationYaw + ( mc.player.rotationYaw - mc.player.prevRotationYaw ) * mc.getRenderPartialTicks( );
        return setYaw( yaw, niggers );
    }

    public double round ( double value, int places ) {
        BigDecimal b = new BigDecimal( value ).setScale( places, RoundingMode.HALF_UP );
        return b.doubleValue( );
    }


    public boolean checkMove ( ) {
        return mc.player.moveForward != 0.0F || mc.player.moveStrafing != 0.0F;
    }

    public void setVanilaSpeed(EventMove event, double speed) {
        float moveForward = mc.player.movementInput.moveForward;
        float moveStrafe = mc.player.movementInput.moveStrafe;
        float rotationYaw = mc.player.rotationYaw;

        if (moveForward == 0.0f && moveStrafe == 0.0f) {
            event.set_x(0);
            event.set_z(0);

            return;
        } else if (moveForward != 0.0f) {
            if (moveStrafe >= 1.0f) {
                rotationYaw += moveForward > 0.0f ? -45.0f : 45.0f;
                moveStrafe = 0.0f;
            } else if (moveStrafe <= -1.0f) {
                rotationYaw += moveForward > 0.0f ? 45.0f : -45.0f;
                moveStrafe = 0.0f;
            }

            if (moveForward > 0.0f)
                moveForward = 1.0f;
            else if (moveForward < 0.0f)
                moveForward = -1.0f;
        }

        double motionX = Math.cos(Math.toRadians(rotationYaw + 90.0f));
        double motionZ = Math.sin(Math.toRadians(rotationYaw + 90.0f));

        double newX = moveForward * speed * motionX + moveStrafe * speed * motionZ;
        double newZ = moveForward * speed * motionZ - moveStrafe * speed * motionX;

        event.set_x(newX);
        event.set_z(newZ);
        event.setCanceled(true);
    }


    public static double getJumpSpeed()
    {
        double defaultSpeed = 0.0;
        if (mc.player.isPotionActive(MobEffects.JUMP_BOOST))
        {
            int amplifier = mc.player.getActivePotionEffect(MobEffects.JUMP_BOOST).getAmplifier();
            defaultSpeed += (amplifier + 1) * 0.1;
        }

        return defaultSpeed;
    }
}
