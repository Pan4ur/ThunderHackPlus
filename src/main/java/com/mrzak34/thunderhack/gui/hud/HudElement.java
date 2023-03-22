package com.mrzak34.thunderhack.gui.hud;

import com.mrzak34.thunderhack.events.Render2DEvent;
import com.mrzak34.thunderhack.gui.hud.elements.HudEditorGui;
import com.mrzak34.thunderhack.gui.thundergui2.ThunderGui2;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.ColorSetting;
import com.mrzak34.thunderhack.setting.PositionSetting;
import com.mrzak34.thunderhack.setting.Setting;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Mouse;


public class HudElement extends Module {

    int height;
    int width;
    int dragX, dragY = 0;
    private boolean mousestate = false;
    float x1 = 0;
    float y1 = 0;

    public final Setting<ColorSetting> shadowColor = this.register(new Setting<>("ShadowColor", new ColorSetting(0xFF101010)));
    public final Setting<ColorSetting> color2 = this.register(new Setting<>("Color", new ColorSetting(0xFF101010)));
    public final Setting<ColorSetting> color3 = this.register(new Setting<>("Color2", new ColorSetting(0xC59B9B9B)));
    public final Setting<ColorSetting> textColor = this.register(new Setting<>("TextColor", new ColorSetting(0xBEBEBE)));

    public HudElement(String name, String description,int width, int height) {
        super(name, description, Category.HUD);
        this.height = height;
        this.width = width;
    }

    public HudElement(String name, String description, String eng_description,int width, int height) {
        super(name, description,eng_description, Category.HUD);
        this.height = height;
        this.width = width;
    }

    private final Setting<PositionSetting> pos = this.register(new Setting<>("Position", new PositionSetting(0.5f, 0.5f)));

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
