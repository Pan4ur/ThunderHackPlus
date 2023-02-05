package com.mrzak34.thunderhack.gui.thundergui.components.items.buttons;

import com.mrzak34.thunderhack.gui.thundergui.ThunderGui;
import com.mrzak34.thunderhack.gui.thundergui.fontstuff.FontRender;
import com.mrzak34.thunderhack.modules.client.ThunderHackGui;
import com.mrzak34.thunderhack.setting.ColorSetting;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.render.RenderUtil;
import com.mrzak34.thunderhack.util.Timer;
import com.mrzak34.thunderhack.util.Util;
import org.lwjgl.input.Mouse;

import java.awt.*;

public class TColorPicker extends TItem{


    private final Setting colorSetting;
    public int alpha;
    public int red;
    public int green;
    public int blue;
    private final Timer timer = new Timer();


    public TColorPicker(Setting colorSetting, int x, int y) {  //127 40   //84  27
        super(colorSetting.getName());
        this.colorSetting = colorSetting;
        this.alpha = (getColorSetting().getRawColor() >> 24) & 0xff;
        this.red = (getColorSetting().getRawColor() >> 16) & 0xFF;
        this.green = (getColorSetting().getRawColor() >> 8) & 0xFF;
        this.blue = (getColorSetting().getRawColor()) & 0xFF;
        this.setLocation(x,y);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (this.y < ThunderGui.thunderguiY || this.y > ThunderGui.thunderguiY + ThunderGui.thunderguiscaleY + 43) {
            return;
        }
        if (Mouse.isButtonDown(0)) {
            this.dragSetting(mouseX, mouseY);
        }

        RenderUtil.drawRect2(this.x,this.y,this.x + 94,this.y + 90, ThunderHackGui.getInstance().buttsColor.getValue().getRawColor());
        float[] hsb = Color.RGBtoHSB(red, green, blue, null);

        FontRender.drawString3(colorSetting.getName(),(int) this.x + 3, (int) this.y + 2,new Color(red,green,blue).getRGB());
        RenderUtil.draw2DGradientRect(this.x + 3, this.y + 12, this.x + 91, this.y + 52, Color.getHSBColor(hsb[0], 0f, 0f).getRGB(), Color.getHSBColor(hsb[0], 0f, 1f).getRGB(), Color.getHSBColor(hsb[0], 1f, 0f).getRGB(), Color.getHSBColor(hsb[0], 1f, 1f).getRGB());



        float steps = 8.8f;
        for (float i = 0.0F; i + steps <= 96.8; i += steps) {
            RenderUtil.draw1DGradientRect(this.x + 3 + i, this.y + 57, this.x + 3 + steps + i, this.y + 62, Color.getHSBColor(i / 88, 1f, 1f).getRGB(), Color.getHSBColor((i + steps) / 88, 1f, 1f).getRGB());
        }



        int maxAlpha = ((0xFF) << 24) |
                ((red & 0xFF) << 16) |
                ((green & 0xFF) << 8) |
                ((blue & 0xFF));

        int minAlpha = ((0) << 24) |
                ((red & 0xFF) << 16) |
                ((green & 0xFF) << 8) |
                ((blue & 0xFF));

        RenderUtil.draw2DGradientRect(this.x + 3 , this.y + 70 , this.x + 91, this.y + 75, minAlpha, minAlpha, maxAlpha, maxAlpha);


        int indicatorColor = new Color(255, 255, 255, 140).hashCode();

        int indicatorAlpha = (int) ((this.x + 3)  + (int) ((alpha / 255f) * 88));
        RenderUtil.drawRect(indicatorAlpha,this.y + 69, 2 + indicatorAlpha,6 + this.y + 70,indicatorColor);


        int indicatorHue = (int) ((this.x + 3) + (int) (hsb[0] * 88));
        RenderUtil.drawRect(indicatorHue,this.y + 56, 2 + indicatorHue,3 + this.y + 61,indicatorColor);


        int indicatorX = (int) (this.x + 3 + (int) (hsb[1] * 88));
        int indicatorY = (int) ((40 + this.y + 12) - (int) (hsb[2] * 40));
//        this.x + 3, this.y + 12, this.x + 91, this.y + 52
        RenderUtil.drawRect(indicatorX - 1F, indicatorY - 1F, indicatorX + 1F, indicatorY + 1F, indicatorColor);
//        RenderUtil.drawRect2(this.x,this.y,this.x + 94,this.y + 90,new Color(0xB3232323, true).getRGB());
        RenderUtil.drawSmoothRect(this.x + 3, this.y + 80,this.x + 91,this.y + 88,new Color(red,green,blue).getRGB());
        String values = "R" + this.red + "/" + "G" + this.green + "/" + "B" + this.blue + "/" + "A" + this.alpha;
        Util.fr.drawString(values,(int) this.x + 5, (int) this.y + 80,getContrastColor(new Color(red,green,blue)).getRGB());

        refresh();
    }



