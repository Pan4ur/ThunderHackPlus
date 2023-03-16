package com.mrzak34.thunderhack.modules.player;

import com.mrzak34.thunderhack.events.EventPostMotion;
import com.mrzak34.thunderhack.events.EventPreMotion;
import com.mrzak34.thunderhack.events.Render2DEvent;
import com.mrzak34.thunderhack.mixin.mixins.IMinecraft;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.setting.SubBind;
import com.mrzak34.thunderhack.util.PlayerUtils;
import com.mrzak34.thunderhack.util.Timer;
import com.mrzak34.thunderhack.util.Util;
import com.mrzak34.thunderhack.util.render.PaletteHelper;
import com.mrzak34.thunderhack.util.render.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemExpBottle;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

import java.awt.*;

public class AutoMend extends Module {

    public AutoMend() {
        super("AutoMend", "необходимо включить-AutoArmor!","turn on AutoArmor!", Module.Category.PLAYER);
    }

    public Setting<Integer> waterMarkZ1 = register(new Setting("Y", 10, 0, 524));
    public Setting<Integer> waterMarkZ2 = register(new Setting("X", 20, 0, 862));
    public Setting<SubBind> subBind = this.register(new Setting<>("subbind", new SubBind(Keyboard.KEY_LMENU)));
    private final Setting<Integer> threshold = this.register(new Setting<>("Percent", 100, 0, 100));
    private final Setting<Integer> dlay = this.register(new Setting<>("ThrowDelay", 100, 0, 100));
    private final Setting<Integer> armdlay = this.register(new Setting<>("ArmorDelay", 100, 0, 1000));

    private final Timer timer = new Timer();
    private final Timer timer2 = new Timer();
    int arm1,arm2,arm3,arm4,totalarmor,prev_item;
    public static boolean isMending = false;





    @SubscribeEvent
    public void onEntitySync(EventPreMotion event) {
        if (PlayerUtils.isKeyDown(subBind.getValue().getKey())) {
            mc.player.rotationPitch = 90;
            isMending = true;
        } else {
            isMending = false;
        }
    }

