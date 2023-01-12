package com.mrzak34.thunderhack.util;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.command.Command;
import com.mrzak34.thunderhack.mixin.ducks.IPlayerControllerMP;
import net.minecraft.block.Block;
import net.minecraft.block.BlockObsidian;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.Slot;
import net.minecraft.item.*;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.util.EnumHand;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.mrzak34.thunderhack.util.phobos.HelperRotation.acquire;

public class InventoryUtil
        implements Util {


    public static int hotbarToInventory(int slot)
    {
        if (slot == -2)
        {
            return 45;
        }

        if (slot > -1 && slot < 9)
        {
            return 36 + slot;
        }

        return slot;
    }
    public static void bypassSwitch(int slot)
    {
        if (slot >= 0)
        {
            mc.playerController.pickItem(slot);
        }
    }

    public static void switchTo(int slot)
    {
        if (mc.player.inventory.currentItem != slot && slot > -1 && slot < 9)
        {
            mc.player.inventory.currentItem = slot;
            syncItem();
        }
    }

    public static void switchToBypass(int slot)
    {
        acquire(() ->
       {
            if (mc.player.inventory.currentItem != slot
                    && slot > -1 && slot < 9)
            {
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
     * @param slot INVENTORY SLOT (NOT HOTBAR) to switch to
     */
    public static void switchToBypassAlt(int slot)
    {
        acquire(() ->
        {
            if (mc.player.inventory.currentItem != slot
                    && slot > -1 && slot < 9)
            {
                acquire(() ->
                mc.playerController.windowClick(0, slot, mc.player.inventory.currentItem, ClickType.SWAP, mc.player))
            ;
            }
        });
    }

    public static EnumHand getHand(int slot)
    {
        return slot == -2 ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;
    }
    public static void syncItem()
    {
        ((IPlayerControllerMP) mc.playerController).syncItem();
    }
    public static EnumHand getHand(Item item)
    {
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
    public static void put(int slot, ItemStack stack)
    {
        if (slot == -2)
        {
            mc.player.inventory.setItemStack(stack);
        }

        mc.player.inventoryContainer.putStackInSlot(slot, stack);

        int invSlot = containerToSlots(slot);
        if (invSlot != -1) {
            mc.player.inventory.setInventorySlotContents(invSlot, stack);
        }
    }
    public static void click(int slot)
    {
        mc.playerController
                .windowClick(0, slot, 0, ClickType.PICKUP, mc.player);
    }

    public static void clickLocked(int slot, int to, Item inSlot, Item inTo)
    {

            if ((slot == -1 || get(slot).getItem() == inSlot)
                    && get(to).getItem() == inTo)
            {
                boolean multi = slot >= 0;
                if (multi)
                {
                    click(slot);
                }

                click(to);

                if (multi)
                {
                }
            }

    }


    public static int findEmptyHotbarSlot()
    {
        int result = -1;
        for (int i = 8; i > -1; i--)
        {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack.isEmpty() || stack.getItem() == Items.AIR)
            {
                result = i;
            }
        }

        return result;
    }


    public static int findItem(Item item, boolean xCarry)
    {
        return findItem(item, xCarry, Collections.emptySet());
    }


    public static int findItem(Item item, boolean xCarry, Set<Integer> ignore)
    {
        if (mc.player.inventory.getItemStack().getItem() == item
                && !ignore.contains(-2))
        {
            return -2;
        }

        for (int i = 9; i < 45; i++)
        {
            if (ignore.contains(i))
            {
                continue;
            }

            if (get(i).getItem() == item)
            {
                return i;
            }
        }

        if (xCarry)
        {
            for (int i = 1; i < 5; i++)
            {
                if (ignore.contains(i))
                {
                    continue;
                }

                if (get(i).getItem() == item)
                {
                    return i;
                }
            }
        }

        return -1;
    }
    public static int getCount(Item item)
    {
        int result = 0;
        for (int i = 0; i < 46; i++)
        {
            ItemStack stack = mc.player
                    .inventoryContainer
                    .getInventory()
                    .get(i);

            if (stack.getItem() == item)
            {
                result += stack.getCount();
            }
        }

        if (mc.player.inventory.getItemStack().getItem() == item)
        {
            result += mc.player.inventory.getItemStack().getCount();
        }

        return result;
    }
    public static ItemStack get(int slot)
    {
        if (slot == -2)
        {
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

    public static int findFirstItemSlot(Class<? extends Item> itemToFind, int lower, int upper) {
        int slot = -1;
        List<ItemStack> mainInventory = mc.player.inventory.mainInventory;

        for (int i = lower; i <= upper; i++) {
            ItemStack stack = mainInventory.get(i);

            if (stack == ItemStack.EMPTY || !(itemToFind.isInstance(stack.getItem()))) {
                continue;
            }

            if (itemToFind.isInstance(stack.getItem())) {
                slot = i;
                break;
            }
        }
        return slot;
    } //ШИИИИИШ

    public static void switchToHotbarSlot(Class clazz, boolean silent) {
        int slot = InventoryUtil.findHotbarBlock(clazz);
        if (slot > -1) {
            InventoryUtil.switchToHotbarSlot(slot, silent);
        }
    }


    public static int getAxeAtHotbar() {
        for (int i = 0; i < 9; ++i) {
            ItemStack itemStack = Util.mc.player.inventory.getStackInSlot(i);
            if (!(itemStack.getItem() instanceof ItemAxe)) continue;
            return i;
        }
        return -1;
    }
    public static int getSwordAtHotbar() {
        for (int i = 0; i < 9; ++i) {
            ItemStack itemStack = Util.mc.player.inventory.getStackInSlot(i);
            if (!(itemStack.getItem() instanceof ItemSword)) continue;
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

    public static int getCappuchinoAtHotbar() {
        for (int i = 0; i < 9; ++i) {
            ItemStack itemStack = Util.mc.player.inventory.getStackInSlot(i);
            if (!(itemStack.getItem() == Items.POTIONITEM)) continue;
            if (!(itemStack.getDisplayName().contains("Каппучино"))) continue;
            return i;
        }
        return -1;
    }

    public static int getAmericanoAtHotbar() {
        for (int i = 0; i < 9; ++i) {
            ItemStack itemStack = Util.mc.player.inventory.getStackInSlot(i);
            if (!(itemStack.getItem() == Items.POTIONITEM)) continue;
            if (!(itemStack.getDisplayName().contains("Американо"))) continue;
            return i;
        }
        return -1;
    }

    public static ItemStack getPotionItemStack() {
        for (int i = 0; i < 9; ++i) {
            ItemStack itemStack = Util.mc.player.inventory.getStackInSlot(i);
            if (!(itemStack.getItem() == Items.POTIONITEM)) continue;
            if (!(itemStack.getDisplayName().contains("Каппучино"))) continue;
            return itemStack;
        }
        return null;
    }


    public static boolean holdingItem(Class clazz) {
        boolean result = false;
        ItemStack stack = mc.player.getHeldItemMainhand();
        result = InventoryUtil.isInstanceOf(stack, clazz);
        if (!result) {
            ItemStack offhand = mc.player.getHeldItemOffhand();
            result = InventoryUtil.isInstanceOf(stack, clazz);
        }
        return result;
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



    public static boolean isHolding(Block block)
    {
        ItemStack mainHand = mc.player.getHeldItemMainhand();
        ItemStack offHand  = mc.player.getHeldItemOffhand();

        if(!(mainHand.getItem() instanceof ItemBlock)  || !(offHand.getItem() instanceof ItemBlock) )return false;
        return ((ItemBlock) mainHand.getItem()).getBlock() == block || ((ItemBlock) offHand.getItem()).getBlock() == block;
    }



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

    public static int getRodSlot() {
        for (int i = 0; i < 9; i++) {
            final ItemStack item = mc.player.inventory.getStackInSlot(i);
            if (item.getItem() == Items.FISHING_ROD && item.getItemDamage() < 52) {
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

}

