package com.mrzak34.thunderhack.modules.render;

import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.ColorSetting;
import com.mrzak34.thunderhack.setting.Setting;

public class Models extends Module {
    public Setting<Boolean> onlySelf = register(new Setting<>("onlySelf", false));
    public Setting<Boolean> friends = register(new Setting<>("friends", false));
    public Setting<Boolean> friendHighlight = register(new Setting<>("friendHighLight", false));
    public Setting<mode> Mode = register(new Setting("Mode", mode.Freddy));
    public Setting<ColorSetting> eyeColor = this.register(new Setting<>("eyeColor", new ColorSetting(-2009289807)));
    public Setting<ColorSetting> bodyColor = this.register(new Setting<>("bodyColor", new ColorSetting(-2009289807)));
    public Setting<ColorSetting> legsColor = this.register(new Setting<>("legsColor", new ColorSetting(-2009289807)));
    public Models() {
        super("Models", "ну типа модельки", Category.RENDER);
    }
    public enum mode {
        Amogus, Rabbit, Freddy
    }


}


