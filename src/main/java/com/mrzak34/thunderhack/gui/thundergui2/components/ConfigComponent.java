package com.mrzak34.thunderhack.gui.thundergui2.components;

import com.mrzak34.thunderhack.gui.clickui.ColorUtil;
import com.mrzak34.thunderhack.gui.fontstuff.FontRender;
import com.mrzak34.thunderhack.gui.hud.elements.Particles;
import com.mrzak34.thunderhack.gui.thundergui2.ThunderGui2;
import com.mrzak34.thunderhack.manager.ConfigManager;
import com.mrzak34.thunderhack.modules.client.ThunderHackGui;
import com.mrzak34.thunderhack.util.RoundedShader;
import com.mrzak34.thunderhack.util.render.Drawable;
import com.mrzak34.thunderhack.util.render.RenderUtil;
import com.mrzak34.thunderhack.util.render.Stencil;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class ConfigComponent {

    float scroll_animation = 0f;
    private final String name;
    private final String date;
    private int posX;
    private int posY;
    private int progress;
    private int fade;
    private final int index;
    private boolean first_open = true;
    private float scrollPosY;
    private float prevPosY;

    public ConfigComponent(String name, String date, int posX, int posY, int index) {
        this.name = name;
        this.date = date;
        this.posX = posX;
        this.posY = posY;
        fade = 0;
        this.index = index * 5;
        scrollPosY = posY;
        scroll_animation = 0f;
    }


    public void render(int MouseX, int MouseY) {
        if (scrollPosY != posY) {
            scroll_animation = ThunderGui2.fast(scroll_animation, 1, 15f);
            posY = (int) RenderUtil.interpolate(scrollPosY, prevPosY, scroll_animation);
        }

        if ((posY > ThunderGui2.getInstance().main_posY + ThunderGui2.getInstance().height) || posY < ThunderGui2.getInstance().main_posY) {
            return;
        }

        if (ConfigManager.currentConfig.getName().equals(name + ".th")) {
            RoundedShader.drawGradientRound(posX + 5, posY, 285, 30, 4f,
                    ColorUtil.applyOpacity(new Color(55, 44, 66, 255), getFadeFactor()),
                    ColorUtil.applyOpacity(new Color(25, 20, 30, 255), getFadeFactor()),
                    ColorUtil.applyOpacity(ThunderHackGui.getInstance().onColor1.getValue().getColorObject(), getFadeFactor()),
                    ColorUtil.applyOpacity(ThunderHackGui.getInstance().onColor2.getValue().getColorObject(), getFadeFactor()));
        } else {
            RoundedShader.drawRound(posX + 5, posY, 285, 30, 4f, ColorUtil.applyOpacity(new Color(44, 35, 52, 255), getFadeFactor()));
        }


        if (first_open) {
            GL11.glPushMatrix();
            Stencil.write(false);
            Particles.roundedRect(posX - 0.5 + 5, posY - 0.5, 286, 31, 8, ColorUtil.applyOpacity(new Color(0, 0, 0, 255), getFadeFactor()));
            Stencil.erase(true);
            RenderUtil.drawBlurredShadow(MouseX - 20, MouseY - 20, 40, 40, 60, ColorUtil.applyOpacity(new Color(0xC3555A7E, true), getFadeFactor()));
            Stencil.dispose();
            GL11.glPopMatrix();
            first_open = false;
        }

        if (isHovered(MouseX, MouseY)) {
            GL11.glPushMatrix();
            Stencil.write(false);
            Particles.roundedRect(posX - 0.5 + 5, posY - 0.5, 286, 31, 8, ColorUtil.applyOpacity(new Color(0, 0, 0, 255), getFadeFactor()));
            Stencil.erase(true);
            RenderUtil.drawBlurredShadow(MouseX - 20, MouseY - 20, 40, 40, 60, ColorUtil.applyOpacity(new Color(0xC3555A7E, true), getFadeFactor()));
            Stencil.dispose();
            GL11.glPopMatrix();
        }

        RoundedShader.drawRound(posX + 250, posY + 8, 30, 14, 2f, ColorUtil.applyOpacity(new Color(25, 20, 30, 255), getFadeFactor()));

        if (Drawable.isHovered(MouseX, MouseY, posX + 252, posY + 10, 10, 10)) {
            RoundedShader.drawRound(posX + 252, posY + 10, 10, 10, 2f, ColorUtil.applyOpacity(new Color(21, 58, 0, 255), getFadeFactor()));
        } else {
            RoundedShader.drawRound(posX + 252, posY + 10, 10, 10, 2f, ColorUtil.applyOpacity(new Color(32, 89, 0, 255), getFadeFactor()));

        }
        if (Drawable.isHovered(MouseX, MouseY, posX + 268, posY + 10, 10, 10)) {
            RoundedShader.drawRound(posX + 268, posY + 10, 10, 10, 2f, ColorUtil.applyOpacity(new Color(65, 1, 13, 255), getFadeFactor()));
        } else {
            RoundedShader.drawRound(posX + 268, posY + 10, 10, 10, 2f, ColorUtil.applyOpacity(new Color(94, 1, 18, 255), getFadeFactor()));
        }
        FontRender.drawIcon("x", posX + 252, posY + 13, ColorUtil.applyOpacity(-1, getFadeFactor()));
        FontRender.drawIcon("w", posX + 268, posY + 13, ColorUtil.applyOpacity(-1, getFadeFactor()));


        FontRender.drawMidIcon("u", posX + 7, posY + 5, ColorUtil.applyOpacity(-1, getFadeFactor()));
        FontRender.drawString6(name, posX + 37, posY + 6, ColorUtil.applyOpacity(-1, getFadeFactor()), false);
        FontRender.drawString7("updated on: " + date, posX + 37, posY + 17, ColorUtil.applyOpacity(new Color(0xFFBDBDBD, true).getRGB(), getFadeFactor()), false);
    }

    private float getFadeFactor() {
        return fade / (5f + index);
    }


    public void onTick() {
        if (progress > 4) {
            progress = 0;
        }
        progress++;

        if (fade < 10 + index) {
            fade++;
        }
    }


    private boolean isHovered(int mouseX, int mouseY) {
        return mouseX > posX && mouseX < posX + 295 && mouseY > posY && mouseY < posY + 30;
    }

    public void movePosition(float deltaX, float deltaY) {
        this.posY += deltaY;
        this.posX += deltaX;
        scrollPosY = posY;
    }

    public void mouseClicked(int MouseX, int MouseY, int clickedButton) {
        if ((posY > ThunderGui2.getInstance().main_posY + ThunderGui2.getInstance().height) || posY < ThunderGui2.getInstance().main_posY) {
            return;
        }
        if (Drawable.isHovered(MouseX, MouseY, posX + 252, posY + 10, 10, 10)) {
            ConfigManager.load(name);
        }
        if (Drawable.isHovered(MouseX, MouseY, posX + 268, posY + 10, 10, 10)) {
            ConfigManager.delete(name);
            ThunderGui2.getInstance().loadConfigs();
        }

    }

    public double getPosX() {
        return this.posX;
    }

    public double getPosY() {
        return this.posY;
    }

    public void scrollElement(float deltaY) {
        scroll_animation = 0;
        prevPosY = posY;
        this.scrollPosY += deltaY;
    }
}
