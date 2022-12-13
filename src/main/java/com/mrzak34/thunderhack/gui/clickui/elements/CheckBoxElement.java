package com.mrzak34.thunderhack.gui.clickui.elements;

import com.mrzak34.thunderhack.util.RoundedShader;
import com.mrzak34.thunderhack.gui.clickui.base.AbstractElement;
import com.mrzak34.thunderhack.gui.thundergui.fontstuff.FontRender;
import com.mrzak34.thunderhack.modules.client.ClickGui;
import com.mrzak34.thunderhack.notification.Animation;
import com.mrzak34.thunderhack.notification.DecelerateAnimation;
import com.mrzak34.thunderhack.notification.Direction;
import com.mrzak34.thunderhack.setting.Setting;


import java.awt.*;

public class CheckBoxElement extends AbstractElement {

    private final Animation animation;

    public CheckBoxElement(Setting setting) {
        super(setting);
        animation = new DecelerateAnimation(200, 1F, (Boolean) setting.getValue() ? Direction.FORWARDS : Direction.BACKWARDS);
    }

    @Override
    public void init() {
        animation.setDirection(Direction.BACKWARDS);
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        super.render(mouseX, mouseY, delta);
        animation.setDirection((Boolean) setting.getValue() ? Direction.FORWARDS : Direction.BACKWARDS);
        double paddingX = 7 * animation.getOutput();


    	Color color = ClickGui.getInstance().getColor(0);
        RoundedShader.drawRound((float) (x + width - 18), (float) (y + height / 2 - 4), 15, 8, 4, paddingX > 4 ? color : new Color(0xFFB2B1B1));

        RoundedShader.drawRound((float) (x + width - 17 + paddingX), (float) (y + height / 2 - 3), 6, 6, 3, true, new Color(-1));

        FontRender.drawString5(setting.getName(), (float) (x + 3), (float) (y + height / 2 - (FontRender.getFontHeight5() / 2f)), -1);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (hovered && button == 0) {
            setting.setValue(!((Boolean) setting.getValue()));
            animation.setDirection((Boolean) setting.getValue() ? Direction.FORWARDS : Direction.BACKWARDS);
        }
    }

    @Override
    public void resetAnimation() {
        animation.setDirection(Direction.BACKWARDS);
    }

}