    public static Color getContrastColor(Color color) {
        double y = (299 * color.getRed() + 587 * color.getGreen() + 114 * color.getBlue()) / 1000;
        return y >= 128 ? Color.black : Color.white;
    }

    public void refresh() {
        this.alpha = (getColorSetting().getRawColor() >> 24) & 0xff;
        this.red = (getColorSetting().getRawColor() >> 16) & 0xFF;
        this.green = (getColorSetting().getRawColor() >> 8) & 0xFF;
        this.blue = (getColorSetting().getRawColor()) & 0xFF;
    }

    private void updateColor() {
        int rgb = ((alpha & 0xFF) << 24) |
                ((red & 0xFF) << 16) |
                ((green & 0xFF) << 8) |
                ((blue & 0xFF));
        getColorSetting().setColor(rgb);
    }

    public ColorSetting getColorSetting() {
        return (ColorSetting) colorSetting.getValue();
    }

    public Setting getSetting() {
        return colorSetting;
    }


    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        handleMouseClick(mouseX, mouseY, mouseButton);
    }


    private boolean handleMouseClick(int mouseX, int mouseY, int mouseButton) {
        if (!mouseWithinBounds(mouseX, mouseY, this.x,this.y,94,90)) {
            return false;
        }
        if (mouseButton != 0) return false;
            if (mouseWithinBounds(mouseX, mouseY,this.x + 3f, this.y + 12f, 91f, 40f)) {
                getRGBfromClick(mouseX, mouseY);
            } else if (mouseWithinBounds(mouseX, mouseY, this.x + 3, this.y + 57, 88, 5)) {
                getHueFromClick(mouseX);
            } else if (mouseWithinBounds(mouseX, mouseY,this.x + 3 , this.y + 70 , 88, 5)) {
                getAlphaFromClick(mouseX);
            }
        return true;
    }


    public static boolean mouseWithinBounds(int mouseX, int mouseY, double x, double y, double width, double height) {
        return (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height);
    }


    private void getRGBfromClick(float mouseX, float mouseY) {
        float saturation = (mouseX - this.x) / 91f;

        float brightness = 1f - (mouseY - (this.y + 12)) / 40;


        float[] hsb = Color.RGBtoHSB(red, green, blue, null);
        int rgb = Color.HSBtoRGB(hsb[0], saturation, brightness);
        this.red = (rgb >> 16) & 0xFF;
        this.green = (rgb >> 8) & 0xFF;
        this.blue = (rgb) & 0xFF;
        updateColor();
    }

    private void getHueFromClick(float mouseX) {
        float hue = ((mouseX - 3 - this.x)) / 88;
        float[] hsb = Color.RGBtoHSB(red, green, blue, null);
        int rgb = Color.HSBtoRGB(hue, hsb[1], hsb[2]);
        this.red = (rgb >> 16) & 0xFF;
        this.green = (rgb >> 8) & 0xFF;
        this.blue = (rgb) & 0xFF;
        updateColor();
    }

    private void dragSetting(int mouseX, int mouseY) {
        handleMouseClick(mouseX, mouseY,0);
    }

    private void getAlphaFromClick(float mouseX) {
        this.alpha = (int) (((mouseX - 3 - this.x) / 88) * 255);
        updateColor();
    }


}
