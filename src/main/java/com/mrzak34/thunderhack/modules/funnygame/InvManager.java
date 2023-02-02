package com.mrzak34.thunderhack.modules.funnygame;

import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.Timer;
import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.*;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class InvManager extends Module{
    public InvManager() {
        super("InvManager", "очищает инвентарь-от хлама", Category.FUNNYGAME);
    }

    public static int weaponSlot = 36, pickaxeSlot = 37, axeSlot = 38, shovelSlot = 39;
    public static List<Block> invalidBlocks = Arrays.asList(Blocks.ENCHANTING_TABLE, Blocks.FURNACE, Blocks.CARPET, Blocks.CRAFTING_TABLE, Blocks.TRAPPED_CHEST, Blocks.CHEST, Blocks.DISPENSER, Blocks.AIR, Blocks.WATER, Blocks.LAVA, Blocks.FLOWING_WATER, Blocks.FLOWING_LAVA, Blocks.SAND, Blocks.SNOW_LAYER, Blocks.TORCH, Blocks.ANVIL, Blocks.JUKEBOX, Blocks.STONE_BUTTON, Blocks.WOODEN_BUTTON, Blocks.LEVER, Blocks.NOTEBLOCK, Blocks.STONE_PRESSURE_PLATE, Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE, Blocks.WOODEN_PRESSURE_PLATE, Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE, Blocks.STONE_SLAB, Blocks.WOODEN_SLAB, Blocks.STONE_SLAB2, Blocks.RED_MUSHROOM, Blocks.BROWN_MUSHROOM, Blocks.YELLOW_FLOWER, Blocks.RED_FLOWER, Blocks.ANVIL, Blocks.GLASS_PANE, Blocks.STAINED_GLASS_PANE, Blocks.IRON_BARS, Blocks.CACTUS, Blocks.LADDER, Blocks.WEB);
    private final Timer timer = new Timer();


    public Setting <Integer> cap = this.register ( new Setting <> ( "Block Cap", 128, 8, 256 ) );
    public final Setting<Float> delay1 = this.register(new Setting<Float>("Sort Delay", Float.valueOf(1.0f), Float.valueOf(0.0f), Float.valueOf(10.0f)));
    public Setting <Boolean> archer = this.register ( new Setting <> ( "Archer", false));
    public Setting <Boolean> food = this.register ( new Setting <> ( "Food", false));
    public Setting <Boolean> sword = this.register ( new Setting <> ( "Sword", true));
    public Setting <Boolean> cleaner = this.register ( new Setting <> ( "Inv Cleaner", true));
    public Setting <Boolean> openinv = this.register ( new Setting <> ( "Open Inv", true));

    @Override
    public void onUpdate() {
        long delay = (long) (delay1.getValue() * 50);
        if (!(mc.currentScreen instanceof GuiInventory) && (openinv.getValue()))
            return;
        if (mc.currentScreen == null || mc.currentScreen instanceof GuiInventory || mc.currentScreen instanceof GuiChat) {
            if (timer.passedMs(delay) && weaponSlot >= 36) {
                if (!mc.player.inventoryContainer.getSlot(weaponSlot).getHasStack()) {
                    getBestWeapon(weaponSlot);
                } else {
                    if (!isBestWeapon(mc.player.inventoryContainer.getSlot(weaponSlot).getStack())) {
                        getBestWeapon(weaponSlot);
                    }
                }
            }
            if (timer.passedMs(delay) && pickaxeSlot >= 36) {
                getBestPickaxe();
            }
            if (timer.passedMs(delay) && shovelSlot >= 36) {
                getBestShovel();
            }
            if (timer.passedMs(delay) && axeSlot >= 36) {
                getBestAxe();
            }
            if (timer.passedMs(delay) && cleaner.getValue()) {
                for (int i = 9; i < 45; i++) {
                    if (mc.player.inventoryContainer.getSlot(i).getHasStack()) {
                        ItemStack is = mc.player.inventoryContainer.getSlot(i).getStack();
                        if (shouldDrop(is, i)) {
                            drop(i);
                            if (delay == 0) {
                                mc.player.closeScreen();
                            }
                            timer.reset();
                            if (delay > 0)
                                break;
                        }
                    }
                }
            }
        }
    }

    public void swap(int slot, int hotbarSlot) {
        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, slot, hotbarSlot, ClickType.SWAP, mc.player);
    }

    public void drop(int slot) {
        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, slot, 1, ClickType.THROW, mc.player);
    }

    public boolean isBestWeapon(ItemStack stack) {
        float damage = getDamage(stack);
        for (int i = 9; i < 45; i++) {
            if (mc.player.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack is = mc.player.inventoryContainer.getSlot(i).getStack();
                if (getDamage(is) > damage && (is.getItem() instanceof ItemSword || !sword.getValue()))
                    return false;
            }
        }
        return stack.getItem() instanceof ItemSword || !sword.getValue();
    }

    public void getBestWeapon(int slot) {
        for (int i = 9; i < 45; i++) {
            if (mc.player.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack is = mc.player.inventoryContainer.getSlot(i).getStack();
                if (isBestWeapon(is) && getDamage(is) > 0 && (is.getItem() instanceof ItemSword || !sword.getValue())) {
                    swap(i, slot - 36);
                    timer.reset();
                    break;
                }
            }
        }
    }

    private float getDamage(ItemStack stack) {
        float damage = 0;
        Item item = stack.getItem();
        if (item instanceof ItemTool) {
            ItemTool tool = (ItemTool) item;
            damage += tool.getMaxDamage();
        }
        if (item instanceof ItemSword) {
            ItemSword sword = (ItemSword) item;
            damage += sword.getMaxDamage();
        }
        damage += EnchantmentHelper.getEnchantmentLevel(Objects.requireNonNull(Enchantment.getEnchantmentByID(16)), stack) * 1.25f + EnchantmentHelper.getEnchantmentLevel(Objects.requireNonNull(Enchantment.getEnchantmentByID(20)), stack) * 0.01f;
        return damage;
    }

    public boolean shouldDrop(ItemStack stack, int slot) {
        if (stack.getDisplayName().toLowerCase().contains("/")) {
            return false;
        }
        if (stack.getDisplayName().toLowerCase().contains("предметы")) {
            return false;
        }
        if (stack.getDisplayName().toLowerCase().contains("§k||")) {
            return false;
        }
        if (stack.getDisplayName().toLowerCase().contains("kit")) {
            return false;
        }
        if (stack.getDisplayName().toLowerCase().contains("wool")) {
            return false;
        }
        if (stack.getDisplayName().toLowerCase().contains("лобби")) {
            return false;
        }
        if ((slot == weaponSlot && isBestWeapon(mc.player.inventoryContainer.getSlot(weaponSlot).getStack())) ||
                (slot == pickaxeSlot && isBestPickaxe(mc.player.inventoryContainer.getSlot(pickaxeSlot).getStack()) && pickaxeSlot >= 0) ||
                (slot == axeSlot && isBestAxe(mc.player.inventoryContainer.getSlot(axeSlot).getStack()) && axeSlot >= 0) ||
                (slot == shovelSlot && isBestShovel(mc.player.inventoryContainer.getSlot(shovelSlot).getStack()) && shovelSlot >= 0)) {
            return false;
        }
        if (stack.getItem() instanceof ItemArmor) {
            for (int type = 1; type < 5; type++) {
                if (mc.player.inventoryContainer.getSlot(4 + type).getHasStack()) {
                    ItemStack is = mc.player.inventoryContainer.getSlot(4 + type).getStack();
                    if (isBestArmor(is, type)) {
                        continue;
                    }
                }
                if (isBestArmor(stack, type)) {
                    return false;
                }
            }
        }
        if (stack.getItem() instanceof ItemBlock && (getBlockCount() > cap.getValue() || invalidBlocks.contains(((ItemBlock) stack.getItem()).getBlock()))) {
            return true;
        }
        if (stack.getItem() instanceof ItemPotion) {
            if (isBadPotion(stack)) {
                return true;
            }
        }

        if (stack.getItem() instanceof ItemFood && food.getValue() && !(stack.getItem() instanceof ItemAppleGold)) {
            return true;
        }
        if (stack.getItem() instanceof ItemHoe || stack.getItem() instanceof ItemTool || stack.getItem() instanceof ItemSword || stack.getItem() instanceof ItemArmor) {
            return true;
        }
        if ((stack.getItem() instanceof ItemBow || stack.getItem().getUnlocalizedNameInefficiently(stack).contains("arrow")) && archer.getValue()) {
            return true;
        }

        return (stack.getItem().getUnlocalizedNameInefficiently(stack).contains("tnt")) ||
                (stack.getItem().getUnlocalizedNameInefficiently(stack).contains("stick")) ||
                (stack.getItem().getUnlocalizedNameInefficiently(stack).contains("egg")) ||
                (stack.getItem().getUnlocalizedNameInefficiently(stack).contains("string")) ||
                (stack.getItem().getUnlocalizedNameInefficiently(stack).contains("cake")) ||
                (stack.getItem().getUnlocalizedNameInefficiently(stack).contains("mushroom")) ||
                (stack.getItem().getUnlocalizedNameInefficiently(stack).contains("flint")) ||
                (stack.getItem().getUnlocalizedNameInefficiently(stack).contains("dyePowder")) ||
                (stack.getItem().getUnlocalizedNameInefficiently(stack).contains("feather")) ||
                (stack.getItem().getUnlocalizedNameInefficiently(stack).contains("bucket")) ||
                (stack.getItem().getUnlocalizedNameInefficiently(stack).contains("chest") && !stack.getDisplayName().toLowerCase().contains("collect")) ||
                (stack.getItem().getUnlocalizedNameInefficiently(stack).contains("snow")) ||
                (stack.getItem().getUnlocalizedNameInefficiently(stack).contains("fish")) ||
                (stack.getItem().getUnlocalizedNameInefficiently(stack).contains("enchant")) ||
                (stack.getItem().getUnlocalizedNameInefficiently(stack).contains("exp")) ||
                (stack.getItem().getUnlocalizedNameInefficiently(stack).contains("shears")) ||
                (stack.getItem().getUnlocalizedNameInefficiently(stack).contains("anvil")) ||
                (stack.getItem().getUnlocalizedNameInefficiently(stack).contains("torch")) ||
                (stack.getItem().getUnlocalizedNameInefficiently(stack).contains("seeds")) ||
                (stack.getItem().getUnlocalizedNameInefficiently(stack).contains("leather")) ||
                (stack.getItem().getUnlocalizedNameInefficiently(stack).contains("reeds")) ||
                (stack.getItem().getUnlocalizedNameInefficiently(stack).contains("skull")) ||
                (stack.getItem().getUnlocalizedNameInefficiently(stack).contains("wool")) ||
                (stack.getItem().getUnlocalizedNameInefficiently(stack).contains("record")) ||
                (stack.getItem().getUnlocalizedNameInefficiently(stack).contains("snowball")) ||
                (stack.getItem() instanceof ItemGlassBottle) ||
                (stack.getItem().getUnlocalizedNameInefficiently(stack).contains("piston"));
    }

    private int getBlockCount() {
        int blockCount = 0;
        for (int i = 0; i < 45; i++) {
            if (mc.player.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack is = mc.player.inventoryContainer.getSlot(i).getStack();
                Item item = is.getItem();
                if (is.getItem() instanceof ItemBlock && !invalidBlocks.contains(((ItemBlock) item).getBlock())) {
                    blockCount += is.stackSize;
                }
            }
        }
        return blockCount;
    }


    public static boolean isBestArmor(ItemStack stack, int type) {
        String armorType = "";
        if (type == 1) {
            armorType = "helmet";
        } else if (type == 2) {
            armorType = "chestplate";
        } else if (type == 3) {
            armorType = "leggings";
        } else if (type == 4) {
            armorType = "boots";
        }
        if (!stack.getItem().getUnlocalizedNameInefficiently(stack).contains(armorType)) {
            return false;
        }
        for (int i = 5; i < 45; i++) {
            if (mc.player.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack is = mc.player.inventoryContainer.getSlot(i).getStack();
            }
        }
        return true;
    }

    private void getBestPickaxe() {
        for (int i = 9; i < 45; i++) {
            if (mc.player.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack is = mc.player.inventoryContainer.getSlot(i).getStack();

                if (isBestPickaxe(is) && pickaxeSlot != i) {
                    if (!isBestWeapon(is))
                        if (!mc.player.inventoryContainer.getSlot(pickaxeSlot).getHasStack()) {
                            swap(i, pickaxeSlot - 36);
                            timer.reset();
                            if (delay1.getValue() > 0)
                                return;
                        } else if (!isBestPickaxe(mc.player.inventoryContainer.getSlot(pickaxeSlot).getStack())) {
                            swap(i, pickaxeSlot - 36);
                            timer.reset();
                            if (delay1.getValue() > 0)
                                return;
                        }
                }
            }
        }
    }

    private void getBestShovel() {
        for (int i = 9; i < 45; i++) {
            if (mc.player.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack is = mc.player.inventoryContainer.getSlot(i).getStack();

                if (isBestShovel(is) && shovelSlot != i) {
                    if (!isBestWeapon(is))
                        if (!mc.player.inventoryContainer.getSlot(shovelSlot).getHasStack()) {
                            swap(i, shovelSlot - 36);
                            timer.reset();
                            if (delay1.getValue() > 0)
                                return;
                        } else if (!isBestShovel(mc.player.inventoryContainer.getSlot(shovelSlot).getStack())) {
                            swap(i, shovelSlot - 36);
                            timer.reset();
                            if (delay1.getValue() > 0)
                                return;
                        }
                }
            }
        }
    }

    private void getBestAxe() {
        for (int i = 9; i < 45; i++) {
            if (mc.player.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack is = mc.player.inventoryContainer.getSlot(i).getStack();

                if (isBestAxe(is) && axeSlot != i) {
                    if (!isBestWeapon(is))
                        if (!mc.player.inventoryContainer.getSlot(axeSlot).getHasStack()) {
                            swap(i, axeSlot - 36);
                            timer.reset();
                            if (delay1.getValue() > 0)
                                return;
                        } else if (!isBestAxe(mc.player.inventoryContainer.getSlot(axeSlot).getStack())) {
                            swap(i, axeSlot - 36);
                            timer.reset();
                            if (delay1.getValue() > 0)
                                return;
                        }
                }
            }
        }
    }

    private boolean isBestPickaxe(ItemStack stack) {
        Item item = stack.getItem();
        if (!(item instanceof ItemPickaxe))
            return false;
        float value = getToolEffect(stack);
        for (int i = 9; i < 45; i++) {
            if (mc.player.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack is = mc.player.inventoryContainer.getSlot(i).getStack();
                if (getToolEffect(is) > value && is.getItem() instanceof ItemPickaxe) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isBestShovel(ItemStack stack) {
        Item item = stack.getItem();
        if (!(item instanceof ItemSpade))
            return false;
        float value = getToolEffect(stack);
        for (int i = 9; i < 45; i++) {
            if (mc.player.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack is = mc.player.inventoryContainer.getSlot(i).getStack();
                if (getToolEffect(is) > value && is.getItem() instanceof ItemSpade) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isBestAxe(ItemStack stack) {
        Item item = stack.getItem();
        if (!(item instanceof ItemAxe))
            return false;
        float value = getToolEffect(stack);
        for (int i = 9; i < 45; i++) {
            if (mc.player.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack is = mc.player.inventoryContainer.getSlot(i).getStack();
                if (getToolEffect(is) > value && is.getItem() instanceof ItemAxe && !isBestWeapon(stack)) {
                    return false;
                }
            }
        }
        return true;
    }

    private float getToolEffect(ItemStack stack) {
        Item item = stack.getItem();
        if (!(item instanceof ItemTool))
            return 0;
        ItemTool tool = (ItemTool) item;
        float value;
        if (item instanceof ItemPickaxe) {
            value = tool.getDestroySpeed(stack, Blocks.STONE.getDefaultState());
        } else if (item instanceof ItemSpade) {
            value = tool.getDestroySpeed(stack, Blocks.DIRT.getDefaultState());
        } else if (item instanceof ItemAxe) {
            value = tool.getDestroySpeed(stack, Blocks.LOG.getDefaultState());
        } else
            return 1f;
        value += EnchantmentHelper.getEnchantmentLevel(Objects.requireNonNull(Enchantment.getEnchantmentByID(32)), stack) * 0.0075D;
        value += EnchantmentHelper.getEnchantmentLevel(Objects.requireNonNull(Enchantment.getEnchantmentByID(34)), stack) / 100d;
        return value;
    }

    private boolean isBadPotion(ItemStack stack) {
        if (stack != null && stack.getItem() instanceof ItemPotion) {
            for (PotionEffect o : PotionUtils.getEffectsFromStack(stack)) {
                if (o.getPotion() == Potion.getPotionById(19) || o.getPotion() == Potion.getPotionById(7) || o.getPotion() == Potion.getPotionById(2) || o.getPotion() == Potion.getPotionById(18)) {
                    return true;
                }
            }
        }
        return false;
    }
}
