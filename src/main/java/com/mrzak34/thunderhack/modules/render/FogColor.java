package com.mrzak34.thunderhack.modules.render;

import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.ColorSetting;
import com.mrzak34.thunderhack.setting.Setting;

public class FogColor extends Module {
    public final Setting<ColorSetting> color = this.register(new Setting<>("Color", new ColorSetting(0x8800FF00)));
    public Setting<Float> distance = register(new Setting("Distance", 1f, 0f, 10f));
    public FogColor() {
        super("FogColor", "меняет цвет тумана", Category.RENDER);
    }

}
