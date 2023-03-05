package com.mrzak34.thunderhack.modules.client;

import com.mrzak34.thunderhack.gui.thundergui2.ThunderGui2;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.ColorSetting;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.Util;

import java.awt.*;

public class ThunderHackGui extends Module {
    public ThunderHackGui() {
        super("ThunderGui", "новый клик гуи",Category.CLIENT);
        this.setInstance();
    }


    public final Setting<ColorSetting> onColor1 = this.register(new Setting<>("OnColor1", new ColorSetting(new Color(71, 0, 117, 255).getRGB())));
    public final Setting<ColorSetting> onColor2 = this.register(new Setting<>("OnColor2", new ColorSetting(new Color(32, 1, 96, 255).getRGB())));


    public Setting<Float> scrollSpeed = register(new Setting("ScrollSpeed", 0.2f, 0.1F, 1.0F));



    private void setInstance() {
        INSTANCE = this;
    }
    private static ThunderHackGui INSTANCE = new ThunderHackGui();

    @Override
    public void onEnable() {
           Util.mc.displayGuiScreen(ThunderGui2.getThunderGui());
           disable();
    }



    public static ThunderHackGui getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ThunderHackGui();
        }
        return INSTANCE;
    }

}
