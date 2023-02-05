package com.mrzak34.thunderhack.gui.thundergui.components.items.buttons;

import com.mrzak34.thunderhack.gui.thundergui.ThunderGui;
import com.mrzak34.thunderhack.gui.thundergui.fontstuff.FontRender;
import com.mrzak34.thunderhack.modules.client.ThunderHackGui;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.render.RenderUtil;
import com.mrzak34.thunderhack.util.Util;
import net.minecraft.util.ChatAllowedCharacters;

import java.awt.*;

public class TStringButt extends TItem{
    private final Setting setting;
    public TStringButt(Setting setting, int x, int y) {  //127 40   //84  27
        super(setting.getName());
        this.setting = setting;
        this.setLocation(x,y);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {


        if(this.y < ThunderGui.thunderguiY || this.y > ThunderGui.thunderguiY + ThunderGui.thunderguiscaleY + 43){
            return;
        }
        RenderUtil.drawRect2(this.x,this.y,this.x + 94,this.y + 30, ThunderHackGui.getInstance().buttsColor.getValue().getColorObject().getRGB());
        FontRender.drawString3(this.setting.getName(), (int) (this.x + 2f), (int) (this.y + 3f),-1);


        RenderUtil.drawRect2(this.x + 2,this.y + 15,this.x + 92,this.y + 26,new Color(0xB3101010, true).getRGB());

        if (this.isListening) {
            Util.fr.drawString(this.currentString.getString() + getIdleSign(),(int)this.x + 6,(int)this.y + 16, -1);
        } else {
            Util.fr.drawString(this.setting.getValue() + " ",(int)this.x + 6,(int)this.y + 16, -1);
        }
    }
    public String getIdleSign(){
        return "...";
    }
    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if(isHoveringItem(this.x + 2,this.y + 15,this.x + 92,this.y + 26,mouseX,mouseY)) {
            ok();
        }
    }

    public boolean isHoveringItem(float x, float y, float x1, float y1, float mouseX, float mouseY){
        return (mouseX >= x && mouseY >= y && mouseX <= x1 && mouseY <= y1);
    }



    @Override
    public void onKeyTyped(char typedChar, int keyCode) {
        if (this.isListening) {
            switch (keyCode) {
                case 1: {
                    return;
                }
                case 28: {
                    this.enterString();
                }
                case 14: {
                    this.setString(removeLastChar(this.currentString.getString()));
                }
            }
            if (ChatAllowedCharacters.isAllowedCharacter(typedChar)) {
                this.setString(this.currentString.getString() + typedChar);
            }
        }
    }

    @Override
    public void update() {
        this.setHidden(!this.setting.isVisible());
    }

    private void enterString() {
        if (this.currentString.getString().isEmpty()) {
            this.setting.setValue(this.setting.getDefaultValue());
        } else {
            this.setting.setValue(this.currentString.getString());
        }
        this.setString("");
        ok();
    }




    public void ok() {
        this.isListening = !this.isListening;
    }


    public boolean getState() {
        return !this.isListening;
    }

    public void setString(String newString) {
        this.currentString = new CurrentString(newString);
    }

    public static class CurrentString {
        private final String string;

        public CurrentString(String string) {
            this.string = string;
        }

        public String getString() {
            return this.string;
        }
    }

    public static String removeLastChar(String str) {
        String output = "";
        if (str != null && str.length() > 0) {
            output = str.substring(0, str.length() - 1);
        }
        return output;
    }

    public boolean isListening;
    private CurrentString currentString = new CurrentString("");

}
