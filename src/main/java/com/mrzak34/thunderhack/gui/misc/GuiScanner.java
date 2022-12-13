package com.mrzak34.thunderhack.gui.misc;

import com.mrzak34.thunderhack.command.Command;
import com.mrzak34.thunderhack.gui.thundergui.fontstuff.FontRender;
import com.mrzak34.thunderhack.modules.misc.NoCom;
import com.mrzak34.thunderhack.util.GuiRenderHelper;
import com.mrzak34.thunderhack.util.RenderUtil;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;

public class GuiScanner extends GuiScreen {

    private static GuiScanner Scannergui;
    private static GuiScanner INSTANCE;

    public ArrayList<NoCom.cout> consoleout = new ArrayList<NoCom.cout>();



    static {
        INSTANCE = new GuiScanner();
    }

    public GuiScanner() {
        this.setInstance();
        this.load();

    }

    public boolean doesGuiPauseGame() {
        return false;
    }

    public static GuiScanner getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new GuiScanner();
        }
        return INSTANCE;
    }

    public static GuiScanner getGuiScanner() {
        return GuiScanner.getInstance();
    }

    private void load() {

    }

    private void setInstance() {
        INSTANCE = this;
    }

    int radarx = 0;
    int radary = 0;
    int radarx1 = 0;
    int radary1 = 0;

    int centerx = 0;
    int centery = 0;

    int consolex= 0;
    int consoley = 0;
    int consolex1= 0;
    int consoley1 = 0;

    int hovery= 0;
    int hoverx = 0;

    int searchx = 0;
    int searchy = 0;

    public static boolean neartrack = false;
    public static boolean track = false;
    public static boolean busy = false;

    //(16000 / 16)/8 = 125 250

    public float getscale(){
        if(NoCom.getInstance().scale.getValue() == 1){
            return 500f;
        }
        if(NoCom.getInstance().scale.getValue() == 2){
            return 250f;
        }
        if(NoCom.getInstance().scale.getValue() == 3){
            return 125f;
        }
        if(NoCom.getInstance().scale.getValue() == 4){
            return 75f;
        }
        return 705f;
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        ScaledResolution sr = new ScaledResolution(mc);
        this.checkMouseWheel(mouseX,mouseY);

        radarx = sr.getScaledWidth()/8;
        radarx1 = ((sr.getScaledWidth()*5)/8);
        radary = (sr.getScaledHeight()/2) -((radarx1 - radarx)/2);
        radary1 = (sr.getScaledHeight()/2) +((radarx1 - radarx)/2);

        centerx = (radarx + radarx1)/2;
        centery = (radary + radary1)/2;

        consolex = (int) ((sr.getScaledWidth()*5.5f)/8f);
        consolex1 = (sr.getScaledWidth() - 50);
        consoley = radary;
        consoley1 = radary1 - 50;


        GuiRenderHelper.drawOutlineRect(consolex, consoley, consolex1 - consolex, consoley1 - consoley, 4f, new Color(0xCDA8A8A8, true).getRGB());
        RenderUtil.drawRect2(consolex, consoley, consolex1, consoley1, new Color(0xF70C0C0C, true).getRGB());

        GuiRenderHelper.drawOutlineRect(consolex, consoley1 + 3, consolex1 - consolex, 15, 4f, new Color(0xCDA8A8A8, true).getRGB());
        RenderUtil.drawRect2(consolex, consoley1 + 3, consolex1, consoley1 + 17, new Color(0xF70C0C0C, true).getRGB());
        FontRender.drawString3("cursor pos: " + hoverx*64 + "x" + "  " + hovery*64 + "z",consolex + 4,consoley1 + 6 ,-1);

        GuiRenderHelper.drawOutlineRect(consolex, consoley1 + 20, consolex1 - consolex, 15, 4f, new Color(0xCDA8A8A8, true).getRGB());

        if(!track) {
           RenderUtil.drawRect2(consolex, consoley1 + 20, consolex1, consoley1 + 35, new Color(0xF70C0C0C, true).getRGB());
           FontRender.drawString3("tracker off", consolex + 4, consoley1 + 26, -1);
        } else {
           RenderUtil.drawRect2(consolex, consoley1 + 20, consolex1, consoley1 + 35, new Color(0xF75E5E5E, true).getRGB());
           FontRender.drawString3("tracker on", consolex + 4, consoley1 + 26, -1);
        }

        GuiRenderHelper.drawOutlineRect(radarx, radary, radarx1 - radarx, radary1 - radary, 4f, new Color(0xCDA8A8A8, true).getRGB());
        RenderUtil.drawRect2(radarx, radary, radarx1, radary1, new Color(0xE0151515, true).getRGB());
        try {
            for (NoCom.Dot point : NoCom.dots) {
                if (point.type == NoCom.DotType.Searched) {
                    RenderUtil.drawRect2((point.posX / 4f) + centerx, (point.posY / 4f) + centery, ((point.posX / 4f) + (radarx1 - radarx) / getscale()) + centerx, ((point.posY / 4f) + (radary1 - radary) / getscale()) + centery, new Color(0xE7A8A8A8, true).getRGB());
                } else {
                    RenderUtil.drawRect2((point.posX / 4f) + centerx, (point.posY / 4f) + centery, ((point.posX / 4f) + (radarx1 - radarx) / getscale()) + centerx, ((point.posY / 4f) + (radary1 - radary) / getscale()) + centery, new Color(0x3CE708).getRGB());
                }
            }
        } catch (Exception e){

        }
        RenderUtil.drawRect2( centerx - 1f,centery - 1f,centerx + 1f, centery + 1f,new Color(0xFF0303).getRGB());
        RenderUtil.drawRect2((mc.player.posX/16 / 4f) + centerx, (mc.player.posZ/16 / 4f) + centery, ((mc.player.posX/16 / 4f) + (radarx1 - radarx) / getscale()) + centerx, ((mc.player.posZ/16 / 4f) + (radary1 - radary) / getscale()) + centery, new Color(0x0012FF).getRGB());

        if(mouseX > radarx && mouseX < radarx1 && mouseY >  radary && mouseY < radary1 ){
                hoverx = mouseX - centerx;
                hovery = mouseY - centery;
        }

        RenderUtil.glScissor(consolex, consoley, consolex1 , consoley1 - 10, sr);
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        try {
            for (NoCom.cout out : consoleout) {
                FontRender.drawString3(out.string, consolex + 4, consoley + 6 + (out.posY * 11) + wheely, -1);
            }
        } catch (Exception ignored){

        }
        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        FontRender.drawString3("X+", radarx1 + 5, centery, -1);
        FontRender.drawString3("X-", radarx - 15, centery, -1);
        FontRender.drawString3("Y+", centerx, radary1 + 5, -1);
        FontRender.drawString3("Y-", centerx, radary - 8, -1);


    }

    public void mouseClicked(int mouseX, int mouseY, int clickedButton) {
        //        GuiRenderHelper.drawOutlineRect(radarx, radary, radarx1 - radarx, radary1 - radary, 4f, new Color(0xCDA8A8A8, true).getRGB());
        if (mouseX > radarx && mouseX < radarx1 && mouseY > radary && mouseY < radary1) {
            busy = true;
            searchx = mouseX - centerx;
            searchy = mouseY - centery;

            Command.sendMessage(searchx * 64 + " " + searchy * 64);
            NoCom.rerun(searchx * 64, searchy * 64);
            GuiScanner.getInstance().consoleout.add(new NoCom.cout(NoCom.getInstance().couti, "Selected pos " + searchx * 65 + "x " + searchy * 64 + "z "));
            ++NoCom.getInstance().couti;
        }
        //           RenderUtil.drawRect2(consolex, consoley1 + 20, consolex1, consoley1 + 30, new Color(0xF70C0C0C, true).getRGB());
        if (mouseX > consolex && mouseX < consolex1 && mouseY > consoley1 + 20 && mouseY < consoley1 + 36) {
            track = !track;
        }
    }

    int wheely = 0;

    public void checkMouseWheel(int mouseX, int mouseY) {
            int dWheel = Mouse.getDWheel();
            if (dWheel < 0) {
                wheely = wheely - 20;
            } else if (dWheel > 0) {
                wheely = wheely + 20;
            }
    }



}
