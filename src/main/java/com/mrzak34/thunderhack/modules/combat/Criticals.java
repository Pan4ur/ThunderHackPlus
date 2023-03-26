package com.mrzak34.thunderhack.modules.combat;

import com.mrzak34.thunderhack.events.AttackEvent;
import com.mrzak34.thunderhack.events.PacketEvent;
import com.mrzak34.thunderhack.mixin.mixins.ICPacketPlayer;
import com.mrzak34.thunderhack.mixin.mixins.IEntityPlayerSP;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.Timer;
import com.mrzak34.thunderhack.mixin.ducks.IEntity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static com.mrzak34.thunderhack.modules.combat.Aura.interpolateRandom;


public class Criticals extends Module {

    public Setting<Mode> mode = this.register(new Setting<>("Mode", Mode.FunnyGame));
    Timer timer = new Timer();
    boolean cancelSomePackets;

    public Criticals() {
        super("Criticals", "Каждый удар станет-критом", Category.COMBAT);
    }

    @SubscribeEvent
    public void onAttack(AttackEvent e) {
        if (e.getStage() == 1) {
            return;
        }

        boolean reasonToReturn = mc.player.fallDistance > 0.08f || mc.player.isInLava() || ((IEntity)mc.player).isInWeb() || mc.player.isRiding() || mc.player.isOnLadder() || e.getEntity() instanceof EntityEnderCrystal;

        if (reasonToReturn) {
            return;
        }
        if (mode.getValue() == Mode.Deadcode) {
            if (!(e.getEntity() instanceof EntityPlayer)) {
                return;
            }
            if (((EntityPlayer) e.getEntity()).hurtTime >= 7) {
                return;
            }
            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.0625, mc.player.posZ, true));
            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
            mc.player.onCriticalHit(e.getEntity());
        }
        if (mode.getValue() == Mode.Nurik) {
            if (mc.player.onGround) {
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.0625 + (double) interpolateRandom(-0.09F, 0.09F), mc.player.posZ, false));
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + (0.001 - Math.random() / 10000.0), mc.player.posZ, false));
            }
        }
        if (mode.getValue() == Mode.FunnyGame) {
            if (mc.player.collidedVertically && timer.passedMs(400)) {
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.0627, mc.player.posZ, false));
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
                mc.player.onCriticalHit(e.getEntity());
                timer.reset();
                cancelSomePackets = true;
            }
        }
        if (mode.getValue() == Mode.Strict) {
            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.11, mc.player.posZ, false));
            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.1100013579, mc.player.posZ, false));
            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.3579E-6, mc.player.posZ, false));
        }
        if (mode.getValue() == Mode.OldNCP) {
            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.05000000074505806, mc.player.posZ, false));
            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.012511000037193298, mc.player.posZ, false));
            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPacketSend(PacketEvent.Send e) {
        if (e.getPacket() instanceof CPacketPlayer) {
            if (cancelSomePackets) {
                cancelSomePackets = false;
                e.setCanceled(true);
            }
        }
    }

    private enum Mode {
        OldNCP, Strict, Nurik, FunnyGame, Deadcode
    }

}

