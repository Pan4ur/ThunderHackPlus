package com.mrzak34.thunderhack.gui.classic.components.items.buttons;

import com.mojang.realmsclient.gui.ChatFormatting;
import com.mrzak34.thunderhack.gui.classic.ClassicGui;
import com.mrzak34.thunderhack.gui.classic.components.Component;
import com.mrzak34.thunderhack.gui.thundergui.fontstuff.FontRender;
import com.mrzak34.thunderhack.modules.client.ClickGui;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.RenderHelper;
import com.mrzak34.thunderhack.util.RenderUtil;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.util.Objects;

public class Slider extends Button {
    private final Number min;
    private final Number max;
    private final int difference;
    public Setting setting;
    public boolean listening;
    public String Stringnumber = "";

    public Slider(Setting setting) {
        super(setting.getName());
        this.setting = setting;
        this.min = (Number) setting.getMin();
        this.max = (Number) setting.getMax();
        this.difference = this.max.intValue() - this.min.intValue();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.dragSetting(mouseX, mouseY);
        RenderUtil.drawRect(this.x + 3, this.y + 10.4f, this.x + 3 +  75f , this.y + this.height - 1.5f, ClickGui.getInstance().downColor.getValue().getColor());
        RenderUtil.drawRect(this.x + 3, this.y + 10.4f, ((Number) this.setting.getValue()).floatValue() <= this.min.floatValue() ? this.x + 3 : this.x + 3 + ((float) 75f ) * this.partialMultiplier(), this.y + (float) this.height - 1.5f, ClickGui.getInstance().slidercolor.getValue().getColor());
        RenderHelper.drawCircle(((Number) this.setting.getValue()).floatValue() <= this.min.floatValue() ? this.x + 3 : this.x + 3 + ((float) 75f ) * this.partialMultiplier(), this.y + 11.7f,2f,true, new Color(0xEAEAEA));

        if(!listening) {
            FontRender.drawString5(this.getName() + " " + ChatFormatting.GRAY + (this.setting.getValue() instanceof Float ? this.setting.getValue() : Double.valueOf(((Number) this.setting.getValue()).doubleValue())), (this.x + 2.3f), (this.y - 1.7f - (float) ClassicGui.getClickGui().getTextOffset()), -1);
        } else {
            if(Objects.equals(Stringnumber, "")) {
                FontRender.drawString5(this.getName() + " ...", (this.x + 2.3f), (this.y - 1.7f - (float) ClassicGui.getClickGui().getTextOffset()), -1);
            } else {
                FontRender.drawString5(this.getName() + " " + Stringnumber, (this.x + 2.3f), (this.y - 1.7f - (float) ClassicGui.getClickGui().getTextOffset()), -1);
            }
        }
    }


    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (this.isHovering(mouseX, mouseY) && Mouse.isButtonDown(0)) {
            this.setSettingFromX(mouseX);
        }
        if(this.isHovering(mouseX, mouseY) && Mouse.isButtonDown(2)){
            Stringnumber = "";
            this.listening = true;
        }
    }

    @Override
    public boolean isHovering(int mouseX, int mouseY) {
        for (Component component : ClassicGui.getClickGui().getComponents()) {
            if (!component.drag) continue;
            return false;
        }
        return (float) mouseX >= this.getX() + 3 && (float) mouseX <= this.getX() + 3 + (float) 75f && (float) mouseY >= this.getY() && (float) mouseY <= this.getY() + (float) this.height;
    }

    @Override
    public void update() {
        this.setHidden(!this.setting.isVisible());
    }

    private void dragSetting(int mouseX, int mouseY) {
        if (this.isHovering(mouseX, mouseY) && Mouse.isButtonDown(0)) {
            this.setSettingFromX(mouseX);
        }
    }

    @Override
    public float getHeight() {
        return 14;
    }

    private void setSettingFromX(int mouseX) {
        float percent = ((float) mouseX - (this.x + 3)) / ((float) 75f);
        if (this.setting.getValue() instanceof Double) {
            double result = (Double) this.setting.getMin() + (double) ((float) this.difference * percent);
            this.setting.setValue((double) Math.round(10.0 * result) / 10.0);
        } else if (this.setting.getValue() instanceof Float) {
            float result = (Float) this.setting.getMin() + (float) this.difference * percent;
            this.setting.setValue((float) Math.round(100.0f * result) / 100.0f);



        } else if (this.setting.getValue() instanceof Integer) {
            this.setting.setValue((Integer) this.setting.getMin() + (int) ((float) this.difference * percent));
        }
    }

    @Override
    public void onKeyTyped(char typedChar, int keyCode) {
        if (this.listening) {
            switch (keyCode) {
                case 1: {
                    listening = false;
                    Stringnumber = "";
                    return;
                }
                case 28: {
                    try {
                        this.searchNumber();

                    }catch (Exception e){
                        Stringnumber = "";
                        listening = false;
                    }
                }
                case 14: {
                    this.Stringnumber = removeLastChar(this.Stringnumber);
                }
            }

            this.Stringnumber  = this.Stringnumber + typedChar;

        }
    }

    public static String removeLastChar(String str) {
        String output = "";
        if (str != null && str.length() > 0) {
            output = str.substring(0, str.length() - 1);
        }
        return output;
    }

    private void searchNumber() {
        if (this.setting.getValue() instanceof Float) {
            this.setting.setValue(Float.valueOf(Stringnumber));
            Stringnumber = "";
            listening = false;
        } else if (this.setting.getValue() instanceof Integer) {
            this.setting.setValue(Integer.valueOf(Stringnumber));
            Stringnumber = "";
            listening = false;
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

