package com.mrzak34.thunderhack.gui.clickui.button;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.mrzak34.thunderhack.command.Command;
import com.mrzak34.thunderhack.gui.clickui.ColorUtil;
import com.mrzak34.thunderhack.gui.clickui.elements.*;
import com.mrzak34.thunderhack.setting.*;
import com.mrzak34.thunderhack.util.Drawable;
import com.mrzak34.thunderhack.gui.clickui.base.AbstractElement;
import com.mrzak34.thunderhack.gui.thundergui.fontstuff.FontRender;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.modules.client.ClickGui;
import com.mrzak34.thunderhack.notification.Animation;
import com.mrzak34.thunderhack.notification.DecelerateAnimation;
import com.mrzak34.thunderhack.notification.Direction;
import org.lwjgl.input.Keyboard;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.client.renderer.GlStateManager;

public class ModuleButton {

	private final Animation animation = new DecelerateAnimation(260, 1f, Direction.BACKWARDS);
	public final Animation openAnimation = new DecelerateAnimation(200, 1f, Direction.BACKWARDS);
	private final Animation enableAnimation = new DecelerateAnimation(180, 1f, Direction.BACKWARDS);

	private final List<AbstractElement> elements;
	private final Module module;
	private double x, y, width, height;
	private double offsetY;
	private boolean open;
	private boolean hovered;

	private boolean binding = false;

	public ModuleButton(Module module) {
		this.module = module;
		elements = new ArrayList<>();

		for (Setting setting : module.getSettings()) {
			if (setting.getValue() instanceof Boolean && !setting.getName().equals("Enabled") && !setting.getName().equals("Drawn")) {
				elements.add(new CheckBoxElement(setting));
			} else if (setting.getValue() instanceof ColorSetting) {
				elements.add(new ColorPickerElement(setting));
			} else if (setting.isNumberSetting() && setting.hasRestriction()) {
				elements.add(new SliderElement(setting));
			} else if (setting.isEnumSetting() && !(setting.getValue() instanceof Parent) && !(setting.getValue() instanceof PositionSetting)){
				elements.add(new ComboBoxElement(setting));
			} else if (setting.getValue() instanceof SubBind) {
				elements.add(new SubBindElement(setting));
			}else if ((setting.getValue() instanceof String || setting.getValue() instanceof Character) && !setting.getName().equalsIgnoreCase("displayName")) {
				elements.add(new StringElement(setting));
			}
		}
	}

	public void init() {
		elements.forEach(AbstractElement::init);
	}

	public void tick() {
		if (isOpen())
			elements.forEach(AbstractElement::tick);
	}

