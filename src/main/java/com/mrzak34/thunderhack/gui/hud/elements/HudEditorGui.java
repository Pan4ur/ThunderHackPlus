package com.mrzak34.thunderhack.gui.hud.elements;

import com.google.common.collect.Lists;
import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.gui.clickui.EaseBackIn;
import com.mrzak34.thunderhack.gui.clickui.window.ModuleWindow;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.modules.client.ClickGui;
import com.mrzak34.thunderhack.notification.Animation;
import com.mrzak34.thunderhack.notification.DecelerateAnimation;
import com.mrzak34.thunderhack.notification.Direction;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.List;

public class HudEditorGui extends GuiScreen {

    private static HudEditorGui INSTANCE = new HudEditorGui();
    private final List<ModuleWindow> windows;
    private Animation openAnimation, bgAnimation, rAnimation;
    private double scrollSpeed;
    private boolean firstOpen;
    private double dWheel;

    public static boolean mouse_state;
    public static int mouse_x;
    public static int mouse_y;


    public HudEditorGui() {
        windows = Lists.newArrayList();
        firstOpen = true;
        this.setInstance();
    }

    public static HudEditorGui getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new HudEditorGui();
        }
        return INSTANCE;
    }

    public static HudEditorGui getHudGui() {
        return HudEditorGui.getInstance();
    }

    private void setInstance() {
        INSTANCE = this;
    }


    @Override
    public void initGui() {
        openAnimation = new EaseBackIn(270, .4f, 1.13f);
        rAnimation = new DecelerateAnimation(300, 1f);
        bgAnimation = new DecelerateAnimation(300, 1f);
        if (firstOpen) {
            double x = 20, y = 20;
            double offset = 0;
            int windowHeight = 18;
            ScaledResolution sr = new ScaledResolution(mc);
            int i = 0;
            for (final Module.Category category : Thunderhack.moduleManager.getCategories()) {
                if (!category.getName().contains("HUD")) continue;
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
        if (openAnimation.isDone() && openAnimation.getDirection().equals(Direction.BACKWARDS)) {
            windows.forEach(ModuleWindow::onClose);
            mc.currentScreen = null;
            mc.displayGuiScreen(null);
        }

        dWheel = Mouse.getDWheel();

        mouse_x = mouseX;
        mouse_y = mouseY;

        if (dWheel > 0)
            scrollSpeed += 14;
        else if (dWheel < 0)
            scrollSpeed -= 14;

        double anim = (openAnimation.getOutput() + .6f);


        GlStateManager.pushMatrix();

        double centerX = width >> 1;
        double centerY = height >> 1;

        GlStateManager.translate(centerX, centerY, 0);
        GlStateManager.scale(anim, anim, 1);
        GlStateManager.translate(-centerX, -centerY, 0);

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

            window.render(mouseX, mouseY, delta, ClickGui.getInstance().hcolor1.getValue().getColorObject(), openAnimation.isDone() && openAnimation.getDirection() == Direction.FORWARDS);
        }
        GlStateManager.popMatrix();

        super.drawScreen(mouseX, mouseY, delta);
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
        mouse_state = true;
        super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int button) {
        windows.forEach(w -> w.mouseReleased(mouseX, mouseY, button));
        mouse_state = false;
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
            bgAnimation.setDirection(Direction.BACKWARDS);
            rAnimation.setDirection(Direction.BACKWARDS);
            openAnimation.setDirection(Direction.BACKWARDS);
        }
    }
}
