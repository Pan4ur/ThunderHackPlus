package com.mrzak34.thunderhack.gui.thundergui2.components;

import com.mrzak34.thunderhack.gui.fontstuff.FontRender;
import com.mrzak34.thunderhack.gui.thundergui2.ThunderGui2;
import com.mrzak34.thunderhack.modules.client.ClickGui;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.RoundedShader;
import com.mrzak34.thunderhack.util.math.MathUtil;
import net.minecraft.client.Minecraft;

import java.awt.*;

public class BooleanComponent extends SettingElement {

    float animation = 0f;

    public BooleanComponent(Setting setting) {
        super(setting);
    }


    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);
        if((getY() > ThunderGui2.getInstance().main_posY + ThunderGui2.getInstance().height) || getY() < ThunderGui2.getInstance().main_posY){
            return;
        }
        FontRender.drawString6(getSetting().getName(), (float) getX(), (float) getY() + 5,isHovered() ? -1 : new Color(0xB0FFFFFF, true).getRGB(),false);
        animation = fast(animation, (boolean) setting.getValue() ? 1 : 0, 15f);
        double paddingX = 7 * animation;
        Color color = ClickGui.getInstance().getColor(0);
        RoundedShader.drawRound((float) (x + width - 18), (float) (y + height / 2 - 4), 15, 8, 4, paddingX > 4 ? color : new Color(0xFFB2B1B1));
        RoundedShader.drawRound((float) (x + width - 17 + paddingX), (float) (y + height / 2 - 3), 6, 6, 3, true, new Color(-1));
    }


    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if((getY() > ThunderGui2.getInstance().main_posY + ThunderGui2.getInstance().height) || getY() < ThunderGui2.getInstance().main_posY){
            return;
        }
        if(isHovered()){
            setting.setValue(!((Boolean) setting.getValue()));
        }
    }

    public static double deltaTime() {
        return Minecraft.getDebugFPS() > 0 ? (1.0000 / Minecraft.getDebugFPS()) : 1;
    }

    public static float fast(float end, float start, float multiple) {
        return (1 - MathUtil.clamp((float) (deltaTime() * multiple), 0, 1)) * end + MathUtil.clamp((float) (deltaTime() * multiple), 0, 1) * start;
    }
}
