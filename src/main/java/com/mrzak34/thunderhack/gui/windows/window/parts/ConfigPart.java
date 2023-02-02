package com.mrzak34.thunderhack.gui.windows.window.parts;

import com.mrzak34.thunderhack.command.Command;
import com.mrzak34.thunderhack.gui.thundergui.fontstuff.FontRender;
import com.mrzak34.thunderhack.manager.ConfigManager;
import com.mrzak34.thunderhack.util.render.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

import static com.mrzak34.thunderhack.gui.thundergui.components.items.buttons.TFriendComponent.drawImage;
import static com.mrzak34.thunderhack.modules.player.ElytraSwap.drawCompleteImage;
import static com.mrzak34.thunderhack.util.Util.mc;

public class ConfigPart {
    public String name;

    private int posX;
    private int posY;
    private int width;
    private final int id;
    private int dwheel;


    ResourceLocation configpng = new ResourceLocation("textures/configpng.png");
    ResourceLocation loadpng = new ResourceLocation("textures/loadpng.png");
    ResourceLocation bin = new ResourceLocation("textures/trashbinnigga.png");

    public ConfigPart(String name,int posX,int posY, int width,int id){
        this.name = name;
        this.posX = posX;
        this.posY = posY;
        this.width = width;
        this.id = id;
    }

    public void renderPart(int x,int y){
        RenderUtil.drawSmoothRect(posX + 5,posY + 20 + 23*id + dwheel,posX + width - 5, posY + 40 + 23*id + dwheel,new Color(0xBDFFFFFF, true).getRGB());

        RenderUtil.drawSmoothRect(posX + width - 20, posY + 25 + 23 * id + dwheel, posX + width - 10, posY + 35 + 23 * id + dwheel, isHoveringDelButton(x,y) ? new Color(0xD50022A0, true).getRGB(): new Color(0x990022A8, true).getRGB());
        drawImage(bin, posX + width - 18, posY + 27 + 23*id + dwheel, 6, 6,isHoveringDelButton(x,y) ?new Color(0xFFFFFFFF, true)  : new Color(0xB3FFFFFF, true));

        RenderUtil.drawSmoothRect(posX + width - 20 - 23, posY + 25 + 23 * id + dwheel, posX + width - 10 - 23, posY + 35 + 23 * id + dwheel, isHoveringLoadButton(x,y) ? new Color(0xD5177700, true).getRGB(): new Color(0x99308101, true).getRGB());
        drawImage(loadpng, posX + width - 18 - 23, posY + 27 + 23*id + dwheel, 6, 6,isHoveringLoadButton(x,y) ?new Color(0xFFFFFFFF, true)  : new Color(0xB3FFFFFF, true));

        GlStateManager.color(1f,1f,1f,1f);
        mc.getTextureManager().bindTexture(configpng);
        drawCompleteImage(posX + 7,posY + 20 + 23*id + dwheel,18, 18);
        FontRender.drawString(name,posX + 7 + 22, posY + 20 + 23*id + dwheel, new Color(0x343434).getRGB());

    }


    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if(isHoveringDelButton(mouseX,mouseY)){
            boolean a = ConfigManager.delete(name);
            if(a){
                Command.sendMessage("Удален конфиг " + name);
            }
        }
        if(isHoveringLoadButton(mouseX,mouseY)){
            ConfigManager.load(name);
        }
    }


    boolean isHoveringDelButton(int x, int y){
        return x > posX + width - 20 && x < posX + width - 10 && y > posY + 25 + 23*id + dwheel && y < posY + 35 + 23*id + dwheel;
    }

    boolean isHoveringLoadButton(int x, int y){
        return x > posX + width - 20 - 23 && x < posX + width - 10 - 23 && y > posY + 25 + 23*id + dwheel && y < posY + 35 + 23*id + dwheel;
    }

    public void setDwheel(int dw) {
        dwheel = dw;
    }

    public int getPosX() {
        return posX;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public int getPosY() {
        return posY;
    }

    public void setPosY(int posY) {
        this.posY = posY;
    }


    public int getWidth() {
        return width;
    }

    public void setWidth(int width){
        this.width = width;
    }
}
