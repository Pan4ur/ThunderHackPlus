package com.mrzak34.thunderhack.modules.player;

import com.mrzak34.thunderhack.modules.Module;

public class LiquidInteract
        extends Module {
    private static LiquidInteract INSTANCE = new LiquidInteract();

    public LiquidInteract() {
        super("LiquidInteract", "ставить блоки на воду", Module.Category.PLAYER);
        this.setInstance();
    }

    public static LiquidInteract getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new LiquidInteract();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }
}

