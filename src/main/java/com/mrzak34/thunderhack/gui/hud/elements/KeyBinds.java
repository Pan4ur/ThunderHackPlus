package com.mrzak34.thunderhack.gui.hud.elements;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.events.Render2DEvent;
import com.mrzak34.thunderhack.gui.fontstuff.FontRender;
import com.mrzak34.thunderhack.gui.hud.HudElement;
import com.mrzak34.thunderhack.gui.hud.elements.HudEditorGui;
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

public class KeyBinds extends HudElement {

    public final Setting<ColorSetting> shadowColor = this.register(new Setting<>("ShadowColor", new ColorSetting(0xFF101010)));
    public final Setting<ColorSetting> color2 = this.register(new Setting<>("Color", new ColorSetting(0xFF101010)));
    public final Setting<ColorSetting> color3 = this.register(new Setting<>("Color2", new ColorSetting(0xC59B9B9B)));
    public final Setting<ColorSetting> textColor = this.register(new Setting<>("TextColor", new ColorSetting(0xBEBEBE)));
    public final Setting<ColorSetting> oncolor = this.register(new Setting<>("OnColor", new ColorSetting(0xBEBEBE)));
    public final Setting<ColorSetting> offcolor = this.register(new Setting<>("OffColor", new ColorSetting(0x646464)));

    public KeyBinds() {
        super("KeyBinds", "KeyBinds", 100,100);
    }

    public static void size(double width, double height, double animation) {
        GL11.glTranslated(width, height, 0);
        GL11.glScaled(animation, animation, 1);
        GL11.glTranslated(-width, -height, 0);
    }

    @SubscribeEvent
    public void onRender2D(Render2DEvent e) {
        super.onRender2D(e);
        int y_offset1 = 0;
        for (Module feature : Thunderhack.moduleManager.modules) {
            if (!Objects.equals(feature.getBind().toString(), "None") && !feature.getName().equalsIgnoreCase("clickgui") && !feature.getName().equalsIgnoreCase("thundergui")) {
                y_offset1 += 10;
            }
        }

        GlStateManager.pushMatrix();

        RenderUtil.drawBlurredShadow(getPosX(), getPosY(), 100, 20 + y_offset1, 20, shadowColor.getValue().getColorObject());
        RoundedShader.drawRound(getPosX(), getPosY(), 100, 20 + y_offset1, 7f, color2.getValue().getColorObject());
        FontRender.drawCentString6("KeyBinds", getPosX() + 50, getPosY() + 5, textColor.getValue().getColor());
        RoundedShader.drawRound(getPosX() + 2, getPosY() + 13, 96, 1, 0.5f, color3.getValue().getColorObject());

        int y_offset = 0;
        for (Module feature : Thunderhack.moduleManager.modules) {
            if (!Objects.equals(feature.getBind().toString(), "None") && !feature.getName().equalsIgnoreCase("clickgui") && !feature.getName().equalsIgnoreCase("thundergui")) {
                GlStateManager.pushMatrix();
                GlStateManager.resetColor();
                FontRender.drawString6("[" + feature.getBind().toString() + "]  " + feature.getName(), getPosX() + 5, getPosY() + 18 + y_offset, feature.isOn() ? oncolor.getValue().getColor() : offcolor.getValue().getColor(), false);
                GlStateManager.resetColor();
                GlStateManager.popMatrix();
                y_offset += 10;
            }
        }
        GlStateManager.popMatrix();
    }
}
