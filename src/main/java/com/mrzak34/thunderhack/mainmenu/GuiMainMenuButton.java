package com.mrzak34.thunderhack.mainmenu;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;

import java.awt.*;

import static com.mrzak34.thunderhack.gui.thundergui.components.items.buttons.TFriendComponent.drawImage;
import static com.mrzak34.thunderhack.util.RenderHelper.drawBlurredShadow;
import static com.mrzak34.thunderhack.util.Util.mc;

public class GuiMainMenuButton extends GuiButton {
    ResourceLocation power = new ResourceLocation("textures/powerbut.png");

    int imgid;
    private int fade = 20;
    static ScaledResolution sr;

    public GuiMainMenuButton(int buttonId, int x, int y,int imageId ) {
        super(buttonId, x, y,"m");
        sr = new ScaledResolution(mc);
        imgid = imageId;
    }


    public static int getMouseX() {
        return Mouse.getX() * sr.getScaledWidth() / Minecraft.getMinecraft().displayWidth;
    }

    public static int getMouseY() {
        return sr.getScaledHeight() - Mouse.getY() * sr.getScaledHeight() / Minecraft.getMinecraft().displayHeight - 1;
    }

    public void drawButton(Minecraft mc, int mouseX, int mouseY, float mouseButton) {
        if (this.visible) {
            this.hovered = (mouseX >= this.x && mouseY >= (this.y) && mouseX < this.x + this.width && mouseY < (this.y) + this.height + 10);
            if (this.hovered) {
                if (this.fade < 100)
                    this.fade += 8;
            } else {
                if (this.fade > 20)
                    this.fade -= 8;
            }
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.blendFunc(770, 771);
            Color color = new Color(255 - fade, 255 - fade, 255 - fade, 255);

            if(imgid == 0){
                drawImage(power, x,y, 30,30,color);
            }



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