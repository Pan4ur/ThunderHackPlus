package com.mrzak34.thunderhack.modules.movement;

import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;

public class Sprint extends Module {

    public Sprint() {
        super("Sprint", "автоматически-спринтится", Category.MOVEMENT, true, false, false);
    }

    public enum mode {
        legit, Rage;
    }

    private Setting<mode> Mode = register(new Setting("Mode", mode.legit));



    @Override
    public void onUpdate() {
        if(nullCheck())return;
        if (Mode.getValue() == mode.legit) {
            if (mc.gameSettings.keyBindForward.isKeyDown()) {
                mc.player.setSprinting(true);
            }
        } else {
            mc.player.setSprinting(true);
        }
    }

    @Override
    public String getDisplayInfo() {
        return  Mode.getValue().toString();
    }
}
