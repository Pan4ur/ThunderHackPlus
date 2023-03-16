package com.mrzak34.thunderhack.modules.funnygame;

import com.mrzak34.thunderhack.command.Command;
import com.mrzak34.thunderhack.events.Render2DEvent;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.InventoryUtil;
import com.mrzak34.thunderhack.util.Timer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Random;

import static com.mrzak34.thunderhack.util.PlayerUtils.getPlayerPos;


public class AutoPot extends Module {
    public static int neededCap = 0;
    public Setting<Integer> triggerhealth = this.register(new Setting<Integer>("TriggerHealth", 10, 1, 36));
    public Setting<Integer> delay = this.register(new Setting<Integer>("delay", 200, 1, 2000));
    public Setting<Boolean> animation = register(new Setting<>("Animation", true));
    public Timer timer = new Timer();
    public Timer alerttimer = new Timer();
    private int itemActivationTicks;
    private float itemActivationOffX;
    private float itemActivationOffY;
    private ItemStack itemActivationItem;
    private final Random random = new Random();


    public AutoPot() {
        super("AutoCappuccino", "автокаппучино для-фангейма", Category.FUNNYGAME);
    }

    @Override
    public void onUpdate() {
        if (mc.player.getHealth() < triggerhealth.getValue() && timer.passedMs(delay.getValue()) && InventoryUtil.getCappuchinoAtHotbar() != -1) {
            itemActivationItem = InventoryUtil.getPotionItemStack();
            int hotbarslot = mc.player.inventory.currentItem;
            mc.world.playSound(getPlayerPos(), SoundEvents.BLOCK_BREWING_STAND_BREW, SoundCategory.AMBIENT, 150.0f, 1.0F, true);
            mc.player.connection.sendPacket(new CPacketHeldItemChange(InventoryUtil.getCappuchinoAtHotbar()));
            mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
            mc.player.connection.sendPacket(new CPacketHeldItemChange(hotbarslot));
            ++neededCap;
            aboba();
            timer.reset();
        }
        if ((InventoryUtil.getCappuchinoAtHotbar() == -1) && alerttimer.passedMs(1000)) {
            Command.sendMessage("Нема зелек!!!!");
            mc.world.playSound(getPlayerPos(), SoundEvents.ENTITY_BLAZE_HURT, SoundCategory.AMBIENT, 150.0f, 10.0F, true);
            alerttimer.reset();
        }
        if (this.itemActivationTicks > 0) {
            --this.itemActivationTicks;
            if (this.itemActivationTicks == 0) {
                this.itemActivationItem = null;
            }
        }
    }

    @SubscribeEvent
    public void onRender2D(Render2DEvent e) {
        if (animation.getValue())
            renderItemActivation(e.scaledResolution.getScaledWidth(), e.scaledResolution.getScaledHeight(), mc.getRenderPartialTicks());
    }

    public void aboba() {
        this.itemActivationTicks = 40;
        this.itemActivationOffX = this.random.nextFloat() * 2.0F - 1.0F;
        this.itemActivationOffY = this.random.nextFloat() * 2.0F - 1.0F;
    }


    public void renderItemActivation(int p_190563_1_, int p_190563_2_, float p_190563_3_) {

        if (this.itemActivationItem != null && this.itemActivationTicks > 0) {
            int i = 40 - this.itemActivationTicks;
            float f = ((float) i + p_190563_3_) / 40.0F;
            float f1 = f * f;
            float f2 = f * f1;
            float f3 = 10.25F * f2 * f1 + -24.95F * f1 * f1 + 25.5F * f2 + -13.8F * f1 + 4.0F * f;
            float f4 = f3 * 3.1415927F;
            float f5 = this.itemActivationOffX * (float) (p_190563_1_ / 4);
            float f6 = this.itemActivationOffY * (float) (p_190563_2_ / 4);
            GlStateManager.enableAlpha();
            GlStateManager.pushMatrix();
            GlStateManager.pushAttrib();
            GlStateManager.enableDepth();
            GlStateManager.disableCull();
            net.minecraft.client.renderer.RenderHelper.enableStandardItemLighting();
            GlStateManager.translate((float) (p_190563_1_ / 2) + f5 * MathHelper.abs(MathHelper.sin(f4 * 2.0F)), (float) (p_190563_2_ / 2) + f6 * MathHelper.abs(MathHelper.sin(f4 * 2.0F)), -50.0F);
            float f7 = 50.0F + 175.0F * MathHelper.sin(f4);
            GlStateManager.scale(f7, -f7, f7);
            GlStateManager.rotate(900.0F * MathHelper.abs(MathHelper.sin(f4)), 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(6.0F * MathHelper.cos(f * 8.0F), 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(6.0F * MathHelper.cos(f * 8.0F), 0.0F, 0.0F, 1.0F);
            this.mc.getRenderItem().renderItem(itemActivationItem, ItemCameraTransforms.TransformType.FIXED);
            GlStateManager.popAttrib();
            GlStateManager.popMatrix();
            RenderHelper.disableStandardItemLighting();
            GlStateManager.enableCull();
            GlStateManager.disableDepth();
        }

    }


}
