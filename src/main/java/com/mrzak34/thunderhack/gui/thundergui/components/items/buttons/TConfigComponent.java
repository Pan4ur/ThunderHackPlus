package com.mrzak34.thunderhack.gui.thundergui.components.items.buttons;

import com.mrzak34.thunderhack.command.Command;
import com.mrzak34.thunderhack.gui.thundergui.ThunderGui;
import com.mrzak34.thunderhack.gui.thundergui.fontstuff.FontRender;
import com.mrzak34.thunderhack.manager.ConfigManager;
import com.mrzak34.thunderhack.modules.client.ThunderHackGui;
import com.mrzak34.thunderhack.util.render.RenderUtil;
import com.mrzak34.thunderhack.util.Util;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class TConfigComponent extends TItem{

    private final String configname;

    public TConfigComponent(String configname,int x,int y) {
        super(configname);
        this.configname = configname;
        this.setLocation(x,y);
    }

    ResourceLocation configpng = new ResourceLocation("textures/configpng.png");
    ResourceLocation loadpng = new ResourceLocation("textures/loadpng.png");
    ResourceLocation bin = new ResourceLocation("textures/trashbinnigga.png");


    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {

        if(this.y + 26 < 81 + ThunderGui.thunderguiY){
            return;
        }



        RenderUtil.drawRect2(this.x, this.y, this.x + 432, 26 + this.y, ThunderHackGui.getInstance().LeftsideColor.getValue().getRawColor());
        FontRender.drawString(configname,this.x + 30, this.y + 3, new Color(0xC5C5C5).getRGB());

      //  FontRender.drawString3("fsdfdf",(int) this.x + 30,(int) this.y + 16, new Color(0x777777).getRGB());

        if(isHoveringItem(this.x + 410, this.y + 5,this.x + 426,this.y + 21,mouseX,mouseY)) {
            RenderUtil.drawSmoothRect(this.x + 410, this.y + 5, this.x + 426, this.y + 21, new Color(0xFD4C4E).getRGB());
        } else {
            RenderUtil.drawSmoothRect(this.x + 410, this.y + 5, this.x + 426, this.y + 21, new Color(0xFC0303).getRGB());
        }

        if(isHoveringItem(this.x + 380, this.y + 5, this.x + 416, this.y + 21, mouseX,mouseY)) {
            RenderUtil.drawSmoothRect(this.x + 380, this.y + 5, this.x + 396, this.y + 21, new Color(0xD9D8D8).getRGB());
        } else {
            RenderUtil.drawSmoothRect(this.x + 380, this.y + 5, this.x + 396, this.y + 21, new Color(0x9F9E9E).getRGB());
        }


        drawImage(configpng,this.x + 2, this.y + 2,22, 22, new Color(0x606060));

        if(isHoveringItem(this.x + 410, this.y + 5,this.x + 426,this.y + 21,mouseX,mouseY)) {
            drawImage(bin, this.x + 411, this.y + 6, 13, 13, new Color(0x606060));
        } else {
            drawImage(bin, this.x + 411, this.y + 6, 13, 13, new Color(0xFCFCFC));
        }

        if(isHoveringItem(this.x + 380, this.y + 5, this.x + 396, this.y + 21,mouseX,mouseY)) {
            drawImage(loadpng, this.x + 381, this.y + 6, 13, 13, new Color(0xFFFFFF));
        } else {
            drawImage(loadpng,this.x + 381, this.y + 6, 13, 13, new Color(0x969696));
        }


    }



    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if(this.y + 26 < 81 + ThunderGui.thunderguiY){
            return;
        }
        if(isHoveringItem(this.x + 410, this.y + 5,this.x + 426,this.y + 21,mouseX,mouseY)){
            boolean a =  ConfigManager.delete(configname);
            if(a){
                Command.sendMessage("Удален конфиг " + configname);
            }
        }
        if(isHoveringItem(this.x + 380, this.y + 5, this.x + 396, this.y + 21,mouseX,mouseY)) {
            ConfigManager.load(configname);
        }

    }

    public boolean isHoveringItem(float x, float y, float x1, float y1, float mouseX, float mouseY){
        return (mouseX >= x && mouseY >= y && mouseX <= x1 && mouseY <= y1);
    }

    public static void drawImage(ResourceLocation resourceLocation, float x, float y, float width, float height, Color color) {

        GL11.glPushMatrix();
        GL11.glDisable(2929);
        GL11.glEnable(3042);
        GL11.glDepthMask(false);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        setColor(color.getRGB());
        Util.mc.getTextureManager().bindTexture(resourceLocation);
        Gui.drawModalRectWithCustomSizedTexture((int) x, (int) y, 0.0F, 0.0F, (int) width, (int) height, width, height);
        GL11.glDepthMask(true);
        GL11.glDisable(3042);
        GL11.glEnable(2929);
        GL11.glPopMatrix();
    }


    public static void setColor(int color) {
        GL11.glColor4ub((byte)(color >> 16 & 0xFF), (byte)(color >> 8 & 0xFF), (byte)(color & 0xFF), (byte)(color >> 24 & 0xFF));
    }

    public static void setColor(Color color, float alpha) {
        float red = color.getRed() / 255.0F;
        float green = color.getGreen() / 255.0F;
        float blue = color.getBlue() / 255.0F;
        GlStateManager.color(red, green, blue, alpha);
    }


}