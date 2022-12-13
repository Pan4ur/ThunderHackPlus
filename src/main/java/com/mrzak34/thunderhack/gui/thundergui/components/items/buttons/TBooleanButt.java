package com.mrzak34.thunderhack.gui.thundergui.components.items.buttons;

import com.mrzak34.thunderhack.gui.thundergui.ThunderGui;
import com.mrzak34.thunderhack.gui.thundergui.fontstuff.FontRender;
import com.mrzak34.thunderhack.modules.client.ThunderHackGui;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.RenderUtil;

import java.awt.*;

public class TBooleanButt extends TItem{
    private boolean state;
    private final Setting setting;
    public TBooleanButt(Setting setting, int x, int y) {  //127 40   //84  23
        super(setting.getName());
        this.setting = setting;
        this.setLocation(x,y);
    }


    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if(this.y < ThunderGui.thunderguiY || this.y > ThunderGui.thunderguiY + ThunderGui.thunderguiscaleY + 43){
            return;
        }
        RenderUtil.drawRect2(this.x,this.y,this.x + 94,this.y + 23, ThunderHackGui.getInstance().buttsColor.getValue().getRawColor());
        RenderUtil.drawRect2(this.x + 77,this.y + 6,this.x + 88,this.y + 17,new Color(0xBB424242, true).getRGB());
        if(this.getState()){
            RenderUtil.drawRect2(this.x + 79,this.y + 8,this.x + 86,this.y + 15,ThunderGui.getCatColor().getRGB());
        }
        FontRender.drawString3(this.setting.getName(), (int) (this.x + 2f), (int) (this.y + 3f),-1);
    }



    public boolean getState() {
        return (Boolean) this.setting.getValue();
    }


    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if(!(mouseButton == 0 && mouseX >= this.x + 77 && mouseX <= this.x + 88 && mouseY >= this.y + 6 && mouseY <= this.y + 17)){
            return;
        } else {
            this.onNigger();
        }
    }

    public void onNigger() {
        this.state = !this.state;
        this.etoshdbratik();
    }

    public void etoshdbratik() {
        this.setting.setValue(!((Boolean) this.setting.getValue()));
    }

    public boolean isHovering(int mouseX, int mouseY) {
       // for (TModuleButt component : ThunderGui.getThunderGui().getComponents()) {
       //     if (!component.drag) continue;
       //     return false;
        //}
        return (float) mouseX >= this.getX() && (float) mouseX <= this.getX() + (float) this.getWidth() && (float) mouseY >= this.getY() && (float) mouseY <= this.getY() + (float) this.height;
    }
}
