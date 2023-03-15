package com.mrzak34.thunderhack.util.phobos;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.Timer;
import net.minecraft.util.math.BlockPos;

public class DamageSyncHelper {
    private final DiscreteTimer discreteTimer = new GuardTimer(1000, 5);
    private final Timer timer = new Timer();
    private final Setting<Integer> syncDelay;
    private final Setting<Boolean> discrete;
    private final Setting<Boolean> danger;
    private final Confirmer confirmer;

    private float lastDamage;

    public DamageSyncHelper(
            Setting<Boolean> discrete,
            Setting<Integer> syncDelay,
            Setting<Boolean> danger) {
        this.danger = danger;
        this.confirmer = Confirmer.createAndSubscribe();
        this.syncDelay = syncDelay;
        this.discrete = discrete;
        this.discreteTimer.reset(syncDelay.getValue());
    }

    public void setSync(BlockPos pos, float damage, boolean newVer) {
        int placeTime = (int) (Thunderhack.serverManager.getPing() / 2.0 + 1);
        confirmer.setPos(pos.toImmutable(), newVer, placeTime);
        lastDamage = damage;

        if (discrete.getValue() && discreteTimer.passed(syncDelay.getValue())) {
            discreteTimer.reset(syncDelay.getValue());
        } else if (!discrete.getValue() && timer.passed(syncDelay.getValue())) {
            timer.reset();
        }
    }

    public boolean isSyncing(float damage,
                             boolean damageSync) {
        return damageSync
                && (!danger.getValue())
                && confirmer.isValid()
                && damage <= lastDamage
                && (discrete.getValue()
                && !discreteTimer.passed(syncDelay.getValue())
                || !discrete.getValue()
                && !timer.passed(syncDelay.getValue()));
    }

    public Confirmer getConfirmer() {
        return confirmer;
    }

}