package com.mrzak34.thunderhack.gui.mainmenu;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.gui.fontstuff.FontRender;
import com.mrzak34.thunderhack.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Session;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.lang.reflect.Field;


public class AltCompoment {

    int posX;
    int posY;

    String name;

    ResourceLocation head = null;
    ResourceLocation crackedSkin = new ResourceLocation("textures/cracked.png");

    public AltCompoment(int posX, int posY,String name){
        this.posX = posX;
        this.posY = posY;
        head = PNGtoResourceLocation.getTexture2(name, "png");
        this.name = name;
    }

    public void render(int mouseX, int mouseY){
        Color color = new Color(0x62000000, true);
        Color selected_color = new Color(0x811D7201, true);


        RoundedShader.drawRound(posX, posY, 210,  40, 8f, Util.mc.session.getUsername().equals(this.name) ? selected_color : color);

        renderCustomTexture(posX + 5, posY + 5, 30, 30,16,16);
        FontRender.drawString3(name, posX + 38, posY + 5, Util.mc.session.getUsername().equals(this.name) ? new Color(0x7A7A7A).getRGB() : -1 );

        RoundedShader.drawRound(posX + 165, posY + 5, 35,  12, 3f, isHoveringLoggin(mouseX,mouseY) ? new Color(0x8104F839, true) : new Color(0x813EFF00, true));
        FontRender.drawCentString6("log", posX + 182.5f, posY + 10, isHoveringLoggin(mouseX,mouseY) ? new Color(0x7A7A7A).getRGB() : -1 );

        RoundedShader.drawRound(posX + 165, posY + 22, 35,  12, 3f, isHoveringDelete(mouseX,mouseY) ? new Color(0x81FF0000, true) : new Color(0x81F60202, true));
        FontRender.drawCentString6("del", posX + 182.5f, posY + 27, isHoveringDelete(mouseX,mouseY) ? new Color(0x7A7A7A).getRGB() : -1 );

        if(Mouse.isButtonDown(0)){
            mouseClicked(mouseX,mouseY,0);
        }
    }


    private boolean isHoveringLoggin(int x, int y){
        return x >= posX + 165 && x <= posX + 200 && y >= posY + 5 && y <= posY + 17;
    }

    private boolean isHoveringDelete(int x, int y){
        return x >= posX + 165 && x <= posX + 200 && y >= posY + 22 && y <= posY + 34;
    }



    public void mouseClicked(int x, int y, int button){

        if(!GuiAltManager.clicktimer.passedMs(500)){
            return;
        }
        if(isHoveringLoggin(x,y)){
            login(this.name);
            GuiAltManager.clicktimer.reset();
        }
        if(isHoveringDelete(x,y)){
            Thunderhack.alts.remove(this.name);
            GuiAltManager.clicktimer.reset();
        }
    }


    public void renderCustomTexture(final double x, final double y, final int width, final int height, final float tileWidth, final float tileHeight) {
        if(head != null){
            Util.mc.getTextureManager().bindTexture(head);
        } else {
            Util.mc.getTextureManager().bindTexture(crackedSkin);
        }
        GL11.glEnable(GL11.GL_BLEND);
        Gui.drawScaledCustomSizeModalRect((int) x, (int) y, 0, 0, 16, 16, width, height, tileWidth, tileHeight);
        GL11.glDisable(GL11.GL_BLEND);
    }

    public static void login(String string) {
        try {
            Field field = Minecraft.class.getDeclaredField("field_71449_j"); //session
            field.setAccessible(true);
            Field field2 = Field.class.getDeclaredField("modifiers");
            field2.setAccessible(true);
            field2.setInt(field, field.getModifiers() & 0xFFFFFFEF);
            field.set(Util.mc, new Session(string, "", "", "mojang"));
            System.out.println("logged in " + string);
        }
        catch (Exception e) {
            System.out.println("ALT MANAGER ERROR!");
            e.printStackTrace();
        }
    }
}
