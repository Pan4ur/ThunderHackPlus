package com.mrzak34.thunderhack.modules.movement;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.command.Command;
import com.mrzak34.thunderhack.event.events.EventPlayerTravel;;
import com.mrzak34.thunderhack.event.events.PacketEvent;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.Util;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.network.play.client.CPacketInput;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketVehicleMove;
import net.minecraft.network.play.server.SPacketMoveVehicle;
import net.minecraft.util.EnumHand;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketDisconnect;
import net.minecraft.network.play.server.SPacketEntity;
import net.minecraft.network.play.server.SPacketEntityAttach;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import io.netty.util.internal.ConcurrentSet;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BoatFly extends Module {
    public BoatFly() {
        super("BoatFly", "BoatFly", Category.MOVEMENT, true, false, false);
    }


    private Setting<Mode> mode  = this.register(new Setting("Mode", Mode.Packet));

    public enum Mode {
        Packet, Motion
    }


    private  Setting<Float> speed = this.register(new Setting<Float>("Speed", 2f, 0.0f, 45f));
    private  Setting<Float> yspeed = this.register(new Setting<Float>("YSpeed", 1f, 0.0f, 10f));
    private  Setting<Float> glidespeed = this.register(new Setting<Float>("GlideSpeed", 1f, 0.0f, 10f));
    private  Setting<Float> timer = this.register(new Setting<Float>("Timer", 1f, 0.0f, 5f));
    private  Setting<Float> height = this.register(new Setting<Float>("Height", 127f, 0.0f, 256f));
    private  Setting<Float> offset = this.register(new Setting<Float>("Offset", 0.1f, 0.0f, 10f));
    private Setting<Integer> enableticks = this.register(new Setting("EnableTicks", 10, 1, 100));
    private Setting<Integer> waitticks = this.register(new Setting("WaitTicks", 10, 1, 100));


    public  Setting<Boolean> strict = this.register(new Setting<>("Strict", false));
    public  Setting<Boolean> limit = this.register(new Setting<>("Limit", true));
    public  Setting<Boolean> phase = this.register(new Setting<>("Phase", true));
    public  Setting<Boolean> gravity = this.register(new Setting<>("Gravity", true));


    public  Setting<Boolean> ongroundpacket = this.register(new Setting<>("OnGroundPacket", false));
    public  Setting<Boolean> spoofpackets = this.register(new Setting<>("SpoofPackets", false));
    public  Setting<Boolean> cancelrotations = this.register(new Setting<>("CancelRotations", true));
    public  Setting<Boolean> cancel = this.register(new Setting<>("Cancel", true));
    public  Setting<Boolean> remount = this.register(new Setting<>("Remount", true));
    public  Setting<Boolean> stop = this.register(new Setting<>("Stop", false));
    public  Setting<Boolean> ylimit = this.register(new Setting<>("yLimit", false));
    public  Setting<Boolean> debug = this.register(new Setting<>("Debug", true));
    public  Setting<Boolean> automount = this.register(new Setting<>("AutoMount", true));
    public  Setting<Boolean> stopunloaded = this.register(new Setting<>("StopUnloaded", true));



    private final ConcurrentSet Field2263 = new ConcurrentSet();
    private int Field2264 = 0;
    private int Field2265 = 0;
    private boolean Field2266 = false;
    private boolean Field2267 = false;
    private boolean Field2268 = false;



    @Override
    public void onEnable() {
        if (Util.mc.player == null || Util.mc.player.world == null) {
            this.toggle();
            return;
        }
        if ((this.automount.getValue())) {
            this.Method2868();
        }
    }

    @Override
    public void onDisable() {
        Thunderhack.TICK_TIMER = (1.0f);
        this.Field2263.clear();
        this.Field2266 = false;
        if (Util.mc.player == null) {
            return;
        }
        if ((this.phase.getValue()) && this.mode.getValue() == Mode.Motion) {
            if (Util.mc.player.getRidingEntity() != null) {
                Util.mc.player.getRidingEntity().noClip = false;
            }
            Util.mc.player.noClip = false;
        }
        if (Util.mc.player.getRidingEntity() != null) {
            Util.mc.player.getRidingEntity().setNoGravity(false);
        }
        Util.mc.player.setNoGravity(false);
    }


    private float Method2874() {
        this.Field2268 = !this.Field2268;
        return this.Field2268 ? (this.offset.getValue()) : -(this.offset.getValue());
    }

    private void Method2875(CPacketVehicleMove cPacketVehicleMove) {
        this.Field2263.add((Object)cPacketVehicleMove);
        Util.mc.player.connection.sendPacket(cPacketVehicleMove);
    }

    private void Method2876(Entity entity) {
        double d = entity.posY;
        BlockPos blockPos = new BlockPos(entity.posX, (int)entity.posY, entity.posZ);
        for (int i = 0; i < 255; ++i) {
            if (!Util.mc.world.getBlockState(blockPos).getMaterial().isReplaceable() || Util.mc.world.getBlockState(blockPos).getBlock() == Blocks.WATER) {
                entity.posY = blockPos.getY() + 1;
                if (this.debug.getValue()) {
                    Command.sendMessage("GroundY" + entity.posY);
                }
                this.Method2875(new CPacketVehicleMove(entity));
                entity.posY = d;
                break;
            }
            blockPos = blockPos.add(0, -1, 0);
        }
    }

    private void Method2868() {
        for (Entity entity : Util.mc.world.loadedEntityList) {
            if (!(entity instanceof EntityBoat) || !(Util.mc.player.getDistance(entity) < 5.0f)) continue;
            Util.mc.player.connection.sendPacket(new CPacketUseEntity(entity, EnumHand.MAIN_HAND));
            break;
        }
    }


    public static double[] Method1330(double d) {
        float f = Util.mc.player.movementInput.moveForward;
        float f2 = Util.mc.player.movementInput.moveStrafe;
        float f3 = Util.mc.player.prevRotationYaw + (Util.mc.player.rotationYaw - Util.mc.player.prevRotationYaw) * Util.mc.getRenderPartialTicks();
        if (f != 0.0f) {
            if (f2 > 0.0f) {
                f3 += (f > 0.0f ? -45 : 45);
            } else if (f2 < 0.0f) {
                f3 += (f > 0.0f ? 45 : -45);
            }
            f2 = 0.0f;
            if (f > 0.0f) {
                f = 1.0f;
            } else if (f < 0.0f) {
                f = -1.0f;
            }
        }
        double d2 = Math.sin(Math.toRadians(f3 + 90.0f));
        double d3 = Math.cos(Math.toRadians(f3 + 90.0f));
        double d4 = f * d * d3 + f2 * d * d2;
        double d5 = f * d * d2 - f2 * d * d3;
        return new double[]{d4, d5};
    }


    @SubscribeEvent
    public void onPlayerTravel(EventPlayerTravel eventPlayerTravel) {
        if (Util.mc.player == null || Util.mc.world == null) {
            return;
        }
        if (Util.mc.player.getRidingEntity() == null) {
            if (this.automount.getValue()) {
                this.Method2868();
            }
            return;
        }
        if (this.phase.getValue() && this.mode.getValue() == Mode.Motion) {
            Util.mc.player.getRidingEntity().noClip = true;
            Util.mc.player.getRidingEntity().setNoGravity(true);
            Util.mc.player.noClip = true;
        }
        if (!this.Field2267) {
            Util.mc.player.getRidingEntity().setNoGravity(!(this.gravity.getValue()));
            Util.mc.player.setNoGravity(!(this.gravity.getValue()));
        }
        if (this.stop.getValue()) {
            if (this.Field2264 > this.enableticks.getValue() && !this.Field2266) {
                this.Field2264 = 0;
                this.Field2266 = true;
                this.Field2265 = this.waitticks.getValue();
            }
            if (this.Field2265 > 0 && this.Field2266) {
                --this.Field2265;
                return;
            }
            if (this.Field2265 <= 0) {
                this.Field2266 = false;
            }
        }
        Entity entity = Util.mc.player.getRidingEntity();
        if (this.debug.getValue()) {
            Command.sendMessage("Y" + entity.posY);
            Command.sendMessage("Fall" + entity.fallDistance);
        }
        if ((!Util.mc.world.isChunkGeneratedAt(entity.getPosition().getX() >> 4, entity.getPosition().getZ() >> 4) || entity.getPosition().getY() < 0) && this.stopunloaded.getValue()) {
            if (this.debug.getValue()) {
                Command.sendMessage("Detected unloaded chunk!");
            }
            this.Field2267 = true;
            return;
        }
        if (this.timer.getValue() != 1.0f) {
            Thunderhack.TICK_TIMER = (this.timer.getValue());
        }
        entity.rotationYaw = Util.mc.player.rotationYaw;
        double[] dArray = Method1330(this.speed.getValue());
        double d = entity.posX + dArray[0];
        double d2 = entity.posZ + dArray[1];
        double d3 = entity.posY;
        if ((!Util.mc.world.isChunkGeneratedAt((int)d >> 4, (int)d2 >> 4) || entity.getPosition().getY() < 0) && (this.stopunloaded.getValue())) {
            if (this.debug.getValue()) {
                Command.sendMessage("Detected unloaded chunk!");
            }
            this.Field2267 = true;
            return;
        }
        this.Field2267 = false;
        entity.motionY = -((this.glidespeed.getValue()) / 100.0f);
        if (this.mode.getValue() == Mode.Motion) {
            entity.motionX = dArray[0];
            entity.motionZ = dArray[1];
        }
        if (Util.mc.player.movementInput.jump) {
            if (!(this.ylimit.getValue()) || entity.posY <= (this.height.getValue())) {
                if (this.mode.getValue() == Mode.Motion) {
                    entity.motionY += (this.yspeed.getValue());
                } else {
                    d3 += (this.yspeed.getValue());
                }
            }
        } else if (Util.mc.player.movementInput.sneak) {
            if (this.mode.getValue() == Mode.Motion) {
                entity.motionY += (-(this.yspeed.getValue()));
            } else {
                d3 += (-(this.yspeed.getValue()));
            }
        }
        if (Util.mc.player.movementInput.moveStrafe == 0.0f && Util.mc.player.movementInput.moveForward == 0.0f) {
            entity.motionX = 0.0;
            entity.motionZ = 0.0;
        }
        if ((this.ongroundpacket.getValue())) {
            this.Method2876(entity);
        }
        if (this.mode.getValue() != Mode.Motion) {
            entity.setPosition(d, d3, d2);
        }
        if (this.mode.getValue() == Mode.Packet) {
            this.Method2875(new CPacketVehicleMove(entity));
        }
        if (this.strict.getValue()) {
            Util.mc.player.connection.sendPacket(new CPacketClickWindow(0, 0, 0, ClickType.CLONE, ItemStack.EMPTY, (short) 0));
        }
        if ((this.spoofpackets.getValue())) {
            Vec3d vec3d = entity.getPositionVector().add(0.0, this.Method2874(), 0.0);
            EntityBoat entityBoat = new EntityBoat((World)Util.mc.world, vec3d.x, vec3d.y, vec3d.z);
            entityBoat.rotationYaw = entity.rotationYaw;
            entityBoat.rotationPitch = entity.rotationPitch;
            this.Method2875(new CPacketVehicleMove((Entity)entityBoat));
        }
        if ((this.remount.getValue())) {
            Util.mc.player.connection.sendPacket(new CPacketUseEntity(entity, EnumHand.MAIN_HAND));
        }
        eventPlayerTravel.setCanceled(true);
        ++this.Field2264;
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive eventNetworkPrePacketEvent) {
        if(fullNullCheck()){
            return;
        }
        if (eventNetworkPrePacketEvent.getPacket() instanceof SPacketDisconnect) {
            this.toggle();
        }
        if (!Util.mc.player.isRiding() || this.Field2267 || this.Field2266) {
            return;
        }
        if (eventNetworkPrePacketEvent.getPacket() instanceof SPacketMoveVehicle && Util.mc.player.isRiding() && (this.cancel.getValue())) {
            eventNetworkPrePacketEvent.setCanceled(true);
        }
        if (eventNetworkPrePacketEvent.getPacket() instanceof SPacketPlayerPosLook && Util.mc.player.isRiding() && (this.cancel.getValue())) {
            eventNetworkPrePacketEvent.setCanceled(true);
        }
        if (eventNetworkPrePacketEvent.getPacket() instanceof SPacketEntity && (this.cancel.getValue())) {
            eventNetworkPrePacketEvent.setCanceled(true);
        }
        if (eventNetworkPrePacketEvent.getPacket() instanceof SPacketEntityAttach && (this.cancel.getValue())) {
            eventNetworkPrePacketEvent.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send eventNetworkPostPacketEvent) {
        if (Util.mc.player == null || Util.mc.world == null) {
            return;
        }
        if ((eventNetworkPostPacketEvent.getPacket() instanceof CPacketPlayer.Rotation && (this.cancelrotations.getValue()) || eventNetworkPostPacketEvent.getPacket() instanceof CPacketInput) && Util.mc.player.isRiding()) {
            eventNetworkPostPacketEvent.setCanceled(true);
        }
        if (this.Field2267 && eventNetworkPostPacketEvent.getPacket() instanceof CPacketVehicleMove) {
            eventNetworkPostPacketEvent.setCanceled(true);
        }
        if (!Util.mc.player.isRiding() || this.Field2267 || this.Field2266) {
            return;
        }
        Entity entity = Util.mc.player.getRidingEntity();
        if ((!Util.mc.world.isChunkGeneratedAt(entity.getPosition().getX() >> 4, entity.getPosition().getZ() >> 4) || entity.getPosition().getY() < 0) && (this.stopunloaded.getValue())) {
            return;
        }
        if (eventNetworkPostPacketEvent.getPacket() instanceof CPacketVehicleMove && (this.limit.getValue()) && this.mode.getValue() == Mode.Packet) {
            CPacketVehicleMove cPacketVehicleMove = eventNetworkPostPacketEvent.getPacket();
            if (this.Field2263.contains(cPacketVehicleMove)) {
                this.Field2263.remove(cPacketVehicleMove);
            } else {
                eventNetworkPostPacketEvent.setCanceled(true);
            }
        }
    }
}
