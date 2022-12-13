package com.mrzak34.thunderhack.gui.thundergui.components.items.buttons;

import com.mrzak34.thunderhack.gui.thundergui.ThunderGui;
import com.mrzak34.thunderhack.gui.thundergui.fontstuff.FontRender;
import com.mrzak34.thunderhack.modules.client.ThunderHackGui;
import com.mrzak34.thunderhack.setting.PositionSetting;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.RenderUtil;
import org.lwjgl.input.Mouse;

import java.awt.*;

import static com.mrzak34.thunderhack.gui.misc.GuiMiddleClickMenu.mouseWithinBounds;

public class TPositionSelector extends TItem{
    private final Setting setting;
    float ratioX;
    float ratioY;

    public TPositionSelector(Setting setting, int x, int y) {  //127 40   //84  27
        super(setting.getName());
        this.setting = setting;
        this.setLocation(x,y);
        this.ratioX = getPosSetting().x;
        this.ratioY = getPosSetting().y;
    }

    public PositionSetting getPosSetting() {
        return (PositionSetting) setting.getValue();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (this.y < ThunderGui.thunderguiY || this.y > ThunderGui.thunderguiY + ThunderGui.thunderguiscaleY + 43) {
            return;
        }
        if (Mouse.isButtonDown(0)) {
            this.dragSetting(mouseX, mouseY);
        }

        FontRender.drawString3(setting.getName(),(int) this.x + 3, (int) this.y + 2,-1);

        RenderUtil.drawRect2(this.x, this.y, this.x + 94, this.y + 67, ThunderHackGui.getInstance().buttsColor.getValue().getColorObject().getRGB());

        RenderUtil.drawRect2(this.x + 3, this.y + 12, this.x + 91, this.y + 62, new Color(0xB30C0C0C, true).getRGB());

        //88 на 50
        //полосочки по у

        //RenderUtil.drawRect2(this.x + 2,this.y + 10,this.x + 92,this.y + 10.5,new Color(0xB31A1A1A, true).getRGB());
        RenderUtil.drawRect2(this.x + 3,this.y + 20,this.x + 91,this.y + 20.5,new Color(0xB31A1A1A, true).getRGB());
        RenderUtil.drawRect2(this.x + 3,this.y + 30,this.x + 91,this.y + 30.5,new Color(0xB31A1A1A, true).getRGB());
        RenderUtil.drawRect2(this.x + 3,this.y + 40,this.x + 91,this.y + 40.5,new Color(0xB31A1A1A, true).getRGB());
        RenderUtil.drawRect2(this.x + 3,this.y + 49.5,this.x + 91,this.y + 50,new Color(0xB31A1A1A, true).getRGB());

        //полосочки по x
        RenderUtil.drawRect2(this.x + 11+ 3,this.y + 12,this.x + 11.5+ 3,this.y + 62,new Color(0xB31A1A1A, true).getRGB());
        RenderUtil.drawRect2(this.x + 22+ 3,this.y + 12,this.x + 22.5+ 3,this.y + 62,new Color(0xB31A1A1A, true).getRGB());
        RenderUtil.drawRect2(this.x + 33+ 3,this.y + 12,this.x + 33.5+ 3,this.y + 62,new Color(0xB31A1A1A, true).getRGB());
        RenderUtil.drawRect2(this.x + 44+ 3,this.y + 12,this.x + 44.5+ 3,this.y + 62,new Color(0xB31A1A1A, true).getRGB());
        RenderUtil.drawRect2(this.x + 55+ 3,this.y + 12,this.x + 55.5+ 3,this.y + 62,new Color(0xB31A1A1A, true).getRGB());
        RenderUtil.drawRect2(this.x + 66+ 3,this.y + 12,this.x + 66.5+ 3,this.y + 62,new Color(0xB31A1A1A, true).getRGB());
        RenderUtil.drawRect2(this.x + 77+ 3,this.y + 12,this.x + 77.5+ 3,this.y + 62,new Color(0xB31A1A1A, true).getRGB());


        RenderUtil.drawRect2(this.x + 3,this.y +11 + (50*getPosSetting().y),this.x + 91,this.y + (50*getPosSetting().y) + 12,ThunderGui.getCatColor().getRGB());
        RenderUtil.drawRect2(this.x - 1 + (88*getPosSetting().x),this.y + 12,this.x + (88*getPosSetting().x),this.y + 62,ThunderGui.getCatColor().getRGB());
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        handleMouseClick(mouseX, mouseY, mouseButton);
    }


    private boolean handleMouseClick(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton != 0) return false;
        if (mouseWithinBounds(mouseX, mouseY,this.x + 3f, this.y + 12f, 91f, 50f)) {
            getPosFromClick(mouseX, mouseY);
        }
        return true;
    }

    private void getPosFromClick(float mouseX, float mouseY) {
        float x1 = (mouseX - this.x) / 92f;
        float y1 = (mouseY - (this.y + 12)) / 50;

        getPosSetting().setX(x1);
        getPosSetting().setY(y1);

    }

    private void dragSetting(int mouseX, int mouseY) {
        handleMouseClick(mouseX, mouseY,0);
    }


}
