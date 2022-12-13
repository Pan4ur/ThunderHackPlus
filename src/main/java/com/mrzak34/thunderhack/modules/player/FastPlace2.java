package com.mrzak34.thunderhack.modules.player;

import com.mrzak34.thunderhack.event.events.EventPreMotion;
import com.mrzak34.thunderhack.event.events.Render2DEvent;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.setting.SubBind;
import com.mrzak34.thunderhack.util.PaletteHelper;
import com.mrzak34.thunderhack.util.*;
import net.minecraft.client.gui.ScaledResolution;
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

public class FastPlace2
        extends Module {
    public FastPlace2() {
        super("FastPlace", "пайро автоменд", Module.Category.PLAYER, true, false, false);
    }
    public static BlockPos target;


    private  Setting<Integer> threshold = this.register (new Setting<>("Percent", 100, 0, 100));
    public Setting<Integer> waterMarkZ1 = register(new Setting("Y", 10, 0, 524));
    public Setting<Integer> waterMarkZ2 = register(new Setting("X", 20, 0, 862));
    private  Setting<Integer> dlay = this.register (new Setting<>("delay", 100, 0, 100));
    private  Setting<Integer> armdlay = this.register (new Setting<>("ArmorDelay", 100, 0, 1000));
    public Setting<SubBind> aboba = this.register(new Setting<>("Butt", new SubBind(Keyboard.KEY_LMENU)));

    public Setting<Boolean> afast = this.register ( new Setting <> ( "AlwaysFast", false));




    public static boolean isMending = false;
    private boolean shouldMend = false;


    private Timer timer = new Timer();
    private Timer timer2 = new Timer();
    int arm1;
    int arm2;
    int arm3;
    int arm4;
    int totalarmor;




    private float yawi;
    int startingItem ;
    public void setYaw(float yawi) {
        this.yawi = yawi;
        Util.mc.getMinecraft();
        Util.mc.player.rotationYawHead = yawi;
        Util.mc.getMinecraft();
        Util.mc.player.renderYawOffset = yawi;
        Util.mc.player.rotationYawHead = yawi;
    }
    @SubscribeEvent
    public void onUpdateWalkingPlayer(EventPreMotion e) {
        if (mc.player == null || mc.world == null) return;
        ItemStack[] armorStacks2 = new ItemStack[]{
                mc.player.inventory.getStackInSlot(39),
                mc.player.inventory.getStackInSlot(38),
                mc.player.inventory.getStackInSlot(37),
                mc.player.inventory.getStackInSlot(36)
        };

        ItemStack stack2 = armorStacks2[0];
        ItemStack stack3 = armorStacks2[1];
        ItemStack stack4 = armorStacks2[2];
        ItemStack stack5 = armorStacks2[3];
        if (PlayerUtils.isKeyDown(aboba.getValue().getKey()) && (ArmorUtils.calculatePercentage(stack2) < threshold.getValue() || ArmorUtils.calculatePercentage(stack3) < threshold.getValue() || ArmorUtils.calculatePercentage(stack4) < threshold.getValue() || ArmorUtils.calculatePercentage(stack5) < threshold.getValue())) {


                int itemSlot = getXpSlot();
                boolean changeItem = mc.player.inventory.currentItem != itemSlot && itemSlot != -1;
                startingItem = mc.player.inventory.currentItem;

                if (changeItem) {
                    mc.player.inventory.currentItem = itemSlot;
                    mc.player.connection.sendPacket(new CPacketHeldItemChange(itemSlot));
                }
        }




            if (mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem() == Items.EXPERIENCE_BOTTLE || ( getXpSlot() != -1) && target != null) {
                if (PlayerUtils.isKeyDown(aboba.getValue().getKey()) && (ArmorUtils.calculatePercentage(stack2) < threshold.getValue() || ArmorUtils.calculatePercentage(stack3) < threshold.getValue() || ArmorUtils.calculatePercentage(stack4) < threshold.getValue() || ArmorUtils.calculatePercentage(stack5) < threshold.getValue())) {


                    shouldMend = false;



                   // FastPlace2.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Rotation(0.0f,  90.0f,  true));
                    target = FastPlace2.mc.player.getPosition().add(0.0f,-1.0f,0.0f);


                    ItemStack[] armorStacks = new ItemStack[]{
                            mc.player.inventory.getStackInSlot(39),
                            mc.player.inventory.getStackInSlot(38),
                            mc.player.inventory.getStackInSlot(37),
                            mc.player.inventory.getStackInSlot(36)
                    };

                    for (int i = 0; i < 4; i++) {

                        ItemStack stack = armorStacks[i];

                        if (!(stack.getItem() instanceof ItemArmor)) continue;

                        if (ArmorUtils.calculatePercentage(stack) < threshold.getValue()) continue;

                        for (int s = 0; s < 36; s++) {

                            ItemStack emptyStack = mc.player.inventory.getStackInSlot(s);

                            if (!emptyStack.isEmpty() || !(emptyStack.getItem() == Items.AIR)) continue;

                            isMending = true;
                            if(timer2.passedMs(armdlay.getValue())) {
                                mc.playerController.windowClick(mc.player.inventoryContainer.windowId, i + 5, 0, ClickType.PICKUP, mc.player);
                                mc.playerController.windowClick(mc.player.inventoryContainer.windowId, s < 9 ? s + 36 : s, 0, ClickType.PICKUP, mc.player);
                                mc.playerController.windowClick(mc.player.inventoryContainer.windowId, i + 5, 0, ClickType.PICKUP, mc.player);
                                mc.playerController.updateController();
                                timer2.reset();
                                return;
                            }
                        }

                    }

                    for (int i = 0; i < 4; i++) {
                        ItemStack stack = armorStacks[i];

                        if (!(stack.getItem() instanceof ItemArmor)) continue;

                        if (ArmorUtils.calculatePercentage(stack) >= threshold.getValue()) continue;

                        shouldMend = true;
                    }

                    if (!shouldMend) {
                        isMending = false;
                    }



                if (shouldMend) {



                    if (mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemExpBottle && timer.passedMs(dlay.getValue())) {
                        mc.playerController.processRightClick(mc.player, mc.world, EnumHand.MAIN_HAND);
                       // FastPlace2.mc.player.connection.sendPacket((Packet)new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
                        timer.reset();
                    }

                }
            } else {
                isMending = false;
                mc.player.connection.sendPacket(new CPacketHeldItemChange(startingItem));
                arm2 = 0;
                arm3 = 0;
                arm4 = 0;
                totalarmor = 0;
                target = null;
            }
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
    @Override
    public void onDisable() {
        isMending = false;
        target = null;
    }














    @Override
    public void onUpdate() {
        if(afast.getValue())
            mc.rightClickDelayTimer = 0;
    }



    @SubscribeEvent
    public void onRender2D(Render2DEvent e){
        ItemStack[] armorStacks21 = new ItemStack[]{
                mc.player.inventory.getStackInSlot(39),
                mc.player.inventory.getStackInSlot(38),
                mc.player.inventory.getStackInSlot(37),
                mc.player.inventory.getStackInSlot(36)
        };

        ItemStack stack21 = armorStacks21[0];
        ItemStack stack31 = armorStacks21[1];
        ItemStack stack41 = armorStacks21[2];
        ItemStack stack51 = armorStacks21[3];
        if (PlayerUtils.isKeyDown(aboba.getValue().getKey()) && (ArmorUtils.calculatePercentage(stack21) < threshold.getValue() || ArmorUtils.calculatePercentage(stack31) < threshold.getValue() || ArmorUtils.calculatePercentage(stack41) < threshold.getValue() || ArmorUtils.calculatePercentage(stack51) < threshold.getValue())) {
            ScaledResolution sr = new ScaledResolution(mc);
            //sr.getScaledWidth() / 2f
            //sr.getScaledHeight() / 2f

            int color;




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


            // Command.sendMessage(String.valueOf(ArmorUtils.calculatePercentage(stack)) + " " + String.valueOf(ArmorUtils.calculatePercentage(stack1)) + " " + String.valueOf(ArmorUtils.calculatePercentage(stack2)) + " " + String.valueOf(ArmorUtils.calculatePercentage(stack3)) );

            if (!((int) ArmorUtils.calculatePercentage(stack) < arm1)) {
                arm1 = (int) ArmorUtils.calculatePercentage(stack);
            }
            if (!((int) ArmorUtils.calculatePercentage(stack1) < arm2)) {
                arm2 = (int) ArmorUtils.calculatePercentage(stack1);
            }
            if (!((int) ArmorUtils.calculatePercentage(stack2) < arm3)) {
                arm3 = (int) ArmorUtils.calculatePercentage(stack2);
            }
            if (!((int) ArmorUtils.calculatePercentage(stack3) < arm4)) {
                arm4 = (int) ArmorUtils.calculatePercentage(stack3);
            }

            totalarmor = (arm1 + arm3 + arm4 + arm2) / 4;

            float progress;
            progress = (float) (arm1 + arm3 + arm4 + arm2) / 400;

          //  color = new Color(25, 255, 0, 255);
            color = PaletteHelper.fade(new Color(255, 0, 0, 255).getRGB(),new Color(0, 255, 0, 255).getRGB(), progress);




            final int expCount = this.getExpCount();

            FastPlace2.mc.renderItem.renderItemIntoGUI(new ItemStack(Items.EXPERIENCE_BOTTLE), (int) (waterMarkZ2.getValue() + offset + 70 +11), waterMarkZ1.getValue() + 17);
            final String s3 = String.valueOf(expCount);
            Util.fr.drawStringWithShadow(s3, waterMarkZ2.getValue() + offset + 85 +11, waterMarkZ1.getValue() + 9 + offset + 17, 16777215);

            RenderUtil.drawSmoothRect(waterMarkZ2.getValue() + 3, waterMarkZ1.getValue() + 12, totalarmor + waterMarkZ2.getValue() + 5, 15 + waterMarkZ1.getValue(), color);

            Util.fr.drawStringWithShadow("Mending...", waterMarkZ2.getValue() + 3, waterMarkZ1.getValue() + 1, PaletteHelper.astolfo(false, (int) 1).getRGB());




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
            if (FastPlace2.mc.player.inventory.getStackInSlot(i).getItem().equals(Items.EXPERIENCE_BOTTLE)) {
                expCount = expCount + FastPlace2.mc.player.inventory.getStackInSlot(i).stackSize;;
            }
        }
        if (FastPlace2.mc.player.getHeldItemOffhand().getItem().equals(Items.EXPERIENCE_BOTTLE)) {
            ++expCount;
        }
        return expCount;
    }




    public boolean rotate = true;


    @SubscribeEvent
    public void onUpdateWalkingPlayerEvent(EventPreMotion event) {
        if (target != null) {
            float[] angle = calcAngle(mc.player.getPositionEyes(mc.getRenderPartialTicks()), new Vec3d(target));
            mc.player.rotationPitch = (angle[1]);
        }
    }

    public static float[] calcAngle(Vec3d from, Vec3d to) {
        double difX = to.x - from.x;
        double difY = (to.y - from.y) * -1.0;
        double difZ = to.z - from.z;
        double dist = MathHelper.sqrt(difX * difX + difZ * difZ);
        return new float[]{(float) MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(difZ, difX)) - 90.0), (float) MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(difY, dist)))};
    }


}