package com.mrzak34.thunderhack.util.phobos;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.events.ConnectToServerEvent;
import com.mrzak34.thunderhack.events.PacketEvent;
import com.mrzak34.thunderhack.modules.Feature;
import com.mrzak34.thunderhack.util.RotationUtil;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Deque;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedDeque;

public class PositionHistoryHelper extends Feature {
    private static final int REMOVE_TIME = 1000;

    private final Deque<RotationHistory> packets;

    public PositionHistoryHelper() {
        this.packets = new ConcurrentLinkedDeque<>();
    }

    @SubscribeEvent
    public void onConnect(ConnectToServerEvent e) {
        packets.clear();
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send e) {
        if (e.getPacket() instanceof CPacketPlayer) {
            onPlayerPacket(e.getPacket());
        }
        if (e.getPacket() instanceof CPacketPlayer.Position) {
            onPlayerPacket(e.getPacket());
        }
        if (e.getPacket() instanceof CPacketPlayer.Rotation) {
            onPlayerPacket(e.getPacket());
        }
        if (e.getPacket() instanceof CPacketPlayer.PositionRotation) {
            onPlayerPacket(e.getPacket());
        }
    }

    private void onPlayerPacket(CPacketPlayer packet) {
        packets.removeIf(h ->
                h == null || System.currentTimeMillis() - h.time > REMOVE_TIME);
        packets.addFirst(new RotationHistory(packet));
    }

    public boolean arePreviousRotationsLegit(Entity entity,
                                             int time,
                                             boolean skipFirst) {
        if (time == 0) {
            return true;
        }

        Iterator<RotationHistory> itr = packets.iterator();
        while (itr.hasNext()) {
            RotationHistory next = itr.next();
            if (skipFirst) {
                // SkipFirst, since in most cases we
                // already checked the first Rotations
                continue;
            }

            if (next != null) {
                if (System.currentTimeMillis() - next.time > REMOVE_TIME) {
                    itr.remove();
                } else if (System.currentTimeMillis() - next.time > time) {
                    break;
                } else if (!isLegit(next, entity)) {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean isLegit(RotationHistory history, Entity entity) {
        RayTraceResult result =
                RayTracer.rayTraceEntities(mc.world,
                        RotationUtil.getRotationPlayer(),
                        7.0,
                        history.x,
                        history.y,
                        history.z,
                        history.yaw,
                        history.pitch,
                        history.bb,
                        e ->
                                e != null && e.equals(entity),
                        entity,
                        entity);
        return result != null
                && entity.equals(result.entityHit);
    }

    public Deque<RotationHistory> getPackets() {
        return packets;
    }

    public static final class RotationHistory {
        public final double x;
        public final double y;
        public final double z;
        public final float yaw;
        public final float pitch;
        public final long time;
        public final AxisAlignedBB bb;
        public final boolean hasLook;
        public final boolean hasPos;
        public final boolean hasChanged;

        public RotationHistory(CPacketPlayer packet) {
            this(packet.getX(Thunderhack.positionManager.getX()),
                    packet.getY(Thunderhack.positionManager.getY()),
                    packet.getZ(Thunderhack.positionManager.getZ()),
                    packet.getYaw(Thunderhack.rotationManager.getServerYaw()),
                    packet.getPitch(Thunderhack.rotationManager.getServerPitch()),
                    packet instanceof CPacketPlayer.Rotation || packet instanceof CPacketPlayer.PositionRotation,
                    packet instanceof CPacketPlayer.Position || packet instanceof CPacketPlayer.PositionRotation);
        }

        public RotationHistory(double x,
                               double y,
                               double z,
                               float yaw,
                               float pitch,
                               boolean hasLook,
                               boolean hasPos) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.yaw = yaw;
            this.pitch = pitch;
            this.hasLook = hasLook;
            this.hasPos = hasPos;
            this.time = System.currentTimeMillis();
            float w = mc.player.width / 2.0f;
            float h = mc.player.height;
            this.bb = new AxisAlignedBB(x - w, y, z - w, x + w, y + h, z + w);
            this.hasChanged = hasLook || hasPos;
        }
    }

}