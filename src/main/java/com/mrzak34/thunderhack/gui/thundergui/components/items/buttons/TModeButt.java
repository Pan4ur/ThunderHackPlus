package com.mrzak34.thunderhack.gui.thundergui.components.items.buttons;

import com.mrzak34.thunderhack.gui.thundergui.ThunderGui;
import com.mrzak34.thunderhack.gui.thundergui.fontstuff.FontRender;
import com.mrzak34.thunderhack.modules.client.ThunderHackGui;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.RenderUtil;
import com.mrzak34.thunderhack.util.Util;

import java.awt.*;

public class TModeButt extends TItem{

    private final Setting setting;
    public TModeButt(Setting setting, int x, int y) {  //127 40   //84  27
        super(setting.getName());
        this.setting = setting;
        this.setLocation(x,y);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {


        if(this.y < ThunderGui.thunderguiY || this.y > ThunderGui.thunderguiY + ThunderGui.thunderguiscaleY + 43){
            return;
        }
        if(this.y < ThunderGui.thunderguiY || this.y > ThunderGui.thunderguiY + ThunderGui.thunderguiscaleY + 43){
            return;
        }

        RenderUtil.drawRect2(this.x,this.y,this.x + 94,this.y + 30,ThunderHackGui.getInstance().buttsColor.getValue().getColorObject().getRGB());
        FontRender.drawString3(this.setting.getName(), (int) (this.x + 2f), (int) (this.y + 3f),-1);


        RenderUtil.drawRect2(this.x + 2,this.y + 15,this.x + 61,this.y + 26,new Color(0xB3101010, true).getRGB());
        Util.fr.drawString(setting.currentEnumName(),(int)this.x + 6,(int)this.y + 16, -1);


        if(!isHoveringArrow(this.x + 79,this.y + 15,this.x + 90,this.y + 26,mouseX,mouseY)) {
            RenderUtil.drawRect2(this.x + 79, this.y + 15, this.x + 90, this.y + 26, new Color(0xB3101010, true).getRGB());
            FontRender.drawString3(">", (int) this.x + 82, (int) this.y + 17, -1);
        } else {
            RenderUtil.drawRect2(this.x + 79, this.y + 15, this.x + 90, this.y + 26, new Color(0xB32F2F2F, true).getRGB());
            FontRender.drawString3(">", (int) this.x + 82, (int) this.y + 17, ThunderGui.getCatColor().getRGB());
        }

        if(!isHoveringArrow(this.x + 64,this.y + 15,this.x + 75,this.y + 26,mouseX,mouseY)) {
            RenderUtil.drawRect2(this.x + 64, this.y + 15, this.x + 75, this.y + 26, new Color(0xB3101010, true).getRGB());
            FontRender.drawString3("<", (int) this.x + 67, (int) this.y + 17, -1);
        } else {
            RenderUtil.drawRect2(this.x + 64, this.y + 15, this.x + 75, this.y + 26, new Color(0xB32F2F2F, true).getRGB());
            FontRender.drawString3("<", (int) this.x + 67, (int) this.y + 17, ThunderGui.getCatColor().getRGB());
        }

    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if(isHoveringArrow(this.x + 64,this.y + 15,this.x + 75,this.y + 26,mouseX,mouseY)) {
            this.setting.naoborotEnum();
        }
        if(isHoveringArrow(this.x + 79,this.y + 15,this.x + 90,this.y + 26,mouseX,mouseY)) {
            this.setting.increaseEnum();
        }
    }

    public boolean isHoveringArrow(float x, float y, float x1, float y1, float mouseX, float mouseY){
        return (mouseX >= x && mouseY >= y && mouseX <= x1 && mouseY <= y1);
    }
}