    @SubscribeEvent
    public void postEntitySync(EventPostMotion e) {
        if (fullNullCheck()) return;
        if (
                PlayerUtils.isKeyDown(subBind.getValue().getKey()) && (
                        calculatePercentage(mc.player.inventory.getStackInSlot(39)) < threshold.getValue()
                                || calculatePercentage(mc.player.inventory.getStackInSlot(38)) < threshold.getValue()
                                || calculatePercentage(mc.player.inventory.getStackInSlot(37)) < threshold.getValue()
                                || calculatePercentage(mc.player.inventory.getStackInSlot(36)) < threshold.getValue()
                ) && getXpSlot() != -1
        ) {
            prev_item = mc.player.inventory.currentItem;
            mc.player.inventory.currentItem = getXpSlot();
            mc.player.connection.sendPacket(new CPacketHeldItemChange(getXpSlot()));

            ItemStack[] armorStacks = new ItemStack[]{
                    mc.player.inventory.getStackInSlot(39),
                    mc.player.inventory.getStackInSlot(38),
                    mc.player.inventory.getStackInSlot(37),
                    mc.player.inventory.getStackInSlot(36)
            };

            for (int i = 0; i < 4; i++) {
                ItemStack stack = armorStacks[i];
                if (!(stack.getItem() instanceof ItemArmor)) continue;
                if (calculatePercentage(stack) < threshold.getValue()) continue;
                for (int s = 0; s < 36; s++) {
                    ItemStack emptyStack = mc.player.inventory.getStackInSlot(s);
                    if (!emptyStack.isEmpty() || !(emptyStack.getItem() == Items.AIR)) continue;
                    if (timer2.passedMs(armdlay.getValue())) {
                        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, i + 5, 0, ClickType.PICKUP, mc.player);
                        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, s < 9 ? s + 36 : s, 0, ClickType.PICKUP, mc.player);
                        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, i + 5, 0, ClickType.PICKUP, mc.player);
                        mc.playerController.updateController();
                        timer2.reset();
                        return;
                    }
                }
            }

            if (timer.passedMs(dlay.getValue())) {
                mc.playerController.processRightClick(mc.player, mc.world, EnumHand.MAIN_HAND);
                timer.reset();
            }

        } else if (prev_item != -1) {
            mc.player.connection.sendPacket(new CPacketHeldItemChange(prev_item));
            prev_item = -1;
            arm1 = 0;
            arm2 = 0;
            arm3 = 0;
            arm4 = 0;
            totalarmor = 0;
        }
    }

    private int getXpSlot() {
        ItemStack stack = mc.player.getHeldItemMainhand();

        if (!stack.isEmpty() && stack.getItem() instanceof ItemExpBottle) {
            return mc.player.inventory.currentItem;
        } else {
            for (int i = 0; i < 9; ++i) {
                stack = mc.player.inventory.getStackInSlot(i);
                if (!stack.isEmpty() && stack.getItem() instanceof ItemExpBottle) {
                    return i;
                }
            }
        }
        return -1;
    }


    @SubscribeEvent
    public void onRender2D(Render2DEvent e) {
        if (PlayerUtils.isKeyDown(subBind.getValue().getKey())) {
            RenderUtil.drawSmoothRect(waterMarkZ2.getValue(), waterMarkZ1.getValue(), 106 + waterMarkZ2.getValue(), 35 + waterMarkZ1.getValue(), new Color(35, 35, 40, 230).getRGB());
            RenderUtil.drawSmoothRect(waterMarkZ2.getValue() + 3, waterMarkZ1.getValue() + 12, 103 + waterMarkZ2.getValue(), 15 + waterMarkZ1.getValue(), new Color(51, 51, 58, 230).getRGB());

            ItemStack[] armorStacks = new ItemStack[]{
                    mc.player.inventory.getStackInSlot(39),
                    mc.player.inventory.getStackInSlot(38),
                    mc.player.inventory.getStackInSlot(37),
                    mc.player.inventory.getStackInSlot(36)
            };

            ItemStack stack = armorStacks[0];
            ItemStack stack1 = armorStacks[1];
            ItemStack stack2 = armorStacks[2];
            ItemStack stack3 = armorStacks[3];


            if (!((int) calculatePercentage(stack) < arm1)) {
                arm1 = (int) calculatePercentage(stack);
            }
            if (!((int) calculatePercentage(stack1) < arm2)) {
                arm2 = (int) calculatePercentage(stack1);
            }
            if (!((int) calculatePercentage(stack2) < arm3)) {
                arm3 = (int) calculatePercentage(stack2);
            }
            if (!((int) calculatePercentage(stack3) < arm4)) {
                arm4 = (int) calculatePercentage(stack3);
            }

            totalarmor = (arm1 + arm3 + arm4 + arm2) / 4;

            float progress = (float) (arm1 + arm3 + arm4 + arm2) / 400;


            final int expCount = this.getExpCount();

            AutoMend.mc.getRenderItem().renderItemIntoGUI(new ItemStack(Items.EXPERIENCE_BOTTLE), waterMarkZ2.getValue() + 70 + 11, waterMarkZ1.getValue() + 17);
            final String s3 = String.valueOf(expCount);
            Util.fr.drawStringWithShadow(s3, waterMarkZ2.getValue() + 85 + 11, waterMarkZ1.getValue() + 9 + 17, 16777215);

            RenderUtil.drawSmoothRect(waterMarkZ2.getValue() + 3, waterMarkZ1.getValue() + 12, totalarmor + waterMarkZ2.getValue() + 5, 15 + waterMarkZ1.getValue(), PaletteHelper.fade(new Color(255, 0, 0, 255).getRGB(), new Color(0, 255, 0, 255).getRGB(), progress));

            Util.fr.drawStringWithShadow("Mending...", waterMarkZ2.getValue() + 3, waterMarkZ1.getValue() + 1, PaletteHelper.astolfo(false, 1).getRGB());

            int width = waterMarkZ2.getValue() + -12;
            int height = waterMarkZ1.getValue() + 17;
            GlStateManager.enableTexture2D();
            int i = width;
            int iteration = 0;
            int y = height;
            for (ItemStack is : mc.player.inventory.armorInventory) {
                iteration++;
                if (is.isEmpty())
                    continue;
                int x = i - 90 + (9 - iteration) * 20 + 2;
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
            }
            GlStateManager.enableDepth();
            GlStateManager.disableLighting();
        }
    }

    private int getExpCount() {
        int expCount = 0;
        for (int i = 0; i < 45; ++i) {
            if (AutoMend.mc.player.inventory.getStackInSlot(i).getItem().equals(Items.EXPERIENCE_BOTTLE)) {
                expCount = expCount + AutoMend.mc.player.inventory.getStackInSlot(i).getCount();
            }
        }
        if (AutoMend.mc.player.getHeldItemOffhand().getItem().equals(Items.EXPERIENCE_BOTTLE)) {
            ++expCount;
        }
        return expCount;
    }

    public static float calculatePercentage(ItemStack stack) {
        float durability = stack.getMaxDamage() - stack.getItemDamage();
        return (durability / (float) stack.getMaxDamage()) * 100F;
    }

}