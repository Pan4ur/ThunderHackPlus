package com.mrzak34.thunderhack.modules.render;

import com.mrzak34.thunderhack.modules.Module;

public class CustomEnchants extends Module{
    public CustomEnchants() {
        super("RainbowEnchants", "радужные зачары", Module.Category.RENDER, true, false, false);
        this.setInstance();
    }
    public static CustomEnchants getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CustomEnchants();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }
    private static CustomEnchants INSTANCE = new CustomEnchants();


}
