package com.mrzak34.thunderhack.util.phobos;

import com.mrzak34.thunderhack.events.PacketEvent;
import com.mrzak34.thunderhack.mixin.mixins.ICPacketUseEntity;
import com.mrzak34.thunderhack.modules.combat.AutoCrystal;
import com.mrzak34.thunderhack.util.InventoryUtil;
import com.mrzak34.thunderhack.util.Util;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemSpade;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.*;
import net.minecraft.util.EnumHand;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class IDHelper {
    private static final ScheduledExecutorService THREAD;

    static {
        THREAD = ThreadUtil.newDaemonScheduledExecutor("ID-Helper");
    }

    private volatile int highestID;
    private boolean updated;

    public IDHelper() {
        MinecraftForge.EVENT_BUS.register(this); //TODO не уверен
    }

    public static CPacketUseEntity attackPacket(int id) {
        CPacketUseEntity packet = new CPacketUseEntity();
        ((ICPacketUseEntity) packet).setEntityId(id);
        ((ICPacketUseEntity) packet).setAction(CPacketUseEntity.Action.ATTACK);
        return packet;

    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketSpawnObject) {
            checkID(((SPacketSpawnObject) event.getPacket()).getEntityID());
        }
        if (event.getPacket() instanceof SPacketSpawnExperienceOrb) {
            checkID(((SPacketSpawnExperienceOrb) event.getPacket()).getEntityID());
        }
        if (event.getPacket() instanceof SPacketSpawnPlayer) {
            checkID(((SPacketSpawnPlayer) event.getPacket()).getEntityID());
        }
        if (event.getPacket() instanceof SPacketSpawnGlobalEntity) {
            checkID(((SPacketSpawnGlobalEntity) event.getPacket()).getEntityId());
        }
        if (event.getPacket() instanceof SPacketSpawnPainting) {
            checkID(((SPacketSpawnPainting) event.getPacket()).getEntityID());
        }
        if (event.getPacket() instanceof SPacketSpawnMob) {
            checkID(((SPacketSpawnMob) event.getPacket()).getEntityID());
        }
    }

    public int getHighestID() {
        return highestID;
    }

    public void setHighestID(int id) {
        this.highestID = id;
    }

    public boolean isUpdated() {
        return updated;
    }

    public void setUpdated(boolean updated) {
        this.updated = updated;
    }

    public void update() {
        int highest = getHighestID();
        for (Entity entity : Util.mc.world.loadedEntityList) {
            if (entity.getEntityId() > highest) {
                highest = entity.getEntityId();
            }
        }

        if (highest > highestID) {
            highestID = highest;
        }
    }

    public boolean isSafe(List<EntityPlayer> players,
                          boolean holdingCheck,
                          boolean toolCheck) {
        if (!holdingCheck) {
            return true;
        }

        for (EntityPlayer player : players) {
            if (isDangerous(player, true, toolCheck)) {
                return false;
            }
        }

        return true;
    }

    public boolean isDangerous(EntityPlayer player,
                               boolean holdingCheck,
                               boolean toolCheck) {
        if (!holdingCheck) {
            return false;
        }

        return InventoryUtil.isHolding(player, Items.BOW)
                || InventoryUtil.isHolding(player, Items.EXPERIENCE_BOTTLE)
                || toolCheck && (
                player.getHeldItemMainhand().getItem() instanceof ItemPickaxe
                        || player.getHeldItemMainhand().getItem() instanceof ItemSpade);
    }

    public void attack(AutoCrystal.SwingTime breakSwing,
                       AutoCrystal.PlaceSwing godSwing,
                       int idOffset,
                       int packets,
                       int sleep) {
        if (sleep <= 0) {
            attackPackets(breakSwing, godSwing, idOffset, packets);
        } else {
            THREAD.schedule(() -> {
                        update();
                        attackPackets(breakSwing, godSwing, idOffset, packets);
                    },
                    sleep,
                    TimeUnit.MILLISECONDS);
        }
    }

    private void attackPackets(AutoCrystal.SwingTime breakSwing,
                               AutoCrystal.PlaceSwing godSwing,
                               int idOffset,
                               int packets) {
        for (int i = 0; i < packets; i++) {
            int id = highestID + idOffset + i;
            Entity entity = Util.mc.world.getEntityByID(id);
            if (entity == null || entity instanceof EntityEnderCrystal) {
                if (godSwing == AutoCrystal.PlaceSwing.Always
                        && breakSwing == AutoCrystal.SwingTime.Pre) {
                    Swing.Packet.swing(EnumHand.MAIN_HAND);
                }

                CPacketUseEntity packet = attackPacket(id);
                Util.mc.player.connection.sendPacket(packet);

                if (godSwing == AutoCrystal.PlaceSwing.Always && breakSwing == AutoCrystal.SwingTime.Post) {
                    Swing.Packet.swing(EnumHand.MAIN_HAND);
                }
            }
        }

        if (godSwing == AutoCrystal.PlaceSwing.Once) {
            Swing.Packet.swing(EnumHand.MAIN_HAND);
        }
    }

    private void checkID(int id) {
        if (id > highestID) {
            highestID = id;
        }
    }

}