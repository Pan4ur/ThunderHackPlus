package com.mrzak34.thunderhack.gui.clickui.base;

import java.awt.Color;
import java.io.IOException;

import com.mrzak34.thunderhack.util.render.Drawable;
import com.mrzak34.thunderhack.setting.Setting;


public abstract class AbstractElement {

	protected Setting setting;

	protected double x, y, width, height;
	protected double offsetY;

	protected boolean hovered;
	
	protected int bgcolor = new Color(24, 24, 27).getRGB();

	public AbstractElement(Setting setting) {
		this.setting = setting;
	}

	public void render(int mouseX, int mouseY, float delta) {
		hovered = Drawable.isHovered(mouseX, mouseY, x, y, width, height);
	}

	public void init() {
	}

	public void mouseClicked(int mouseX, int mouseY, int button) {
	}

	public void tick() {
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
		this.y = y + offsetY;
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

}
