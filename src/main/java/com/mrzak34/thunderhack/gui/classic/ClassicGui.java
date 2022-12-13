package com.mrzak34.thunderhack.gui.classic;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.modules.Feature;
import com.mrzak34.thunderhack.gui.classic.components.Component;
import com.mrzak34.thunderhack.gui.classic.components.items.Item;
import com.mrzak34.thunderhack.gui.classic.components.items.buttons.ModuleButton;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.modules.client.ClickGui;
import com.mrzak34.thunderhack.setting.Bind;
import com.mrzak34.thunderhack.util.ColorUtil;
import com.mrzak34.thunderhack.util.RenderUtil;
import com.mrzak34.thunderhack.util.Util;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;

public class ClassicGui
        extends GuiScreen {
    private static ClassicGui INSTANCE;

    static {
        INSTANCE = new ClassicGui();
    }


    public boolean hudeditor = false;

    private final ArrayList<Component> components = new ArrayList();


    public String search = "";


    public ClassicGui() {
        this.setInstance();
        this.load();
    }

    public static ClassicGui getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ClassicGui();
        }
        return INSTANCE;
    }

    public static ClassicGui getClickGui() {
        return ClassicGui.getInstance();
    }

    private void setInstance() {
        INSTANCE = this;
    }

    private void load() {

        int x = -84;
        for (final Module.Category category : Thunderhack.moduleManager.getCategories()) {
            if(Objects.equals(category.getName(), "HUD") && !hudeditor){
                return;
            }
            if(!(Objects.equals(category.getName(), "HUD")) && hudeditor){
                return;
            }
            this.components.add(new Component(category.getName(), x += 90, 40, true) {
                @Override
                public void setupItems() {
                    Thunderhack.moduleManager.getModulesByCategory(category).forEach(module -> {
                        if (!module.hidden) {
                            this.addButton(new ModuleButton(module));

                        }
                    });
                }
            });
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


        if(animopen  && animopenY < 20f){
            animopenY = animopenY + 1.0f;
        }
        if(!(animopen)  && animopenY > 5f ){
            if(!searching) {
                if (search == null || search.equals("")) {
                    animopenY = animopenY - 1.0f;
                }
            }
        }


        ScaledResolution sr = new ScaledResolution(mc);
        RenderUtil.drawSmoothRect((float) (sr.getScaledWidth()/4) - 0.3f, animopenY - 15.3f, (float) (sr.getScaledWidth() * 0.75)+ 0.3f,animopenY+ 0.3f,color);
        RenderUtil.drawSmoothRect((float) (sr.getScaledWidth()/4), animopenY - 15F, (float) (sr.getScaledWidth() * 0.75),animopenY,new Color(0, 31, 31, 255).getRGB());


        if(search != null && !search.equals("")){
            if(searching) {
                Util.fr.drawStringWithShadow(search + "_", (sr.getScaledWidth() / 4f) + 0.7f, 5F + 0.4f, -1);
            }
        }

        this.checkMouseWheel();
        if(ClickGui.getInstance().darkBackGround.getValue()) {
            this.drawDefaultBackground();
        }
        this.components.forEach(components -> components.drawScreen(mouseX, mouseY, partialTicks));
        animopen = mouseX >= (sr.getScaledWidth() / 4) && mouseX <= (sr.getScaledWidth() * 0.75) + width && mouseY >= 5F && mouseY <= 20F;

    }

    public boolean searching;
    public boolean animopen;

    public void mouseClicked(int mouseX, int mouseY, int clickedButton) {
        this.components.forEach(components -> components.mouseClicked(mouseX, mouseY, clickedButton));
        ScaledResolution sr = new ScaledResolution(mc);
        if (mouseX >= (sr.getScaledWidth()/4) && mouseX <= (sr.getScaledWidth() * 0.75) + width && mouseY >= 5F && mouseY <= 20F ) {
            searching = true;
            color = ColorUtil.toARGB(ClickGui.getInstance().topColor.getValue().getRed(), ClickGui.getInstance().topColor.getValue().getGreen(), ClickGui.getInstance().topColor.getValue().getBlue(), 180);

        } else {
            searching = false;
            color = ColorUtil.toARGB(ClickGui.getInstance().topColor.getValue().getRed(), ClickGui.getInstance().topColor.getValue().getGreen(), ClickGui.getInstance().topColor.getValue().getBlue(), 75);
        }


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

    public void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        this.components.forEach(component -> component.onKeyTyped(typedChar, keyCode));

        if(searching) {
            Bind bind = new Bind(keyCode);
            if(bind.toString().equalsIgnoreCase("RShift")){
                return;
            }
            if(bind.toString().equalsIgnoreCase("LShift")){
                return;
            }
            if(bind.toString().equalsIgnoreCase("Shift")){
                return;
            }
            if(bind.toString().equalsIgnoreCase("R_Shift")){
                return;
            }
            if(bind.toString().equalsIgnoreCase("L_Shift")){
                return;
            }
            if (bind.toString().equalsIgnoreCase("Back")) {
                if (removeLastChar(search) != null) {
                    search = removeLastChar(search);
                } else {
                    search = "";
                }

            }
            if (bind.toString().equalsIgnoreCase("Escape")) {
                search = "";
            } else if (!bind.toString().equalsIgnoreCase("Back")) {
                search = search + typedChar;
            }
        } else {
            search = "";
        }

    }

    public static String removeLastChar(String s) {
        return (s == null || s.length() == 0) ? null : (s.substring(0, s.length() - 1));
    }

}