	public void render(int mouseX, int mouseY, float delta, Color color, boolean finished) {
		hovered = Drawable.isHovered(mouseX, mouseY, x, y, width, height);
		float fontSize = 0.9f;
		double ix = x + 5;
		double iy = y + height / 2 - (FontRender.getFontHeight6() / 2f);
		enableAnimation.setDirection(module.isEnabled() ? Direction.BACKWARDS : Direction.FORWARDS);

		animation.setDirection(hovered ? Direction.FORWARDS : Direction.BACKWARDS);

		if (isOpen()) {

			int sbg = new Color(24, 24, 27).getRGB();
			Drawable.horizontalGradient(x, y + height + 2, (x + width) * (1 - enableAnimation.getOutput()),
					(y + height + 2) + getElementsHeight() * (1 - enableAnimation.getOutput()),
					module.isEnabled() ? ColorUtil.applyOpacity(ClickGui.getInstance().getColor(200), 0.7f).getRGB() : sbg, //200
					module.isEnabled() ? ColorUtil.applyOpacity(ClickGui.getInstance().getColor(0), 0.7f).getRGB() : sbg); //0

			double offsetY = 0;
			for (AbstractElement element : elements) {
				if (!element.isVisible())
					continue;

				element.setOffsetY(offsetY);
				element.setX(x);
				element.setY(y + height + 2);
				element.setWidth(width);
				element.setHeight(15);

				if (element instanceof ColorPickerElement)
					element.setHeight(56);

				else if (element instanceof SliderElement)
					element.setHeight(18);

				if (element instanceof ComboBoxElement) {
					ComboBoxElement combobox = (ComboBoxElement) element;
					combobox.setWHeight(17);

					if (combobox.isOpen()) {
						offsetY += (combobox.getSetting().getModes().length * 12);
					element.setHeight(element.getHeight() + (combobox.getSetting().getModes().length * 12) + 3);

				}
					else
						element.setHeight(17);
				}

				element.render(mouseX, mouseY, delta);

				offsetY += element.getHeight();
			}

			Drawable.drawBlurredShadow((int) x, (int) (y + height), (int) width, 3, 9, new Color(0, 0, 0, 190));
		}

		Drawable.drawRectWH(x, y, width, isOpen() ? height + 2 : height, new Color(32, 32, 35, 255).getRGB());

		if (!enableAnimation.finished(Direction.FORWARDS)) {
			Drawable.horizontalGradient(x, y, (x + width) * (1 - enableAnimation.getOutput()),
					y + ((isOpen() ? height + 2 : height)),
					ColorUtil.applyOpacity(ClickGui.getInstance().getColor(200), 0.9f).getRGB(), //200
					ColorUtil.applyOpacity(ClickGui.getInstance().getColor(0), 0.9f).getRGB());// 0
		}

		if(!ClickGui.getInstance().showBinds.getValue() ){
			if (module.getSettings().size() > 4)
				FontRender.drawCentString6(isOpen() ? "-" : "+", (float) x + (float) width - 8f, (float) y + 6, -1);
		} else {
			if(!module.getBind().toString().equalsIgnoreCase("none"))
				FontRender.drawString5(module.getBind().toString(), (float) x + (float) width - FontRender.getStringWidth5(module.getBind().toString()) - 3f, (float) y + 6, -1);
		}


		GlStateManager.pushMatrix();

		float scale = (float) (1f - (0.03f * animation.getOutput()));
		GlStateManager.translate(ix, iy, 0);
		GlStateManager.scale(scale, scale, 1);
		GlStateManager.translate(-ix, -iy, 0);

		if (binding) {
			FontRender.drawString6("Keybind: " + (module.getBind().toString()), (float) ix,(float) iy, 0xFFEAEAEA,true);
		} else
			FontRender.drawString6(module.getName(), (float) ix, (float) iy + 3, 0xFFEAEAEA,true);

		GlStateManager.popMatrix();

	}

	public void mouseClicked(int mouseX, int mouseY, int button) {
		if (hovered) {
			if (button == 0) {
				module.toggle();
			} else if (button == 1 && (module.getSettings().size() > 4)) {
				setOpen(!isOpen());
			}
			if (button == 2)
				binding = !binding;

		}

		if (open)
			elements.forEach(element -> {
				if (element.isVisible())
					element.mouseClicked(mouseX, mouseY, button);
			});
		else
			resetAnimation();
	}

	public void mouseReleased(int mouseX, int mouseY, int button) {
		if (isOpen())
			elements.forEach(element -> element.mouseReleased(mouseX, mouseY, button));
	}

	public void handleMouseInput() throws IOException {
		if (!isOpen())
			return;
		for (AbstractElement element : elements)
			element.handleMouseInput();
	}

	public void keyTyped(char chr, int keyCode) {
		if (isOpen()) {
			for (AbstractElement element : elements)
				element.keyTyped(chr, keyCode);
		}

		if (binding) {
			if (keyCode == Keyboard.KEY_ESCAPE || keyCode == Keyboard.KEY_SPACE || keyCode == Keyboard.KEY_DELETE) {
				module.setBind(Keyboard.KEY_NONE);
				Command.sendMessage("Удален бинд с модуля " + ChatFormatting.LIGHT_PURPLE + module.getName());
			} else {
				module.setBind(keyCode);
				Command.sendMessage(ChatFormatting.LIGHT_PURPLE + module.getName() + ChatFormatting.WHITE + " бинд изменен на " + ChatFormatting.GREEN + Keyboard.getKeyName(module.getBind().getKey()));
			}
			binding = false;
		}
	}

	public void onGuiClosed() {
		elements.forEach(AbstractElement::onClose);
		resetAnimation();
	}

	public void resetAnimation() {
		elements.forEach(AbstractElement::resetAnimation);
	}


	public List<AbstractElement> getElements() {
		return elements;
	}

	public double getElementsHeight() {
		double offsetY = 0;
		double openedY = 0;
		if (isOpen()) {
			for (AbstractElement element : getElements()) {
				if (element.isVisible())
					offsetY += element.getHeight();
			}
		}
		return offsetY + openedY;
	}

	public boolean isOpen() {
		return open;
	}

	public void setOpen(boolean open) {
		this.open = open;
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
		this.y = y + offsetY;
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

	public void setOffsetY(double offsetY) {
		this.offsetY = offsetY;
	}

}