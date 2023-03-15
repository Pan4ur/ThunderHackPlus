package com.mrzak34.thunderhack.gui.hud;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.events.Render2DEvent;
import com.mrzak34.thunderhack.gui.fontstuff.FontRender;
import com.mrzak34.thunderhack.gui.thundergui2.ThunderGui2;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.ColorSetting;
import com.mrzak34.thunderhack.setting.PositionSetting;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.RoundedShader;
import com.mrzak34.thunderhack.util.render.RenderUtil;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.util.Objects;

public class KeyBinds extends Module {

    public final Setting<ColorSetting> shadowColor = this.register(new Setting<>("ShadowColor", new ColorSetting(0xFF101010)));
    public final Setting<ColorSetting> color2 = this.register(new Setting<>("Color", new ColorSetting(0xFF101010)));
    public final Setting<ColorSetting> color3 = this.register(new Setting<>("Color2", new ColorSetting(0xC59B9B9B)));
    public final Setting<ColorSetting> textColor = this.register(new Setting<>("TextColor", new ColorSetting(0xBEBEBE)));
    public final Setting<ColorSetting> oncolor = this.register(new Setting<>("OnColor", new ColorSetting(0xBEBEBE)));
    public final Setting<ColorSetting> offcolor = this.register(new Setting<>("OffColor", new ColorSetting(0x646464)));
    private final Setting<PositionSetting> pos = this.register(new Setting<>("Position", new PositionSetting(0.5f, 0.5f)));
    private final Setting<Float> psize = this.register(new Setting<>("Size", 1f, 0.1f, 2f));
    float x1 = 0;
    float y1 = 0;
    int dragX, dragY = 0;
    boolean mousestate = false;

    public KeyBinds() {
        super("KeyBinds", "KeyBinds", Module.Category.HUD);
    }

    public static void size(double width, double height, double animation) {
        GL11.glTranslated(width, height, 0);
        GL11.glScaled(animation, animation, 1);
        GL11.glTranslated(-width, -height, 0);
    }

    @SubscribeEvent
    public void onRender2D(Render2DEvent e) {
        y1 = e.scaledResolution.getScaledHeight() * pos.getValue().getY();
        x1 = e.scaledResolution.getScaledWidth() * pos.getValue().getX();


        int y_offset1 = 0;
        for (Module feature : Thunderhack.moduleManager.modules) {
            if (!Objects.equals(feature.getBind().toString(), "None") && !feature.getName().equalsIgnoreCase("clickgui") && !feature.getName().equalsIgnoreCase("thundergui")) {
                y_offset1 += 10;
            }
        }

        GlStateManager.pushMatrix();
        size(x1 + 50, y1 + (20 + y_offset1) / 2f, psize.getValue());

        RenderUtil.drawBlurredShadow(x1, y1, 100, 20 + y_offset1, 20, shadowColor.getValue().getColorObject());


        RoundedShader.drawRound(x1, y1, 100, 20 + y_offset1, 7f, color2.getValue().getColorObject());
        FontRender.drawCentString6("KeyBinds", x1 + 50, y1 + 5, textColor.getValue().getColor());
        RoundedShader.drawRound(x1 + 2, y1 + 13, 96, 1, 0.5f, color3.getValue().getColorObject());

        int y_offset = 0;
        for (Module feature : Thunderhack.moduleManager.modules) {
            if (!Objects.equals(feature.getBind().toString(), "None") && !feature.getName().equalsIgnoreCase("clickgui") && !feature.getName().equalsIgnoreCase("thundergui")) {
                GlStateManager.pushMatrix();
                GlStateManager.resetColor();
                FontRender.drawString6("[" + feature.getBind().toString() + "]  " + feature.getName(), x1 + 5, y1 + 18 + y_offset, feature.isOn() ? oncolor.getValue().getColor() : offcolor.getValue().getColor(), false);
                GlStateManager.resetColor();
                GlStateManager.popMatrix();
                y_offset += 10;
            }
        }

        GlStateManager.popMatrix();

        if (mc.currentScreen instanceof GuiChat || mc.currentScreen instanceof HudEditorGui || mc.currentScreen instanceof ThunderGui2) {
            if (isHovering()) {
                if (Mouse.isButtonDown(0) && mousestate) {
                    pos.getValue().setX((float) (normaliseX() - dragX) / e.scaledResolution.getScaledWidth());
                    pos.getValue().setY((float) (normaliseY() - dragY) / e.scaledResolution.getScaledHeight());
                }
            }
        }
        if (Mouse.isButtonDown(0) && isHovering()) {
            if (!mousestate) {
                dragX = (int) (normaliseX() - (pos.getValue().getX() * e.scaledResolution.getScaledWidth()));
                dragY = (int) (normaliseY() - (pos.getValue().getY() * e.scaledResolution.getScaledHeight()));
            }
            mousestate = true;
        } else {
            mousestate = false;
        }
    }

    public int normaliseX() {
        return (int) ((Mouse.getX() / 2f));
    }

    public int normaliseY() {
        ScaledResolution sr = new ScaledResolution(mc);
        return (((-Mouse.getY() + sr.getScaledHeight()) + sr.getScaledHeight()) / 2);
    }

    public boolean isHovering() {
        return normaliseX() > x1 - 10 && normaliseX() < x1 + 100 && normaliseY() > y1 && normaliseY() < y1 + 100;
    }
}
