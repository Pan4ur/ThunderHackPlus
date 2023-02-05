package com.mrzak34.thunderhack.modules.client;

import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.ColorSetting;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.Util;
import com.mrzak34.thunderhack.gui.thundergui.ThunderGui;

public class ThunderHackGui extends Module {
    public ThunderHackGui() {
        super("ThunderGui", "новый клик гуи",Category.CLIENT);
        this.setInstance();
    }


    public final Setting<ColorSetting> buttsColor = this.register(new Setting<>("ButtonsColor", new ColorSetting(-955051502)));
    public final Setting<ColorSetting> catcolorinmodule = this.register(new Setting<>("CatColor", new ColorSetting(-162567959)));

    public final Setting<ColorSetting> LeftsideColor = this.register(new Setting<>("Leftside Color", new ColorSetting(-1390009051)));
    public final Setting<ColorSetting> thplate = this.register(new Setting<>("thplate Color", new ColorSetting(-652291704)));

    public Setting<Integer> blurstr = this.register(new Setting<Integer>("blurstr", 100, 0, 100));


    private void setInstance() {
        INSTANCE = this;
    }
    private static ThunderHackGui INSTANCE = new ThunderHackGui();
    @Override
    public void onEnable() {
        Util.mc.displayGuiScreen(ThunderGui.getThunderGui());
    }

    public static ThunderHackGui getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ThunderHackGui();
        }
        return INSTANCE;
    }

}
