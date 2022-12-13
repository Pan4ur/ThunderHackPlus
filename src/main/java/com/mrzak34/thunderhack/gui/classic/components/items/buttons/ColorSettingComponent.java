package com.mrzak34.thunderhack.gui.classic.components.items.buttons;

import com.mrzak34.thunderhack.gui.thundergui.fontstuff.FontRender;
import com.mrzak34.thunderhack.modules.client.ClickGui;
import com.mrzak34.thunderhack.setting.ColorSetting;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.input.Mouse;

import java.awt.*;

import static com.mrzak34.thunderhack.gui.misc.GuiMiddleClickMenu.mouseWithinBounds;


public class ColorSettingComponent extends Button {
    private final Setting colorSetting;
    public int alpha;
    public int red;
    public int green;
    public int blue;

    float sliderWidth = 45F;
    float steps = sliderWidth / 10F;
    float sliderX = getX() + 1F;
    float sliderY = getY() + 48F;
    float sliderHeight = 10F;

    float pickerBoxX = getX() + 1F;
    float pickerBoxY = getY() + 18F;
    float pickerBoxHeight = 26F;
    float pickerBoxWidth = 44F;
    int niggerh = 15;


    private final Timer timer = new Timer();

    public ColorSettingComponent(Setting colorSetting) {
        super(colorSetting.getName());
        this.colorSetting = colorSetting;
        this.alpha = (getColorSetting().getRawColor() >> 24) & 0xff;
        this.red = (getColorSetting().getRawColor() >> 16) & 0xFF;
        this.green = (getColorSetting().getRawColor() >> 8) & 0xFF;
        this.blue = (getColorSetting().getRawColor()) & 0xFF;
        this.height = niggerh;
    }



    public void refresh() {
        this.alpha = (getColorSetting().getRawColor() >> 24) & 0xff;
        this.red = (getColorSetting().getRawColor() >> 16) & 0xFF;
        this.green = (getColorSetting().getRawColor() >> 8) & 0xFF;
        this.blue = (getColorSetting().getRawColor()) & 0xFF;
    }

    public boolean isOpen() {
        return true;
    }



    int color = ColorUtil.toARGB(ClickGui.getInstance().mainColor.getValue().getRed(), ClickGui.getInstance().mainColor.getValue().getGreen(), ClickGui.getInstance().mainColor.getValue().getBlue(), 170);


    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        color = ColorUtil.toARGB(ClickGui.getInstance().mainColor.getValue().getRed(), ClickGui.getInstance().mainColor.getValue().getGreen(), ClickGui.getInstance().mainColor.getValue().getBlue(), 170);

        if (Mouse.isButtonDown(0)) {
            this.dragSetting(mouseX, mouseY);
        }

        float[] hsb = Color.RGBtoHSB(red, green, blue, null);

      //  GuiRenderHelper.drawRect(getX(), getY(), 15, 12F, color);

        color = new Color(96, 96, 96, 100).hashCode();


       // GuiRenderHelper.drawRect(getX(), getY(), getWidth(), 12F, color);
        FontRender.drawString5(getName(), (int) (getX() + 5.0F), (int) (getY() + 8f - (ClickGUIFontRenderWrapper.getFontHeight() / 2) - 0.5F), -1);



            GuiRenderHelper.drawRect(getX(), getY() + 17F, 46F, 28F, color);

            pickerBoxX = getX() + 1F;
            pickerBoxY = getY() + 18F;

            RenderUtil.draw2DGradientRect(pickerBoxX, pickerBoxY, pickerBoxX + pickerBoxWidth, pickerBoxY + pickerBoxHeight, Color.getHSBColor(hsb[0], 0f, 0f).getRGB(), Color.getHSBColor(hsb[0], 0f, 1f).getRGB(), Color.getHSBColor(hsb[0], 1f, 0f).getRGB(), Color.getHSBColor(hsb[0], 1f, 1f).getRGB());

            GuiRenderHelper.drawRect(getX(), getY() + 47F, 46F, 12F, color);

            sliderX = getX() + 1F;
            sliderY = getY() + 48F;

            for (float i = 0.0F; i + steps <= sliderWidth; i += steps) {
                RenderUtil.draw1DGradientRect(sliderX + i, sliderY, sliderX + steps + i, sliderY + sliderHeight, Color.getHSBColor(i / sliderWidth, 1f, 1f).getRGB(), Color.getHSBColor((i + steps) / sliderWidth, 1f, 1f).getRGB());
            }

            GuiRenderHelper.drawRect(getX() + 45F, getY() + 47F, 1F, 12F, color);

            int maxAlpha = ((0xFF) << 24) |
                    ((red & 0xFF) << 16) |
                    ((green & 0xFF) << 8) |
                    ((blue & 0xFF));

            int minAlpha = ((0) << 24) |
                    ((red & 0xFF) << 16) |
                    ((green & 0xFF) << 8) |
                    ((blue & 0xFF));

            float alphaX = getX() + getWidth() - 1F;
            float alphaY = getY() + 17F;
            float alphaWidth = 10F;
            float alphaHeight = 42F;


            RenderUtil.draw2DGradientRect(alphaX , alphaY , getX() + getWidth() + 9 , getY() + 60f - 2F, minAlpha, maxAlpha, minAlpha, maxAlpha);

            // DRAW RGBA TEXT

