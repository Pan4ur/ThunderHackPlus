package com.mrzak34.thunderhack.gui.clickui.elements;

import com.mrzak34.thunderhack.gui.clickui.base.AbstractElement;
import com.mrzak34.thunderhack.gui.fontstuff.FontRender;
import com.mrzak34.thunderhack.notification.Animation;
import com.mrzak34.thunderhack.notification.DecelerateAnimation;
import com.mrzak34.thunderhack.notification.Direction;
import com.mrzak34.thunderhack.setting.Parent;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.render.Drawable;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class ParentElement extends AbstractElement {

    private final Setting<Parent> parentSetting;
    private final Animation rotation = new DecelerateAnimation(240, 1, Direction.FORWARDS);

    public ParentElement(Setting setting) {
        super(setting);
        this.parentSetting = setting;
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        super.render(mouseX, mouseY, delta);

        rotation.setDirection(getParentSetting().getValue().isExtended() ? Direction.BACKWARDS : Direction.FORWARDS);
        float tx = (float) (x + width - 7);
        float ty = (float) (y + 8.5f);
        float thetaRotation = (float) (-180f * rotation.getOutput());
        GlStateManager.pushMatrix();

        GlStateManager.translate(tx, ty, 0);
        GlStateManager.rotate(thetaRotation, 0, 0, 1);
        GlStateManager.translate(-tx, -ty, 0);

        Drawable.drawTexture(new ResourceLocation("textures/arrow.png"), x + width - 10, y + 5.5f, 6, 6);
        GlStateManager.popMatrix();
        FontRender.drawString5(setting.getName(), (float) (x + 3), (float) (y + height / 2 - (FontRender.getFontHeight5() / 2f)) + 3, -1);
    }


    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (hovered) {
            getParentSetting().getValue().setExtended(!getParentSetting().getValue().isExtended());
        }
    }


    public Setting<Parent> getParentSetting() {
        return parentSetting;
    }

}