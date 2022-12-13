package com.mrzak34.thunderhack.modules.render;

import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.ColorSetting;
import com.mrzak34.thunderhack.setting.Setting;

public class Ambience extends Module {
    public Ambience() {
        super("Ambience", "изменяет цвет-окружения", Category.RENDER, true, false, false);
    }

    public final Setting<ColorSetting> colorLight = this.register(new Setting<>("Color Light", new ColorSetting(0x8800FF00)));

}
