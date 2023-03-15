package com.mrzak34.thunderhack.modules.client;

import com.mrzak34.thunderhack.gui.thundergui2.ThunderGui2;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.ColorSetting;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.Util;

import java.awt.*;

public class ThunderHackGui extends Module {
    private static ThunderHackGui INSTANCE = new ThunderHackGui();
    public final Setting<ColorSetting> onColor1 = this.register(new Setting<>("OnColor1", new ColorSetting(new Color(71, 0, 117, 255).getRGB())));
    public final Setting<ColorSetting> onColor2 = this.register(new Setting<>("OnColor2", new ColorSetting(new Color(32, 1, 96, 255).getRGB())));


    public Setting<Float> scrollSpeed = register(new Setting("ScrollSpeed", 0.2f, 0.1F, 1.0F));


    public ThunderHackGui() {
        super("ThunderGui", "новый клик гуи", Category.CLIENT);
        this.setInstance();
    }

    public static ThunderHackGui getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ThunderHackGui();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @Override
    public void onEnable() {
        Util.mc.displayGuiScreen(ThunderGui2.getThunderGui());
        disable();
    }

    public Color getColorByTheme(int id) {
        switch (id) {
            case 0:
                return new Color(37, 27, 41, 250); // Основная плита
            case 1:
                return new Color(50, 35, 60, 250); // плита лого
            case 2:
                return new Color(-1); // надпись THUNDERHACK+, белые иконки
            case 3:
                return new Color(0x656565); // версия под надписью
            case 4:
                return new Color(50, 35, 60, 178); // плита под категориями, выбор режима гуи (выкл)
            case 5:
                return new Color(133, 93, 162, 178); // выбор режима гуи (вкл)
            case 6:
                return new Color(88, 64, 107, 178); // цвет разделителя качели выбора режима
            case 7:
                return new Color(25, 20, 30, 255); // цвет плиты настроек
            case 8:
                return new Color(0x656565); // версия под надписью
            case 9:
                return new Color(50, 35, 60, 178); // плита под категориями
        }
        return new Color(37, 27, 41, 250);
    }

}
