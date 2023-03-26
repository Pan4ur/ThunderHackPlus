package com.mrzak34.thunderhack.util;

import com.mrzak34.thunderhack.mixin.ducks.IPlayerControllerMP;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.*;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.util.EnumHand;

import java.util.List;

import static com.mrzak34.thunderhack.util.phobos.HelperRotation.acquire;

public class InventoryUtil implements Util {

    public static int getBestSword() {
        int b = -1;
        float f = 1.0F;
        for (int b1 = 0; b1 < 9; b1++) {
            ItemStack itemStack = Util.mc.player.inventory.getStackInSlot(b1);
            if (itemStack != null && itemStack.getItem() instanceof ItemSword) {
                ItemSword itemSword = (ItemSword) itemStack.getItem();
                float f1 = itemSword.getMaxDamage();
                f1 += EnchantmentHelper.getEnchantmentLevel(Enchantment.getEnchantmentByID(20), itemStack);
                if (f1 > f) {
                    f = f1;
                    b = b1;
                }
            }
        }
        return b;
    }

    public static int getItemCount(Item item) {
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

    public static int getBestAxe() {
        int b = -1;
        float f = 1.0F;
        for (int b1 = 0; b1 < 9; b1++) {
            ItemStack itemStack = Util.mc.player.inventory.getStackInSlot(b1);
            if (itemStack != null && itemStack.getItem() instanceof ItemAxe) {
                ItemAxe axe = (ItemAxe) itemStack.getItem();
                float f1 = axe.getMaxDamage();
                f1 += EnchantmentHelper.getEnchantmentLevel(Enchantment.getEnchantmentByID(20), itemStack);
                if (f1 > f) {
                    f = f1;
                    b = b1;
                }
            }
        }
        return b;
    }

    public static int hotbarToInventory(int slot) {
        if (slot == -2) {
            return 45;
        }

        if (slot > -1 && slot < 9) {
            return 36 + slot;
        }

        return slot;
    }

    public static void bypassSwitch(int slot) {
        if (slot >= 0) {
            mc.playerController.pickItem(slot);
        }
    }

    public static void switchTo(int slot) {
        if (mc.player.inventory.currentItem != slot && slot > -1 && slot < 9) {
            mc.player.inventory.currentItem = slot;
            syncItem();
        }
    }

    public static void switchToBypass(int slot) {
        acquire(() ->
        {
            if (mc.player.inventory.currentItem != slot
                    && slot > -1 && slot < 9) {
                int lastSlot = mc.player.inventory.currentItem;
                int targetSlot = hotbarToInventory(slot);
                int currentSlot = hotbarToInventory(lastSlot);
                mc.playerController.windowClick(0, targetSlot, 0, ClickType.PICKUP, mc.player);
                mc.playerController.windowClick(0, currentSlot, 0, ClickType.PICKUP, mc.player);
                mc.playerController.windowClick(0, targetSlot, 0, ClickType.PICKUP, mc.player);
            }
        });
    }

    /**
     * Bypasses NCP item switch cooldown
     *
     * @param slot INVENTORY SLOT (NOT HOTBAR) to switch to
     */
    public static void switchToBypassAlt(int slot) {
        acquire(() ->
        {
            if (mc.player.inventory.currentItem != slot
                    && slot > -1 && slot < 9) {
                acquire(() ->
                        mc.playerController.windowClick(0, slot, mc.player.inventory.currentItem, ClickType.SWAP, mc.player))
                ;
            }
        });
    }

    public static EnumHand getHand(int slot) {
        return slot == -2 ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;
    }

    public static void syncItem() {
        ((IPlayerControllerMP) mc.playerController).syncItem();
    }

    public static EnumHand getHand(Item item) {
        return mc.player.getHeldItemMainhand().getItem() == item
                ? EnumHand.MAIN_HAND
                : mc.player.getHeldItemOffhand().getItem() == item
                ? EnumHand.OFF_HAND
                : null;
    }

    public static void switchToHotbarSlot(int slot, boolean silent) {
        if (mc.player.inventory.currentItem == slot || slot < 0) {
            return;
        }
        if (silent) {
            mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
            mc.playerController.updateController();
        } else {
            mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
            mc.player.inventory.currentItem = slot;
            mc.playerController.updateController();
        }
    }

    public static int containerToSlots(int containerSlot) {
        if (containerSlot < 5 || containerSlot > 45) { // crafting slots
            return -1;
        }

        if (containerSlot <= 9) {
            return 44 - containerSlot;
        }

        if (containerSlot < 36) {
            return containerSlot;
        }

        if (containerSlot < 45) {
            return containerSlot - 36;
        }

        return 40; // offhand is 40 here
    }

    public static void put(int slot, ItemStack stack) {
        if (slot == -2) {
            mc.player.inventory.setItemStack(stack);
        }

        mc.player.inventoryContainer.putStackInSlot(slot, stack);

        int invSlot = containerToSlots(slot);
        if (invSlot != -1) {
            mc.player.inventory.setInventorySlotContents(invSlot, stack);
        }
    }

    public static int getCount(Item item) {
        int result = 0;
        for (int i = 0; i < 46; i++) {
            ItemStack stack = mc.player
                    .inventoryContainer
                    .getInventory()
                    .get(i);

            if (stack.getItem() == item) {
                result += stack.getCount();
            }
        }

        if (mc.player.inventory.getItemStack().getItem() == item) {
            result += mc.player.inventory.getItemStack().getCount();
        }

        return result;
    }

    public static ItemStack get(int slot) {
        if (slot == -2) {
            return mc.player.inventory.getItemStack();
        }

        return mc.player.inventoryContainer.getInventory().get(slot);
    }

    public static int findSoupAtHotbar() {
        int b = -1;
        for (int a = 0; a < 9; ++a) {
            if (InventoryUtil.mc.player.inventory.getStackInSlot(a).getItem() != Items.MUSHROOM_STEW) continue;
            b = a;
        }
        return b;
    }


    public static int findFirstBlockSlot(Class<? extends Block> blockToFind, int lower, int upper) {
        int slot = -1;
        List<ItemStack> mainInventory = mc.player.inventory.mainInventory;

        for (int i = lower; i <= upper; i++) {
            ItemStack stack = mainInventory.get(i);

            if (stack == ItemStack.EMPTY || !(stack.getItem() instanceof ItemBlock)) {
                continue;
            }

            if (blockToFind.isInstance(((ItemBlock) stack.getItem()).getBlock())) {
                slot = i;
                break;
            }
        }
        return slot;
    }

    public static int getBowAtHotbar() {
        for (int i = 0; i < 9; ++i) {
            ItemStack itemStack = Util.mc.player.inventory.getStackInSlot(i);
            if (!(itemStack.getItem() instanceof ItemBow)) continue;
            return i;
        }
        return -1;
    }

    public static int getCrysathotbar() {
        for (int i = 0; i < 9; ++i) {
            ItemStack itemStack = Util.mc.player.inventory.getStackInSlot(i);
            if (!(itemStack.getItem() instanceof ItemEndCrystal)) continue;
            return i;
        }
        return -1;
    }

    public static int getPicatHotbar() {
        for (int i = 0; i < 9; ++i) {
            ItemStack itemStack = Util.mc.player.inventory.getStackInSlot(i);
            if (!(itemStack.getItem() instanceof ItemPickaxe)) continue;
            return i;
        }
        return -1;
    }


    public static int getPowderAtHotbar() {
        for (int i = 0; i < 9; ++i) {
            ItemStack itemStack = Util.mc.player.inventory.getStackInSlot(i);
            if (!(itemStack.getItem().getItemStackDisplayName(itemStack).equals("Порох"))) continue;
            return i;
        }
        return 1;
    }


    public static int findHotbarBlock(Class clazz) {
        for (int i = 0; i < 9; ++i) {
            Block block;
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack == ItemStack.EMPTY) continue;
            if (clazz.isInstance(stack.getItem())) {
                return i;
            }
            if (!(stack.getItem() instanceof ItemBlock) || !clazz.isInstance(block = ((ItemBlock) stack.getItem()).getBlock()))
                continue;
            return i;
        }
        return -1;
    }


