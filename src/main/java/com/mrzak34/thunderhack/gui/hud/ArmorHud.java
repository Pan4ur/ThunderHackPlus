package com.mrzak34.thunderhack.gui.hud;

import com.mrzak34.thunderhack.event.events.Render2DEvent;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.ColorSetting;
import com.mrzak34.thunderhack.setting.PositionSetting;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.ArmorUtils;
import com.mrzak34.thunderhack.util.ColorUtil;
import com.mrzak34.thunderhack.util.RenderUtil;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ArmorHud extends Module{
    public ArmorHud() {
        super("ArmorHud", "fps", Module.Category.HUD, true, false, false);
    }

    public final Setting<ColorSetting> color = this.register(new Setting<>("Color", new ColorSetting(0x8800FF00)));


    private final Setting<PositionSetting> pos = this.register(new Setting<>("Position", new PositionSetting(0.5f,0.5f)));



    float x1 =0;
    float y1= 0;

    @SubscribeEvent
    public void onRender2D(Render2DEvent e){
        ScaledResolution sr = new ScaledResolution(mc);

        y1 = sr.getScaledHeight() * pos.getValue().getY();
        x1 = sr.getScaledWidth() * pos.getValue().getX();
        renderArmorHUD(true);

    }
    public void renderArmorHUD(boolean percent) {
        ScaledResolution sr = new ScaledResolution(mc);

        y1 = sr.getScaledHeight() * pos.getValue().getY();
        x1 = sr.getScaledWidth() * pos.getValue().getX();
        GlStateManager.enableTexture2D();

        int iteration = 0;
        int y = (int) (y1 - 55 - ((mc.player.isInWater() && mc.playerController.gameIsSurvivalOrAdventure()) ? 10 : 0));





        for (ItemStack is : mc.player.inventory.armorInventory) {
            iteration++;
            if (is.isEmpty())
                continue;
            int x = (int) (x1 - 90 + (9 - iteration) * 20 + 2);
            GlStateManager.enableDepth();
            RenderUtil.itemRender.zLevel = 200.0F;
            RenderUtil.itemRender.renderItemAndEffectIntoGUI(is, x, y);
            RenderUtil.itemRender.renderItemOverlayIntoGUI(mc.fontRenderer, is, x, y, "");
            RenderUtil.itemRender.zLevel = 0.0F;
            GlStateManager.enableTexture2D();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            String s = (is.getCount() > 1) ? (is.getCount() + "") : "";
            mc.fontRenderer.drawStringWithShadow(s, (x + 19 - 2 - mc.fontRenderer.getStringWidth(s)), (y + 9), 16777215);
            if (percent) {
                int dmg = (int) ArmorUtils.calculatePercentage(is);
                mc.fontRenderer.drawStringWithShadow(dmg + "", (x + 8 - mc.fontRenderer.getStringWidth(dmg + "") / 2), (y - 11), ColorUtil.toRGBA((int) (0 * 255.0F), (int) (1 * 255.0F), 0));
            }
        }
        GlStateManager.enableDepth();
        GlStateManager.disableLighting();
    }
}
