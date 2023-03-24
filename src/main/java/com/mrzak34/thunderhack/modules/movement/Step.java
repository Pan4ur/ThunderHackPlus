package com.mrzak34.thunderhack.modules.movement;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.events.StepEvent;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.modules.player.FreeCam;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.Timer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityLlama;
import net.minecraft.entity.passive.EntityMule;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;


public class Step extends Module {

    private final Timer stepTimer = new Timer();
    public Setting<Float> height = register(new Setting("Height", 2.0F, 1F, 2.5F));
    public Setting<Boolean> entityStep = this.register(new Setting<>("EntityStep", false));
    public Setting<Boolean> useTimer = this.register(new Setting<>("Timer", true));
    public Setting<Boolean> strict = this.register(new Setting<>("Strict", false));
    public Setting<Integer> stepDelay = register(new Setting("StepDelay", 200, 0, 1000));
    private final Setting<Mode> mode = this.register(new Setting<>("Mode", Mode.NORMAL));
    private boolean timer;
    private Entity entityRiding;
    /**
     * @author Doogie13, linustouchtips, aesthetical
     * @since 12/27/2021
     */

    public Step() {
        super("Step", "ходить по блокам 1 или 2 блока", Category.MOVEMENT);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        mc.player.stepHeight = 0.6F;
        if (entityRiding != null) {
            if (entityRiding instanceof EntityHorse || entityRiding instanceof EntityLlama || entityRiding instanceof EntityMule || entityRiding instanceof EntityPig && entityRiding.isBeingRidden() && ((EntityPig) entityRiding).canBeSteered()) {
                entityRiding.stepHeight = 1;
            } else {
                entityRiding.stepHeight = 0.5F;
            }
        }
    }

    @Override
    public void onUpdate() {
        if (mc.player.capabilities.isFlying || Thunderhack.moduleManager.getModuleByClass(FreeCam.class).isOn()) {
            mc.player.stepHeight = 0.6F;
            return;
        }
        if (Jesus.isInLiquid()) {
            mc.player.stepHeight = 0.6F;
            return;
        }
        if (timer && mc.player.onGround) {
            Thunderhack.TICK_TIMER = 1f;
            timer = false;
        }

        if (mc.player.onGround && stepTimer.passedMs(stepDelay.getValue())) {
            if (mc.player.isRiding() && mc.player.getRidingEntity() != null) {
                entityRiding = mc.player.getRidingEntity();
                if (entityStep.getValue()) {
                    mc.player.getRidingEntity().stepHeight = height.getValue().floatValue();
                }
            } else {
                mc.player.stepHeight = height.getValue().floatValue();
            }
        } else {
            if (mc.player.isRiding() && mc.player.getRidingEntity() != null) {
                entityRiding = mc.player.getRidingEntity();
                if (entityRiding != null) {
                    if (entityRiding instanceof EntityHorse || entityRiding instanceof EntityLlama || entityRiding instanceof EntityMule || entityRiding instanceof EntityPig && entityRiding.isBeingRidden() && ((EntityPig) entityRiding).canBeSteered()) {
                        entityRiding.stepHeight = 1;
                    } else {
                        entityRiding.stepHeight = 0.5F;
                    }
                }
            } else {
                mc.player.stepHeight = 0.6F;
            }
        }
    }

    @SubscribeEvent
    public void onStep(StepEvent event) {
        if (mode.getValue().equals(Mode.NORMAL)) {
            double stepHeight = event.getAxisAlignedBB().minY - mc.player.posY;
            if (stepHeight <= 0 || stepHeight > height.getValue()) {
                return;
            }
            double[] offsets = getOffset(stepHeight);
            if (offsets != null && offsets.length > 1) {
                if (useTimer.getValue()) {
                    Thunderhack.TICK_TIMER = 1F / offsets.length;
                    timer = true;
                }
                for (double offset : offsets) {
                    mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + offset, mc.player.posZ, false));
                }
            }
            stepTimer.reset();
        }
    }

    public double[] getOffset(double height) {
        if (height == 0.75) {
            if (strict.getValue()) {
                return new double[]{0.42, 0.753, 0.75};
            } else {
                return new double[]{0.42, 0.753};
            }
        } else if (height == 0.8125) {
            if (strict.getValue()) {
                return new double[]{0.39, 0.7, 0.8125};
            } else {
                return new double[]{0.39, 0.7};
            }
        } else if (height == 0.875) {
            if (strict.getValue()) {
                return new double[]{0.39, 0.7, 0.875};
            } else {
                return new double[]{0.39, 0.7};
            }
        } else if (height == 1) {
            if (strict.getValue()) {
                return new double[]{0.42, 0.753, 1};
            } else {
                return new double[]{0.42, 0.753};
            }
        } else if (height == 1.5) {
            return new double[]{0.42, 0.75, 1.0, 1.16, 1.23, 1.2};
        } else if (height == 2) {
            return new double[]{0.42, 0.78, 0.63, 0.51, 0.9, 1.21, 1.45, 1.43};
        } else if (height == 2.5) {
            return new double[]{0.425, 0.821, 0.699, 0.599, 1.022, 1.372, 1.652, 1.869, 2.019, 1.907};
        }

        return null;
    }

    public enum Mode {
        NORMAL,
        VANILLA
    }
}

