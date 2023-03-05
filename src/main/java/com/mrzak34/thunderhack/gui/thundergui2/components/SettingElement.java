package com.mrzak34.thunderhack.gui.thundergui2.components;

import com.mrzak34.thunderhack.gui.thundergui2.ThunderGui2;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.render.Drawable;
import com.mrzak34.thunderhack.util.render.RenderUtil;

import java.awt.*;
import java.io.IOException;

public class SettingElement {
    protected Setting setting;

    protected double x, y, width, height;
    protected double offsetY;

    protected double prev_offsetY;
    protected double scroll_offsetY;
    protected float scroll_animation;



    protected boolean hovered;

    public SettingElement(Setting setting) {
        this.setting = setting;
        scroll_animation = 0;
        prev_offsetY = y;
        scroll_offsetY = 0;
    }

    public void render(int mouseX, int mouseY, float delta) {
        hovered = Drawable.isHovered(mouseX, mouseY, x, y, width, height);
        if(scroll_offsetY != y) {
            scroll_animation = ThunderGui2.fast(scroll_animation, 1, 15f);
            y = (int) RenderUtil.interpolate(scroll_offsetY,prev_offsetY,scroll_animation);
        }
    }

    public void init() {
    }


    public void onTick(){

    }

    public void mouseClicked(int mouseX, int mouseY, int button) {
    }

    public void tick() {
    }

    public boolean isHovered(){
        return hovered;
    }

    public void mouseReleased(int mouseX, int mouseY, int button) {
    }

    public void handleMouseInput() throws IOException {
    }

    public void keyTyped(char chr, int keyCode) {
    }

    public void onClose() {
    }

    public void resetAnimation() {
    }

    public Setting getSetting() {
        return setting;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        prev_offsetY = this.y;
        this.scroll_offsetY = y + offsetY;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public void setOffsetY(double offsetY) {
        this.offsetY = offsetY;
    }

    public boolean isVisible() {
        return setting.isVisible();
    }

    public void checkMouseWheel(float dWheel) {
        if(dWheel != 0){
            scroll_animation = 0;
        }
    }
}
