package com.mrzak34.thunderhack.gui.hud;

import com.mrzak34.thunderhack.events.Render2DEvent;
import com.mrzak34.thunderhack.gui.hud.elements.HudEditorGui;
import com.mrzak34.thunderhack.gui.thundergui2.ThunderGui2;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.PositionSetting;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.render.RenderUtil;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Mouse;

import java.awt.*;

public class HudElement extends Module {
    public HudElement(String name, String description,int width, int height) {
        super(name, description, Category.HUD);
        this.height = height;
        this.width = width;
    }

    private final Setting<PositionSetting> pos = this.register(new Setting<>("Position", new PositionSetting(0.5f, 0.5f)));

    int height;
    int width;
    int dragX, dragY = 0;
    private boolean mousestate = false;
    float x1 = 0;
    float y1 = 0;

    public int normaliseX() {
        return (int) ((Mouse.getX() / 2f));
    }

    public int normaliseY() {
        ScaledResolution sr = new ScaledResolution(mc);
        return (((-Mouse.getY() + sr.getScaledHeight()) + sr.getScaledHeight()) / 2);
    }

    public boolean isHovering() {
        return normaliseX() > x1 && normaliseX() < x1 + width && normaliseY() > y1 && normaliseY() < y1 + height;
    }

    public void onRender2D(Render2DEvent e) {
        y1 = e.scaledResolution.getScaledHeight() * pos.getValue().getY();
        x1 = e.scaledResolution.getScaledWidth() * pos.getValue().getX();
        if (mc.currentScreen instanceof GuiChat || mc.currentScreen instanceof HudEditorGui || mc.currentScreen instanceof ThunderGui2) {
            if (Mouse.isButtonDown(0) && mousestate) {
                pos.getValue().setX((float) (normaliseX() - dragX) / e.scaledResolution.getScaledWidth());
                pos.getValue().setY((float) (normaliseY() - dragY) / e.scaledResolution.getScaledHeight());
            }
        }
        if (Mouse.isButtonDown(0)) {
            if (!mousestate && isHovering()) {
                dragX = (int) (normaliseX() - (pos.getValue().getX() * e.scaledResolution.getScaledWidth()));
                dragY = (int) (normaliseY() - (pos.getValue().getY() * e.scaledResolution.getScaledHeight()));
                mousestate = true;
            }
        } else {
            mousestate = false;
        }
    }

    public float getPosX(){
        return x1;
    }

    public float getPosY(){
        return y1;
    }

    public float getX(){
        return pos.getValue().x;
    }

    public float getY(){
        return pos.getValue().y;
    }

    public void setHeight(int h){
        this.height = h;
    }
}
