package com.mrzak34.thunderhack.modules.render;

import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;

public class EzingKids extends Module{
    public static EzingKids INSTANCE;
    public EzingKids() {
        super("TEST", "делает тебя мелким", Module.Category.RENDER, true, false, false);
        EzingKids.INSTANCE = this;
    }

    public Setting<Float> scale = (Setting<Float>)this.register(new Setting("Scale", 0.1f, 0.1f, 1.0f));
    public Setting<Float> translatey = (Setting<Float>)this.register(new Setting("translate", 0f, -5f, 20.0f));

    public Setting<Boolean> onlyme = this.register(new Setting<Boolean>("onlyme", true));

}
