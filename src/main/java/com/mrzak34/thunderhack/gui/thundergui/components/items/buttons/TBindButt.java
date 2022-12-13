package com.mrzak34.thunderhack.gui.thundergui.components.items.buttons;

import com.mrzak34.thunderhack.gui.thundergui.ThunderGui;
import com.mrzak34.thunderhack.gui.thundergui.fontstuff.FontRender;
import com.mrzak34.thunderhack.modules.client.ThunderHackGui;
import com.mrzak34.thunderhack.setting.Bind;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.RenderUtil;
import com.mrzak34.thunderhack.util.Util;

import java.awt.*;

public class TBindButt extends TItem{
    private final Setting setting;
    public boolean isListening = false;

    public TBindButt(Setting setting, int x, int y) {  //127 40   //84  27
        super(setting.getName());
        this.setting = setting;
        this.setLocation(x,y);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {

        if(this.y < ThunderGui.thunderguiY || this.y > ThunderGui.thunderguiY + ThunderGui.thunderguiscaleY + 43){
            return;
        }
        FontRender.drawString3("Bind", (int) this.x + 2,(int) this.y + 3,-1);

        RenderUtil.drawRect2(this.x,this.y,this.x + 94,this.y + 23,ThunderHackGui.getInstance().buttsColor.getValue().getRawColor());
        RenderUtil.drawRect2(this.x + 77 - deobf(),this.y + 6,this.x + 88,this.y + 17,new Color(0xBBD2D2D2, true).getRGB());

        if (this.isListening) {
            Util.fr.drawString("...", (int) this.x + 79,(int) this.y + 8,-1);

        } else {
            if(this.setting.getValue().toString().toUpperCase().length() == 1) {
                FontRender.drawString3(this.setting.getValue().toString().toUpperCase(), (int) this.x + 79, (int) this.y + 8, new Color(0).getRGB());
            } else {
                if(this.setting.getValue().toString().toUpperCase().equals("APOSTROPHE")){
                    Util.fr.drawString("APOSTR", (int) this.x + 79 - deobf(), (int) this.y + 8, new Color(0).getRGB());

                } else {
                    Util.fr.drawString(this.setting.getValue().toString().toUpperCase(), (int) this.x + 79 - deobf(), (int) this.y + 8, new Color(0).getRGB());
                }
            }
        }
    }


    public int deobf(){
        if(this.setting.getValue().toString().toUpperCase().length() == 1){
            return 0;
        } else {
            return 24;
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (this.isHoveringItem(this.x,this.y,this.x + 94,this.y + 23,mouseX, mouseY)) {
            ok();
        }
    }

    public boolean isHoveringItem(float x, float y, float x1, float y1, float mouseX, float mouseY){
        return (mouseX >= x && mouseY >= y && mouseX <= x1 && mouseY <= y1);
    }

    @Override
    public void onKeyTyped(char typedChar, int keyCode) {
        if (this.isListening) {
            Bind bind = new Bind(keyCode);
            if (bind.toString().equalsIgnoreCase("Escape")) {
                return;
            }
            if (bind.toString().equalsIgnoreCase("Delete")) {
                bind = new Bind(-1);
            }
            this.setting.setValue(bind);
            this.ok();
        }
    }

    public void ok() {
        this.isListening = !this.isListening;
    }
}
