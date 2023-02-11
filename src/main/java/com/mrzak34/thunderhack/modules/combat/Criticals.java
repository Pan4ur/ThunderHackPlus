package com.mrzak34.thunderhack.modules.combat;

import com.mrzak34.thunderhack.events.AttackEvent;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.events.PacketEvent;
import com.mrzak34.thunderhack.modules.movement.Jesus;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.Timer;
import com.mrzak34.thunderhack.util.phobos.IEntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.world.GameType;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static com.mrzak34.thunderhack.modules.combat.Aura.interpolateRandom;
import static com.mrzak34.thunderhack.util.MovementUtil.isMoving;


public class Criticals
        extends Module {

    public Criticals() {
        super("Criticals", "Каждый удар станет-критом", Category.COMBAT);
    }

    Timer timer = new Timer();


    public Setting<Mode> mode = this.register(new Setting<>("Mode", Mode.FunnyGame));

    private enum Mode {
        OldNCP, Strict, Nurik, FunnyGame,Deadcode
    }


    public static boolean e() {
        return ( mc.player.isInWater() || mc.player.isInLava() || mc.player.isOnLadder() || mc.player.isRiding()) || (mc.player.isInWeb && (mc.player.moveForward != 0.0f || mc.player.moveStrafing != 0.0f));
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
        if(mode.getValue() == Mode.Nurik){
            if (mc.player.onGround) {
                double[] var8 = new double[]{0.0625 + (double)interpolateRandom(-0.09F, 0.09F), 0.001 - Math.random() / 10000.0};
                for (double var12 : var8) {
                    mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + var12, mc.player.posZ, false));
                }
            }
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
    }

    boolean cancelSomePackets;

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

