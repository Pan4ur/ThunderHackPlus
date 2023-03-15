package com.mrzak34.thunderhack.gui.thundergui2.components;

import com.mrzak34.thunderhack.gui.fontstuff.FontRender;
import com.mrzak34.thunderhack.gui.thundergui2.ThunderGui2;
import com.mrzak34.thunderhack.setting.Parent;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.render.RenderUtil;

import java.awt.*;

public class ParentComponent extends SettingElement {


    public ParentComponent(Setting setting) {
        super(setting);
        Parent parent = (Parent) setting.getValue();
        parent.setExtended(true);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);
        if ((getY() > ThunderGui2.getInstance().main_posY + ThunderGui2.getInstance().height) || getY() < ThunderGui2.getInstance().main_posY) {
            return;
        }
        FontRender.drawCentString6(getSetting().getName(), (float) (getX() + width / 2f), (float) getY() + 5, new Color(0xB0FFFFFF, true).getRGB());
        RenderUtil.draw2DGradientRect((float) (getX() + 10), (float) (getY() + 6), (float) ((getX() + width / 2f) - 20), (float) (getY() + 7), new Color(0x0FFFFFF, true).getRGB(), new Color(0x0FFFFFF, true).getRGB(), new Color(0xB0FFFFFF, true).getRGB(), new Color(0xB0FFFFFF, true).getRGB());
        RenderUtil.draw2DGradientRect((float) (getX() + width / 2f + 20f), (float) (getY() + 6), (float) (getX() + width - 10), (float) (getY() + 7), new Color(0xB0FFFFFF, true).getRGB(), new Color(0xB0FFFFFF, true).getRGB(), new Color(0x0FFFFFF, true).getRGB(), new Color(0x0FFFFFF, true).getRGB());
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if ((getY() > ThunderGui2.getInstance().main_posY + ThunderGui2.getInstance().height) || getY() < ThunderGui2.getInstance().main_posY) {
            return;
        }
        if (hovered) {
            Parent parent = (Parent) setting.getValue();
            parent.setExtended(!parent.isExtended());
        }
    }

}
