package com.mrzak34.thunderhack.gui.thundergui2.components;

import com.mrzak34.thunderhack.gui.clickui.ColorUtil;
import com.mrzak34.thunderhack.gui.hud.Particles;
import com.mrzak34.thunderhack.gui.fontstuff.FontRender;
import com.mrzak34.thunderhack.gui.thundergui2.ThunderGui2;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.modules.client.ThunderHackGui;
import com.mrzak34.thunderhack.setting.Bind;
import com.mrzak34.thunderhack.util.RoundedShader;
import com.mrzak34.thunderhack.util.Util;
import com.mrzak34.thunderhack.util.render.RenderUtil;
import com.mrzak34.thunderhack.util.render.Stencil;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Objects;

public class ModulePlate {

    private Module module;


    private int posX;
    private int posY;

    private float scrollPosY;
    private float prevPosY;

    private int progress;
    private ScaledResolution sr;
    private int fade;
    private int index;
    private boolean first_open = true;
    private boolean listening_bind = false;

    float scroll_animation = 0f;


    public ModulePlate(Module module, int posX, int posY,int index){
        this.module = module;
        this.posX = posX;
        this.posY = posY;
        sr = new ScaledResolution(Util.mc);
        fade = 0;
        this.index = index * 5;
        scrollPosY = posY;
        scroll_animation = 0;
    }


    public void render(int MouseX, int MouseY){

        if(scrollPosY != posY) {
            scroll_animation = ThunderGui2.fast(scroll_animation, 1, 15f);
            posY = (int) RenderUtil.interpolate(scrollPosY,prevPosY,scroll_animation);
        }


        if((posY > ThunderGui2.getInstance().main_posY + ThunderGui2.getInstance().height) || posY < ThunderGui2.getInstance().main_posY){
            return;
        }

        if(module.isOn()) {
            RoundedShader.drawGradientRound(posX, posY, 90, 30, 4f,
                    ColorUtil.applyOpacity(new Color(25, 20, 30, 255),getFadeFactor()),
                    ColorUtil.applyOpacity(new Color(25, 20, 30, 255),getFadeFactor()),
                    ColorUtil.applyOpacity(ThunderHackGui.getInstance().onColor1.getValue().getColorObject(),getFadeFactor()),
                    ColorUtil.applyOpacity(ThunderHackGui.getInstance().onColor2.getValue().getColorObject(),getFadeFactor()));
        } else {
            RoundedShader.drawRound(posX, posY, 90, 30, 4f, ColorUtil.applyOpacity(new Color(25, 20, 30, 255),getFadeFactor()));
        }

        if(first_open){
            GL11.glPushMatrix();
            Stencil.write(false);
            Particles.roundedRect(posX - 0.5, posY - 0.5, 91, 31, 8, ColorUtil.applyOpacity(new Color(0, 0, 0, 255),getFadeFactor()));
            Stencil.erase(true);
            RenderUtil.drawBlurredShadow(MouseX - 20,MouseY - 20,40,40, (int) (60), ColorUtil.applyOpacity(new Color(0xC3555A7E, true),getFadeFactor()));
            Stencil.dispose();
            GL11.glPopMatrix();
            first_open = false;
        }

        if(isHovered(MouseX,MouseY)){
            GL11.glPushMatrix();
            Stencil.write(false);
            Particles.roundedRect(posX - 0.5, posY - 0.5, 91, 31, 8, ColorUtil.applyOpacity(new Color(0, 0, 0, 255),getFadeFactor()));
            Stencil.erase(true);
            RenderUtil.drawBlurredShadow(MouseX - 20,MouseY - 20,40,40, (int) (60), ColorUtil.applyOpacity(new Color(0xC3555A7E, true),getFadeFactor()));
            Stencil.dispose();
            GL11.glPopMatrix();
        }


        GL11.glPushMatrix();
        Stencil.write(false);
        Particles.roundedRect(posX - 0.5, posY - 0.5, 91, 31, 8, ColorUtil.applyOpacity(new Color(0, 0, 0, 255),getFadeFactor()));
        Stencil.erase(true);
        if(ThunderGui2.selected_plate != this)
            FontRender.drawIcon("H", (int) (posX + 80f), (int) (posY + 22f),ColorUtil.applyOpacity(new Color(0xFFECECEC, true).getRGB(),getFadeFactor()));
        else {
            String gear = "H";
            switch (progress){
                case 0:
                    gear = "H";
                    break;
                case 1:
                    gear = "N";
                    break;
                case 2:
                    gear = "O";
                    break;
                case 3:
                    gear = "P";
                    break;
                case 4:
                    gear = "Q";
                    break;
            }
            FontRender.drawBigIcon(gear, (int) (posX + 80f), (int) (posY + 5f),ColorUtil.applyOpacity(new Color(0xFF646464, true).getRGB(),getFadeFactor()));
        }
        Stencil.dispose();
        GL11.glPopMatrix();

        FontRender.drawString6(module.getName(),posX + 5,posY + 5,ColorUtil.applyOpacity(-1,getFadeFactor()),false);


        if(listening_bind){
            FontRender.drawString6("...", posX + 85 - FontRender.getStringWidth6(module.getBind().toString()), posY + 5, ColorUtil.applyOpacity(new Color(0xB0B0B0), getFadeFactor()).getRGB(), false);
        } else if(!Objects.equals(module.getBind().toString(), "None")) {
            FontRender.drawString6(module.getBind().toString(), posX + 85 - FontRender.getStringWidth6(module.getBind().toString()), posY + 5, ColorUtil.applyOpacity(new Color(0xB0B0B0), getFadeFactor()).getRGB(), false);
        }

        String[] splitString = module.getDescription().split("-");
        if(splitString[0] != null && !splitString[0].equals("")) {
            FontRender.drawString7(splitString[0],posX + 5,posY + 13,ColorUtil.applyOpacity(new Color(0xFFBDBDBD, true).getRGB(),getFadeFactor()),false);
        }
        if(splitString.length  > 1) {
            if (splitString[1] != null && !splitString[1].equals("")) {
                FontRender.drawString7(splitString[1],posX + 5,posY + 18,ColorUtil.applyOpacity(new Color(0xFFBDBDBD, true).getRGB(),getFadeFactor()),false);
            }
        }
        if(splitString.length == 3) {
            if (splitString[2] != null && !splitString[2].equals("")) {
                FontRender.drawString7(splitString[2],posX + 5,posY + 23,ColorUtil.applyOpacity(new Color(0xFFBDBDBD, true).getRGB(),getFadeFactor()),false);
            }
        }

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
        return mouseX > posX && mouseX < posX + 90 && mouseY > posY && mouseY < posY + 30;
    }

