package com.mrzak34.thunderhack.modules.movement;

import com.mrzak34.thunderhack.event.events.EventPlayerTravel;
import com.mrzak34.thunderhack.event.events.EventPreMotion;
import com.mrzak34.thunderhack.event.events.PacketEvent;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.MathUtil;
import com.mrzak34.thunderhack.util.RotationUtil;
import com.mrzak34.thunderhack.util.SilentRotaionUtil;
import com.mrzak34.thunderhack.util.Timer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketInput;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketVehicleMove;
import net.minecraft.network.play.server.SPacketMoveVehicle;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BoatFly extends Module {
    public BoatFly() {
        super("BoatFly", "BoatFly", Category.MOVEMENT, true, false, false);
    }
    private  Setting<Float> GlideSpeed = this.register(new Setting<Float>("speed", 3f, 0.5f, 5f));
    private  Setting<Float> speed = this.register(new Setting<Float>("speed", 3f, 0.5f, 5f));
    private  Setting<Float> delay = this.register(new Setting<Float>("speed", 0.48f, 0.0f, 1f));
    public  Setting<Boolean> rotate = this.register(new Setting<>("rotate", true));




    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send e){
        if (e.getStage() != 0) {
            if (e.getPacket() instanceof CPacketVehicleMove && this.mc.player.isRiding() && this.mc.player.ticksExisted % 2 == 0) {
                this.mc.playerController.interactWithEntity((EntityPlayer)this.mc.player, this.mc.player.getRidingEntity(), EnumHand.MAIN_HAND);
            }
            return;
        }
        else {
            if ((e.getPacket() instanceof CPacketPlayer.Rotation || e.getPacket() instanceof CPacketInput) && this.mc.player.isRiding()) {
                e.setCanceled(true);
            }
            return;
        }
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive e){
        if (e.getStage() != 0) {
            return;
        }
        else {
            if (e.getPacket() instanceof SPacketMoveVehicle && this.mc.player.isRiding()) {
                e.setCanceled(true);
            }
            return;
        }
    }

    Entity riding;
    double[] dir;


    Timer timer = new Timer();
    Entity Boats;

    @SubscribeEvent
    public void onUpdateWalkingPlayerPre(EventPreMotion e){
            if (!this.mc.player.isRiding()) {
                if (timer.passedMs((int) (delay.getValue() * 1000f))) {
                    Boats = findaboat();
                    if (Boats != null) {
                        if(rotate.getValue()) {
                            float[] Rots = RotationUtil.getNeededRotations(Boats);
                            SilentRotaionUtil.lookAtAngles(Rots[0], Rots[1]);
                        }
                        this.mc.playerController.interactWithEntity((EntityPlayer) this.mc.player, Boats, EnumHand.MAIN_HAND);
                    }
                    this.timer.reset();
                }
            }

    }

    public Entity findaboat(){
        for(Entity ent : mc.world.loadedEntityList){
            if(isABoat(ent)){
                return ent;
            }
        }
        return null;
    }




    public boolean isABoat(Entity enot){
        if (enot.getDistance((Entity)this.mc.player) > 6f) {
            return false;
        }
        return enot instanceof EntityBoat;
    }

    @SubscribeEvent
    public void onTravel(EventPlayerTravel e){
        if (this.mc.player.isRiding()) {
            riding = this.mc.player.getRidingEntity();
            riding.rotationYaw = this.mc.player.rotationYaw;
            riding.motionY = -this.GlideSpeed.getValue() / 10000.0f;
            dir = MathUtil.directionSpeed(this.speed.getValue());
            if (this.mc.player.movementInput.moveStrafe != 0.0f || this.mc.player.movementInput.moveForward != 0.0f) {
                riding.motionX = dir[0];
                riding.motionY = -(this.GlideSpeed.getValue() / 10000.0f);
                riding.motionZ = dir[1];
            }
            if (this.mc.player.movementInput.jump) {
                riding.motionY = 1.0;
            }
            else if (this.mc.player.movementInput.sneak) {
                riding.motionY = -1.0;
            }
            e.setCanceled(true);
        }
    }

}
