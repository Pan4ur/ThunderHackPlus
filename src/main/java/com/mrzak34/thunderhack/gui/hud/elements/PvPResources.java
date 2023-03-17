package com.mrzak34.thunderhack.gui.hud.elements;

import com.mrzak34.thunderhack.events.Render2DEvent;
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
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Mouse;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PvPResources extends HudElement {
    public final Setting<ColorSetting> shadowColor = this.register(new Setting<>("ShadowColor", new ColorSetting(0xFF101010)));
    public final Setting<ColorSetting> color2 = this.register(new Setting<>("Color", new ColorSetting(0xFF101010)));
    public final Setting<ColorSetting> color3 = this.register(new Setting<>("Color2", new ColorSetting(0xC59B9B9B)));
    public PvPResources() {
        super("PvPResources", "PvPResources", 42,42);
    }

    @SubscribeEvent
    public void onRender2D(Render2DEvent e) {
        super.onRender2D(e);

        RenderUtil.drawBlurredShadow(getPosX(), getPosY(), 42, 42, 20, shadowColor.getValue().getColorObject());
        RoundedShader.drawRound(getPosX(), getPosY(), 42, 42, 7f, color2.getValue().getColorObject());


        int n2 = Method492(Items.TOTEM_OF_UNDYING);
        int n3 = Method492(Items.EXPERIENCE_BOTTLE);
        int n4 = Method492(Items.END_CRYSTAL);
        int n5 = Method492(Items.GOLDEN_APPLE);

        List<ItemStack> list = new ArrayList<>();

        if (n2 > 0) {
            list.add(new ItemStack(Items.TOTEM_OF_UNDYING, n2));
        }
        if (n3 > 0) {
            list.add(new ItemStack(Items.EXPERIENCE_BOTTLE, n3));
        }
        if (n4 > 0) {
            list.add(new ItemStack(Items.END_CRYSTAL, n4));
        }
        if (n5 > 0) {
            list.add(new ItemStack(Items.GOLDEN_APPLE, n5, 1));
        }

        int n6 = ((Collection<?>) list).size();
        for (int i = 0; i < n6; ++i) {
            GlStateManager.pushMatrix();
            GlStateManager.depthMask(true);
            GlStateManager.clear(256);
            RenderHelper.enableStandardItemLighting();
            mc.getRenderItem().zLevel = -150.0f;
            GlStateManager.disableAlpha();
            GlStateManager.enableDepth();
            GlStateManager.disableCull();
            int n7 = (int) getPosX();
            int n8 = (int) getPosY();
            ItemStack itemStack = list.get(i);
            n7 = i % 2 * 20;
            n8 = i / 2 * 20;
            mc.getRenderItem().renderItemAndEffectIntoGUI(itemStack, (int) (getPosX() + n7 + 2), (int) (getPosY() + n8 + 2));
            mc.getRenderItem().renderItemOverlays(mc.fontRenderer, itemStack, (int) (getPosX() + n7 + 2), (int) (getPosY() + n8 + 2));
            mc.getRenderItem().zLevel = 0.0f;
            RenderHelper.disableStandardItemLighting();
            GlStateManager.enableCull();
            GlStateManager.enableAlpha();
            GlStateManager.popMatrix();
        }
    }

    public int Method492(Item item) {
        if (mc.player == null) {
            return 0;
        }
        int n = 0;
        int n2 = 44;
        for (int i = 0; i <= n2; ++i) {
            ItemStack itemStack = mc.player.inventory.getStackInSlot(i);
            if (itemStack.getItem() != item) continue;
            n += itemStack.getCount();
        }
        return n;
    }
}
