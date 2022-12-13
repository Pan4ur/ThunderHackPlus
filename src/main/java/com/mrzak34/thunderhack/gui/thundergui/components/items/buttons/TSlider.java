package com.mrzak34.thunderhack.gui.thundergui.components.items.buttons;

import com.mrzak34.thunderhack.gui.thundergui.ThunderGui;
import com.mrzak34.thunderhack.gui.thundergui.fontstuff.FontRender;
import com.mrzak34.thunderhack.modules.client.ThunderHackGui;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.PaletteHelper;
import com.mrzak34.thunderhack.util.RenderUtil;
import com.mrzak34.thunderhack.util.Util;
import org.lwjgl.input.Mouse;

import java.awt.*;

public class TSlider extends TItem{

    private final Number min;
    private final Number max;
    private final int difference;
    private final Setting setting;

    public TSlider(Setting setting, int x, int y) {  //94  41
        super(setting.getName());
        this.setting = setting;
        this.min = (Number) setting.getMin();
        this.max = (Number) setting.getMax();
        this.difference = this.max.intValue() - this.min.intValue();
        this.setLocation(x,y);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {

        if(this.y < ThunderGui.thunderguiY || this.y > ThunderGui.thunderguiY + ThunderGui.thunderguiscaleY + 43){
            return;
        }
        this.upd();
        this.dragSetting(mouseX, mouseY);
        RenderUtil.drawRect2(this.x,this.y,this.x + 94,this.y + 41, ThunderHackGui.getInstance().buttsColor.getValue().getColorObject().getRGB());
        FontRender.drawString3(this.setting.getName(), (int) (this.x + 2f), (int) (this.y + 3f),-1);

        RenderUtil.drawSmoothRect(this.x + 2,this.y + 23,this.x + 92,this.y + 26,new Color(0xB34B4A4A, true).getRGB());

        RenderUtil.drawSmoothRect(this.x + 2,this.y + 23,((Number) this.setting.getValue()).floatValue() <= this.min.floatValue() ? this.x  : this.x + ((float) this.width + 92f) * this.partialMultiplier(),this.y + 26, PaletteHelper.fadeColor(new Color(0xFFFC4A4A, true).getRGB(),ThunderGui.getCatColor().getRGB(),this.nigger));
        RenderUtil.drawSmoothRect(this.x+ 2,this.y + 22,((Number) this.setting.getValue()).floatValue() <= this.min.floatValue() ? this.x + 1  : this.x + ((float) this.width + 93f) * this.partialMultiplier(),this.y + 27, PaletteHelper.fadeColor(new Color(0x28FC4A4A, true).getRGB(),new Color(ThunderGui.getCatColor().getRed(),ThunderGui.getCatColor().getGreen(),ThunderGui.getCatColor().getBlue(),30).getRGB(),this.nigger));


      //  RenderUtil.drawRect(((Number) this.setting.getValue()).floatValue() <= this.min.floatValue() ? this.x + 2 : this.x + ((float) this.width + 90f) * this.partialMultiplier() + 2,this.y + 21,((Number) this.setting.getValue()).floatValue() <= this.min.floatValue() ? this.x + 5 : this.x + ((float) this.width + 90f) * this.partialMultiplier() + 5,this.y + 27,-1);

        if(partialMultiplier() > 0) {
            RenderUtil.drawRect2((this.x + (90) * partialMultiplier()), this.y + 21, (this.x + (90) * partialMultiplier()) + 3, this.y + 27, -1);
        } else {
            RenderUtil.drawRect2(this.x , this.y + 21, this.x + 3, this.y + 27, -1);

        }

        String value = this.setting.getValue().toString();
        RenderUtil.drawSmoothRect(this.x + 2,this.y + 29, Util.fr.getStringWidth(value) + 10 + this.x, this.y + 38,new Color(0xB34B4A4A, true).getRGB());
        Util.fr.drawString(value,(int)this.x + 4,(int)this.y + 29,new Color(0x8A8A8A).getRGB());

        Util.fr.drawString(this.setting.getDescription(),(int)this.x + 4,(int)this.y + 13,new Color(0x8A8A8A).getRGB());
    }


    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (this.isHovering(mouseX, mouseY)) {
            this.setSettingFromX(mouseX);
        }
    }


    @Override
    public boolean isHovering(int mouseX, int mouseY) {
   //     for (Component component : OyVeyGui.getClickGui().getComponents()) {
       //     if (!component.drag) continue;
       //     return false;
      //  }
        return (float) mouseX >= this.x && (float) mouseX <= this.x + 94 && (float) mouseY >= this.y + 20 && (float) mouseY <= this.y + 29;
    }


    private void dragSetting(int mouseX, int mouseY) {
        if (this.isHovering(mouseX, mouseY) && Mouse.isButtonDown(0)) {
            this.setSettingFromX(mouseX);
        }
    }

    float nigger = 1;

    public void upd(){
        float percent = 0;
        if (this.setting.getValue() instanceof Integer) {

            float a = Float.parseFloat(String.valueOf(this.setting.getMax()));
            float b = Float.parseFloat(String.valueOf(this.setting.getValue()));
            percent = b / a;
        }
        if (this.setting.getValue() instanceof Float) {
            percent = (Float) this.setting.getValue() / (Float) this.setting.getMax();
        }
        if (this.setting.getValue() instanceof Double) {
            percent = (float) ((double) this.setting.getValue() / (double) this.setting.getMax());
        }
        nigger = percent;
    }

    private void setSettingFromX(int mouseX) {
        float percent = ((float) mouseX - (this.x + 2)) / ((float)  90f);


        if (this.setting.getValue() instanceof Double) {
            double result = (Double) this.setting.getMin() + (double) ((float) this.difference * percent);
            this.setting.setValue((double) Math.round(10.0 * result) / 10.0);
        } else if (this.setting.getValue() instanceof Float) {
            float result = ((Float) this.setting.getMin()).floatValue() + (float) this.difference * percent;
            this.setting.setValue(Float.valueOf((float) Math.round(10.0f * result) / 10.0f));
        } else if (this.setting.getValue() instanceof Integer) {
            this.setting.setValue((Integer) this.setting.getMin() + (int) ((float) this.difference * percent));
        }
    }

    private float middle() {
        return this.max.floatValue() - this.min.floatValue();
    }

    private float part() {
        return ((Number) this.setting.getValue()).floatValue() - this.min.floatValue();
    }

    private float partialMultiplier() {
        return this.part() / this.middle();
    }
}
