package com.mrzak34.thunderhack.util.phobos;

import com.mrzak34.thunderhack.modules.combat.AutoCrystal;
import com.mrzak34.thunderhack.setting.Setting;

public class WeaknessHelper {
    private final Setting<AutoCrystal.AntiWeakness> antiWeakness;
    private final Setting<Integer> cooldown;
    private boolean weaknessed;

    public WeaknessHelper(Setting<AutoCrystal.AntiWeakness> antiWeakness,
                          Setting<Integer> cooldown) {
        this.antiWeakness = antiWeakness;
        this.cooldown = cooldown;
    }

    /**
     * Updates if we are weaknessed. We poll this since
     * we multithread and don't want problems with the
     * PotionMap.
     */
    public void updateWeakness() {
        weaknessed = !DamageUtil.canBreakWeakness(true);
    }

    /**
     * @return <tt>true</tt> if we are weaknessed.
     */
    public boolean isWeaknessed() {
        return weaknessed;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean canSwitch() {
        return antiWeakness.getValue() == AutoCrystal.AntiWeakness.Switch
                && cooldown.getValue() == 0
                && weaknessed;
    }

}