    public void movePosition(float deltaX, float deltaY) {
        this.posY += deltaY;
        this.posX += deltaX;
        scrollPosY = posY;
    }

    public void scrollElement(float deltaY){
        scroll_animation = 0;
        prevPosY = posY;
        this.scrollPosY += deltaY;
    }

    public void mouseClicked(int mouseX, int mouseY, int clickedButton) {
        if((posY > ThunderGui2.getInstance().main_posY + ThunderGui2.getInstance().height) || posY < ThunderGui2.getInstance().main_posY){
            return;
        }
        if(mouseX > posX && mouseX < posX + 90 && mouseY > posY && mouseY < posY + 30){
            switch (clickedButton){
                case 0:
                    module.toggle();
                    break;
                case 1:
                    ThunderGui2.selected_plate = this;
                    break;
                case 2:
                    listening_bind = !listening_bind;
                    break;
            }
        }
    }

    public void keyTyped(char typedChar, int keyCode) {
        if (listening_bind) {
            Bind bind = new Bind(keyCode);
            if (bind.toString().equalsIgnoreCase("Escape")) {
                return;
            }
            if (bind.toString().equalsIgnoreCase("Delete")) {
                bind = new Bind(-1);
            }
            module.bind.setValue(bind);
            listening_bind = false;
        }
    }

    public double getPosX() {
        return this.posX;
    }

    public double getPosY() {
        return this.posY;
    }

    public Module getModule(){
        return this.module;
    }

}
