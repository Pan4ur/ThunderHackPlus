package com.mrzak34.thunderhack.gui.clickui.elements;

import com.mrzak34.thunderhack.util.render.Drawable;
import com.mrzak34.thunderhack.gui.clickui.base.AbstractElement;
import com.mrzak34.thunderhack.gui.fontstuff.FontRender;
import com.mrzak34.thunderhack.modules.client.ClickGui;
import com.mrzak34.thunderhack.notification.Animation;
import com.mrzak34.thunderhack.notification.DecelerateAnimation;
import com.mrzak34.thunderhack.notification.Direction;
import com.mrzak34.thunderhack.setting.Setting;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

public class ModeElement extends AbstractElement {

	public Setting setting2;
	private boolean open;
	private double wheight;


	private final Animation rotation = new DecelerateAnimation(240, 1, Direction.FORWARDS);

	public ModeElement(Setting setting) {
		super(setting);
		this.setting2 = setting;
	}

	@Override
	public void render(int mouseX, int mouseY, float delta) {
		super.render(mouseX, mouseY, delta);
		rotation.setDirection(open ? Direction.BACKWARDS : Direction.FORWARDS);
		

		float tx = (float) (x + width - 7);
		float ty = (float) (y + (wheight / 2));

		float thetaRotation = (float) (-180f * rotation.getOutput());
		GlStateManager.pushMatrix();

		GlStateManager.translate(tx, ty, 0);
		GlStateManager.rotate(thetaRotation, 0, 0, 1);
		GlStateManager.translate(-tx, -ty, 0);

		Drawable.drawTexture(new ResourceLocation("textures/arrow.png"), x + width - 10, y + (wheight - 6) / 2, 6, 6);
		GlStateManager.popMatrix();

		FontRender.drawString5(setting2.getName(), (float) (x + 3), (float) (y + wheight / 2 - (FontRender.getFontHeight5() / 2f)) +3, -1);
		FontRender.drawString5(setting2.currentEnumName(), (float)(x + width - 16 - FontRender.getStringWidth5(setting.currentEnumName())), 3 +(float)(y + wheight / 2 - (FontRender.getFontHeight5() / 2f)), -1);

		if (open) {
			Color color = ClickGui.getInstance().getColor(0);
			double offsetY = 0;
			for(int i = 0; i <= setting2.getModes().length - 1; i++){
				FontRender.drawCentString5(setting2.getModes()[i], (float)x + (float)width / 2f, (float)(y + wheight + ((12 >> 1) - (FontRender.getFontHeight5() / 2f) - 1) + offsetY), setting2.currentEnumName().equalsIgnoreCase(setting2.getModes()[i]) ? color.getRGB() : -1);
				offsetY += 12;
			}
		}

	}

	@Override
	public void mouseClicked(int mouseX, int mouseY, int button) {
		if (Drawable.isHovered(mouseX, mouseY, x, y, width, wheight)) {
			if (button == 0) {
				setting2.increaseEnum();
			} else
				open = !open;
		}

		if (open) {
			double offsetY = 0;
			for(int i = 0; i <= setting2.getModes().length - 1; i++){
				if (Drawable.isHovered(mouseX, mouseY, x, y + wheight + offsetY, width, 12) && button == 0)
					setting2.setEnumByNumber(i);
				offsetY += 12;
			}
		}
	}

	@Override
	public void resetAnimation() {

	}

	public void setWHeight(double height) {
		this.wheight = height;
	}

	public boolean isOpen() {
		return open;
	}

}
