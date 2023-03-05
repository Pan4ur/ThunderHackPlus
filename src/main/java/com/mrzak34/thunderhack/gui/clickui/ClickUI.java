package com.mrzak34.thunderhack.gui.clickui;

import java.io.IOException;
import java.util.List;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.gui.clickui.window.ModuleWindow;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.modules.client.ClickGui;
import com.mrzak34.thunderhack.notification.Animation;
import com.mrzak34.thunderhack.notification.DecelerateAnimation;
import com.mrzak34.thunderhack.notification.Direction;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;


import com.google.common.collect.Lists;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;


/**
 * Created by sprayD on 06/09/2021. edited 15.05.2022
 */
public class ClickUI extends GuiScreen {
	private final List<ModuleWindow> windows;

	private double scrollSpeed;
	private boolean firstOpen;

	public ClickUI() {
		windows = Lists.newArrayList();
		firstOpen = true;
		this.setInstance();
	}

	private static ClickUI INSTANCE = new ClickUI();

	public static ClickUI getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new ClickUI();
		}
		return INSTANCE;
	}

	public static ClickUI getClickGui() {
		return ClickUI.getInstance();
	}

	private void setInstance() {
		INSTANCE = this;
	}


	@Override
	public void initGui() {
		if (firstOpen) {
			double x = 20, y = 20;
			double offset = 0;
			int windowHeight = 18;
			ScaledResolution sr = new ScaledResolution(mc);
			int i = 0;
			for (final Module.Category category : Thunderhack.moduleManager.getCategories()) {
				if(category.getName().contains("HUD")) continue;
				ModuleWindow window = new ModuleWindow(category.getName(), Thunderhack.moduleManager.getModulesByCategory(category), i, x + offset, y, 108, windowHeight);
				window.setOpen(true);
				windows.add(window);
				offset += 110;

				if (offset > sr.getScaledWidth()) {
					offset = 0;
				}
				i++;
			}
			firstOpen = false;
		}

		windows.forEach(ModuleWindow::init);

		super.initGui();
	}


	@Override
	public void drawScreen(int mouseX, int mouseY, float delta) {
		double dWheel = Mouse.getDWheel();

		if (dWheel > 0)
			scrollSpeed += 14;
		else if (dWheel < 0)
			scrollSpeed -= 14;


		for (ModuleWindow window : windows) {
			if (Keyboard.isKeyDown(Keyboard.KEY_DOWN))
				window.setY(window.getY() + 2);
			else if (Keyboard.isKeyDown(Keyboard.KEY_UP))
				window.setY(window.getY() - 2);
			else if (Keyboard.isKeyDown(Keyboard.KEY_LEFT))
				window.setX(window.getX() - 2);
			else if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT))
				window.setX(window.getX() + 2);
			if (dWheel != 0)
				window.setY(window.getY() + scrollSpeed);
			else
				scrollSpeed = 0;
			window.render(mouseX, mouseY, delta, ClickGui.getInstance().hcolor1.getValue().getColorObject(), true);
		}
/*
		GlStateManager.pushMatrix();
		if(EventManager.hoveredModule != null){
			RenderUtil.drawBlurredShadow(mouseX,mouseY, FontRender.getStringWidth6(EventManager.hoveredModule.getDescription()) + 20, 20, 20, new Color(0xA61E1E1E, true));
			RoundedShader.drawRound(mouseX,mouseY, FontRender.getStringWidth6(EventManager.hoveredModule.getDescription()) + 20, 20, 6f, new Color(0xA61E1E1E, true));
			FontRender.drawString6(EventManager.hoveredModule.getDescription(), mouseX + 6,mouseY, -1,false);
			EventManager.hoveredModule = null;
		}
		GlStateManager.popMatrix();

 */

	}

	@Override
	public void onGuiClosed() {

	}

	@Override
	public void updateScreen() {
		windows.forEach(ModuleWindow::tick);
		super.updateScreen();
	}

	@Override
	public void mouseClicked(int mouseX, int mouseY, int button) throws IOException {
		windows.forEach(w -> {
			w.mouseClicked(mouseX, mouseY, button);

			windows.forEach(w1 -> {
				if (w.dragging && w != w1)
					w1.dragging = false;
			});
		});
		super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public void mouseReleased(int mouseX, int mouseY, int button) {
		windows.forEach(w -> w.mouseReleased(mouseX, mouseY, button));
		super.mouseReleased(mouseX, mouseY, button);
	}

	@Override
	public void handleMouseInput() throws IOException {
		windows.forEach(w -> {
			try {
				w.handleMouseInput();
			} catch (IOException ignored) {

			}
		});
		super.handleMouseInput();
	}

	@Override
	public void keyTyped(char chr, int keyCode) throws IOException {
		windows.forEach(w -> {
			w.keyTyped(chr, keyCode);
		});

		if (keyCode == 1 || keyCode == Thunderhack.moduleManager.getModuleByClass(ClickGui.class).getBind().getKey()) {
			mc.currentScreen = null;
			mc.displayGuiScreen(null);
		}
	}

}
