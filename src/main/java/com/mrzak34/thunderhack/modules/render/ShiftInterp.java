package com.mrzak34.thunderhack.modules.render;

import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;

public class ShiftInterp extends Module {

    private static ShiftInterp INSTANCE = new ShiftInterp();
    public Setting<Boolean> sleep = this.register(new Setting<>("Sleep", false));
    public Setting<Boolean> aboba = this.register(new Setting<>("aboba", false));

    public ShiftInterp() {
        super("ShiftInterp", "все игроки будут-на шифте", Category.RENDER);
        this.setInstance();
    }

    public static ShiftInterp getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ShiftInterp();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }


}
