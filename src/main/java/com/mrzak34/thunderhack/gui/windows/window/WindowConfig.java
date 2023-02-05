package com.mrzak34.thunderhack.gui.windows.window;

import com.mrzak34.thunderhack.gui.thundergui.components.items.buttons.TConfigComponent;
import com.mrzak34.thunderhack.gui.thundergui.fontstuff.FontRender;
import com.mrzak34.thunderhack.gui.windows.window.parts.ConfigPart;
import com.mrzak34.thunderhack.manager.ConfigManager;
import com.mrzak34.thunderhack.util.render.RenderUtil;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.mrzak34.thunderhack.gui.thundergui.ThunderGui.removeLastChar;
import static com.mrzak34.thunderhack.util.Util.mc;

public class WindowConfig {
    private static int posX = 350;
    private static int posY;
    private static int width = 300;
    private static int height = 200;

    private static boolean listeningString = false;

    private static boolean drag = false;
    private static int dragX, dragY;

    private static boolean rescale = false;
    private static int rescaleX, rescaleY;


    private static int dwheel;

    private static int configPartId = 0;

    static String header = "Config Manager";

    static String addString = "Type here";



    static ArrayList<ConfigPart> configs = new ArrayList<>();
    static ArrayList<String> already = new ArrayList<String>();

    static File file;

    static boolean once = false;

    public static void once(){
        file = new File("ThunderHack/");
    }

    public static void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if(!once){
            once();
            once = false;
        }
        ScaledResolution sr = new ScaledResolution(mc);
        if(drag){
            posX = mouseX - dragX;
            posY = mouseY - dragY;
            configs.forEach(configPart -> configPart.setPosX(mouseX - dragX));
            configs.forEach(configPart -> configPart.setPosY(mouseY - dragY));
        }
        if(rescale){
            width = mouseX - rescaleX;
            height = mouseY - rescaleY;
            configs.forEach(configPart -> configPart.setWidth(mouseX - rescaleX));
        }

        if(!listeningString){
            addString = "Type here";
        }

        // база
        RenderUtil.drawSmoothRect(posX,posY,posX + width,posY + height, new Color(0xF200002C, true).getRGB());

        // граница
        RenderUtil.drawRect(posX + 5,posY + 9,posX + width - 5f,posY + 11f, new Color(0x2CFFFFFF, true).getRGB());

        //Хедер
        FontRender.drawCentString5(header, (posX + (posX + width)) / 2f,posY  + FontRender.getFontHeight5() - 2, new Color(0xC7FFFFFF, true).getRGB());

        //строка
        RenderUtil.drawRect(posX + 5,posY + height  - 20,posX + width - 5f,posY + height  - 5, new Color(0x32FFFFFF, true).getRGB());
        FontRender.drawString5(addString + (listeningString ? "_" : ""), posX + 10,posY + height  - 18 + 5, new Color(0xEAFFFDFD, true).getRGB());

        //кнопка
        if(isHoveringAddButton(mouseX,mouseY)) {
            RenderUtil.drawRect(posX + width - 7f - 40f, posY + height - 18, posX + width - 7f, posY + height - 7, new Color(0xE43F58F8, true).getRGB());
            FontRender.drawCentString5("ADD", ((posX + width - 7f - 40f) + (posX + width - 7f)) / 2f, posY + height - 18 + 5, new Color(0x99FFFFFF, true).getRGB());
        } else {
            RenderUtil.drawRect(posX + width - 7f - 40f, posY + height - 18, posX + width - 7f, posY + height - 7, new Color(0xBA3F58F8, true).getRGB());
            FontRender.drawCentString5("ADD", ((posX + width - 7f - 40f) + (posX + width - 7f)) / 2f, posY + height - 18 + 5, new Color(0x69FFFFFF, true).getRGB());
        }


        for (String file1 : Objects.requireNonNull(ConfigManager.getConfigList())) {
            if(already.contains(file1)){
                continue;
            }
            configs.add(new ConfigPart(file1, posX, posY,width,configPartId));
            already.add(file1);
            ++configPartId;
        }



        RenderUtil.glScissor(posX,posY + 12f,posX + width,posY + height  - 25,sr);
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        configs.forEach(configPart -> configPart.renderPart(mouseX,mouseY));
        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        configs.forEach(configPart -> configPart.setDwheel(dwheel));
        if(isInWindow(mouseX,mouseY)) {
            checkMouseWheel();
        }
    }

    public static void keyTyped(char typedChar, int keyCode) {
        if (listeningString){

            if(keyCode == 42){
                return;
            }

            if(keyCode == 54){
                return;
            }

            if(keyCode == 1){
                addString = "Type here";
                listeningString = false;
                return;
            }
            if(keyCode == 14){
                addString = removeLastChar(addString);
                return;
            }
            if(keyCode == 28){
                ConfigManager.save(addString);
                addString = "Type here";
                listeningString = false;
                return;
            }
            addString = addString + typedChar;
        }

    }

    public static void mouseClicked(int mouseX, int mouseY, int mouseButton){
        if(isHoveringHeader(mouseX,mouseY)){
            drag = true;
            dragX = mouseX - posX;
            dragY = mouseY - posY;
        }
        if(isHoveringCorner(mouseX,mouseY)){
            rescale = true;
            rescaleX = mouseX - width;
            rescaleY = mouseY - height;
        }
        if(isHoveringString(mouseX,mouseY)){
            addString = "";
            listeningString = true;
        }
        if(isHoveringAddButton(mouseX,mouseY)){
            if(Objects.equals(addString, "Type here")){
                return;
            }
            ConfigManager.save(addString);
            listeningString = false;
        }
        if(isInWindow(mouseX,mouseY)){
            configs.forEach(configPart -> configPart.mouseClicked(mouseX,mouseY,mouseButton));
            if(isHoveringDelButton(mouseX)){
                reset();
            }
        }
    }

    public static void mouseReleased(int mouseX, int mouseY, int state) {
        drag = rescale = false;
    }

    static boolean isHoveringHeader(int x, int y){
        return x > posX && x < posX + width && y > posY && y < posY + 20;
    }

    static boolean isHoveringString(int x, int y){
        return x > posX + 5 && x < posX + width - 7f - 40f && y > posY + height  - 20 && y < posY + height  - 5;
    }

    static boolean isHoveringAddButton(int x, int y){
        return x > posX + width - 7f - 40f && x < posX + width - 7f && y > posY + height  - 18 && y < posY + height  - 7;
    }

    static boolean isInWindow(int x, int y){
        return x > posX && x < posX + width && y > posY + 12f && y < posY + height  - 25;
    }

    static boolean isHoveringDelButton(int x){
        return x > posX + width - 20 && x < posX + width - 10;
    }

    static boolean isHoveringCorner(int x, int y){
        return x > posX + width - 5 && x < posX + width && y > posY + height - 5 && y < posY + height;
    }

    public static void reset(){
        already.clear();
        configs.clear();
        configPartId = 0;
        once();
    }

    public static void checkMouseWheel() {
        int dWheel = Mouse.getDWheel();
        if (dWheel < 0) {
            dwheel -= 10;
        } else if (dWheel > 0) {
            dwheel += 10;
        }
    }
}
