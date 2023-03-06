package com.mrzak34.thunderhack.gui.clickui.elements;

import com.mrzak34.thunderhack.util.RoundedShader;
import com.mrzak34.thunderhack.gui.clickui.base.AbstractElement;
import com.mrzak34.thunderhack.gui.fontstuff.FontRender;
import com.mrzak34.thunderhack.modules.client.ClickGui;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.math.MathUtil;
import net.minecraft.client.Minecraft;


import java.awt.*;

public class CheckBoxElement extends AbstractElement {

    public static double deltaTime() {
        return Minecraft.getDebugFPS() > 0 ? (1.0000 / Minecraft.getDebugFPS()) : 1;
    }

    public static float fast(float end, float start, float multiple) {
        return (1 - MathUtil.clamp((float) (deltaTime() * multiple), 0, 1)) * end + MathUtil.clamp((float) (deltaTime() * multiple), 0, 1) * start;
    }

    public CheckBoxElement(Setting setting) {
        super(setting);
    }

    @Override
    public void init() {
    }

    float animation = 0f;

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        super.render(mouseX, mouseY, delta);

        animation = fast(animation, (boolean) setting.getValue() ? 1 : 0, 15f);

        double paddingX = 7 * animation;


    	Color color = ClickGui.getInstance().getColor(0);
        RoundedShader.drawRound((float) (x + width - 18), (float) (y + height / 2 - 4), 15, 8, 4, paddingX > 4 ? color : new Color(0xFFB2B1B1));

        RoundedShader.drawRound((float) (x + width - 17 + paddingX), (float) (y + height / 2 - 3), 6, 6, 3, true, new Color(-1));

        FontRender.drawString5(setting.getName(), (float) (x + 3), (float) (y + height / 2 - (FontRender.getFontHeight5() / 2f)) + 2, -1);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (hovered && button == 0) {
            setting.setValue(!((Boolean) setting.getValue()));
        }
    }

    @Override
    public void resetAnimation() {
    }

}
