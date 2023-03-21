package com.mrzak34.thunderhack.gui.clickui.elements;

import com.mrzak34.thunderhack.gui.clickui.base.AbstractElement;
import com.mrzak34.thunderhack.gui.fontstuff.FontRender;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.RoundedShader;
import com.mrzak34.thunderhack.util.math.MathUtil;
import com.mrzak34.thunderhack.util.render.RenderUtil;
import net.minecraft.util.math.MathHelper;

import java.awt.*;
import java.util.Objects;

public class SliderElement extends AbstractElement {

    private final float min;
    private final float max;
    public boolean listening;
    public String Stringnumber = "";
    private float animation;
    private double stranimation;
    private boolean dragging;

    public SliderElement(Setting setting) {
        super(setting);
        this.min = ((Number) setting.getMin()).floatValue();
        this.max = ((Number) setting.getMax()).floatValue();
    }

    public static String removeLastChar(String str) {
        String output = "";
        if (str != null && str.length() > 0) {
            output = str.substring(0, str.length() - 1);
        }
        return output;
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {


        double currentPos = (((Number) setting.getValue()).floatValue() - min) / (max - min);
        stranimation = stranimation + (((Number) setting.getValue()).floatValue() * 100 / 100 - stranimation) / 2.0D;
        animation = RenderUtil.scrollAnimate(animation, (float) currentPos, .5f);
        super.render(mouseX, mouseY, delta);

        String value = "";

        if (setting.getValue() instanceof Float)
            value = String.valueOf(MathUtil.round((Float)setting.getValue(), 2));

        if (setting.getValue() instanceof Integer)
            value = String.valueOf(MathUtil.round((Integer)setting.getValue(), 2));

        if (!listening) {
            FontRender.drawString5(setting.getName(), (float) (x + 4), (float) (y + 4), -1);
            if (setting.getValue() instanceof Float)
                FontRender.drawString5(String.valueOf(MathUtil.round((Float) setting.getValue(), 2)), (float) (x + width - 4 - FontRender.getStringWidth6(value)), (float) y + 5, -1);
            if (setting.getValue() instanceof Integer)
                FontRender.drawString5(String.valueOf(MathUtil.round((Integer) setting.getValue(), 2)), (float) (x + width - 4 - FontRender.getStringWidth6(value)), (float) y + 5, -1);

        } else {
            FontRender.drawString5(setting.getName(), (float) (x + 4), (float) (y + 4), -1);
            if (Objects.equals(Stringnumber, "")) {
                FontRender.drawString5("...", (float) (x + width - 4 - FontRender.getStringWidth6(value)), (float) y + 5, -1);
            } else {
                FontRender.drawString5(Stringnumber, (float) (x + width - 4 - FontRender.getStringWidth6(value)), (float) y + 5, -1);
            }
        }

        Color color = new Color(0xFFE1E1E1);
        RoundedShader.drawRound((float) (x + 4), (float) (y + height - 4), (float) (width - 8), 1, 0.5f, new Color(0xff0E0E0E));
        RoundedShader.drawRound((float) (x + 4), (float) (y + height - 4), (float) ((width - 8) * animation), 1, 0.5f, color);
        RoundedShader.drawRound((float) ((x + 2 + (width - 8) * animation)), (float) (y + height - 5.5f), (float) ((4)), 4, 1.5f, color);

        animation = MathUtil.clamp(animation, 0, 1);

        if (dragging)
            setValue(mouseX, x + 7, width - 14);
    }

    private void setValue(int mouseX, double x, double width) {
        double diff = ((Number) setting.getMax()).floatValue() - ((Number) setting.getMin()).floatValue();
        double percentBar = MathHelper.clamp((mouseX - x) / width, 0.0, 1.0);
        double value = ((Number) setting.getMin()).floatValue() + percentBar * diff;


        if (this.setting.getValue() instanceof Float) {
            this.setting.setValue((float) value);
        } else if (this.setting.getValue() instanceof Integer) {
            this.setting.setValue((int) value);
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (button == 0 && hovered) {
            this.dragging = true;
        } else if (hovered) {
            Stringnumber = "";
            this.listening = true;
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int button) {
        this.dragging = false;
    }

    @Override
    public void resetAnimation() {
        dragging = false;
        animation = 0f;
        stranimation = 0;
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
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

                    } catch (Exception e) {
                        Stringnumber = "";
                        listening = false;
                    }
                }
                case 14: {
                    this.Stringnumber = removeLastChar(this.Stringnumber);
                }
            }

            this.Stringnumber = this.Stringnumber + typedChar;

        }
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

}
