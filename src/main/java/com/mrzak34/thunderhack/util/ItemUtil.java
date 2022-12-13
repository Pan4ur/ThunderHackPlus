package com.mrzak34.thunderhack.util;


import net.minecraft.client.Minecraft;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketHeldItemChange;


public class ItemUtil implements Util{
    public static int getGappleSlot(boolean crapple) {
        if (Items.GOLDEN_APPLE == mc.player.getHeldItemOffhand().getItem() && (crapple == (mc.player.getHeldItemOffhand().getRarity().equals(EnumRarity.RARE))))
            return -1;
        for (int i = 36; i >= 0; i--) {
            final ItemStack item = mc.player.inventory.getStackInSlot(i);
            if ((crapple == item.getRarity().equals(EnumRarity.RARE)) && item.getItem() == Items.GOLDEN_APPLE) {
                if (i < 9) {
                    i += 36;
                }
                return i;
            }
        }
        return -1;
    }


    public static int getItemSlot(Item input) {
        if (input == mc.player.getHeldItemOffhand().getItem()) return -1;
        for (int i = 36; i >= 0; i--) {
            final Item item = mc.player.inventory.getStackInSlot(i).getItem();
            if (item == input) {
                if (i < 9) {
                    i += 36;
                }
                return i;
            }
        }
        return -1;
    }

    public static void swapToOffhandSlot(int slot) {
        if (slot == -1) return;
        mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, mc.player);
        mc.playerController.windowClick(0, 45, 0, ClickType.PICKUP, mc.player);
        mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, mc.player);
        mc.playerController.updateController();
    }

    public static int swapToHotbarSlot(int slot, boolean silent){
        if (mc.player.inventory.currentItem == slot || slot < 0 || slot > 8) return slot;
        mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
        if (!silent) mc.player.inventory.currentItem = slot;
        mc.playerController.updateController();
        return slot;
    }

    public static int findItem(Class clazz) {
        for (int i = 0; i < 9; ++i) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack == ItemStack.EMPTY) continue;
            if (clazz.isInstance(stack.getItem())) {
                return i;
            }
            if (!(stack.getItem() instanceof ItemBlock) || !clazz.isInstance((( ItemBlock ) stack.getItem()).getBlock()))
                continue;
            return i;
        }
        return -1;
    }


    public static final Minecraft mc = Minecraft.getMinecraft();

}
