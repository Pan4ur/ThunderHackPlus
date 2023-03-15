package com.mrzak34.thunderhack.gui.mainmenu;

import com.mrzak34.thunderhack.gui.fontstuff.FontRender;
import com.mrzak34.thunderhack.util.RoundedShader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.SoundEvents;
import org.lwjgl.input.Mouse;

import java.awt.*;

import static com.mrzak34.thunderhack.util.Util.mc;

public class GuiMainMenuButton extends GuiButton {

    static ScaledResolution sr;
    boolean _double;
    boolean alt;

    String name;

    public GuiMainMenuButton(int buttonId, int x, int y, boolean _double, String name, boolean alt) {
        super(buttonId, x, y, name);
        sr = new ScaledResolution(mc);
        this._double = _double;
        if (_double) {
            setWidth(222);
        } else {
            setWidth(107);
        }
        this.name = name;
        this.alt = alt;
    }


    public static int getMouseX() {
        return Mouse.getX() * sr.getScaledWidth() / Minecraft.getMinecraft().displayWidth;
    }

    public static int getMouseY() {
        return sr.getScaledHeight() - Mouse.getY() * sr.getScaledHeight() / Minecraft.getMinecraft().displayHeight - 1;
    }

    public void drawButton(Minecraft mc, int mouseX, int mouseY, float mouseButton) {
        if (this.visible) {
            this.hovered = (mouseX >= this.x && mouseY >= (this.y) && mouseX < this.x + this.width && mouseY < (this.y) + 35);

            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.blendFunc(770, 771);

            Color color = new Color(0x86000000, true);

            GlStateManager.pushMatrix();


            if (!alt) {
                if (_double) {
                    RoundedShader.drawGradientRound(hovered ? x + 1 : x, hovered ? y + 1 : y, hovered ? 220 : 222, hovered ? 33 : 35, 7f, color, color, color, color);
                    FontRender.drawCentString6(name, x + 111, y + 17f, hovered ? new Color(0x7A7A7A).getRGB() : -1);
                } else {
                    RoundedShader.drawGradientRound(hovered ? x + 1 : x, hovered ? y + 1 : y, hovered ? 105 : 107, hovered ? 33 : 35, 7f, color, color, color, color);
                    FontRender.drawCentString6(name, x + 53.5f, y + 17f, hovered ? new Color(0x7A7A7A).getRGB() : -1);
                }
            } else {
                if (_double) {
                    RoundedShader.drawGradientRound(hovered ? x + 1 : x, hovered ? y + 1 : y, hovered ? 237 : 239, hovered ? 28 : 30, 7f, color, color, color, color);
                    FontRender.drawCentString6(name, x + 124.5f, y + 15f, hovered ? new Color(0x7A7A7A).getRGB() : -1);
                } else {
                    RoundedShader.drawGradientRound(hovered ? x + 1 : x, hovered ? y + 1 : y, hovered ? 113 : 115, hovered ? 28 : 30, 7f, color, color, color, color);
                    FontRender.drawCentString6(name, x + 53.5f, y + 15f, hovered ? new Color(0x7A7A7A).getRGB() : -1);
                }
            }

            GlStateManager.popMatrix();


            this.mouseDragged(mc, mouseX, mouseY);
        }
    }

    protected void mouseDragged(Minecraft mc, int mouseX, int mouseY) {
    }

    public void mouseReleased(int mouseX, int mouseY) {

    }

    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        return (this.enabled && this.visible && mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height + 10);
    }

    public boolean isMouseOver() {
        return this.hovered;
    }

    public void drawButtonForegroundLayer(int mouseX, int mouseY) {

    }

    public void playPressSound(SoundHandler soundHandlerIn) {
        soundHandlerIn.playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    public int getButtonWidth() {
        return this.width;
    }

    public void setWidth(int width) {
        this.width = width;
    }
}