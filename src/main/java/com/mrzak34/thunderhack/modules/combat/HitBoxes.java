package com.mrzak34.thunderhack.modules.combat;

import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;

public class HitBoxes extends Module {
    public HitBoxes() {
        super("HitBoxes", "Увеличивает хитбоксы", Category.COMBAT);
    }

    public  Setting<Float> expand = register(new Setting("Value", 0.0f, 0.0f, 5.0f));

}
