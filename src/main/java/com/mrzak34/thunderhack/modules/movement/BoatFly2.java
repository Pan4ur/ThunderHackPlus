package com.mrzak34.thunderhack.modules.movement;

import com.mrzak34.thunderhack.event.events.EventPlayerTravel;
import com.mrzak34.thunderhack.event.events.EventPreMotion;
import com.mrzak34.thunderhack.event.events.PacketEvent;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.RotationUtil;
import com.mrzak34.thunderhack.util.SilentRotaionUtil;
import com.mrzak34.thunderhack.util.Timer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketMoveVehicle;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.network.play.server.SPacketSetPassengers;
import net.minecraft.util.EnumHand;
import net.minecraft.util.MovementInput;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Collection;

public class BoatFly2 extends Module {

    public BoatFly2() {
        super("BoatFly2", "BoatFly2", Category.MOVEMENT, true, false, false);
    }


    public  Setting<Boolean> aaaaa = this.register(new Setting<>("aaaa", true));
    public  Setting<Boolean> aaaa = this.register(new Setting<>("aa", true));




    private final Setting<Float> speed = this.register(new Setting<Float>("speed", 1.0f, 0.1f, 10.0f));
    private final Setting<Float> upspeed = this.register(new Setting<Float>("upSpeed", 1.0f, 0.1f, 10.0f));
    private final Setting<Float> downSpeed = this.register(new Setting<Float>("downSpeed", 1.0f, 0.1f, 10.0f));

    public int Field4448;
    public boolean Field4449;
    public int Field4450;
    public double Field4451 = -1.0;
    public int Field4452;
    public long Field4453;
    public float Field4454;


    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive f4e2) {

        if(mc.player == null || mc.world == null){
            return;
        }

        if (f4e2.getStage() == 0 && !f4e2.isCanceled()) {
            if (f4e2.getPacket() instanceof SPacketPlayerPosLook) {
                Packet packet = f4e2.getPacket();
                SPacketPlayerPosLook sPacketPlayerPosLook = (SPacketPlayerPosLook)packet;
                this.Field4450 = sPacketPlayerPosLook.getTeleportId();
                if (mc.player.isRiding()) {
                    f4e2.setCanceled(true);
                }
            } else if (f4e2.getPacket() instanceof SPacketMoveVehicle) {
                boolean bl;
                Packet packet = f4e2.getPacket();
                SPacketMoveVehicle sPacketMoveVehicle = (SPacketMoveVehicle)packet;
                if (mc.player.isRiding()) {
                    Entity entity = mc.player.getRidingEntity();
                    Collection collection = mc.world.getCollisionBoxes(null, entity.getEntityBoundingBox().grow(0.0625));
                    boolean bl2 = false;
                    boolean bl3 = !collection.isEmpty();
                    if (bl3) {
                        return;
                    }
                }

                if (aaaa.getValue()) {
                    f4e2.setCanceled(true);
                } else if (aaaaa.getValue()) {
                    Method2268(sPacketMoveVehicle.getX(), sPacketMoveVehicle.getY(), sPacketMoveVehicle.getZ());
                }

            } else if (f4e2.getPacket() instanceof SPacketSetPassengers) {
                if (mc.player.isRiding()) {
                    Packet packet = f4e2.getPacket();
                    SPacketSetPassengers sPacketSetPassengers = (SPacketSetPassengers)packet;
                    int n = sPacketSetPassengers.getEntityId();
                    Entity entity = mc.player.getRidingEntity();
                    if (n == entity.getEntityId()) {
                        for (int n2 : sPacketSetPassengers.getPassengerIds()) {
                            if (n2 != mc.player.getEntityId()) continue;
                            f4e2.setCanceled(true);
                            return;
                        }
                    }
                }
            }
        }
    }


    public void Method2268(double d, double d2, double d3) {
        this.Field1613 = System.currentTimeMillis() + 2000L;
    }


    @SubscribeEvent
    public void onTravel(EventPlayerTravel e){
        if(mc.player == null || mc.world == null){
            return;
        }
        Method5384();
    }

    public void Method5384() {
        double[] arrd;
        if (!mc.player.isRiding()) {
            this.Field4451 = -1.0;
            return;
        }
        Entity entity = mc.player.getRidingEntity();
        if (entity == null) return;
        int n = this.Field4448;
        this.Field4448 = n + 1;
        boolean bl = this.Field4449 = !mc.player.movementInput.sneak && mc.player.ticksExisted % 2 == 0 && !entity.collidedHorizontally && mc.world.getCollisionBoxes(null, entity.getEntityBoundingBox().grow(0.0625).expand(0.0, -0.05, 0.0)).isEmpty();
        if (this.Field4448 > 2) {
            this.Field4448 = 0;
        }
        entity.motionY = 0.0;
        double[] arrd2 = Method723(speed.getValue());
        if (mc.player.movementInput.jump) {
            entity.motionY = upspeed.getValue();
        } else if (mc.player.movementInput.sneak) {
            entity.motionY = -downSpeed.getValue();
        }
        entity.rotationYaw = this.mc.player.rotationYaw;
        Method722(speed.getValue());
    }

    public static double[] Method723(double d) {
        double d2 = Method729().moveForward;
        double d3 = Method729().moveStrafe;
        float f = mc.player.rotationYaw;
        if (d2 == 0.0 && d3 == 0.0) {
            return new double[]{0.0, 0.0};
        }
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
        return new double[]{d2 * d * Math.cos(Math.toRadians(f + 90.0f)) + d3 * d * Math.sin(Math.toRadians(f + 90.0f)), d2 * d * Math.sin(Math.toRadians(f + 90.0f)) - d3 * d * Math.cos(Math.toRadians(f + 90.0f))};
    }

    public static MovementInput Method729() {
        return mc.player.movementInput;
    }

    public long Field1613;
    public static void Method722(double d) {
        if (mc.player.getRidingEntity() != null) {
            MovementInput movementInput = mc.player.movementInput;
            double d2 = movementInput.moveForward;
            double d3 = movementInput.moveStrafe;
            float f = mc.player.rotationYaw;
            if (d2 == 0.0 && d3 == 0.0) {
                mc.player.getRidingEntity().motionX = 0.0;
                mc.player.getRidingEntity().motionZ = 0.0;
                return;
            }
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
            mc.player.getRidingEntity().motionX = d2 * d * Math.cos(Math.toRadians(f + 90.0f)) + d3 * d * Math.sin(Math.toRadians(f + 90.0f));
            mc.player.getRidingEntity().motionZ = d2 * d * Math.sin(Math.toRadians(f + 90.0f)) - d3 * d * Math.cos(Math.toRadians(f + 90.0f));
        }
    }






    Timer timer = new Timer();
    Entity Boats;

    @SubscribeEvent
    public void onUpdateWalkingPlayerPre(EventPreMotion e){
        if(mc.player == null || mc.world == null){
            return;
        }
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


    private  Setting<Float> delay = this.register(new Setting<Float>("delat", 0.48f, 0.0f, 1f));
    public  Setting<Boolean> rotate = this.register(new Setting<>("rotate", true));

    public boolean isABoat(Entity enot){
        if (enot.getDistance((Entity)this.mc.player) > 6f) {
            return false;
        }
        return enot instanceof EntityBoat;
    }

}
