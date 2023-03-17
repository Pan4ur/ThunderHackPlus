package com.mrzak34.thunderhack.gui.thundergui2.components;

import com.mrzak34.thunderhack.gui.fontstuff.FontRender;
import com.mrzak34.thunderhack.gui.hud.elements.Particles;
import com.mrzak34.thunderhack.gui.thundergui2.ThunderGui2;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.util.RoundedShader;
import com.mrzak34.thunderhack.util.Util;
import com.mrzak34.thunderhack.util.math.MathUtil;
import com.mrzak34.thunderhack.util.render.RenderUtil;
import com.mrzak34.thunderhack.util.render.Stencil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class CategoryPlate {
    float category_animation = 0f;
    private final Module.Category cat;
    private int posX;
    private int posY;
    private final ScaledResolution sr;


    public CategoryPlate(Module.Category cat, int posX, int posY) {
        this.cat = cat;
        this.posX = posX;
        this.posY = posY;
        sr = new ScaledResolution(Util.mc);
    }

    public static double deltaTime() {
        return Minecraft.getDebugFPS() > 0 ? (1.0000 / Minecraft.getDebugFPS()) : 1;
    }

    public static float fast(float end, float start, float multiple) {
        return (1 - MathUtil.clamp((float) (deltaTime() * multiple), 0, 1)) * end + MathUtil.clamp((float) (deltaTime() * multiple), 0, 1) * start;
    }

    public void render(int MouseX, int MouseY) {
        category_animation = fast(category_animation, isHovered(MouseX, MouseY) ? 1 : 0, 15f);
        if (isHovered(MouseX, MouseY)) {
            RoundedShader.drawRound(posX, posY, 84, 15, 2f, new Color(25, 20, 30, (int) MathUtil.clamp(65 * category_animation, 0, 255)));
            GL11.glPushMatrix();
            Stencil.write(false);
            Particles.roundedRect(posX - 1, posY - 1, 85.5f, 16.5, 4, new Color(0, 0, 0, 255));
            Stencil.erase(true);
            RenderUtil.drawBlurredShadow(MouseX - 20, MouseY - 20, 40, 40, 60, new Color(0xC3555A7E, true));
            Stencil.dispose();
            GL11.glPopMatrix();
        }
        FontRender.drawString6(cat.getName(), posX + 5, posY + 6, -1, false);
    }

    public void movePosition(float deltaX, float deltaY) {
        this.posY += deltaY;
        this.posX += deltaX;
    }

    public void mouseClicked(int mouseX, int mouseY, int clickedButton) {
        if (isHovered(mouseX, mouseY)) {
            ThunderGui2.getInstance().new_category = this.cat;
            if (ThunderGui2.getInstance().current_category == null) {
                ThunderGui2.getInstance().current_category = Module.Category.HUD;
                ThunderGui2.getInstance().new_category = this.cat;
            }
        }
    }

    private boolean isHovered(int mouseX, int mouseY) {
        return mouseX > posX && mouseX < posX + 84 && mouseY > posY && mouseY < posY + 15;
    }

    public Module.Category getCategory() {
        return this.cat;
    }

    public int getPosY() {
        return this.posY;
    }
}
