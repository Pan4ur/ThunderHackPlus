package com.mrzak34.thunderhack.gui.thundergui2.components;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.command.Command;
import com.mrzak34.thunderhack.gui.clickui.ColorUtil;
import com.mrzak34.thunderhack.gui.fontstuff.FontRender;
import com.mrzak34.thunderhack.gui.hud.Particles;
import com.mrzak34.thunderhack.gui.thundergui2.ThunderGui2;
import com.mrzak34.thunderhack.manager.ConfigManager;
import com.mrzak34.thunderhack.util.PNGtoResourceLocation;
import com.mrzak34.thunderhack.util.RoundedShader;
import com.mrzak34.thunderhack.util.Util;
import com.mrzak34.thunderhack.util.render.Drawable;
import com.mrzak34.thunderhack.util.render.RenderUtil;
import com.mrzak34.thunderhack.util.render.Stencil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;


public class FriendComponent {
    private String name;

    private int posX;
    private int posY;
    private int progress;
    private int fade;
    private int index;
    private boolean first_open = true;
    float scroll_animation = 0f;
    private float scrollPosY;
    private float prevPosY;

    ResourceLocation head = null;
    ResourceLocation crackedSkin = new ResourceLocation("textures/cracked.png");

    public FriendComponent(String name, int posX, int posY,int index){
        this.name = name;
        this.posX = posX;
        this.posY = posY;
        fade = 0;
        this.index = index * 5;
        head = PNGtoResourceLocation.getTexture2(name, "png");
        scrollPosY = posY;
        scroll_animation = 0f;
    }


    public void render(int MouseX, int MouseY){
        if(scrollPosY != posY) {
            scroll_animation = ThunderGui2.fast(scroll_animation, 1, 15f);
            posY = (int) RenderUtil.interpolate(scrollPosY,prevPosY,scroll_animation);
        }
        if((posY > ThunderGui2.getInstance().main_posY + ThunderGui2.getInstance().height) || posY < ThunderGui2.getInstance().main_posY){
            return;
        }
        RoundedShader.drawRound(posX + 5, posY, 285, 30, 4f, ColorUtil.applyOpacity(new Color(44, 35, 52, 255),getFadeFactor()));

        if(first_open){
            GL11.glPushMatrix();
            Stencil.write(false);
            Particles.roundedRect(posX - 0.5 + 5, posY - 0.5, 286, 31, 8, ColorUtil.applyOpacity(new Color(0, 0, 0, 255),getFadeFactor()));
            Stencil.erase(true);
            RenderUtil.drawBlurredShadow(MouseX - 20,MouseY - 20,40,40, (int) (60), ColorUtil.applyOpacity(new Color(0xC3555A7E, true),getFadeFactor()));
            Stencil.dispose();
            GL11.glPopMatrix();
            first_open = false;
        }

        if(isHovered(MouseX,MouseY)){
            GL11.glPushMatrix();
            Stencil.write(false);
            Particles.roundedRect(posX - 0.5 + 5, posY - 0.5, 286, 31, 8, ColorUtil.applyOpacity(new Color(0, 0, 0, 255),getFadeFactor()));
            Stencil.erase(true);
            RenderUtil.drawBlurredShadow(MouseX - 20,MouseY - 20,40,40, 60, ColorUtil.applyOpacity(new Color(0xC3555A7E, true),getFadeFactor()));
            Stencil.dispose();
            GL11.glPopMatrix();
        }

        RoundedShader.drawRound(posX + 266, posY + 8, 14, 14, 2f, ColorUtil.applyOpacity(new Color(25, 20, 30, 255),getFadeFactor()));

        if(Drawable.isHovered(MouseX,MouseY,posX + 268, posY + 10, 10, 10)) {
            RoundedShader.drawRound(posX + 268, posY + 10, 10, 10, 2f, ColorUtil.applyOpacity(new Color(65, 1, 13, 255), getFadeFactor()));
        } else {
            RoundedShader.drawRound(posX + 268, posY + 10, 10, 10, 2f, ColorUtil.applyOpacity(new Color(94, 1, 18, 255), getFadeFactor()));
        }
        FontRender.drawIcon("w",posX + 268, posY + 13, ColorUtil.applyOpacity(-1,getFadeFactor()));

        GL11.glPushMatrix();
        Stencil.write(false);
        Particles.roundedRect(posX + 10, posY + 3,22, 22, 8, ColorUtil.applyOpacity(new Color(0, 0, 0, 255),getFadeFactor()));
        Stencil.erase(true);
        if(head != null){
            Util.mc.getTextureManager().bindTexture(head);
            drawCompleteImage(posX + 10, posY + 3,22, 22,ColorUtil.applyOpacity(new Color(255, 255, 255, 255),getFadeFactor()));
        } else {
            Util.mc.getTextureManager().bindTexture(crackedSkin);
            drawCompleteImage(posX + 10, posY + 3,22, 22,ColorUtil.applyOpacity(new Color(255, 255, 255, 255),getFadeFactor()));
        }
        Stencil.dispose();
        GL11.glPopMatrix();

        FontRender.drawString6(name,posX + 37,posY + 6,ColorUtil.applyOpacity(-1,getFadeFactor()),false);

        boolean online = Util.mc.player.connection.getPlayerInfo(name) != null;

        FontRender.drawString7( online ? "online" : "offline",posX + 37,posY + 17,online ? ColorUtil.applyOpacity(new Color(0xFF0B7A00, true).getRGB(),getFadeFactor()) : ColorUtil.applyOpacity(new Color(0xFFBDBDBD, true).getRGB(),getFadeFactor()),false);
    }

