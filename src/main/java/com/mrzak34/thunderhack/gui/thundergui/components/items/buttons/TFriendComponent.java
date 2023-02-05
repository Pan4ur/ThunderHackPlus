package com.mrzak34.thunderhack.gui.thundergui.components.items.buttons;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.gui.thundergui.ThunderGui;
import com.mrzak34.thunderhack.gui.thundergui.fontstuff.FontRender;
import com.mrzak34.thunderhack.modules.client.ThunderHackGui;
import com.mrzak34.thunderhack.util.PNGtoResourceLocation;
import com.mrzak34.thunderhack.util.render.RenderUtil;
import com.mrzak34.thunderhack.util.Util;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;

import static com.mrzak34.thunderhack.gui.thundergui.ThunderGui.drawCompleteImage;

public class TFriendComponent extends TItem{

    private final String nickname;

    public TFriendComponent(String nickname,int x,int y) {
        super(nickname);
        this.nickname = nickname;
        this.setLocation(x,y);

        head = PNGtoResourceLocation.getTexture2(nickname, "png");
    }
    ResourceLocation head = null;
    ResourceLocation crackedSkin = new ResourceLocation("textures/cracked.png");
    ResourceLocation bin = new ResourceLocation("textures/trashbinnigga.png");


    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {

        if(this.y + 26 < 81 + ThunderGui.thunderguiY){
            return;
        }



        RenderUtil.drawRect2(this.x, this.y, this.x + 432, 26 + this.y, ThunderHackGui.getInstance().LeftsideColor.getValue().getRawColor());
        FontRender.drawString(nickname,this.x + 30, this.y + 3, new Color(0xC5C5C5).getRGB());
        FontRender.drawString3(checkOnline(nickname) ? "Online" : "Offline",(int) this.x + 30,(int) this.y + 16, checkOnline(nickname) ? new Color(0x01DC0A).getRGB() : new Color(0x777777).getRGB());

        if(isHoveringItem(this.x + 410, this.y + 5,this.x + 426,this.y + 21,mouseX,mouseY)) {
            RenderUtil.drawSmoothRect(this.x + 410, this.y + 5, this.x + 426, this.y + 21, new Color(0xFD4C4E).getRGB());
        } else {
            RenderUtil.drawSmoothRect(this.x + 410, this.y + 5, this.x + 426, this.y + 21, new Color(0xFC0303).getRGB());
        }

        if(head != null){
            Util.mc.getTextureManager().bindTexture(head);
           drawCompleteImage(this.x + 2, this.y + 2,22, 22);
        } else {
            Util.mc.getTextureManager().bindTexture(crackedSkin);
            drawCompleteImage(this.x + 2, this.y + 2,22, 22);
        }
        if(isHoveringItem(this.x + 410, this.y + 5,this.x + 426,this.y + 21,mouseX,mouseY)) {
            drawImage(bin, this.x + 411, this.y + 6, 14, 14, new Color(0x606060));
        } else {
            drawImage(bin, this.x + 411, this.y + 6, 13, 13, new Color(0xFCFCFC));

        }
    }



    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if(this.y + 26 < 81 + ThunderGui.thunderguiY){
            return;
        }
        if(isHoveringItem(this.x + 410, this.y + 5,this.x + 426,this.y + 21,mouseX,mouseY)){
            Thunderhack.friendManager.removeFriend(this.nickname);
        }

    }

    public boolean isHoveringItem(float x, float y, float x1, float y1, float mouseX, float mouseY){
        return (mouseX >= x && mouseY >= y && mouseX <= x1 && mouseY <= y1);
    }

    public boolean checkOnline(String name){
        if(mc.player.connection.getPlayerInfo(name) != null){
            return true;
        } else {
            return false;
        }
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
