package com.mrzak34.thunderhack.gui.mainmenu;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.gui.fontstuff.FontRender;
import com.mrzak34.thunderhack.modules.client.MainSettings;
import com.mrzak34.thunderhack.util.RoundedShader;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.awt.*;
import java.io.IOException;


public class ThunderMenu extends GuiScreen {
    private MainMenuShader backgroundShader;

    public ThunderMenu() {
        /*
        try {
            this.backgroundShader = new MainMenuShader("/mainmenu.fsh");
            initTime = System.currentTimeMillis();
        } catch (IOException var9) {
            throw new IllegalStateException("Failed to load backgound shader", var9);

        }

         */
        try {
            if (Thunderhack.moduleManager != null) {
                switch (Thunderhack.moduleManager.getModuleByClass(MainSettings.class).shaderMode.getValue()) {
                    case WarThunder:
                        backgroundShader = new MainMenuShader("/moon.fsh");
                        break;
                    case Smoke:
                        backgroundShader = new MainMenuShader("/mainmenu.fsh");
                        break;
                    case Dicks:
                        backgroundShader = new MainMenuShader("/dicks.fsh");
                        break;
                }
            }
        } catch (IOException var9) {
            throw new IllegalStateException("Failed to load backgound shader", var9);
        }
    }

    public static float func(float var0) {
        if ((var0 %= 360.0F) >= 180.0F) {
            var0 -= 360.0F;
        }

        if (var0 < -180.0F) {
            var0 += 360.0F;
        }

        return var0;
    }

    @Override
    public void initGui() {
        ScaledResolution sr = new ScaledResolution(this.mc);
        this.width = sr.getScaledWidth();
        this.height = sr.getScaledHeight();

        this.buttonList.add(new GuiMainMenuButton(1, sr.getScaledWidth() / 2 - 110, sr.getScaledHeight() / 2 - 70, false, "SINGLEPLAYER", false));
        this.buttonList.add(new GuiMainMenuButton(2, sr.getScaledWidth() / 2 + 4, sr.getScaledHeight() / 2 - 70, false, "MULTIPLAYER", false));
        this.buttonList.add(new GuiMainMenuButton(0, sr.getScaledWidth() / 2 - 110, sr.getScaledHeight() / 2 - 29, false, "SETTINGS", false));
        this.buttonList.add(new GuiMainMenuButton(14, sr.getScaledWidth() / 2 + 4, sr.getScaledHeight() / 2 - 29, false, "ALTMANAGER", false));
        this.buttonList.add(new GuiMainMenuButton(666, sr.getScaledWidth() / 2 - 110, sr.getScaledHeight() / 2 + 12, true, "EXIT", false));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        ScaledResolution sr = new ScaledResolution(this.mc);
        GlStateManager.disableCull();
        this.backgroundShader.useShader((int) (sr.getScaledWidth() * 2.0f), (int) (sr.getScaledHeight() * 2.0f), (float) mouseX, (float) mouseY, (float) (System.currentTimeMillis() - Thunderhack.initTime) / 1000.0F);

        GL11.glBegin(7);
        GL11.glVertex2f(-1.0F, -1F);
        GL11.glVertex2f(-1.0F, 1.0F);
        GL11.glVertex2f(1.0F, 1.0F);
        GL11.glVertex2f(1.0F, -1.0F);
        GL11.glEnd();
        GL20.glUseProgram(0);
        GlStateManager.disableCull();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        Color color = new Color(0x86000000, true);


        float half_w = sr.getScaledWidth() / 2f;
        float halh_h = sr.getScaledHeight() / 2f;


        RoundedShader.drawGradientRound(half_w - 120, halh_h - 80, 240, 140, 15f, color, color, color, color);


        FontRender.drawCentString8("THUNDERHACK", (int) half_w - 52, (int) halh_h - 82 - FontRender.getFontHeight8(), -1);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(final GuiButton button) {
        if (button.id == 0) {
            this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));
        }
        if (button.id == 1) {
            this.mc.displayGuiScreen(new GuiWorldSelection(this));
        }
        if (button.id == 2) {
            this.mc.displayGuiScreen(new GuiMultiplayer(this));
        }
        if (button.id == 14) {
            this.mc.displayGuiScreen(new GuiAltManager());
        }
        if (button.id == 666) {
            Thunderhack.unload(false);
            this.mc.shutdown();
        }
    }

}
