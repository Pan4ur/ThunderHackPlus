package com.mrzak34.thunderhack.modules.render;

import com.mrzak34.thunderhack.modules.Module;

public class CustomEnchants extends Module {
    private static CustomEnchants INSTANCE = new CustomEnchants();

    public CustomEnchants() {
        super("RainbowEnchants", "радужные зачары", Module.Category.RENDER);
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


}
