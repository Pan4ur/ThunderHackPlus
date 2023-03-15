package com.mrzak34.thunderhack.gui.clickui.window;

import com.mrzak34.thunderhack.gui.clickui.EaseBackIn;
import com.mrzak34.thunderhack.gui.clickui.base.AbstractElement;
import com.mrzak34.thunderhack.gui.clickui.button.ModuleButton;
import com.mrzak34.thunderhack.gui.fontstuff.FontRender;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.modules.client.ClickGui;
import com.mrzak34.thunderhack.notification.Animation;
import com.mrzak34.thunderhack.notification.DecelerateAnimation;
import com.mrzak34.thunderhack.notification.Direction;
import com.mrzak34.thunderhack.util.RoundedShader;
import com.mrzak34.thunderhack.util.render.Drawable;
import com.mrzak34.thunderhack.util.render.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.mrzak34.thunderhack.util.Util.mc;

public class ModuleWindow {

    private final List<ModuleButton> buttons;
    private final ResourceLocation ICON;

    private final Animation animation = new EaseBackIn(270, 1f, 1.03f, Direction.BACKWARDS);
    private final Animation dragAnimation = new DecelerateAnimation(260, 1F, Direction.BACKWARDS);
    private final Animation rotationAnim = new DecelerateAnimation(260, 1F, Direction.FORWARDS);
    public double animationY;
    public boolean dragging;
    protected double prevTargetX;
    protected double x, y, width, height;
    protected boolean hovered;
    protected double factor;
    private double prevScrollProgress;
    private double scrollProgress;
    private boolean scrollHover;
    private float rotation = 0;
    private final String name;
    private double prevX, prevY;
    private boolean open;

    public ModuleWindow(String name, List<Module> features, int index, double x, double y, double width, double height) {

        this.name = name;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.open = false;

        buttons = new ArrayList<>();
        ICON = new ResourceLocation("textures/" + name.toLowerCase() + ".png");

        features.forEach(feature -> {
            ModuleButton button = new ModuleButton(feature);
            button.setHeight(15);
            buttons.add(button);
        });
    }

    public void init() {
        buttons.forEach(ModuleButton::init);
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

        double maxHeight = 4000;

        scrollHover = Drawable.isHovered(mouseX, mouseY, x, y + height, width, maxHeight);

        animation.setDirection(isOpen() ? Direction.FORWARDS : Direction.BACKWARDS);
        dragAnimation.setDirection(dragging ? Direction.FORWARDS : Direction.BACKWARDS);
        rotationAnim.setDirection(Direction.FORWARDS);

        GlStateManager.pushMatrix();

        float centerX = (float) (x + (mouseX - prevTargetX) / 2);
        float centerY = (float) (y + (height) / 2);

        rotation = (prevTargetX > x) ? RenderUtil.scrollAnimate(rotation, (float) -(5 - (x - prevTargetX) * 3.3), .94f) : (prevTargetX < x) ? RenderUtil.scrollAnimate(rotation, (float) (5 + (x - prevTargetX) * 3.3), .94f) : RenderUtil.scrollAnimate(rotation, 0, .8f);

        float dragScale = (float) (1f - (0.016f * dragAnimation.getOutput()));
        GlStateManager.translate(centerX, centerY, 1);
        GlStateManager.scale(dragScale + Math.abs(rotation / 200), dragScale, 1);
        GlStateManager.rotate((rotation), 0, 0, 1);
        GlStateManager.translate(-centerX, -centerY, 1);

        RoundedShader.drawRound((float) x + 2, (float) (y + height - 5), (float) width - 4, (float) ((getButtonsHeight() + 8) * animation.getOutput()), 3, true, ClickGui.getInstance().plateColor.getValue().getColorObject());

        if (animation.finished(Direction.FORWARDS)) {
            Drawable.drawBlurredShadow((int) x + 4, (int) (y + height - 1), (int) width - 8, 3, 7, new Color(0, 0, 0, 180));
            for (ModuleButton button : buttons) {
                button.setX(x + 2);
                button.setY(y + height - getScrollProgress());
                button.setWidth(width - 4);
                button.setHeight(15);
                button.render(mouseX, mouseY, delta, color, finished);
            }
        }
        Drawable.drawRectWH(x, y, width, height, ClickGui.getInstance().catColor.getValue().getColor());
        Drawable.drawTexture(ICON, x + 3, y + (height - 12) / 2, 12, 12);
        FontRender.drawString6(getName(), (float) x + 19, (float) y + (float) height / (float) 2 - (float) (FontRender.getFontHeight6() / 2), -1, true);
        GlStateManager.popMatrix();
        updatePosition();
    }

    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (this.hovered && button == 0) {
            this.dragging = true;
            this.prevX = this.x - mouseX;
            this.prevY = this.y - mouseY;
        }
        if (button == 1 && hovered) {
            setOpen(!isOpen());
        }

        if (isOpen() && scrollHover)
            buttons.forEach(b -> b.mouseClicked(mouseX, mouseY, button));
        else if (!isOpen()) {
            buttons.forEach(ModuleButton::resetAnimation);
        }
    }

    public void tick() {
        if (isOpen()) {
            buttons.forEach(ModuleButton::tick);
        }
    }

    public void mouseReleased(int mouseX, int mouseY, int button) {
        if (button == 0)
            this.dragging = false;
        if (isOpen())
            buttons.forEach(b -> b.mouseReleased(mouseX, mouseY, button));
    }

    public void handleMouseInput() throws IOException {
        for (ModuleButton button : buttons)
            button.handleMouseInput();
    }

    public void keyTyped(char chr, int keyCode) {
        if (isOpen()) {
            for (ModuleButton button : buttons)
                button.keyTyped(chr, keyCode);
        }
    }

    public void onClose() {
        buttons.forEach(ModuleButton::onGuiClosed);
    }

    private double getScrollProgress() {
        return prevScrollProgress + (scrollProgress - prevScrollProgress) * mc.getRenderPartialTicks();
    }

    private void updatePosition() {
        double offsetY = 0;
        double openY = 0;
        for (ModuleButton button : buttons) {
            button.setOffsetY(offsetY);
            if (button.isOpen()) {
                for (AbstractElement element : button.getElements()) {
                    if (element.isVisible())
                        offsetY += element.getHeight();
                }
                offsetY += 2;
            }
            offsetY += button.getHeight() + openY;
        }
    }

    public double getButtonsHeight() {
        double height = 0;
        for (ModuleButton button : buttons) {
            height += button.getElementsHeight();
            height += button.getHeight();
        }

        return height;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public String getName() {
        return name;
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
