package com.mrzak34.thunderhack.util.phobos;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.events.UpdateEntitiesEvent;
import com.mrzak34.thunderhack.mixin.ducks.IEntityPlayer;
import com.mrzak34.thunderhack.modules.combat.AutoCrystal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

import static com.mrzak34.thunderhack.util.Util.mc;

public class ExtrapolationHelper {

    public ExtrapolationHelper(AutoCrystal module) {
    }

    public static void onUpdateEntity(UpdateEntitiesEvent e) {
        for (EntityPlayer player : mc.world.playerEntities) {
            MotionTracker tracker = ((IEntityPlayer) player).getMotionTrackerT();
            MotionTracker breakTracker = ((IEntityPlayer) player).getBreakMotionTrackerT();
            MotionTracker blockTracker = ((IEntityPlayer) player).getBlockMotionTrackerT();
            if (player.getHealth() <= 0 || mc.player.getDistanceSq(player) > 400
                    || !Thunderhack.moduleManager.getModuleByClass(AutoCrystal.class).selfExtrapolation.getValue()
                    && player.equals(mc.player)) {
                if (tracker != null) {
                    tracker.active = false;
                }

                if (breakTracker != null) {
                    breakTracker.active = false;
                }

                if (blockTracker != null) {
                    blockTracker.active = false;
                }

                continue;
            }

            if (tracker == null && Thunderhack.moduleManager.getModuleByClass(AutoCrystal.class).extrapol.getValue() != 0) {
                tracker = new MotionTracker(mc.world, player);
                ((IEntityPlayer) player).setMotionTrackerT(tracker);
            }

            if (breakTracker == null && Thunderhack.moduleManager.getModuleByClass(AutoCrystal.class).bExtrapol.getValue() != 0) {
                breakTracker = new MotionTracker(mc.world, player);
                ((IEntityPlayer) player).setBreakMotionTrackerT(breakTracker);
            }

            if (blockTracker == null && Thunderhack.moduleManager.getModuleByClass(AutoCrystal.class).blockExtrapol.getValue() != 0) {
                blockTracker = new MotionTracker(mc.world, player);
                ((IEntityPlayer) player).setBlockMotionTrackerT(blockTracker);
            }

            updateTracker(tracker, Thunderhack.moduleManager.getModuleByClass(AutoCrystal.class).extrapol.getValue());
            updateTracker(breakTracker, Thunderhack.moduleManager.getModuleByClass(AutoCrystal.class).bExtrapol.getValue());
            updateTracker(blockTracker, Thunderhack.moduleManager.getModuleByClass(AutoCrystal.class).blockExtrapol.getValue());
        }
    }

    private static void updateTracker(MotionTracker tracker, int ticks) {
        if (tracker == null) {
            return;
        }

        tracker.active = false;
        tracker.copyLocationAndAnglesFrom(tracker.tracked);
        tracker.gravity = Thunderhack.moduleManager.getModuleByClass(AutoCrystal.class).gravityExtrapolation.getValue();
        tracker.gravityFactor = Thunderhack.moduleManager.getModuleByClass(AutoCrystal.class).gravityFactor.getValue();
        tracker.yPlusFactor = Thunderhack.moduleManager.getModuleByClass(AutoCrystal.class).yPlusFactor.getValue();
        tracker.yMinusFactor = Thunderhack.moduleManager.getModuleByClass(AutoCrystal.class).yMinusFactor.getValue();
        for (tracker.ticks = 0; tracker.ticks < ticks; tracker.ticks++) {
            tracker.updateFromTrackedEntity();
        }

        tracker.active = true;
    }

    public MotionTracker getTrackerFromEntity(Entity player) {
        return ((IEntityPlayer) player).getMotionTrackerT();
    }

    public MotionTracker getBreakTrackerFromEntity(Entity player) {
        return ((IEntityPlayer) player).getBreakMotionTrackerT();
    }

    public MotionTracker getBlockTracker(Entity player) {
        return ((IEntityPlayer) player).getBlockMotionTrackerT();
    }

}