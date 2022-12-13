package com.mrzak34.thunderhack.gui.clickui.elements;

import com.mrzak34.thunderhack.util.Drawable;
import com.mrzak34.thunderhack.util.RenderUtil;
import com.mrzak34.thunderhack.util.RoundedShader;
import com.mrzak34.thunderhack.gui.clickui.base.AbstractElement;
import com.mrzak34.thunderhack.gui.thundergui.fontstuff.FontRender;
import com.mrzak34.thunderhack.setting.ColorSetting;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.MathUtil;

import java.awt.*;

public class ColorPickerElement extends AbstractElement {

    private float hue;
    private float saturation;
    private float brightness;
    private int alpha;

    private boolean afocused;
    private boolean hfocused;
    private boolean sbfocused;

    private float spos, bpos, hpos, apos;

    private Color prevColor;
    
    private boolean firstInit;

    private Setting colorSetting;
    public ColorSetting getColorSetting() {
        return (ColorSetting) colorSetting.getValue();
    }

    public ColorPickerElement(Setting setting) {
        super(setting);
        this.colorSetting = setting;
        prevColor = getColorSetting().getColorObject();
        updatePos();
        
        double cx = x + 4;
        double cy = y + 17;
        double cw = width - 34;
        double ch = height - 20;
        
        firstInit = true;
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        RoundedShader.drawRound((float) x + 2, (float) y + 2, (float) width - 4, (float) height - 4, 4, new Color(bgcolor));

        FontRender.drawString5(setting.getName(), (float) x + 4, (float) y + 4, -1);
        Drawable.drawBlurredShadow((int) (x + width - 20), (int) (y + 5), 14, 6, 10, getColorSetting().getColorObject());
        RoundedShader.drawRound((float) (x + width - 20), (float) (y + 5), 14, 6, 1, getColorSetting().getColorObject());

        renderPicker(mouseX, mouseY, getColorSetting().getColorObject());
    }

    private void renderPicker(int mouseX, int mouseY, Color color) {
        double cx = x + 6;
        double cy = y + 16;
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

        spos = RenderUtil.scrollAnimate(spos, (float) ((cx + cw) - (cw - (cw * saturation))), .6f);
        bpos = RenderUtil.scrollAnimate(bpos, (float) (cy + (ch - (ch * brightness))), .6f);
        hpos = RenderUtil.scrollAnimate(hpos, (float) (cy + (ch - 3 + ((ch - 3) * hue))), .6f);
        apos = RenderUtil.scrollAnimate(apos, (float) (cy + (ch - 3 - ((ch - 3) * (alpha / 255f)))), .6f);

        Color colorA = Color.getHSBColor(hue, 0.0F, 1.0F), colorB = Color.getHSBColor(hue, 1.0F, 1.0F);
        Color colorC = new Color(0, 0, 0, 0), colorD = new Color(0, 0, 0);

        Drawable.horizontalGradient((float) cx, (float) cy, cx + cw, cy + ch, colorA.getRGB(), colorB.getRGB());
        Drawable.verticalGradient(cx, cy, cx + cw, cy + ch, colorC.getRGB(), colorD.getRGB());

        for (float i = 1f; i < ch - 2f; i += 1f) {
            float curHue = (float) (1f / (ch / i));
            Drawable.drawRectWH(cx + cw + 4, cy + i, 8, 1, Color.getHSBColor(curHue, 1f, 1f).getRGB());
        }

        Drawable.drawRectWH(cx + cw + 17, cy + 1, 8, ch - 3, 0xFFFFFFFF);

        Drawable.verticalGradient(cx + cw + 17, cy + 0.8, cx + cw + 25, cy + ch - 2,
                new Color(color.getRed(), color.getGreen(), color.getBlue(), 255).getRGB(),
                new Color(0, 0, 0, 0).getRGB());

        Drawable.drawRectWH(cx + cw + 3, hpos + 0.5f, 10, 1, -1);
        Drawable.drawRectWH(cx + cw + 16, apos + 0.5f, 10, 1, -1);
        RoundedShader.drawRoundOutline(spos, bpos, 3, 3, 1.5f, 1, color, new Color(-1));

        Color value = Color.getHSBColor(hue, saturation, brightness);

        if (sbfocused) {
            saturation = (float) ((MathUtil.clamp((float) (mouseX - cx), 0f, (float) cw)) / cw);
            brightness = (float) ((ch - MathUtil.clamp((float) (mouseY - cy), 0, (float) ch)) / ch);
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
        double cx = x + 4;
        double cy = y + 17;
        double cw = width - 34;
        double ch = height - 20;

        if (Drawable.isHovered(mouseX, mouseY, cx + cw + 17, cy, 8, ch) && button == 0)
            afocused = true;

        else if (Drawable.isHovered(mouseX, mouseY, cx + cw + 4, cy, 8, ch) && button == 0)
            hfocused = true;

        else if (Drawable.isHovered(mouseX, mouseY, cx, cy, cw, ch) && button == 0)
            sbfocused = true;
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

}
