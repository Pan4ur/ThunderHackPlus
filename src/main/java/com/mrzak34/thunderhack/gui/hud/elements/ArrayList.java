package com.mrzak34.thunderhack.gui.hud.elements;

import com.mojang.realmsclient.gui.ChatFormatting;
import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.events.Render2DEvent;
import com.mrzak34.thunderhack.gui.fontstuff.FontRender;
import com.mrzak34.thunderhack.gui.hud.HudElement;
import com.mrzak34.thunderhack.gui.thundergui2.ThunderGui2;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.ColorSetting;
import com.mrzak34.thunderhack.setting.PositionSetting;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.render.PaletteHelper;
import com.mrzak34.thunderhack.util.render.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Mouse;

import java.awt.*;

import static com.mrzak34.thunderhack.util.render.RenderUtil.TwoColoreffect;

public class ArrayList extends HudElement {
    private final Setting<ColorSetting> color = register(new Setting<>("Color", new ColorSetting(0x8800FF00)));
    private final Setting<Float> rainbowSpeed = register(new Setting("Speed", 10.0f, 1.0f, 20.0f));
    private final Setting<Float> saturation = register(new Setting("Saturation", 0.5f, 0.1f, 1.0f));
    private final Setting<Integer> gste = register(new Setting("GS", 30, 10, 50));
    private final Setting<Boolean> glow = register(new Setting<>("glow", true));
    private final Setting<Boolean> shadoiw = register(new Setting<>("shadow", true));
    private final Setting<cMode> cmode = register(new Setting<>("ColorMode", cMode.Rainbow));
    private final Setting<Boolean> hrender = register(new Setting<>("HideHud", true));
    private final Setting<Boolean> hhud = register(new Setting<>("HideRender", true));
    private final Setting<ColorSetting> color2 = this.register(new Setting<>("Color2", new ColorSetting(-2353224)));

    public ArrayList() {super("ArrayList", "arraylist",50,30);}

    boolean reverse;

    public static void drawRect(double left, double top, double right, double bottom, int color) {
        if (left < right) {
            double i = left;
            left = right;
            right = i;
        }
        if (top < bottom) {
            double j = top;
            top = bottom;
            bottom = j;
        }
        float f3 = (float) (color >> 24 & 0xFF) / 255.0f;
        float f = (float) (color >> 16 & 0xFF) / 255.0f;
        float f1 = (float) (color >> 8 & 0xFF) / 255.0f;
        float f2 = (float) (color & 0xFF) / 255.0f;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.color(f, f1, f2, f3);
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
        bufferbuilder.pos(left, bottom, 0.0).endVertex();
        bufferbuilder.pos(right, bottom, 0.0).endVertex();
        bufferbuilder.pos(right, top, 0.0).endVertex();
        bufferbuilder.pos(left, top, 0.0).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    @SubscribeEvent
    public void onRender2D(Render2DEvent e) {
        super.onRender2D(e);

        int stringWidth;
        reverse = getPosX() > (float) (e.getScreenWidth() / 2);
        int offset = 0;
        int yTotal = 0;
        for (int i = 0; i < Thunderhack.moduleManager.sortedModules.size(); ++i) {
            yTotal += FontRender.getFontHeight6() + 3;
        }
        setHeight(yTotal);


        for (int k = 0; k < Thunderhack.moduleManager.sortedModules.size(); k++) {
            Module module = Thunderhack.moduleManager.sortedModules.get(k);
            if (!module.isDrawn()) {
                continue;
            }
            if(hrender.getValue() && module.getCategory() == Category.RENDER){
                continue;
            }
            if(hhud.getValue() && module.getCategory() == Category.HUD){
                continue;
            }
            Color color1 = null;

            if(cmode.getValue() == cMode.Rainbow){
                color1 = PaletteHelper.astolfo(offset, yTotal, saturation.getValue(), rainbowSpeed.getValue());
            } else if(cmode.getValue() == cMode.DoubleColor){
                color1 = TwoColoreffect(color.getValue().getColorObject(), color2.getValue().getColorObject(), Math.abs(System.currentTimeMillis() / 10) / 100.0 + offset * ((20f - rainbowSpeed.getValue()) / 200) );
            } else {
                color1 = new Color(color.getValue().getColor()).darker();
            }

            if (!reverse) {
                stringWidth = FontRender.getStringWidth6(module.getDisplayName() + ChatFormatting.GRAY + ((module.getDisplayInfo() != null) ? (" [" + ChatFormatting.WHITE + module.getDisplayInfo() + ChatFormatting.GRAY + "]") : "")) + 3;
                if (glow.getValue()) {
                    RenderHelper.drawBlurredShadow(getPosX() - 3, getPosY() + (float) offset - 1, (float) stringWidth + 4.0f, 9.0f, gste.getValue(), color1);
                }
                drawRect(getPosX(), getPosY() + (float) offset, getPosX() + (float) stringWidth + 1.0f, getPosY() + (float) offset + 8.0f, color1.getRGB());
                drawRect(getPosX() - 2.0f, getPosY() + (float) offset, getPosX() + 1.0f, getPosY() + (float) offset + 8.0f, -1);
                FontRender.drawString6(module.getDisplayName() + ChatFormatting.GRAY + ((module.getDisplayInfo() != null) ? (" [" + ChatFormatting.WHITE + module.getDisplayInfo() + ChatFormatting.GRAY + "]") : ""), getPosX() + 3.0f, getPosY() + 2.0f + (float) offset, -1, shadoiw.getValue());
            }
            if (reverse) {
                stringWidth = FontRender.getStringWidth6(module.getDisplayName() + ChatFormatting.GRAY + ((module.getDisplayInfo() != null) ? (" [" + ChatFormatting.WHITE + module.getDisplayInfo() + ChatFormatting.GRAY + "]") : "")) + 3;
                if (glow.getValue()) {
                    RenderHelper.drawBlurredShadow(getPosX() - (float) stringWidth - 3, getPosY() + (float) offset - 1, stringWidth + 4, 9f, gste.getValue(), color1);
                }
                drawRect(getPosX() - (float) stringWidth, getPosY() + (float) offset, getPosX() + 1.0f, getPosY() + (float) offset + 8.0f, color1.getRGB());
                drawRect(getPosX() + 1f, getPosY() + (float) offset, getPosX() + 4.0f, getPosY() + (float) offset + 8.0f, -1);
                FontRender.drawString6(module.getDisplayName() + ChatFormatting.GRAY + ((module.getDisplayInfo() != null) ? (" [" + ChatFormatting.WHITE + module.getDisplayInfo() + ChatFormatting.GRAY + "]") : ""), getPosX() - stringWidth + 2.0f, getPosY() + 2.0f + (float) offset, -1, shadoiw.getValue());

            }
            offset += 8;
        }
    }

    private enum cMode {
        Rainbow, Custom,DoubleColor
    }

}
