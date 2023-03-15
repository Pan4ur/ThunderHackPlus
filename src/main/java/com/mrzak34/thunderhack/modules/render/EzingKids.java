package com.mrzak34.thunderhack.modules.render;

import com.mrzak34.thunderhack.modules.Module;

public class EzingKids extends Module {
    public static EzingKids INSTANCE;

    public EzingKids() {
        super("AkrienUser", "делает тебя мелким", Module.Category.RENDER);
        EzingKids.INSTANCE = this;
    }
}
