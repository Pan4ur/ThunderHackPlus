package com.mrzak34.thunderhack.modules.combat;

import com.mrzak34.thunderhack.command.Command;
import com.mrzak34.thunderhack.event.events.AttackEvent;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.event.events.PacketEvent;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.Timer;
import com.mrzak34.thunderhack.util.dV;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;


public class Criticals
        extends Module {

    public Criticals() {
        super("Criticals", "Каждый удар станет-критом", Category.COMBAT, true, false, false);
    }


    public Setting<Mode> mode = this.register(new Setting<>("Mode", Mode.FunnyGame));

    private enum Mode {
        OldNCP, Strict, Matrix, FunnyGame,Deadcode
    }


    public static boolean e() {
        return Criticals.z() || Criticals.A();
    }

    private static boolean z() {
        return mc.player.isInWater() || mc.player.isInLava() || mc.player.isOnLadder() || mc.player.isRiding();
    }

    private static  boolean A() {
        return mc.player.isInWeb && dV.a();
    }

    @SubscribeEvent
    public void onAttack(AttackEvent e){
        if(mode.getValue() == Mode.Deadcode){
            if(!(e.getEntity() instanceof EntityPlayer)){
                return;
            }
            if ( ((EntityPlayer) e.getEntity()).hurtTime >= 7) {
                return;
            }
            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.0625, mc.player.posZ,true));
            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
            mc.player.onCriticalHit(e.getEntity());
        }
    }
    
    @SubscribeEvent
    public void onAttackEntity(PacketEvent.SendPost f4h2) {
        if (fullNullCheck()) {
            return;
        }
        if (mode.getValue() == Mode.FunnyGame) {
            return;
        }

        if (!(f4h2.getPacket() instanceof CPacketUseEntity)) {
            return;
        }
            block14:
            {
                block13:
                {
                    if (f4h2.getStage() == 1) {
                        if (mc.player.fallDistance == 1337.0f) {
                            mc.player.fallDistance = 0.0f;
                        }
                        return;
                    }
                    if (mc.player.fallDistance > 0.0f) {
                        return;
                    }
                    if (!mc.player.onGround || !mc.player.collidedVertically) break block13;
                    if (mc.player.isInLava()) break block13;
                    if (!mc.player.isInWater()) break block14;
                }
                return;
            }
            switch (mode.getValue()) {
                case Strict: {
                    mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.11, mc.player.posZ, false));
                    mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.1100013579, mc.player.posZ, false));
                    mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.3579E-6, mc.player.posZ, false));
                    break;
                }
                case OldNCP: {
                    mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.05000000074505806, mc.player.posZ, false));
                    mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
                    mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.012511000037193298, mc.player.posZ, false));
                    mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
                    break;
                }
            }
            mc.player.fallDistance = 1337.0f;

    }

    boolean cancelSomePackets;
    Timer timer = new Timer();

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPacketSend(PacketEvent.Send e) {
        if(mode.getValue() != Mode.FunnyGame){
            return;
        }
        if (e.getPacket() instanceof CPacketUseEntity) {
            if (mc.player.onGround) {
                CPacketUseEntity attack = e.getPacket();
                if (attack.getAction() == CPacketUseEntity.Action.ATTACK) {
                    if (mc.player.collidedVertically && timer.passedMs(400)) {
                        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.0627, mc.player.posZ, false));
                        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
                        Entity entity = attack.getEntityFromWorld(mc.world);
                        if (entity != null) {
                            mc.player.onCriticalHit(entity);
                        }
                        timer.reset();
                        cancelSomePackets = true;
                    }
                }
            }
        } else if (e.getPacket() instanceof CPacketPlayer) {
            if (cancelSomePackets) {
                cancelSomePackets = false;
                e.setCanceled(true);
            }
        }

    }
}

