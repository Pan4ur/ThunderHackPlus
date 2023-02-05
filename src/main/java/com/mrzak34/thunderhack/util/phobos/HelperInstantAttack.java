package com.mrzak34.thunderhack.util.phobos;
import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.events.PacketEvent;
import com.mrzak34.thunderhack.mixin.ducks.ISPacketSpawnObject;
import com.mrzak34.thunderhack.modules.combat.AutoCrystal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.util.EnumHand;

import static com.mrzak34.thunderhack.util.Util.mc;
import static com.mrzak34.thunderhack.util.phobos.HelperRotation.acquire;

public class HelperInstantAttack
{
    public static void attack(AutoCrystal module,
                              SPacketSpawnObject packet,
                              PacketEvent.Receive event,
                              EntityEnderCrystal entityIn,
                              boolean slow)
    {
        attack(module, packet, event, entityIn, slow, true);
    }

    public static void attack(AutoCrystal module,
                              SPacketSpawnObject packet,
                              PacketEvent.Receive event,
                              EntityEnderCrystal entityIn,
                              boolean slow,
                              boolean allowAntiWeakness)
    {
        ((ISPacketSpawnObject) event.getPacket()).setAttacked(true);
        CPacketUseEntity p = new CPacketUseEntity(entityIn);
        WeaknessSwitch w;
        if (allowAntiWeakness)
        {
            w = HelperRotation.antiWeakness(module);
            if (w.needsSwitch())
            {
                if (w.getSlot() == -1 || !module.instantAntiWeak.getValue())
                {
                    return;
                }
            }
        }
        else
        {
            w = WeaknessSwitch.NONE;
        }

        int lastSlot = mc.player.inventory.currentItem;
        Runnable runnable = () ->
        {
            if (w.getSlot() != -1)
            {
                switch (module.antiWeaknessBypass.getValue()) {
                    case None:
                        CooldownBypass.None.switchTo(w.getSlot());
                        break;
                    case Pick:
                        CooldownBypass.Pick.switchTo(w.getSlot());
                        break;
                    case Slot:
                        CooldownBypass.Slot.switchTo(w.getSlot());
                        break;
                    case Swap:
                        CooldownBypass.Swap.switchTo(w.getSlot());
                        break;
                }
            }

            if (module.breakSwing.getValue() == AutoCrystal.SwingTime.Pre)
            {
                Swing.Packet.swing(EnumHand.MAIN_HAND);
            }

            mc.player.connection.sendPacket(p);

            if (module.breakSwing.getValue() == AutoCrystal.SwingTime.Post)
            {
                Swing.Packet.swing(EnumHand.MAIN_HAND);
            }

            if (w.getSlot() != -1)
            {
                switch (module.antiWeaknessBypass.getValue()) {
                    case None:
                        CooldownBypass.None.switchBack(lastSlot, w.getSlot());
                        break;
                    case Pick:
                        CooldownBypass.Pick.switchBack(lastSlot, w.getSlot());
                        break;
                    case Slot:
                        CooldownBypass.Slot.switchBack(lastSlot, w.getSlot());
                        break;
                    case Swap:
                        CooldownBypass.Swap.switchBack(lastSlot, w.getSlot());
                        break;
                }
            }
        };

        if (w.getSlot() != -1)
        {
            acquire(runnable);
        }
        else
        {
            runnable.run();
        }

        module.breakTimer.reset(slow ? module.slowBreakDelay.getValue()
                : module.breakDelay.getValue());

        event.addPostEvent(() ->
        {
            Entity entity = mc.world.getEntityByID(packet.getEntityID());
            if (entity instanceof EntityEnderCrystal)
            {
                module.setCrystal(entity);
            }
        });

        if (module.simulateExplosion.getValue())
        {
            HelperUtil.simulateExplosion(
                    module, packet.getX(), packet.getY(), packet.getZ());
        }

        if (module.pseudoSetDead.getValue())
        {
            event.addPostEvent(() ->
            {
                Entity entity = mc.world.getEntityByID(packet.getEntityID());
                if (entity != null)
                {
                    ((IEntity) entity).setPseudoDead(true);
                }
            });

            return;
        }

        if (module.instantSetDead.getValue())
        {
            event.setCanceled(true);
            mc.addScheduledTask(() ->
            {
                Entity entity = mc.world.getEntityByID(packet.getEntityID());
                if (entity instanceof EntityEnderCrystal)
                {
                    module.crystalRender.onSpawn((EntityEnderCrystal) entity);
                }

                if (!event.isCanceled())
                {
                    return;
                }

                EntityTracker.updateServerPosition(entityIn,
                        packet.getX(),
                        packet.getY(),
                        packet.getZ());
                Thunderhack.setDeadManager.setDead(entityIn);
            });
        }
    }

}