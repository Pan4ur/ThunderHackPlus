package com.mrzak34.thunderhack.modules.movement;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.event.events.EventMove;
import com.mrzak34.thunderhack.event.events.EventPreMotion;
import com.mrzak34.thunderhack.event.events.PacketEvent;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.Util;
import io.netty.util.internal.ConcurrentSet;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.MovementInput;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class PacketFly2 extends Module {
    public PacketFly2() {
        super("PacketFly2", "PacketFly2", Category.MOVEMENT, true, false, false);
    }


    private Setting<Mode> mode = register(new Setting("Mode", Mode.Fast));
    private Setting<Phase> phase = register(new Setting("Phase", Phase.Full));
    private Setting<Type> type = register(new Setting("Type", Type.Preserve));

    public enum Mode {
        Fast,Factor,Rubber,Limit
    }
    public enum Phase {
        Full,Off,Semi
    }    
    public enum Type {
        Preserve,Up,Down,Bounds
    }

    public Setting<Boolean> autoClip = register(new Setting<>("AutoClip", false));//(antiCheat);
    public Setting<Boolean> limit = register(new Setting<>("Limit", true));//(antiCheat);
    public Setting<Boolean> antiKick = register(new Setting<>("AntiKick", true));//(antiCheat);


    public Setting<Float> speed = register(new Setting("Speed", 1.0f, 0.0f, 3.0f));//(antiCheat);
    public Setting<Float> timer = register(new Setting("Timer", 1f, 0.0f, 2f));//(antiCheat);
    public Setting<Integer> increaseTicks = register(new Setting("IncreaseTicks", 20, 1, 20));//(antiCheat);
    public Setting<Integer> factor = register(new Setting("Factor", 1, 1, 10));//(antiCheat);



    private int Field3526 = -1;
    private ConcurrentSet Field3527 = new ConcurrentSet();
    private Random Field3528 = new Random();
    private ConcurrentHashMap Field3529 = new ConcurrentHashMap();
    private int Field3530 = 0;
    private int Field3531 = 0;
    private int Field3532 = 0;
    private boolean Field3533 = false;
    private boolean Field3534 = false;


    @Override
    public void onEnable() {
        this.Field3526 = -1;
        this.Field3531 = 0;
        this.Field3532 = 0;
        if (fullNullCheck() && Util.mc.player != null) {
            this.Method4286();
        }
        if ((this.autoClip.getValue()) && this.Field3534) {
            Util.mc.player.connection.sendPacket(new CPacketEntityAction(Util.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
        }
    }

    @Override
    public void onDisable() {
        Thunderhack.TICK_TIMER =(1.0f);
    }



    public void Method4286() {
        this.Field3530 = 0;
        this.Field3526 = 0;
        this.Field3527.clear();
        this.Field3529.clear();
    }

    public boolean Method4289(int n) {
        ++this.Field3530;
        if (this.Field3530 >= n) {
            this.Field3530 = 0;
            return true;
        }
        return false;
    }

    public void Method4290(CPacketPlayer cPacketPlayer) {
        this.Field3527.add(cPacketPlayer);
        Util.mc.player.connection.sendPacket(cPacketPlayer);
    }

    private int Method4291() {
        if (Util.mc.isSingleplayer()) {
            return 2000;
        }
        int n = this.Field3528.nextInt(29000000);
        if (this.Field3528.nextBoolean()) {
            return n;
        }
        return -n;
    }

    public Vec3d Method4292(Vec3d vec3d, Vec3d vec3d2) {
        Vec3d vec3d3 = vec3d.add(vec3d2);
        switch ((this.type.getValue())) {
            case Preserve: {
                vec3d3 = vec3d3.add((double)this.Method4291(), 0.0, (double)this.Method4291());
                break;
            }
            case Up: {
                vec3d3 = vec3d3.add(0.0, 1337.0, 0.0);
                break;
            }
            case Down: {
                vec3d3 = vec3d3.add(0.0, -1337.0, 0.0);
                break;
            }
            case Bounds: {
                vec3d3 = new Vec3d(vec3d3.x, Util.mc.player.posY <= 10.0 ? 255.0 : 1.0, vec3d3.z);
            }
        }
        return vec3d3;
    }

    public void Method4293(Double d, Double d2, Double d3, Boolean bl) {
        Vec3d vec3d = new Vec3d(d.doubleValue(), d2.doubleValue(), d3.doubleValue());
        Vec3d vec3d2 = Util.mc.player.getPositionVector().add(vec3d);
        Vec3d vec3d3 = this.Method4292(vec3d, vec3d2);
        this.Method4290((CPacketPlayer)new CPacketPlayer.Position(vec3d2.x, vec3d2.y, vec3d2.z, Util.mc.player.onGround));
        this.Method4290((CPacketPlayer)new CPacketPlayer.Position(vec3d3.x, vec3d3.y, vec3d3.z, Util.mc.player.onGround));
        if (bl) {
            Util.mc.player.connection.sendPacket(new CPacketConfirmTeleport(++this.Field3526));
            this.Field3529.put(this.Field3526, new Class443(vec3d2.x, vec3d2.y, vec3d2.z, System.currentTimeMillis()));
        }
    }

    private boolean Method4294() {
        return !Util.mc.world.getCollisionBoxes(Util.mc.player, Util.mc.player.getEntityBoundingBox().expand(-0.0625, -0.0625, -0.0625)).isEmpty();
    }

    @SubscribeEvent
    public void Method4282(PacketEvent.Receive eventNetworkPrePacketEvent) {
        if(fullNullCheck()){
            return;
        }
        if (Util.mc.player != null && eventNetworkPrePacketEvent.getPacket() instanceof SPacketPlayerPosLook) {
            SPacketPlayerPosLook sPacketPlayerPosLook = (SPacketPlayerPosLook)eventNetworkPrePacketEvent.getPacket();
            Class443 class443 = (Class443)this.Field3529.remove(sPacketPlayerPosLook.teleportId);
            if (Util.mc.player.isEntityAlive() && Util.mc.world.isBlockLoaded(new BlockPos(Util.mc.player.posX, Util.mc.player.posY, Util.mc.player.posZ), false) && !(Util.mc.currentScreen instanceof GuiDownloadTerrain) && this.mode.getValue() != Mode.Rubber && class443 != null && Class443.Method1920(class443) == sPacketPlayerPosLook.x && Class443.Method1921(class443) == sPacketPlayerPosLook.y && Class443.Method1922(class443) == sPacketPlayerPosLook.z) {
                eventNetworkPrePacketEvent.setCanceled(true);
                return;
            }
            sPacketPlayerPosLook.yaw = Util.mc.player.rotationYaw;
            sPacketPlayerPosLook.pitch = Util.mc.player.rotationPitch;
            this.Field3526 = sPacketPlayerPosLook.getTeleportId();
        }
    }
    
    
    @SubscribeEvent
    public void Method4281(PacketEvent.Send eventNetworkPostPacketEvent) {
        if (eventNetworkPostPacketEvent.getPacket() instanceof CPacketPlayer) {
            if (this.Field3527.contains(eventNetworkPostPacketEvent.getPacket())) {
                this.Field3527.remove(eventNetworkPostPacketEvent.getPacket());
                return;
            }
            eventNetworkPostPacketEvent.setCanceled(true);
        }
    }

    @Override
    public void onUpdate() {
        this.Field3529.entrySet().removeIf(PacketFly2::Method4295);
    }

    private static boolean Method4295(Object o) {
        return System.currentTimeMillis() - ((Class443)((Map.Entry)o).getValue()).Method1915() > TimeUnit.SECONDS.toMillis(30L);
    }



    @SubscribeEvent
    public void Method4279(EventMove eventPlayerMove) {
        if (!eventPlayerMove.isCanceled()) {
            if (this.mode.getValue() != Mode.Rubber && this.Field3526 == 0) {
                return;
            }
            eventPlayerMove.setCanceled(true);
            eventPlayerMove.set_x(Util.mc.player.motionX);
            eventPlayerMove.set_y(Util.mc.player.motionY);
            eventPlayerMove.set_z(Util.mc.player.motionZ);
            if (this.phase.getValue() != Phase.Off && (this.phase.getValue() == Phase.Semi || this.Method4294())) {
                Util.mc.player.noClip = true;
            }
        }
    }

    @SubscribeEvent
    public void Method4278(EventPreMotion eventPlayerUpdateWalking) {
        if ((double)(this.timer.getValue()) != 1.0) {
            Thunderhack.TICK_TIMER =((this.timer.getValue()));
        }
        Util.mc.player.setVelocity(0.0, 0.0, 0.0);
        if (this.mode.getValue() != Mode.Rubber && this.Field3526 == 0) {
            if (this.Method4289(4)) {
                this.Method4293(0.0, 0.0, 0.0, false);
            }
            return;
        }
        boolean bl = this.Method4294();
        double d = 0.0;
        d = Util.mc.player.movementInput.jump && (bl || !Method103()) ? ((this.antiKick.getValue()) && !bl ? (this.Method4289(this.mode.getValue() == Mode.Rubber ? 10 : 20) ? -0.032 : 0.062) : 0.062) : (Util.mc.player.movementInput.sneak ? -0.062 : (!bl ? (this.Method4289(4) ? ((this.antiKick.getValue()) ? -0.04 : 0.0) : 0.0) : 0.0));
        if (this.phase.getValue() == Phase.Full && bl && Method103() && d != 0.0) {
            d = Util.mc.player.movementInput.jump ? (d /= 2.5) : (d /= 1.5);
        }
        if (Util.mc.player.movementInput.jump && (this.autoClip.getValue())) {
            Util.mc.player.connection.sendPacket(new CPacketEntityAction( Util.mc.player, this.Field3534 ? CPacketEntityAction.Action.START_SNEAKING : CPacketEntityAction.Action.STOP_SNEAKING));
            this.Field3534 = !this.Field3534;
        }
        double[] dArray = Method3180(this.phase.getValue() == Phase.Full && bl ? 0.034444444444444444 : (double)(this.speed.getValue()) * 0.26);
        int n = 1;
        if (this.mode.getValue() == Mode.Factor && Util.mc.player.ticksExisted % (Integer)this.increaseTicks.getValue() == 0) {
            n = this.factor.getValue();
        }
        for (int i = 1; i <= n; ++i) {
            if (this.mode.getValue() == Mode.Limit) {
                if (Util.mc.player.ticksExisted % 2 == 0) {
                    if (this.Field3533 && d >= 0.0) {
                        this.Field3533 = false;
                        d = -0.032;
                    }
                    Util.mc.player.motionX = dArray[0] * (double)i;
                    Util.mc.player.motionZ = dArray[1] * (double)i;
                    Util.mc.player.motionY = d * (double)i;
                    this.Method4293(Util.mc.player.motionX, Util.mc.player.motionY, Util.mc.player.motionZ, this.limit.getValue() == false);
                    continue;
                }
                if (!(d < 0.0)) continue;
                this.Field3533 = true;
                continue;
            }
            Util.mc.player.motionX = dArray[0] * (double)i;
            Util.mc.player.motionZ = dArray[1] * (double)i;
            Util.mc.player.motionY = d * (double)i;
            this.Method4293(Util.mc.player.motionX, Util.mc.player.motionY, Util.mc.player.motionZ, this.mode.getValue() != Mode.Rubber);
        }
    }



    public static class Class443 {
        private double Field1501;
        private double Field1502;
        private double Field1503;
        private long Field1504;

        public Class443(double d, double d2, double d3, long l) {
            this.Field1501 = d;
            this.Field1502 = d2;
            this.Field1503 = d3;
            this.Field1504 = l;
        }


        public long Method1915() {
            return this.Field1504;
        }


        static double Method1920(Class443 class443) {
            return class443.Field1501;
        }

        static double Method1921(Class443 class443) {
            return class443.Field1502;
        }

        static double Method1922(Class443 class443) {
            return class443.Field1503;
        }
    }
    public static boolean Method103() {
        return Util.mc.player != null && (Util.mc.player.movementInput.moveForward != 0.0f || Util.mc.player.movementInput.moveStrafe != 0.0f);
    }





    public static double[] Method3180(double d) {
        double d2 = Util.mc.player.movementInput.moveForward;
        double d3 = Util.mc.player.movementInput.moveStrafe;
        float f = Util.mc.player.rotationYaw;
        double[] dArray = new double[2];
        if (d2 == 0.0 && d3 == 0.0) {
            dArray[0] = 0.0;
            dArray[1] = 0.0;
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
            dArray[0] = d2 * d * Math.cos(Math.toRadians(f + 90.0f)) + d3 * d * Math.sin(Math.toRadians(f + 90.0f));
            dArray[1] = d2 * d * Math.sin(Math.toRadians(f + 90.0f)) - d3 * d * Math.cos(Math.toRadians(f + 90.0f));
        }
        return dArray;
    }

}
