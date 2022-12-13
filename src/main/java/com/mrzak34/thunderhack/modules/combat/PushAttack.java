package com.mrzak34.thunderhack.modules.combat;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
public class PushAttack extends Module {
    public PushAttack() {
        super("PushAttack", "Грызть геплы-и бить одновременно", Category.COMBAT, true, false, false);
    }
    public Setting<Float> clickCoolDown = register(new Setting<>("clickCoolDown", 1.0F, 0.5F, 1.0F));


    @Override
    public void onUpdate() {
        if (mc.player.getCooledAttackStrength(0) == clickCoolDown.getValue() && mc.gameSettings.keyBindAttack.pressed) {
            mc.clickMouse();
        }
    }

}