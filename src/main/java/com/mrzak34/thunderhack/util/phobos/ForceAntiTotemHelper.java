package com.mrzak34.thunderhack.util.phobos;

import com.mrzak34.thunderhack.setting.Setting;
import net.minecraft.util.math.BlockPos;

public class ForceAntiTotemHelper {
    private final DamageSyncHelper damageSyncHelper;
    private final Setting<Integer> placeConfirm;
    private final Setting<Integer> breakConfirm;
    private BlockPos pos;

    public ForceAntiTotemHelper(
            Setting<Boolean> discrete,
            Setting<Integer> syncDelay,
            Setting<Integer> placeConfirm,
            Setting<Integer> breakConfirm,
            Setting<Boolean> dangerForce) {
        this.damageSyncHelper = new DamageSyncHelper(
                discrete,
                syncDelay,
                dangerForce);
        this.placeConfirm = placeConfirm;
        this.breakConfirm = breakConfirm;
    }

    public void setSync(BlockPos pos, boolean newVer) {
        damageSyncHelper.setSync(pos, Float.MAX_VALUE, newVer);
        this.pos = pos;
    }

    public boolean isForcing(boolean damageSync) {
        Confirmer c = damageSyncHelper.getConfirmer();
        if (c.isValid() // This is mostly to confirm place/break
                && !(c.isPlaceConfirmed(placeConfirm.getValue())
                && c.isBreakConfirmed(breakConfirm.getValue()))) {
            // Could've been set to not valid
            return c.isValid();
        }

        return damageSyncHelper.isSyncing(0.0f, damageSync);
    }

    public BlockPos getPos() {
        return pos;
    }

}