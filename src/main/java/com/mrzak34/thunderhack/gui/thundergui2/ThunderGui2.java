package com.mrzak34.thunderhack.gui.thundergui2;


import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.command.Command;
import com.mrzak34.thunderhack.gui.clickui.ColorUtil;
import com.mrzak34.thunderhack.gui.fontstuff.FontRender;
import com.mrzak34.thunderhack.gui.hud.elements.Particles;
import com.mrzak34.thunderhack.gui.thundergui2.components.*;
import com.mrzak34.thunderhack.manager.ConfigManager;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.modules.client.ThunderHackGui;
import com.mrzak34.thunderhack.setting.Parent;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.RoundedShader;
import com.mrzak34.thunderhack.util.math.MathUtil;
import com.mrzak34.thunderhack.util.render.RenderUtil;
import com.mrzak34.thunderhack.util.render.Stencil;
import com.mrzak34.thunderhack.util.shaders.BetterAnimation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ChatAllowedCharacters;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.mrzak34.thunderhack.gui.hud.elements.TargetHud.sizeAnimation;


public class ThunderGui2 extends GuiScreen {


    public static CurrentMode currentMode = CurrentMode.Modules;
    public static boolean scroll_lock = false;
    public static ModulePlate selected_plate, prev_selected_plate;
    public static BetterAnimation open_animation = new BetterAnimation(5);
    public static boolean open_direction = false;
    private static ThunderGui2 INSTANCE;

    static {
        INSTANCE = new ThunderGui2();
    }

    public final ArrayList<ModulePlate> components = new ArrayList<>();
    public final CopyOnWriteArrayList<CategoryPlate> categories = new CopyOnWriteArrayList<>();
    public final ArrayList<SettingElement> settings = new ArrayList<>();
    public final CopyOnWriteArrayList<ConfigComponent> configs = new CopyOnWriteArrayList<>();
    public final CopyOnWriteArrayList<FriendComponent> friends = new CopyOnWriteArrayList<>();
    private final int main_width = 400;

    /**
     * Кто спиздит у того мать у меня под столом
     *
     * @Copyright by Pan4ur#2144
     **/

    public int main_posX = 100;
    public int main_posY = 100;
    public Module.Category current_category = Module.Category.COMBAT;
    public Module.Category new_category = Module.Category.COMBAT;
    float category_animation = 0f;
    float settings_animation = 0f;
    float manager_animation = 0f;
    int prevCategoryY, CategoryY, slider_y, slider_x;
    private int main_height = 250;
    private boolean dragging = false;
    private boolean rescale = false;
    private int drag_x = 0;
    private int drag_y = 0;
    private int rescale_y = 0;
    private float scroll = 0;
    private boolean first_open = true;
    private boolean searching = false;
    private boolean listening_friend = false;
    private boolean listening_config = false;
    private String search_string = "Search";
    private String config_string = "Save config";
    private String friend_string = "Add friend";
    private CurrentMode prevMode = CurrentMode.Modules;

    public ThunderGui2() {
        this.setInstance();
        this.load();
        CategoryY = getCategoryY(new_category);
    }