    private float getFadeFactor(){
        return fade / (5f + index);
    }


    public void onTick(){
        if(progress  > 4){
            progress = 0;
        }
        progress++;

        if(fade < 10 + index){
            fade++;
        }
    }



    private boolean isHovered(int mouseX, int mouseY){
        return mouseX > posX && mouseX < posX + 295 && mouseY > posY && mouseY < posY + 30;
    }

    public void movePosition(float deltaX, float deltaY) {
        this.posY += deltaY;
        this.posX += deltaX;
        scrollPosY = posY;
    }

    public void mouseClicked(int MouseX, int MouseY, int clickedButton) {
        if((posY > ThunderGui2.getInstance().main_posY + ThunderGui2.getInstance().height) || posY < ThunderGui2.getInstance().main_posY){
            return;
        }
        if(Drawable.isHovered(MouseX,MouseY,posX + 268, posY + 10, 10, 10)){
            Thunderhack.friendManager.removeFriend(name);
            ThunderGui2.getInstance().loadFriends();
        }
    }

    public double getPosX() {
        return this.posX;
    }

    public double getPosY() {
        return this.posY;
    }

    public static void drawCompleteImage(float posX, float posY, int width, int height,Color color) {
        GlStateManager.color(color.getRed() / 255f, color.getGreen() / 255f,color.getBlue() / 255f,color.getAlpha() / 255f);
        GL11.glPushMatrix();
        GL11.glTranslatef(posX, posY, 0.0f);
        GL11.glBegin(7);
        GL11.glTexCoord2f(0.0f, 0.0f);
        GL11.glVertex3f(0.0f, 0.0f, 0.0f);
        GL11.glTexCoord2f(0.0f, 1.0f);
        GL11.glVertex3f(0.0f, (float) height, 0.0f);
        GL11.glTexCoord2f(1.0f, 1.0f);
        GL11.glVertex3f((float) width, (float) height, 0.0f);
        GL11.glTexCoord2f(1.0f, 0.0f);
        GL11.glVertex3f((float) width, 0.0f, 0.0f);
        GL11.glEnd();
        GL11.glPopMatrix();
    }

    public void scrollElement(float deltaY){
        scroll_animation = 0;
        prevPosY = posY;
        this.scrollPosY += deltaY;
    }
}
