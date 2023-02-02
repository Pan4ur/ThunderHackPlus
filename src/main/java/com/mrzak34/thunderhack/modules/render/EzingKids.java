package com.mrzak34.thunderhack.modules.render;

import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;

public class EzingKids extends Module{
    public static EzingKids INSTANCE;
    public EzingKids() {
        super("AkrienUser", "делает тебя мелким", Module.Category.RENDER);
        EzingKids.INSTANCE = this;
    }
}
