package com.mrzak34.thunderhack.modules.player;

import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class AutoTool extends Module {

    public Setting<Boolean> swapBack = register(new Setting<>("SwapBack", true));
    public Setting<Boolean> saveItem = register(new Setting<>("SaveItem", true));
    // public Setting<Boolean> silent = register(new Setting<>("Silent", false)); //TODO later
    public Setting<Boolean> echestSilk = register(new Setting<>("EchestSilk", true));
    public int itemIndex;
    private boolean swap;
    private long swapDelay;
    private final ItemStack swapedItem = null;
    private final List<Integer> lastItem = new ArrayList<>();
    public AutoTool() {
        super("AutoTool", "Автоматом свапается на-луший инструмент", Category.PLAYER);
    }

    @Override
    public void onUpdate() {
        if (mc.objectMouseOver.getBlockPos() == null) return;
        if (getTool(mc.objectMouseOver.getBlockPos()) != -1 && mc.gameSettings.keyBindAttack.pressed) {
            if (mc.player.inventory.getCurrentItem() != swapedItem) {
                lastItem.add(mc.player.inventory.currentItem);

                //  if (silent.getValue())
                //       mc.player.connection.sendPacket(new CPacketHeldItemChange(getTool(mc.objectMouseOver.getBlockPos())));
                //   else
                mc.player.inventory.currentItem = getTool(mc.objectMouseOver.getBlockPos());

                itemIndex = getTool(mc.objectMouseOver.getBlockPos());
                swap = true;
            }
            swapDelay = System.currentTimeMillis();

        } else if (swap && !lastItem.isEmpty() && System.currentTimeMillis() >= swapDelay + 300 && swapBack.getValue()) {

            //   if (silent.getValue())
            //      mc.player.connection.sendPacket(new CPacketHeldItemChange(lastItem.get(0)));
            //   else
            mc.player.inventory.currentItem = lastItem.get(0);

            itemIndex = lastItem.get(0);
            lastItem.clear();
            swap = false;
        }
    }


    private int getTool(final BlockPos pos) {
        int index = -1;
        float CurrentFastest = 1.0f;
        for (int i = 0; i < 9; ++i) {
            final ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack != ItemStack.EMPTY) {
                if (!(mc.player.inventory.getStackInSlot(i).getMaxDamage() - mc.player.inventory.getStackInSlot(i).getItemDamage() > 10) && saveItem.getValue())
                    continue;

                final float digSpeed = EnchantmentHelper.getEnchantmentLevel(Enchantments.EFFICIENCY, stack);
                final float destroySpeed = stack.getDestroySpeed(mc.world.getBlockState(pos));

                if (mc.world.getBlockState(pos).getBlock() instanceof BlockAir) return -1;
                if (mc.world.getBlockState(pos).getBlock() instanceof BlockEnderChest && echestSilk.getValue()) {
                    if (EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, stack) > 0 && digSpeed + destroySpeed > CurrentFastest) {
                        CurrentFastest = digSpeed + destroySpeed;
                        index = i;
                    }
                } else if (digSpeed + destroySpeed > CurrentFastest) {
                    CurrentFastest = digSpeed + destroySpeed;
                    index = i;
                }
            }
        }
        return index;
    }
}
