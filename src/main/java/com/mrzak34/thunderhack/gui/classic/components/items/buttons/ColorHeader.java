package com.mrzak34.thunderhack.gui.classic.components.items.buttons;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.gui.classic.ClassicGui;
import com.mrzak34.thunderhack.gui.thundergui.fontstuff.FontRender;
import com.mrzak34.thunderhack.modules.client.ClickGui;
import com.mrzak34.thunderhack.setting.ColorSettingHeader;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.RenderUtil;

public class ColorHeader extends Button {
    private final Setting<ColorSettingHeader> header;


    public ColorHeader(Setting setting) {
        super(setting.getName());
        this.header = setting;
        this.width = 15;
    }


    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        RenderUtil.drawRect(this.x, this.y, this.x + (float) this.width + 7.4f, this.y + (float) this.height - 0.5f, Thunderhack.moduleManager.getModuleByClass(ClickGui.class).hoverAlpha.getValue());
        FontRender.drawString5(this.getName(), this.x + 2.3f, this.y - 1.7f - (float) ClassicGui.getClickGui().getTextOffset(), -1 );
    }

    @Override
    public void update() {
        this.setHidden(!this.header.isVisible());
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (this.isHovering(mouseX, mouseY)) {
            getParentSetting().getValue().setOpenedCSH(!getParentSetting().getValue().getStateCSH());
        }
    }

    @Override
    public float getHeight() {
        return 14;
    }

    public Setting<ColorSettingHeader> getParentSetting() {
        return header;
    }
}
