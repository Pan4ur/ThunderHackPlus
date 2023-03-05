package com.mrzak34.thunderhack.gui.thundergui2.components;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.gui.fontstuff.FontRender;
import com.mrzak34.thunderhack.gui.thundergui2.ThunderGui2;
import com.mrzak34.thunderhack.setting.ColorSetting;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.RoundedShader;
import com.mrzak34.thunderhack.util.math.MathUtil;
import com.mrzak34.thunderhack.util.render.Drawable;
import com.mrzak34.thunderhack.util.render.RenderUtil;

import java.awt.*;

public class ColorPickerComponent extends SettingElement {

    private Setting colorSetting;
    private boolean open;
    private float hue;
    private float saturation;
    private float brightness;
    private int alpha;


    private boolean afocused;
    private boolean hfocused;
    private boolean sbfocused;


    private boolean copy_focused;
    private boolean paste_focused;
    private boolean rainbow_focused;

    private float spos, bpos, hpos, apos;
    private Color prevColor;
    private boolean firstInit;

    public ColorSetting getColorSetting() {
        return (ColorSetting) colorSetting.getValue();
    }


    public ColorPickerComponent(Setting setting) {
        super(setting);
        this.colorSetting = setting;
        firstInit = true;
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        super.render(mouseX, mouseY, delta);
        if((getY() > ThunderGui2.getInstance().main_posY + ThunderGui2.getInstance().height) || getY() < ThunderGui2.getInstance().main_posY){
            return;
        }
        FontRender.drawString6(getSetting().getName(), (float) getX(), (float) getY() + 5,isHovered() ? -1 : new Color(0xB0FFFFFF, true).getRGB(),false);
        Drawable.drawBlurredShadow((int) (x + width - 20), (int) (y + 5), 14, 6, 10, getColorSetting().getColorObject());
        RoundedShader.drawRound((float) (x + width - 20), (float) (y + 5), 14, 6, 1, getColorSetting().getColorObject());
        if(open)
            renderPicker(mouseX, mouseY, getColorSetting().getColorObject());
    }

    @Override
    public void onTick() {
        super.onTick();

    }