    public static int findItemAtHotbar(Item stacks) {
        for (int i = 0; i < 9; ++i) {
            Item stack = mc.player.inventory.getStackInSlot(i).getItem();
            if (stack == Items.AIR) continue;
            if (stack == stacks) {
                return i;
            }
        }
        return -1;
    }

    public static int findHotbarBlock(Block blockIn) {
        for (int i = 0; i < 9; ++i) {
            Block block;
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack == ItemStack.EMPTY || !(stack.getItem() instanceof ItemBlock) || (block = ((ItemBlock) stack.getItem()).getBlock()) != blockIn)
                continue;
            return i;
        }
        return -1;
    }

    public static int getCappuchinoAtHotbar(boolean old) {
        for (int i = 0; i < 9; ++i) {
            ItemStack itemStack = Util.mc.player.inventory.getStackInSlot(i);

            if(old){
                if (!(itemStack.getItem() == Items.POTIONITEM)) continue;
            } else {
                if (!(itemStack.getItem() == Items.DRAGON_BREATH)) continue;
            }

            if (!(itemStack.getDisplayName().contains("Каппучино"))) continue;
            return i;
        }
        return -1;
    }

    public static int getAmericanoAtHotbar(boolean old) {
        for (int i = 0; i < 9; ++i) {
            ItemStack itemStack = Util.mc.player.inventory.getStackInSlot(i);
            if(old){
                if (!(itemStack.getItem() == Items.POTIONITEM)) continue;
            } else {
                if (!(itemStack.getItem() == Items.DRAGON_BREATH)) continue;
            }
            if (!(itemStack.getDisplayName().contains("Американо"))) continue;
            return i;
        }
        return -1;
    }

    public static int getOzeraAtHotbar(boolean old) {
        for (int i = 0; i < 9; ++i) {
            ItemStack itemStack = Util.mc.player.inventory.getStackInSlot(i);
            if(old){
                if (!(itemStack.getItem() == Items.POTIONITEM)) continue;
            } else {
                if (!(itemStack.getItem() == Items.DRAGON_BREATH)) continue;
            }
            if (!(itemStack.getDisplayName().contains("Родные озёра"))) continue;
            return i;
        }
        return -1;
    }

    public static ItemStack getPotionItemStack(boolean old) {
        for (int i = 0; i < 9; ++i) {
            ItemStack itemStack = Util.mc.player.inventory.getStackInSlot(i);
            if(old){
                if (!(itemStack.getItem() == Items.POTIONITEM)) continue;
            } else {
                if (!(itemStack.getItem() == Items.DRAGON_BREATH)) continue;
            }
            if (!(itemStack.getDisplayName().contains("Каппучино"))) continue;
            return itemStack;
        }
        return null;
    }



    public static boolean isInstanceOf(ItemStack stack, Class clazz) {
        if (stack == null) {
            return false;
        }
        Item item = stack.getItem();
        if (clazz.isInstance(item)) {
            return true;
        }
        if (item instanceof ItemBlock) {
            Block block = Block.getBlockFromItem(item);
            return clazz.isInstance(block);
        }
        return false;
    }


    public static boolean isHolding(EntityPlayer player, Item experienceBottle) {
        return player.getHeldItemMainhand().getItem() == experienceBottle || player.getHeldItemOffhand().getItem() == experienceBottle;
    }

    public static boolean isHolding(Item experienceBottle) {
        return mc.player.getHeldItemMainhand().getItem() == experienceBottle || mc.player.getHeldItemOffhand().getItem() == experienceBottle;
    }


    public static boolean isHolding(Block block) {
        ItemStack mainHand = mc.player.getHeldItemMainhand();
        ItemStack offHand = mc.player.getHeldItemOffhand();

        if (!(mainHand.getItem() instanceof ItemBlock) || !(offHand.getItem() instanceof ItemBlock)) return false;
        return ((ItemBlock) mainHand.getItem()).getBlock() == block || ((ItemBlock) offHand.getItem()).getBlock() == block;
    }

    public static int getRodSlot() {
        for (int i = 0; i < 9; i++) {
            final ItemStack item = mc.player.inventory.getStackInSlot(i);
            if (item.getItem() == Items.FISHING_ROD && item.getItemDamage() < 52) {
                return i;
            }
        }
        return -1;
    }

    public static int swapToHotbarSlot(int slot, boolean silent) {
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
            if (!(stack.getItem() instanceof ItemBlock) || !clazz.isInstance(((ItemBlock) stack.getItem()).getBlock()))
                continue;
            return i;
        }
        return -1;
    }


    public static int getElytra() {
        for (ItemStack stack : mc.player.getArmorInventoryList()) {
            if (stack.getItem() == Items.ELYTRA) {
                return -2;
            }
        }
        int slot = -1;
        for (int i = 0; i < 36; i++) {
            ItemStack s = mc.player.inventory.getStackInSlot(i);
            if (s.getItem() == Items.ELYTRA) {
                slot = i;
                break;
            }
        }
        if (slot < 9 && slot != -1) {
            slot = slot + 36;
        }
        return slot;
    }

    public static int getFireWorks() {
        for (int i = 0; i < 9; i++) {
            if (mc.player.inventory.getStackInSlot(i).getItem() instanceof ItemFirework) {
                return i;
            }
        }
        return -1;
    }
}

