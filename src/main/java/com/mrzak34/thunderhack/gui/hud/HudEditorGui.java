package com.mrzak34.thunderhack.gui.hud;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.modules.Feature;
import com.mrzak34.thunderhack.gui.classic.components.Component;
import com.mrzak34.thunderhack.gui.classic.components.items.Item;
import com.mrzak34.thunderhack.gui.classic.components.items.buttons.ModuleButton;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.modules.client.ClickGui;
import com.mrzak34.thunderhack.util.ColorUtil;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Mouse;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;

public class HudEditorGui extends GuiScreen {


    private static HudEditorGui hudGui;
    private static HudEditorGui INSTANCE;

    static {
        INSTANCE = new HudEditorGui();
    }



    private final ArrayList<Component> components = new ArrayList();


    public String search = "";


    public HudEditorGui() {
        this.setInstance();
        this.load();
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

    private void load() {
        for (final Module.Category category : Thunderhack.moduleManager.getCategories()) {
            if(Objects.equals(category.getName(), "HUD")) {
                this.components.add(new Component(category.getName(), 100, 40, true) {
                    @Override
                    public void setupItems() {
                        counter1 = new int[]{1};
                        Thunderhack.moduleManager.getModulesByCategory(category).forEach(module -> {
                            if (!module.hidden) {
                                this.addButton(new ModuleButton(module));
                            }
                        });
                    }
                });
            }
        }
        this.components.forEach(components -> components.getItems().sort(Comparator.comparing(Feature::getName)));
    }

    public void updateModule(Module module) {
        for (Component component : this.components) {
            for (Item item : component.getItems()) {
                if (!(item instanceof ModuleButton)) continue;
                ModuleButton button = (ModuleButton) item;
                Module mod = button.getModule();
                if (module == null || !module.equals(mod)) continue;
                button.initSettings();
            }
        }
    }

    public float animopenY = 5f;
    int color = ColorUtil.toARGB(ClickGui.getInstance().topColor.getValue().getRed(), ClickGui.getInstance().topColor.getValue().getGreen(), ClickGui.getInstance().topColor.getValue().getBlue(), 25);

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {

        this.checkMouseWheel();
        if(ClickGui.getInstance().darkBackGround.getValue()) {
            this.drawDefaultBackground();
        }
        this.components.forEach(components -> components.drawScreen(mouseX, mouseY, partialTicks));

    }


    public void mouseClicked(int mouseX, int mouseY, int clickedButton) {
        this.components.forEach(components -> components.mouseClicked(mouseX, mouseY, clickedButton));
    }

    public void mouseReleased(int mouseX, int mouseY, int releaseButton) {
        this.components.forEach(components -> components.mouseReleased(mouseX, mouseY, releaseButton));
    }

    public boolean doesGuiPauseGame() {
        return false;
    }

    public final ArrayList<Component> getComponents() {
        return this.components;
    }

    public void checkMouseWheel() {
        int dWheel = Mouse.getDWheel();
        if (dWheel < 0) {
            this.components.forEach(component -> component.setY(component.getY() - 10));
        } else if (dWheel > 0) {
            this.components.forEach(component -> component.setY(component.getY() + 10));
        }
    }

    public int getTextOffset() {
        return -6;
    }

    public Component getComponentByName(String name) {
        for (Component component : this.components) {
            if (!component.getName().equalsIgnoreCase(name)) continue;
            return component;
        }
        return null;
    }



}
