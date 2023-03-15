package com.mrzak34.thunderhack.gui.clickui.elements;

import com.mojang.realmsclient.gui.ChatFormatting;
import com.mrzak34.thunderhack.gui.clickui.base.AbstractElement;
import com.mrzak34.thunderhack.gui.fontstuff.FontRender;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.setting.SubBind;


public class SubBindElement extends AbstractElement {
    public boolean isListening;

    public SubBindElement(Setting setting) {
        super(setting);
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        super.render(mouseX, mouseY, delta);
        if (this.isListening) {
            FontRender.drawString5("...", (float) (x + 3), (float) (y + height / 2 - (FontRender.getFontHeight5() / 2f)), -1);
        } else {
            FontRender.drawString5("SubBind " + ChatFormatting.GRAY + this.setting.getValue().toString().toUpperCase(), (float) (x + 3), (float) (y + height / 2 - (FontRender.getFontHeight5() / 2f)), -1);
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (hovered && button == 0) {
            isListening = !isListening;
        }
    }

    @Override
    public void keyTyped(char chr, int keyCode) {
        if (this.isListening) {
            SubBind subBindbind = new SubBind(keyCode);
            if (subBindbind.toString().equalsIgnoreCase("Escape")) {
                return;
            }
            if (subBindbind.toString().equalsIgnoreCase("Delete")) {
                subBindbind = new SubBind(-1);
            }
            this.setting.setValue(subBindbind);
            isListening = !isListening;
        }
    }
}
