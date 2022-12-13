package com.mrzak34.thunderhack.modules.player;

import com.mrzak34.thunderhack.event.events.PacketEvent;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Comparator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PearlBait extends Module {

    public PearlBait() {
        super("PearlBait", "кидаешь перл и-не тепаешься", Category.PLAYER, true, false, false);
    }

    public Setting<Boolean> guarantee = this.register ( new Setting <> ( "Forced Strafe", true));

    private final Queue<CPacketPlayer> packets = new ConcurrentLinkedQueue<>();
    private int thrownPearlId = -1;


    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event){
        if (event.getPacket() instanceof SPacketSpawnObject) {
            SPacketSpawnObject packet = (SPacketSpawnObject) event.getPacket();
            if (packet.getType() == 65) {
                mc.world.playerEntities.stream()
                        .min(Comparator.comparingDouble((p) -> p.getDistance(packet.getX(), packet.getY(), packet.getZ())))
                        .ifPresent((player) -> {
                            if (player.equals(mc.player)) {
                                if (mc.player.onGround) {
                                    // do not allow movement
                                    mc.player.motionX = 0.0;
                                    mc.player.motionY = 0.0;
                                    mc.player.motionZ = 0.0;

                                    mc.player.movementInput.moveForward = 0.0f;
                                    mc.player.movementInput.moveStrafe = 0.0f;

                                    // send rubberband packet
                                    mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.0, mc.player.posZ, false));

                                    thrownPearlId = packet.getEntityID();
                                }
                            }
                        });
            }
        } else if (event.getPacket() instanceof CPacketPlayer && guarantee.getValue() && thrownPearlId != -1) {
            packets.add((CPacketPlayer) event.getPacket());
            event.setCanceled(true);
        }
    }

    @Override
    public void onUpdate(){
        if (thrownPearlId != -1) {
            for (Entity entity : mc.world.loadedEntityList) {
                if (entity.getEntityId() == thrownPearlId && entity instanceof EntityEnderPearl) {
                    EntityEnderPearl pearl = (EntityEnderPearl) entity;
                    if (pearl.isDead) {
                        thrownPearlId = -1;
                    }
                }
            }
        } else {
            if (!packets.isEmpty()) {
                do {
                    mc.player.connection.sendPacket(packets.poll());
                } while (!packets.isEmpty());
            }
        }
    }
}
