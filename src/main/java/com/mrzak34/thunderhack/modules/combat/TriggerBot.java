package com.mrzak34.thunderhack.modules.combat;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.events.EventPreMotion;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.Timer;
import com.mrzak34.thunderhack.util.math.MathUtil;
import com.mrzak34.thunderhack.util.phobos.IEntityLivingBase;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.MobEffects;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static net.minecraft.util.math.MathHelper.clamp;

public class TriggerBot extends Module {
    public TriggerBot() {
        super("TriggerBot", "аттакует сущностей под прицелом", Category.COMBAT);
    }

    public final Setting<Boolean> criticals = register(new Setting<>("Criticals", true));
    public final Setting<Boolean> smartCrit = register(new Setting<>("OnlySpace", true,v-> criticals.getValue()));
    public final Setting<TimingMode> timingMode = register(new Setting("Timing", TimingMode.Default));
    public final Setting<Integer> minCPS = register(new Setting("MinCPS", 10, 1, 20,v -> timingMode.getValue() == TimingMode.Old));//(antiCheat);
    public final Setting<Integer> maxCPS = register(new Setting("MaxCPS", 12, 1, 20,v -> timingMode.getValue() == TimingMode.Old));//(antiCheat);
    public final Setting<Boolean> randomDelay = register(new Setting<>("RandomDelay", true,v-> timingMode.getValue() == TimingMode.Default));


    public enum TimingMode {
        Default, Old
    }
    public final Setting<Float> critdist = register(new Setting("FallDistance", 0.15f, 0.0f, 1.0f,v -> criticals.getValue()));;


    @SubscribeEvent
    public void onPreMotion(EventPreMotion e) {
        Entity entity = TriggerBot.mc.objectMouseOver.entityHit;
        if (canAttack(entity)) {
            TriggerBot.mc.playerController.attackEntity(TriggerBot.mc.player, entity);
            TriggerBot.mc.player.swingArm(EnumHand.MAIN_HAND);
        }
    }


    private boolean canAttack(Entity entity) {
        if(entity == null){
            return  false;
        }
        if(entity instanceof EntityEnderCrystal){
            return false;
        }
        boolean reasonForCancelCritical =
                mc.player.isPotionActive(MobEffects.SLOWNESS)
                        || mc.player.isOnLadder()
                        || (Aura.isInLiquid())
                        || mc.player.isInWeb
                        || (smartCrit.getValue() && (!mc.gameSettings.keyBindJump.isKeyDown()));


        if(timingMode.getValue() == TimingMode.Default) {
            if(!randomDelay.getValue()) {
                if (getCooledAttackStrength() < 0.9) {
                    return false;
                }
            } else {
                float delay = MathUtil.random(0.85f,1f);
                if (getCooledAttackStrength() < delay) {
                    return false;
                }
            }
        } else {
            final int CPS = (int) MathUtil.random(minCPS.getValue(), maxCPS.getValue());
            if (!oldTimer.passedMs((long) ((1000 + (MathUtil.random(1, 50) - MathUtil.random(1, 60) + MathUtil.random(1, 70))) / CPS))) {
                return false;
            }
        }

        if( criticals.getValue() && (mc.world.getBlockState(new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ)).getBlock() instanceof BlockLiquid && mc.world.getBlockState(new BlockPos(mc.player.posX, mc.player.posY + 1, mc.player.posZ)).getBlock() instanceof BlockAir && mc.player.fallDistance >= 0.08f)){
            return true;
        }

        if(criticals.getValue() && !reasonForCancelCritical) {
            boolean onFall = Aura.isBlockAboveHead() ? mc.player.fallDistance > 0 : mc.player.fallDistance >= critdist.getValue();
            return onFall && !mc.player.onGround;
        }
        oldTimer.reset();
        return true;
    }

    private final Timer oldTimer = new Timer();


    private float getCooledAttackStrength() {
        return clamp(((float)  ((IEntityLivingBase) mc.player).getTicksSinceLastSwing()) / getCooldownPeriod(), 0.0F, 1.0F);
    }
    public float getCooldownPeriod() {
        return (float)(1.0 / mc.player.getEntityAttribute(SharedMonsterAttributes.ATTACK_SPEED).getAttributeValue() * ( Thunderhack.moduleManager.getModuleByClass(com.mrzak34.thunderhack.modules.misc.Timer.class).isOn() ? 20f * Thunderhack.moduleManager.getModuleByClass(com.mrzak34.thunderhack.modules.misc.Timer.class).speed.getValue() : 20.0) );
    }

}
