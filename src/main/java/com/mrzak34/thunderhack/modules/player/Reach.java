package com.mrzak34.thunderhack.modules.player;

import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;

public class Reach extends Module
{
    private static Reach INSTANCE;
    public Setting<Float> add  = this.register(new Setting<>("Add", 3.0f,0f,7f));

    public Reach() {
        super("Reach", "Увеличивает дальность-взаимодействий (и член)", Module.Category.PLAYER);
        this.setInstance();
    }

    public static Reach getInstance() {
        if (Reach.INSTANCE == null) {
            Reach.INSTANCE = new Reach();
        }
        return Reach.INSTANCE;
    }

    private void setInstance() {
        Reach.INSTANCE = this;
    }

    public String getDisplayInfo() {
        return this.add.getValue().toString();
    }

    static {
        Reach.INSTANCE = new Reach();
    }
}
////set 1,1:5,1:6,98