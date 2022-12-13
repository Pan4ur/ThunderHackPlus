package com.mrzak34.thunderhack.gui.hud;

import com.mrzak34.thunderhack.event.events.Render2DEvent;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.ColorSetting;
import com.mrzak34.thunderhack.setting.PositionSetting;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.PaletteHelper;
import com.mrzak34.thunderhack.util.RectHelper;
import com.mrzak34.thunderhack.util.RenderUtil;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PvPResources extends Module{
    public PvPResources() {
        super("PvPResources", "PvPResources", Module.Category.HUD, true, false, false);
    }

    public final Setting<ColorSetting> color = this.register(new Setting<>("Color", new ColorSetting(0x8800FF00)));
    private final Setting<PositionSetting> pos = this.register(new Setting<>("Position", new PositionSetting(0.5f,0.5f)));
    public Setting <Integer> sizeRect = this.register( new Setting <> ( "asdasd", 100, 1, 120) );
    public final Setting<ColorSetting> color2 = this.register(new Setting<>("Color2", new ColorSetting(0x8800FF00)));



    float x1 =0;
    float y1= 0;

    @SubscribeEvent
    public void onRender2D(Render2DEvent e){
        ScaledResolution sr = new ScaledResolution(mc);



        y1 = sr.getScaledHeight() * pos.getValue().getY();
        x1 = sr.getScaledWidth() * pos.getValue().getX();

        RectHelper.drawBorderedRect(x1 + 2.5F, y1 + 2.5F, (x1 + sizeRect.getValue()) - 2.5F, (y1 + sizeRect.getValue()) - 2.5F, 0.5F, PaletteHelper.getColor(2),color.getValue().getColor(), false);
        RectHelper.drawBorderedRect(x1 + 3, y1 + 3, x1 + sizeRect.getValue() - 3, y1 + sizeRect.getValue() - 3, 0.2F, PaletteHelper.getColor(2), color.getValue().getColor(), false);

        RectHelper.drawRect(x1 + (sizeRect.getValue() / 2F - 0.5), y1 + 3.5, x1 + (sizeRect .getValue()/ 2F + 0.2), (y1 + sizeRect.getValue()) - 3.5, color2.getValue().getColor());
        RectHelper.drawRect(x1 + 3.5, y1 + (sizeRect .getValue()/ 2F - 0.2), (x1 + sizeRect.getValue()) - 3.5, y1 + (sizeRect .getValue()/ 2F + 0.5), color2.getValue().getColor());

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

        int n6 = ((Collection<?>)list).size();
        for (int i = 0; i < n6; ++i) {
            GlStateManager.pushMatrix();
            GlStateManager.depthMask((boolean)true);
            GlStateManager.clear((int)256);
            RenderHelper.enableStandardItemLighting();
            mc.getRenderItem().zLevel = -150.0f;
            GlStateManager.disableAlpha();
            GlStateManager.enableDepth();
            GlStateManager.disableCull();
            int n7 = (int) x1;
            int n8 = (int) y1;
            ItemStack itemStack = (ItemStack)list.get(i);
            n7 = i % 2 * 20;
            n8 = i / 2 * 20;
            mc.getRenderItem().renderItemAndEffectIntoGUI(itemStack, (int) (x1 + n7 + 2), (int) (y1 + n8 + 2));
            mc.getRenderItem().renderItemOverlays(mc.fontRenderer, itemStack, (int) (x1 +n7 + 2), (int) (y1+ n8 + 2));
            mc.getRenderItem().zLevel = 0.0f;
            RenderHelper.disableStandardItemLighting();
            GlStateManager.enableCull();
            GlStateManager.enableAlpha();
            GlStateManager.popMatrix();
        }






        if(mc.currentScreen instanceof GuiChat || mc.currentScreen instanceof HudEditorGui){
            if(isHovering()){
                if(Mouse.isButtonDown(0) && mousestate){
                    pos.getValue().setX( (float) (normaliseX() - dragX) /  sr.getScaledWidth());
                    pos.getValue().setY( (float) (normaliseY() - dragY) / sr.getScaledHeight());
                }

                RenderUtil.drawRect2(x1 - 10,y1 ,x1 + 50,y1 + 10,new Color(0x73A9A9A9, true).getRGB());
            }
        }

        if(Mouse.isButtonDown(0) && isHovering()){
            if(!mousestate){
                dragX = (int) (normaliseX() - (pos.getValue().getX() * sr.getScaledWidth()));
                dragY = (int) (normaliseY() - (pos.getValue().getY() * sr.getScaledHeight()));
            }
            mousestate = true;
        } else {
            mousestate = false;
        }
    }

    int dragX, dragY = 0;
    boolean mousestate = false;

    public int normaliseX(){
        return (int) ((Mouse.getX()/2f));
    }
    public int normaliseY(){
        ScaledResolution sr = new ScaledResolution(mc);
        return (((-Mouse.getY() + sr.getScaledHeight()) + sr.getScaledHeight())/2);
    }
    public int Method492( Item item) {
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
    public boolean isHovering(){
        return normaliseX() > x1 - 10 && normaliseX()< x1 + 50 && normaliseY() > y1 &&  normaliseY() < y1 + 10;
    }
}