    private void renderPicker(int mouseX, int mouseY, Color color) {
        double cx = x + 6;
        double cy = y + 20;
        double cw = width - 38;
        double ch = height - 20;

        if (prevColor != getColorSetting().getColorObject()) {
            updatePos();
            prevColor = getColorSetting().getColorObject();
        }

        if (firstInit) {
            spos = (float) ((cx + cw) - (cw - (cw * saturation)));
            bpos = (float) ((cy + (ch - (ch * brightness))));
            hpos = (float) ((cy + (ch - 3 + ((ch - 3) * hue))));
            apos = (float) ((cy + (ch - 3 - ((ch - 3) * (alpha / 255f)))));
            firstInit = false;
        }

        spos = RenderUtil.scrollAnimate(spos, (float) (((cx + 40) + (cw - 40)) - ((cw - 40) - ((cw - 40) * saturation))), .6f);
        bpos = RenderUtil.scrollAnimate(bpos, (float) (cy + (ch - (ch * brightness))), .6f);
        hpos = RenderUtil.scrollAnimate(hpos, (float) (cy + (ch - 3 + ((ch - 3) * hue))), .6f);
        apos = RenderUtil.scrollAnimate(apos, (float) (cy + (ch - 3 - ((ch - 3) * (alpha / 255f)))), .6f);

        Color colorA = Color.getHSBColor(hue, 0.0F, 1.0F), colorB = Color.getHSBColor(hue, 1.0F, 1.0F);
        Color colorC = new Color(0, 0, 0, 0), colorD = new Color(0, 0, 0);

        Drawable.horizontalGradient(cx + 40, cy, cx + cw, cy + ch, colorA.getRGB(), colorB.getRGB());
        Drawable.verticalGradient(cx + 40, cy, cx + cw, cy + ch, colorC.getRGB(), colorD.getRGB());

        for (float i = 1f; i < ch - 2f; i += 1f) {
            float curHue = (float) (1f / (ch / i));
            Drawable.drawRectWH(cx + cw + 4, cy + i, 8, 1, Color.getHSBColor(curHue, 1f, 1f).getRGB());
        }

        Drawable.drawRectWH(cx + cw + 17, cy + 1, 8, ch - 3, 0xFFFFFFFF);

        Drawable.verticalGradient(cx + cw + 17, cy + 0.8, cx + cw + 25, cy + ch - 2, new Color(color.getRed(), color.getGreen(), color.getBlue(), 255).getRGB(), new Color(0, 0, 0, 0).getRGB());

        Drawable.drawRectWH(cx + cw + 3, hpos + 0.5f, 10, 1, -1);
        Drawable.drawRectWH(cx + cw + 16, apos + 0.5f, 10, 1, -1);
        RoundedShader.drawRoundOutline(spos, bpos, 3, 3, 1.5f, 1, color, new Color(-1));

        Color value = Color.getHSBColor(hue, saturation, brightness);

        if (sbfocused) {
            saturation = (float) (MathUtil.clamp(mouseX - (cx + 40), 0f,  cw - 40) / (cw - 40));

            brightness = (float) ((ch - MathUtil.clamp((mouseY - cy), 0,  ch)) / ch);
            value = Color.getHSBColor(hue, saturation, brightness);
            setColor(new Color(value.getRed(), value.getGreen(), value.getBlue(), alpha));
        }

        if (hfocused) {
            hue = (float) -((ch - MathUtil.clamp((float) (mouseY - cy), 0, (float) ch)) / ch);
            value = Color.getHSBColor(hue, saturation, brightness);
            setColor(new Color(value.getRed(), value.getGreen(), value.getBlue(), alpha));
        }

        if (afocused) {
            alpha = (int) (((ch - MathUtil.clamp((float) (mouseY - cy), 0, (float) ch)) / ch) * 255);
            setColor(new Color(value.getRed(), value.getGreen(), value.getBlue(), alpha));
        }

        rainbow_focused = Drawable.isHovered(mouseX, mouseY, getX(), cy, 40, 10);
        copy_focused = Drawable.isHovered(mouseX, mouseY, getX(), cy + 13, 40, 10);
        paste_focused = Drawable.isHovered(mouseX, mouseY, getX(), cy + 26, 40, 10);

        RoundedShader.drawRound((float) getX(), (float) cy,40,10, 2f, getColorSetting().isCycle() ? new Color(86, 63, 105,250) : (rainbow_focused ? new Color(66, 48, 80,250) : new Color(50,35,60,250)));
        RoundedShader.drawRound((float) getX(), (float) cy + 13,40,10, 2f,copy_focused ? new Color(66, 48, 80,250) : new Color(50,35,60,250));
        RoundedShader.drawRound((float) getX(), (float) cy + 26,40,9.5f, 2f, paste_focused ? new Color(66, 48, 80,250) : new Color(50,35,60,250));

        FontRender.drawCentString6("rainbow",(float) getX() + 20,(float) cy + 4, rainbow_focused ? -1 : (getColorSetting().isCycle() ? getColorSetting().getColor() : new Color(0xB5FFFFFF, true).getRGB()) );
        FontRender.drawCentString6("copy",(float) getX() + 20,(float) cy + 16.5f, copy_focused ? -1 : new Color(0xB5FFFFFF, true).getRGB() );
        FontRender.drawCentString6("paste",(float) getX() + 20,(float) cy + 29.5f, paste_focused ? -1 : new Color(0xB5FFFFFF, true).getRGB() );

    }

    private void updatePos() {
        float[] hsb = Color.RGBtoHSB(getColorSetting().getColorObject().getRed(), getColorSetting().getColorObject().getGreen(), getColorSetting().getColorObject().getBlue(), null);
        hue = -1 + hsb[0];
        saturation = hsb[1];
        brightness = hsb[2];
        alpha = getColorSetting().getColorObject().getAlpha();
    }

    private void setColor(Color color) {
        getColorSetting().setColor(color.getRGB());
        prevColor = color;
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if((getY() > ThunderGui2.getInstance().main_posY + ThunderGui2.getInstance().height) || getY() < ThunderGui2.getInstance().main_posY){
            return;
        }
        double cx = x + 4;
        double cy = y + 21;
        double cw = width - 34;
        double ch = height - 20;

        if (Drawable.isHovered(mouseX, mouseY, (x + width - 20), (y + 5), 14, 6))
            open = !open;

        if(!open)
            return;

        if (Drawable.isHovered(mouseX, mouseY, cx + cw + 17, cy, 8, ch) && button == 0)
            afocused = true;

        else if (Drawable.isHovered(mouseX, mouseY, cx + cw + 4, cy, 8, ch) && button == 0)
            hfocused = true;

        else if (Drawable.isHovered(mouseX, mouseY, cx + 40, cy, cw - 40, ch) && button == 0)
            sbfocused = true;


        if(rainbow_focused)  getColorSetting().setCycle(!getColorSetting().isCycle());
        if(copy_focused)  Thunderhack.copy_color = getColorSetting().getColorObject();
        if(paste_focused) setColor(Thunderhack.copy_color == null ? getColorSetting().getColorObject() : Thunderhack.copy_color);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int button) {
        hfocused = false;
        afocused = false;
        sbfocused = false;
    }

    @Override
    public void onClose() {
        hfocused = false;
        afocused = false;
        sbfocused = false;
    }

    public boolean isOpen() {
        return open;
    }

}
