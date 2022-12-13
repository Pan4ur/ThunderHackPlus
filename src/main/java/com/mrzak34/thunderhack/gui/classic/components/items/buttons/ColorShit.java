package com.mrzak34.thunderhack.gui.classic.components.items.buttons;

import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.RenderUtil;

import java.awt.*;

public class ColorShit extends Button{

    private final Setting setting;
    public ColorShit(Setting setting) {
        super(setting.getName());
        this.width = 15;
        this.setting = setting;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        RenderUtil.drawRect(this.x, this.y, this.x + (float) this.width + 7.4f, this.y + (float) this.height - 0.5f,new Color(1,1,1,0).getRGB());
        this.setHidden(!this.setting.isVisible());
    }



}
