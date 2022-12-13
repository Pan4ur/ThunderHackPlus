package com.mrzak34.thunderhack.gui.clickui.base;

import com.mrzak34.thunderhack.util.Drawable;
import com.mrzak34.thunderhack.util.RenderUtil;

import java.awt.*;
import java.io.IOException;

public abstract class AbstractWindow {

	private String name;
	public double animationY;
	protected double prevTargetX;

	protected double x, y, width, height;
	private double prevX, prevY;
	protected boolean hovered;
	public boolean dragging;
	
	protected double factor;

	private boolean open;

	public AbstractWindow(String name, double x, double y, double width, double height) {
		this.name = name;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.open = false;
	}

	public void init() {
	}

	public void render(int mouseX, int mouseY, float delta, Color color, boolean finished) {
		hovered = Drawable.isHovered(mouseX, mouseY, x, y, width, height);
		animationY = RenderUtil.interpolate(y, animationY, 0.05);
		if (this.dragging) {
			prevTargetX = x;
			this.x = this.prevX + mouseX;
			this.y = this.prevY + mouseY;
		} else 
			prevTargetX = x;
	}

	public void mouseClicked(int mouseX, int mouseY, int button) {
		if (this.hovered && button == 0) {
			this.dragging = true;
			this.prevX = this.x - mouseX;
			this.prevY = this.y - mouseY;
		}
	}

	public void tick() {
	}

	public void mouseReleased(int mouseX, int mouseY, int button) {
		if (button == 0)
			this.dragging = false;
	}

	public void handleMouseInput() throws IOException {
	}

	public void keyTyped(char chr, int keyCode) {
	}

	public void onClose() {
	}

	public AbstractWindow setOpen(boolean open) {
		this.open = open;
		return this;
	}

	public String getName() {
		return name;
	}

	public boolean isOpen() {
		return open;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getWidth() {
		return width;
	}

	public void setWidth(double width) {
		this.width = width;
	}

	public double getHeight() {
		return height;
	}

	public void setHeight(double height) {
		this.height = height;
	}
	
	public void setFactor(double factor) {
		this.factor = factor;
	}

}
