package com.mrzak34.thunderhack.gui.classic.components.items.buttons;

import com.mrzak34.thunderhack.setting.Setting;

public class PositionSelector extends Button{
    public Setting setting;

    public PositionSelector(Setting setting) {
        super(setting.getName());
        this.setting = setting;
        this.width = 15;
    }




}
