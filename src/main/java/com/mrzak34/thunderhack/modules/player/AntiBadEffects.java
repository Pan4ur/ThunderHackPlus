package com.mrzak34.thunderhack.modules.player;

import com.mrzak34.thunderhack.modules.Module;
import net.minecraft.init.MobEffects;


public class AntiBadEffects extends Module {
    public AntiBadEffects() {
        super("AntiBadEffects", "уберает плохие эффекты", Category.PLAYER);
    }

    @Override
    public void onUpdate() {
        if (mc.player.isPotionActive(MobEffects.BLINDNESS)) {
            mc.player.removeActivePotionEffect(MobEffects.BLINDNESS);
        }
        if (mc.player.isPotionActive(MobEffects.NAUSEA)) {
            mc.player.removeActivePotionEffect(MobEffects.NAUSEA);
        }
        if (mc.player.isPotionActive(MobEffects.MINING_FATIGUE)) {
            mc.player.removeActivePotionEffect(MobEffects.MINING_FATIGUE);
        }
        if (mc.player.isPotionActive(MobEffects.LEVITATION)) {
            mc.player.removeActivePotionEffect(MobEffects.LEVITATION);
        }
        if (mc.player.isPotionActive(MobEffects.SLOWNESS)) {
            mc.player.removeActivePotionEffect(MobEffects.SLOWNESS);
        }
    }
}
