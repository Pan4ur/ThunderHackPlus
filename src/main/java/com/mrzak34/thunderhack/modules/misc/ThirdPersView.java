package com.mrzak34.thunderhack.modules.misc;

import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;

public class ThirdPersView extends Module {

    public Setting<Integer> x = this.register(new Setting<>("x", 0, -180, 180));
    public Setting<Integer> y = this.register(new Setting<>("y", 0, -180, 180));
    public Setting<Float> z = this.register(new Setting<>("z", 1f, 0.1f, 5f));
    public ThirdPersView() {
        super("ThirdPersView", "ThirdPersView", Category.MISC);
    }


}