    public static ThunderGui2 getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ThunderGui2();
        }
        return INSTANCE;
    }

    public static ThunderGui2 getThunderGui() {
        open_animation = new BetterAnimation();
        open_direction = true;
        return ThunderGui2.getInstance();
    }

    public static String removeLastChar(String str) {
        String output = "";
        if (str != null && str.length() > 0) {
            output = str.substring(0, str.length() - 1);
        }
        return output;
    }

    public static double deltaTime() {
        return Minecraft.getDebugFPS() > 0 ? (1.0000 / Minecraft.getDebugFPS()) : 1;
    }

    public static float fast(float end, float start, float multiple) {
        return (1 - MathUtil.clamp((float) (deltaTime() * multiple), 0, 1)) * end + MathUtil.clamp((float) (deltaTime() * multiple), 0, 1) * start;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    public void load() {
        categories.clear();
        components.clear();
        configs.clear();
        friends.clear();

        int module_y = 0;
        for (Module module : Thunderhack.moduleManager.getModulesByCategory(current_category)) {
            components.add(new ModulePlate(module, main_posX + 100, main_posY + 40 + module_y, module_y / 35));
            module_y += 35;
        }

        int category_y = 0;
        for (final Module.Category category : Thunderhack.moduleManager.getCategories()) {
            categories.add(new CategoryPlate(category, main_posX + 8, main_posY + 43 + category_y));
            category_y += 17;
        }

    }

    public void loadConfigs() {
        // Дохуя времени уходит на сбор конфигов, поэтому запихну в отдельный поток
        friends.clear();
        configs.clear();
        (new Thread(() -> {
            int config_y = 3;
            for (String file1 : Objects.requireNonNull(ConfigManager.getConfigList())) {
                configs.add(new ConfigComponent(file1, ConfigManager.getConfigDate(file1), main_posX + 100, main_posY + 40 + config_y, config_y / 35));
                config_y += 35;
            }
        })).start();
    }

    public void loadFriends() {
        configs.clear();
        friends.clear();
        int friend_y = 3;
        for (String friend : Thunderhack.friendManager.getFriends()) {
            friends.add(new FriendComponent(friend, main_posX + 100, main_posY + 40 + friend_y, friend_y / 35));
            friend_y += 35;
        }
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        GlStateManager.pushMatrix();
        sizeAnimation(main_posX + main_width / 2f, main_posY + main_height / 2f, open_animation.getAnimationd());
        if (open_animation.getAnimationd() > 0) {
            renderGui(mouseX, mouseY, partialTicks);
        }
        if (open_animation.getAnimationd() <= 0.01 && !open_direction) {
            open_animation = new BetterAnimation();
            open_direction = false;
            mc.currentScreen = null;
            mc.displayGuiScreen(null);
        }
        GlStateManager.popMatrix();
    }

    public void renderGui(int mouseX, int mouseY, float partialTicks) {
        ScaledResolution sr = new ScaledResolution(mc);
        if (dragging) {
            float deltaX = (mouseX - drag_x) - main_posX;
            float deltaY = (mouseY - drag_y) - main_posY;

            main_posX = mouseX - drag_x;
            main_posY = mouseY - drag_y;

            slider_y += (int) deltaY;
            slider_x += (int) deltaX;

            this.configs.forEach(configComponent -> configComponent.movePosition(deltaX, deltaY));
            this.friends.forEach(friendComponent -> friendComponent.movePosition(deltaX, deltaY));
            this.components.forEach(component -> component.movePosition(deltaX, deltaY));
            this.categories.forEach(category -> category.movePosition(deltaX, deltaY));
        }

        if (rescale) {
            float deltaY = (mouseY - rescale_y) - main_height;
            if (main_height + deltaY > 250)
                main_height += deltaY;
        }

        if (current_category != null && current_category != new_category) {
            prevCategoryY = getCategoryY(current_category);
            CategoryY = getCategoryY(new_category);
            current_category = new_category;
            category_animation = 0;
            slider_y = 0;
            search_string = "Search";
            config_string = "Save config";
            friend_string = "Add friend";
            currentMode = CurrentMode.Modules;
            this.load();
        }


        manager_animation = fast(manager_animation, 1, 15f);
        category_animation = fast(category_animation, 1, 15f);

        checkMouseWheel(mouseX, mouseY);

        // Основная плита
        RoundedShader.drawRound(main_posX, main_posY, main_width, main_height, 9f, ThunderHackGui.getInstance().getColorByTheme(0));


        // Плита с лого
        RoundedShader.drawRound(main_posX + 5, main_posY + 5, 90, 30, 7f, ThunderHackGui.getInstance().getColorByTheme(1));
        FontRender.drawString2("THUNDERHACK+", main_posX + 10, main_posY + 15, ThunderHackGui.getInstance().getColorByTheme(2).getRGB());
        FontRender.drawString5("v2.40", main_posX + 75, main_posY + 30, ThunderHackGui.getInstance().getColorByTheme(3).getRGB());

        // Левая плита под категриями
        RoundedShader.drawRound(main_posX + 5, main_posY + 40, 90, 140, 7f, ThunderHackGui.getInstance().getColorByTheme(4));

        // Выбор между CfgManager и FriendManager
        if (currentMode == CurrentMode.Modules) {
            RoundedShader.drawRound(main_posX + 20, main_posY + 195, 60, 20, 4f, ThunderHackGui.getInstance().getColorByTheme(4));
        } else if (currentMode == CurrentMode.ConfigManager) {
            RoundedShader.drawGradientRound(main_posX + 20, main_posY + 195, 60, 20, 4f, ThunderHackGui.getInstance().getColorByTheme(5), ThunderHackGui.getInstance().getColorByTheme(5), ThunderHackGui.getInstance().getColorByTheme(4), ThunderHackGui.getInstance().getColorByTheme(4));
        } else {
            RoundedShader.drawGradientRound(main_posX + 20, main_posY + 195, 60, 20, 4f, ThunderHackGui.getInstance().getColorByTheme(4), ThunderHackGui.getInstance().getColorByTheme(4), ThunderHackGui.getInstance().getColorByTheme(5), ThunderHackGui.getInstance().getColorByTheme(5));
        }


        RoundedShader.drawRound(main_posX + 49.5f, main_posY + 197, 1, 16, 0.5f, ThunderHackGui.getInstance().getColorByTheme(6));

        FontRender.drawMidIcon("u", main_posX + 20, main_posY + 195, currentMode == CurrentMode.ConfigManager ? ThunderHackGui.getInstance().getColorByTheme(2).getRGB() : new Color(0x8D8D8D).getRGB());
        FontRender.drawMidIcon("v", main_posX + 54, main_posY + 196, currentMode == CurrentMode.FriendManager ? ThunderHackGui.getInstance().getColorByTheme(2).getRGB() : new Color(0x8D8D8D).getRGB());

        if (isHoveringItem(main_posX + 20, main_posY + 195, 60, 20, mouseX, mouseY)) {
            RoundedShader.drawRound(main_posX + 20, main_posY + 195, 60, 20, 4f, new Color(76, 56, 93, 31));
            GL11.glPushMatrix();
            Stencil.write(false);
            Particles.roundedRect(main_posX + 20, main_posY + 195, 61, 21, 8, new Color(0, 0, 0, 255));
            Stencil.erase(true);
            RenderUtil.drawBlurredShadow(mouseX - 20, mouseY - 20, 40, 40, 60, new Color(0xC3555A7E, true));
            Stencil.dispose();
            GL11.glPopMatrix();
        }


        if (first_open) {
            category_animation = 1;
            RoundedShader.drawRound((float) (main_posX + 8), (float) CategoryY + slider_y, 84, 15, 2f, ThunderHackGui.getInstance().getColorByTheme(7));
            first_open = false;
        } else {
            if (currentMode == CurrentMode.Modules)
                RoundedShader.drawRound((float) (main_posX + 8), (float) (RenderUtil.interpolate(CategoryY, prevCategoryY, category_animation)) + slider_y, 84, 15, 2f, ThunderHackGui.getInstance().getColorByTheme(7));
        }

        if (selected_plate != prev_selected_plate) {
            prev_selected_plate = selected_plate;
            settings_animation = 0;
            settings.clear();
            scroll = 0;

            if (selected_plate != null) {
                for (Setting<?> setting : selected_plate.getModule().getSettings()) {
                    if (setting.getValue() instanceof Parent) {
                        settings.add(new ParentComponent(setting));
                    }
                    if (setting.getValue() instanceof Boolean && !setting.getName().equals("Enabled") && !setting.getName().equals("Drawn")) {
                        settings.add(new BooleanComponent(setting));
                    }
                    if (setting.isEnumSetting()) {
                        settings.add(new ModeComponent(setting));
                    }
                    if (setting.isColorSetting()) {
                        settings.add(new ColorPickerComponent(setting));
                    }
                    if (setting.isNumberSetting() && setting.hasRestriction()) {
                        settings.add(new SliderComponent(setting));
                    }
                }
            }
        }


        settings_animation = fast(settings_animation, 1, 15f);


        if (currentMode != prevMode) {
            if (prevMode != CurrentMode.ConfigManager) {
                manager_animation = 0;
                if (currentMode == CurrentMode.ConfigManager) {
                    loadConfigs();
                }
            }

            if (prevMode != CurrentMode.FriendManager) {
                manager_animation = 0;
                if (currentMode == CurrentMode.FriendManager) {
                    loadFriends();
                }
            }
            prevMode = currentMode;
        }

        if (selected_plate != null) {
            if (currentMode == CurrentMode.Modules)
                RoundedShader.drawRound((float) RenderUtil.interpolate(main_posX + 200, selected_plate.getPosX(), settings_animation), (float) RenderUtil.interpolate(main_posY + 40, selected_plate.getPosY(), settings_animation), (float) RenderUtil.interpolate(195, 90, settings_animation), (float) RenderUtil.interpolate(main_height - 45, 30, settings_animation), 4f, ThunderHackGui.getInstance().getColorByTheme(7));
        }


        if (currentMode != CurrentMode.Modules) {
            searching = false;

            RenderUtil.glScissor((float) RenderUtil.interpolate(main_posX + 80, main_posX + 200, manager_animation), main_posY + 39, (float) RenderUtil.interpolate(399, 195, manager_animation) + main_posX + 36, (float) main_height + main_posY - 3, sr, open_animation.getAnimationd());

            GL11.glEnable(GL11.GL_SCISSOR_TEST);
            RoundedShader.drawRound(main_posX + 100, (float) main_posY + 40, (float) 295, (float) main_height - 44, 4f, ThunderHackGui.getInstance().getColorByTheme(7));
            this.configs.forEach(components -> components.render(mouseX, mouseY));
            this.friends.forEach(components -> components.render(mouseX, mouseY));
            RenderUtil.draw2DGradientRect(main_posX + 102, main_posY + 34, main_posX + 393, main_posY + 60, new Color(25, 20, 30, 0).getRGB(), ThunderHackGui.getInstance().getColorByTheme(7).getRGB(), new Color(25, 20, 30, 0).getRGB(), new Color(37, 27, 41, 245).getRGB());
            RenderUtil.draw2DGradientRect(main_posX + 102, main_posY + main_height - 35, main_posX + 393, main_posY + main_height, ThunderHackGui.getInstance().getColorByTheme(7).getRGB(), new Color(25, 20, 30, 0).getRGB(), ThunderHackGui.getInstance().getColorByTheme(7).getRGB(), new Color(37, 27, 41, 0).getRGB());
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
        }


        RenderUtil.glScissor(main_posX + 79, main_posY + 35, main_posX + 396 + 40, main_posY + main_height, sr, open_animation.getAnimationd());
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        this.components.forEach(components -> components.render(mouseX, mouseY));
        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        this.categories.forEach(category -> category.render(mouseX, mouseY));

        if (currentMode == CurrentMode.Modules) {
            RenderUtil.draw2DGradientRect(main_posX + 98, main_posY + 34, main_posX + 191, main_posY + 50, new Color(37, 27, 41, 0).getRGB(), new Color(37, 27, 41, 245).getRGB(), new Color(37, 27, 41, 0).getRGB(), new Color(37, 27, 41, 245).getRGB());
            RenderUtil.draw2DGradientRect(main_posX + 98, main_posY + main_height - 15, main_posX + 191, main_posY + main_height, new Color(37, 27, 41, 245).getRGB(), new Color(37, 27, 41, 0).getRGB(), new Color(37, 27, 41, 245).getRGB(), new Color(37, 27, 41, 0).getRGB());
        }

        RoundedShader.drawRound(main_posX + 100, main_posY + 5, 295, 30, 7f, new Color(25, 20, 30, 250));


        // Конфиг
        if (isHoveringItem(main_posX + 105, main_posY + 14, 11, 11, mouseX, mouseY)) {
            RoundedShader.drawRound(main_posX + 105, main_posY + 14, 11, 11, 3f, new Color(68, 49, 75, 250));
        } else {
            RoundedShader.drawRound(main_posX + 105, main_posY + 14, 11, 11, 3f, new Color(52, 38, 58, 250));
        }
        FontRender.drawString6("current cfg: " + ConfigManager.currentConfig.getName(), main_posX + 120, main_posY + 18, new Color(0xCDFFFFFF, true).getRGB(), false);
        FontRender.drawIcon("t", main_posX + 106, main_posY + 17, new Color(0xC2FFFFFF, true).getRGB());


        // Поиск
        RoundedShader.drawRound(main_posX + 250, main_posY + 15, 140, 10, 3f, new Color(52, 38, 58, 250));
        if (currentMode == CurrentMode.Modules)
            FontRender.drawIcon("s", main_posX + 378, main_posY + 18, searching ? new Color(0xCBFFFFFF, true).getRGB() : new Color(0x83FFFFFF, true).getRGB());
        if (isHoveringItem(main_posX + 250, main_posY + 15, 140, 20, mouseX, mouseY)) {
            GL11.glPushMatrix();
            RoundedShader.drawRound(main_posX + 250, main_posY + 15, 140, 10, 3f, new Color(84, 63, 94, 36));
            Stencil.write(false);
            Particles.roundedRect(main_posX + 250, main_posY + 15, 140, 10, 6, new Color(0, 0, 0, 255));
            Stencil.erase(true);
            RenderUtil.drawBlurredShadow(mouseX - 20, mouseY - 20, 40, 40, 60, new Color(0xC3555A7E, true));
            Stencil.dispose();
            GL11.glPopMatrix();
        }
        if (currentMode == CurrentMode.Modules)
            FontRender.drawString6(search_string, main_posX + 252, main_posY + 18, searching ? new Color(0xCBFFFFFF, true).getRGB() : new Color(0x83FFFFFF, true).getRGB(), false);
        if (currentMode == CurrentMode.ConfigManager) {
            FontRender.drawString6(config_string, main_posX + 252, main_posY + 18, listening_config ? new Color(0xCBFFFFFF, true).getRGB() : new Color(0x83FFFFFF, true).getRGB(), false);
            RoundedShader.drawRound(main_posX + 368, main_posY + 17, 20, 6, 1f, isHoveringItem(main_posX + 368, main_posY + 17, 20, 6, mouseX, mouseY) ? new Color(59, 42, 63, 194) : new Color(33, 23, 35, 194));
            FontRender.drawCentString6("+", main_posX + 378, main_posY + 19, ThunderHackGui.getInstance().getColorByTheme(2).getRGB());
        }
        if (currentMode == CurrentMode.FriendManager) {
            FontRender.drawString6(friend_string, main_posX + 252, main_posY + 18, listening_friend ? new Color(0xCBFFFFFF, true).getRGB() : new Color(0x83FFFFFF, true).getRGB(), false);
            RoundedShader.drawRound(main_posX + 368, main_posY + 17, 20, 6, 1f, isHoveringItem(main_posX + 368, main_posY + 17, 20, 6, mouseX, mouseY) ? new Color(59, 42, 63, 194) : new Color(33, 23, 35, 194));
            FontRender.drawCentString6("+", main_posX + 378, main_posY + 19, ThunderHackGui.getInstance().getColorByTheme(2).getRGB());
        }

        if (selected_plate == null) return;

        float scissorX1 = (float) RenderUtil.interpolate(main_posX + 200, selected_plate.getPosX(), settings_animation) - 20;
        float scissorY1 = (float) RenderUtil.interpolate(main_posY + 40, selected_plate.getPosY(), settings_animation);
        float scissorX2 = Math.max((float) RenderUtil.interpolate(395, 90, settings_animation) + main_posX, main_posX + 205) + 40;
        float scissorY2 = Math.max((float) RenderUtil.interpolate(main_height - 5, 30, settings_animation) + main_posY, main_posY + 45);

        if (scissorX2 < scissorX1) {
            scissorX2 = scissorX1;
        }

        if (scissorY2 < scissorY1) {
            scissorY2 = scissorY1;
        }

        RenderUtil.glScissor(scissorX1, scissorY1, scissorX2, scissorY2, sr, open_animation.getAnimationd());
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        if (!settings.isEmpty()) {
            double offsetY = 0;
            for (SettingElement element : settings) {
                if (!element.isVisible()) {
                    continue;
                }
                element.setOffsetY(offsetY);
                element.setX(main_posX + 215);
                element.setY(main_posY + 45 + scroll);
                element.setWidth(175);
                element.setHeight(15);

                if (element instanceof ColorPickerComponent)
                    if (((ColorPickerComponent) element).isOpen())
                        element.setHeight(56);

                if (element instanceof ModeComponent) {
                    ModeComponent component = (ModeComponent) element;
                    component.setWHeight(15);

                    if (component.isOpen()) {
                        offsetY += (component.getSetting().getModes().length * 6);
                        element.setHeight(element.getHeight() + (component.getSetting().getModes().length * 6) + 3);
                    } else {
                        element.setHeight(15);
                    }
                }
                element.render(mouseX, mouseY, partialTicks);
                offsetY += element.getHeight() + 3;
            }
        }
        if (selected_plate != null && settings_animation < 0.9999) {
            RoundedShader.drawRound((float) RenderUtil.interpolate(main_posX + 200, selected_plate.getPosX(), settings_animation), (float) RenderUtil.interpolate(main_posY + 40, selected_plate.getPosY(), settings_animation), (float) RenderUtil.interpolate(195, 90, settings_animation), (float) RenderUtil.interpolate(main_height - 45, 30, settings_animation), 4f, ColorUtil.applyOpacity(ThunderHackGui.getInstance().getColorByTheme(7), 1f - settings_animation));
        }
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    private int getCategoryY(Module.Category category) {
        for (CategoryPlate categoryPlate : categories) {
            if (categoryPlate.getCategory() == category) {
                return categoryPlate.getPosY();
            }
        }
        return 0;
    }

    public void onTick() {
        open_animation.update(open_direction);
        this.components.forEach(ModulePlate::onTick);
        this.settings.forEach(SettingElement::onTick);
        this.configs.forEach(ConfigComponent::onTick);
        this.friends.forEach(FriendComponent::onTick);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    public void mouseClicked(int mouseX, int mouseY, int clickedButton) {
        if (isHoveringItem(main_posX + 368, main_posY + 17, 20, 6, mouseX, mouseY)) {
            if (listening_config) {
                ConfigManager.save(config_string);
                config_string = "Save config";
                listening_config = false;
                loadConfigs();
                return;
            }
            if (listening_friend) {
                Thunderhack.friendManager.addFriend(friend_string);
                friend_string = "Add friend";
                listening_friend = false;
                loadFriends();
                return;
            }
        }
        if (isHoveringItem(main_posX + 105, main_posY + 14, 11, 11, mouseX, mouseY)) {
            try {
                Desktop.getDesktop().browse(new File("ThunderHack/configs/").toURI());
            } catch (Exception e) {
                Command.sendMessage("Не удалось открыть проводник!");
            }
        }

        if (isHoveringItem(main_posX + 20, main_posY + 195, 28, 20, mouseX, mouseY)) {
            current_category = null;
            currentMode = CurrentMode.ConfigManager;
            settings.clear();
            components.clear();
        }
        if (isHoveringItem(main_posX + 50, main_posY + 195, 28, 20, mouseX, mouseY)) {
            current_category = null;
            currentMode = CurrentMode.FriendManager;
            settings.clear();
            components.clear();
        }
        if (isHoveringItem(main_posX, main_posY, main_width, 30, mouseX, mouseY)) {
            drag_x = mouseX - main_posX;
            drag_y = mouseY - main_posY;
            dragging = true;
        }

        if (isHoveringItem(main_posX + 250, main_posY + 15, 140, 10, mouseX, mouseY) && currentMode == CurrentMode.Modules) {
            searching = true;
        }

        if (isHoveringItem(main_posX + 250, main_posY + 15, 110, 10, mouseX, mouseY) && currentMode == CurrentMode.ConfigManager) {
            listening_config = true;
        }

        if (isHoveringItem(main_posX + 250, main_posY + 15, 110, 10, mouseX, mouseY) && currentMode == CurrentMode.FriendManager) {
            listening_friend = true;
        }

        if (isHoveringItem(main_posX, main_posY + main_height - 6, main_width, 12, mouseX, mouseY)) {
            rescale_y = mouseY - main_height;
            rescale = true;
        }

        this.settings.forEach(component -> component.mouseClicked(mouseX, mouseY, clickedButton));
        this.components.forEach(components -> components.mouseClicked(mouseX, mouseY, clickedButton));
        this.categories.forEach(category -> category.mouseClicked(mouseX, mouseY, clickedButton));
        this.configs.forEach(component -> component.mouseClicked(mouseX, mouseY, clickedButton));
        this.friends.forEach(component -> component.mouseClicked(mouseX, mouseY, clickedButton));

    }

    public void mouseReleased(int mouseX, int mouseY, int releaseButton) {
        dragging = false;
        rescale = false;
        settings.forEach(settingElement -> settingElement.mouseReleased(mouseX, mouseY, releaseButton));
    }

    public void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == 1) {
            open_direction = false;
            searching = false;
        }

        settings.forEach(settingElement -> settingElement.keyTyped(typedChar, keyCode));
        components.forEach(component -> component.keyTyped(typedChar, keyCode));

        if (searching) {
            components.clear();
            if (search_string.equalsIgnoreCase("search")) {
                search_string = "";
            }
            int module_y = 0;
            for (Module module : Thunderhack.moduleManager.getModulesSearch(search_string)) {
                components.add(new ModulePlate(module, main_posX + 100, main_posY + 40 + module_y, module_y / 35));
                module_y += 35;
            }

            switch (keyCode) {
                case 1: {
                    search_string = "Search";
                    searching = false;
                    return;
                }
                case 14: {
                    search_string = (removeLastChar(search_string));
                }
            }
            if (ChatAllowedCharacters.isAllowedCharacter(typedChar)) {
                search_string = (search_string + typedChar);
            }
        }
        if (listening_config) {
            if (config_string.equalsIgnoreCase("Save config")) {
                config_string = "";
            }
            switch (keyCode) {
                case 1: {
                    config_string = "Save config";
                    listening_config = false;
                    return;
                }
                case 14: {
                    config_string = (removeLastChar(config_string));
                    break;
                }
                case 28: {
                    if (!config_string.equals("Save config") && !config_string.equals("")) {
                        ConfigManager.save(config_string);
                        config_string = "Save config";
                        listening_config = false;
                        loadConfigs();
                    }
                    break;
                }
            }
            if (ChatAllowedCharacters.isAllowedCharacter(typedChar)) {
                config_string = (config_string + typedChar);
            }
        }

        if (listening_friend) {
            if (friend_string.equalsIgnoreCase("Add friend")) {
                friend_string = "";
            }
            switch (keyCode) {
                case 1: {
                    friend_string = "Add friend";
                    listening_friend = false;
                    return;
                }
                case 14: {
                    friend_string = (removeLastChar(friend_string));
                    break;
                }
                case 28: {
                    if (!friend_string.equals("Add friend") && !config_string.equals("")) {
                        Thunderhack.friendManager.addFriend(friend_string);
                        friend_string = "Add friend";
                        listening_friend = false;
                        loadFriends();
                    }
                    break;
                }
            }
            if (ChatAllowedCharacters.isAllowedCharacter(typedChar)) {
                friend_string = (friend_string + typedChar);
            }
        }
    }

    @Override
    public void onGuiClosed() {

    }

    public boolean isHoveringItem(float x, float y, float x1, float y1, float mouseX, float mouseY) {
        return (mouseX >= x && mouseY >= y && mouseX <= x1 + x && mouseY <= y1 + y);
    }

    public void checkMouseWheel(int mouseX, int mouseY) {
        final float dWheel = Mouse.getDWheel();
        settings.forEach(component -> component.checkMouseWheel(dWheel));
        if (scroll_lock) {
            scroll_lock = false;
            return;
        }
        if (isHoveringItem(main_posX + 200, main_posY + 40, main_posX + 395, main_posY - 5 + main_height, mouseX, mouseY))
            scroll += dWheel * ThunderHackGui.getInstance().scrollSpeed.getValue();
        else {
            components.forEach(component -> component.scrollElement(dWheel * ThunderHackGui.getInstance().scrollSpeed.getValue()));
        }
        configs.forEach(component -> component.scrollElement(dWheel * ThunderHackGui.getInstance().scrollSpeed.getValue()));
        friends.forEach(component -> component.scrollElement(dWheel * ThunderHackGui.getInstance().scrollSpeed.getValue()));
    }

    public enum CurrentMode {
        Modules, ConfigManager, FriendManager
    }
}
