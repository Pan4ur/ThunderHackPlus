package com.mrzak34.thunderhack.gui.mainmenu;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.gui.fontstuff.FontRender;
import com.mrzak34.thunderhack.modules.client.MainSettings;
import com.mrzak34.thunderhack.util.render.RenderUtil;
import com.mrzak34.thunderhack.util.RoundedShader;
import com.mrzak34.thunderhack.util.ThunderUtils;
import com.mrzak34.thunderhack.util.Timer;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ChatAllowedCharacters;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.mrzak34.thunderhack.Thunderhack.alts;
import static com.mrzak34.thunderhack.gui.clickui.elements.SliderElement.removeLastChar;

public class GuiAltManager extends GuiScreen
{
    private MainMenuShader backgroundShader;

    public static List<AltCompoment> altscomponents = new ArrayList<>();
    public static Timer clicktimer = new Timer();



    public GuiAltManager() {
        try {
            if(Thunderhack.moduleManager != null){
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


    private boolean listening = false;


    // Add
    // Random
    // Back

    @Override
    public void initGui() {
        ScaledResolution sr = new ScaledResolution(this.mc);
        this.width = sr.getScaledWidth();
        this.height = sr.getScaledHeight();


        this.buttonList.add(new GuiMainMenuButton(420,sr.getScaledWidth() / 2 - 120, sr.getScaledHeight() - 135,false,"ADD", true));
        this.buttonList.add(new GuiMainMenuButton(69,sr.getScaledWidth() / 2 + 4, sr.getScaledHeight() - 135,false,"RANDOM", true));
        this.buttonList.add(new GuiMainMenuButton(228,sr.getScaledWidth() / 2 - 120, sr.getScaledHeight() - 96,true,"BACK", true));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        ScaledResolution sr = new ScaledResolution(this.mc);
        GlStateManager.disableCull();
        this.backgroundShader.useShader(sr.getScaledWidth() * 2, sr.getScaledHeight() * 2, (float)mouseX, (float)mouseY, (float)(System.currentTimeMillis() - Thunderhack.initTime) / 1000.0F);
        checkMouseWheel();

        GL11.glBegin(7);
        GL11.glVertex2f(-1.0F, -1.0F);
        GL11.glVertex2f(-1.0F, 1.0F);
        GL11.glVertex2f(1.0F, 1.0F);
        GL11.glVertex2f(1.0F, -1.0F);
        GL11.glEnd();
        GL20.glUseProgram(0);
        GlStateManager.disableCull();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        Color color = new Color(0x86000000, true);
        Color color2 = new Color(0xE4000000, true);
        Color color3 = new Color(0xE4313131, true);


        float half_w = sr.getScaledWidth() / 2f;
        float halh_h = sr.getScaledHeight() / 2f;

        RoundedShader.drawGradientRound(half_w - 120, 20, 240,  sr.getScaledHeight() - 160, 15f, color,color,color,color);

        int alts_y = 0;

        for(String alt : alts){
            altscomponents.add(new AltCompoment((int) (half_w - 105), (int) (30 + alts_y + dwheel), alt));
            alts_y += 49;
        }

        RenderUtil.glScissor((half_w - 110), (20), (half_w - 105) + 215, sr.getScaledHeight() - 140, sr);
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        altscomponents.forEach( altCompoment -> altCompoment.render(mouseX, mouseY));
        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        if(listening){
            RoundedShader.drawGradientRound(half_w - 60, halh_h - 40, 120,  80, 7f, color2,color2,color2,color2);
            RoundedShader.drawGradientRound(half_w - 55, halh_h - 10, 110,  10, 1f, color3,color3,color3,color3);
            RoundedShader.drawGradientRound(half_w - 15, halh_h + 10, 30,  10, 2f, color3,color3,color3,color3);


            boolean hover_add = (mouseX > half_w - 15) && (mouseX < half_w + 15) && (mouseY > halh_h + 10) && (mouseY < halh_h + 20);
            FontRender.drawCentString6("ADD", half_w, halh_h + 14, hover_add ? new Color(0x7A7A7A).getRGB() : -1 );
            FontRender.drawCentString6(listening ? add_name : "name", half_w, halh_h - 7, listening ? -1 : new Color(0x7A7A7A).getRGB());
        }



        //half_w - 15, halh_h + 10, 30,  10
        if((mouseX > half_w - 15) && (mouseX < half_w + 15) && (mouseY > halh_h + 10) && (mouseY < halh_h + 20) && Mouse.isButtonDown(0) && listening){
            alts.add(add_name);
            add_name = "";
            listening = false;
        }



        super.drawScreen(mouseX, mouseY, partialTicks);
        altscomponents.clear();
    }

    private String add_name = "";


    // sr.getScaledWidth() / 2 - 120, sr.getScaledHeight() - 135
    // sr.getScaledWidth() / 2 + 4, sr.getScaledHeight() - 135

    @Override
    public void mouseClicked(int x,int y, int button){

        ScaledResolution sr = new ScaledResolution(mc);
        if (x >= sr.getScaledWidth() / 2 - 120 && x <= sr.getScaledWidth() / 2 - 13 && y >= sr.getScaledHeight() - 135 && y <= sr.getScaledHeight() - 100) {
            listening = true;
        }
        if (x >= sr.getScaledWidth() / 2 + 4 && x <= sr.getScaledWidth() / 2 + 111 && y >= sr.getScaledHeight() - 135 && y <= sr.getScaledHeight() - 100) {
            String name = "Th" + (int)(Math.random() * 10000);
            alts.add(name);
            try {
                new Thread(() -> ThunderUtils.saveUserAvatar("https://minotar.net/helm/" + name +"/16.png", name));
            } catch (Exception e){}
        }
        if (x >= sr.getScaledWidth() / 2 - 120 && x <= sr.getScaledWidth() / 2 + 102 && y >= sr.getScaledHeight() - 96 && y <= sr.getScaledHeight() - 61) {
            this.mc.displayGuiScreen(new ThunderMenu());
        }
    }

    @Override
    public void keyTyped(char chr, int keyCode) {
        if (listening) {
            switch (keyCode) {
                case 1: {
                    return;
                }
                case 28: {
                    alts.add(add_name);
                    ThunderUtils.saveUserAvatar("https://minotar.net/helm/" + add_name + "/16.png", add_name);
                    add_name = "";
                    listening = false;
                }
                case 14: {
                    add_name = removeLastChar(add_name);
                }
            }
            if (ChatAllowedCharacters.isAllowedCharacter(chr)) {
                add_name = add_name + chr;
            }
        }
    }


    int dwheel;

    public void checkMouseWheel() {
        int dWheel = Mouse.getDWheel();
        if (dWheel < 0) {
            dwheel -= 10;
        } else if (dWheel > 0) {
            dwheel += 10;
        }
    }

}