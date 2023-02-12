package com.mrzak34.thunderhack.modules.movement;

import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;

public class KeepSprint extends Module {

    public KeepSprint() {
        super("KeepSprint", "Не сбивать скорость при ударе", Category.MOVEMENT);
    }
    public final Setting<Boolean> sprint = register(new Setting<>("Sprint", true));
    public final Setting<Float> motion = register(new Setting("motion", 1f, 0f, 1f));
}
