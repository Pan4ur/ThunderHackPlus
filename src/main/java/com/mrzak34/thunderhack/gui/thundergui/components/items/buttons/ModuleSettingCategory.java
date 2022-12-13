package com.mrzak34.thunderhack.gui.thundergui.components.items.buttons;

import com.mrzak34.thunderhack.gui.thundergui.ThunderGui;
import com.mrzak34.thunderhack.gui.thundergui.fontstuff.FontRender;
import com.mrzak34.thunderhack.modules.client.ThunderHackGui;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.RenderUtil;

public class ModuleSettingCategory extends TItem{
    private final Setting setting;
    public ModuleSettingCategory(Setting setting, int x, int y) {  //127 40   //84  27
        super(setting.getName());
        this.setting = setting;
        this.setLocation(x,y);
    }



    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {

        if(this.y < ThunderGui.thunderguiY || this.y > ThunderGui.thunderguiY + ThunderGui.thunderguiscaleY + 43){
            return;
        }
        RenderUtil.drawRect2(this.x,this.y,this.x + 270,this.y + 12, ThunderHackGui.getInstance().catcolorinmodule.getValue().getRawColor());
        FontRender.drawString3(setting.getName(),(int) this.x + 1,(int) this.y + 2,-1);
    }
}