            ClickGUIFontRenderWrapper.drawString("R" + red, (int) getX() + 3 + (int) sliderWidth, (int) getY() + 16, 0xFFFFFF);
            ClickGUIFontRenderWrapper.drawString("G" + green, (int) getX() + 3 + (int) sliderWidth, (int) getY() + 18 + ClickGUIFontRenderWrapper.getStringHeight("RGB0:1234567890") - 0.5F, 0xFFFFFF);
            ClickGUIFontRenderWrapper.drawString("B" + blue, (int) getX() + 3 + (int) sliderWidth, (int) getY() + 20 + (ClickGUIFontRenderWrapper.getStringHeight("RGB0:1234567890") * 2) - 0.5F, 0xFFFFFF);

            // DRAW CHECKBOX

            if (getColorSetting().isCycle()) {
                GuiRenderHelper.drawOutlineRect(getX() + 3 + sliderWidth, (int) getY() + 22 + (ClickGUIFontRenderWrapper.getFontHeight() * 3), 10, 10, 2f, getColorSetting().getColor());
            }

            GuiRenderHelper.drawRect(getX() + 4 + sliderWidth, (int) getY() + 23 + (ClickGUIFontRenderWrapper.getFontHeight() * 3), 8, 8, getColorSetting().getColor());

            ClickGUIFontRenderWrapper.drawString("RB", (int) (getX() + 15 + sliderWidth), (int) getY() + 22 + (ClickGUIFontRenderWrapper.getFontHeight() * 3), 0xFFFFFF);

            // DRAW INDICATORS

            int indicatorColor = new Color(255, 255, 255, 140).hashCode();

            // HUE INDICATOR
            int indicatorHue = (int) (sliderX + (int) (hsb[0] * sliderWidth));
            GuiRenderHelper.drawRect(indicatorHue, sliderY, 2F, sliderHeight, indicatorColor);

            // ALPHA INDICATOR
            int indicatorAlpha = (int) ((alphaHeight + alphaY) - (int) ((alpha / 255f) * alphaHeight));
            GuiRenderHelper.drawRect(alphaX, MathHelper.clamp(indicatorAlpha - 1F, alphaY, alphaY + alphaHeight), alphaWidth, 2f, indicatorColor);

            int indicatorX = (int) (pickerBoxX + (int) (hsb[1] * pickerBoxWidth));
            int indicatorY = (int) ((pickerBoxHeight + pickerBoxY) - (int) (hsb[2] * pickerBoxHeight));

            GuiRenderHelper.drawRect(indicatorX - 1F, indicatorY - 1F, 2F, 2F, indicatorColor);
    }
    public static int clipboard = -1;




    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {

        if (mouseWithinBounds(mouseX, mouseY, getX(), getY(), getWidth(), 12F)) {
            if (mouseButton == 2) {
                if (clipboard == -1) {
                    clipboard = getColorSetting().getRawColor();
                } else {
                    getColorSetting().setColor(clipboard);
                    clipboard = -1;
                    refresh();
                }
            } else {
                Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
               // setOpen(!isOpen());
            }
        }

        handleMouseClick(mouseX, mouseY, mouseButton);
    }






    private void dragSetting(int mouseX, int mouseY) {
        handleMouseClick(mouseX, mouseY,0);
    }



    private boolean handleMouseClick(int mouseX, int mouseY, int mouseButton) {
        if (!mouseWithinBounds(mouseX, mouseY, getX(), getY(), getWidth() + 20, 60f)) {
            return false;
        }
        float alphaX = getX() + getWidth() - 1F;
        if (mouseButton != 0) return false;
        float alphaY = getY() + 17F;
        if (isOpen()) {
            if (mouseWithinBounds(mouseX, mouseY, pickerBoxX, pickerBoxY, pickerBoxWidth, pickerBoxHeight - 1F)) {
                getRGBfromClick(mouseX, mouseY);
            } else if (mouseWithinBounds(mouseX, mouseY, sliderX, sliderY, sliderWidth - 2F, sliderHeight)) {
                getHueFromClick(mouseX);
            } else if (mouseWithinBounds(mouseX, mouseY, alphaX, alphaY , 8f , 42F)) {
                getAlphaFromClick(mouseY);

            } else if (mouseWithinBounds(mouseX, mouseY, getX() + 4 + sliderWidth, getY() + 25 + (ClickGUIFontRenderWrapper.getFontHeight() * 3), 8, 8)) {
                if (timer.passedMs(500)) {
                    getColorSetting().toggleCycle();
                    timer.reset();
                }
            }
        }
        return true;
    }




    private void getRGBfromClick(float mouseX, float mouseY) {
        mouseX = mouseX - getX();
        mouseY = mouseY - getY();

        float sat = (mouseX - 1F) / pickerBoxWidth;

        float bri = 1f - (mouseY - 18F) / pickerBoxHeight;

        float[] hsb = Color.RGBtoHSB(red, green, blue, null);
        int rgb = Color.HSBtoRGB(hsb[0], sat, bri);
        this.red = (rgb >> 16) & 0xFF;
        this.green = (rgb >> 8) & 0xFF;
        this.blue = (rgb) & 0xFF;
        updateColor();
    }

    private void getHueFromClick(float mouseX) {
        mouseX = mouseX - getX();
        float hue = (mouseX - 1F) / sliderWidth;
        float[] hsb = Color.RGBtoHSB(red, green, blue, null);
        int rgb = Color.HSBtoRGB(hue, hsb[1], hsb[2]);
        this.red = (rgb >> 16) & 0xFF;
        this.green = (rgb >> 8) & 0xFF;
        this.blue = (rgb) & 0xFF;
        updateColor();
    }

    private void getAlphaFromClick(float mouseY) {
        mouseY = mouseY - getY();
        this.alpha = 255 - (int) (((mouseY - 17F) / 42F) * 255);
        updateColor();
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
}