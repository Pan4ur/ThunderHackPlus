package com.mrzak34.thunderhack.modules.player;

import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Enchantments;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketHeldItemChange;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AutoTool extends Module {
    public AutoTool() {
        super("AutoTool", "Авто инструмент-ну рил хз как назвать", Category.PLAYER, true, true, false);
    }


    public Setting<Boolean> swapBack = register(new Setting<>("Swap Back", true));
    public Setting<Boolean> saveItem = register(new Setting<>("Save Item", true));
    public Setting<Boolean> silentSwitch = register(new Setting<>("Packet Switch", true));


    public int itemIndex;
    private boolean swap;
    private long swapDelay;
    private ItemStack swapedItem = null;
    private List<Integer> lastItem = new ArrayList<>();

    @Override
    public void onUpdate() {
        if (mc.player.isCreative() || mc.player.isSpectator())
            return;

        Block hoverBlock = null;
        if (mc.objectMouseOver.getBlockPos() == null)
            return;
        hoverBlock = mc.world.getBlockState(mc.objectMouseOver.getBlockPos()).getBlock();
        List<Integer> bestItem = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            if (hoverBlock == null)
                break;
            if (!(mc.player.inventory.getStackInSlot(i).getMaxDamage()
                    - mc.player.inventory.getStackInSlot(i).getItemDamage() > 1) && saveItem.getValue())
                continue;
            if (getDigSpeed(mc.world.getBlockState(mc.objectMouseOver.getBlockPos()),
                    mc.player.inventory.getStackInSlot(i)) > 1)
                bestItem.add(i);

        }

        bestItem.sort(Comparator
                .comparingDouble(x -> -getDigSpeed(mc.world.getBlockState(mc.objectMouseOver.getBlockPos()),
                        mc.player.inventory.getStackInSlot(x))));

        if (!bestItem.isEmpty() && mc.gameSettings.keyBindAttack.pressed) {
            if (mc.player.inventory.getCurrentItem() != swapedItem) {
                lastItem.add(mc.player.inventory.currentItem);
                if (silentSwitch.getValue())
                    mc.player.connection.sendPacket(new CPacketHeldItemChange(bestItem.get(0)));
                else
                    mc.player.inventory.currentItem = bestItem.get(0);

                itemIndex = bestItem.get(0);
                swap = true;
            }
            swapDelay = System.currentTimeMillis();
        } else if (swap && !lastItem.isEmpty() && System.currentTimeMillis() >= swapDelay + 300
                && swapBack.getValue()) {

            if (silentSwitch.getValue())
                mc.player.connection.sendPacket(new CPacketHeldItemChange(lastItem.get(0)));
            else
                mc.player.inventory.currentItem = lastItem.get(0);

            itemIndex = lastItem.get(0);
            lastItem.clear();
            swap = false;
        }
    }

    public float getDigSpeed(IBlockState state, ItemStack itemstack) {
        float f = 1;

        if (f > 1.0F) {
            int i = EnchantmentHelper.getEnchantmentLevel(Enchantments.EFFICIENCY, itemstack);
            if (i > 0 && !itemstack.isEmpty()) {
                f += (float) (i * i + 1);
            }
        }

        if (mc.player.isPotionActive(MobEffects.HASTE)) {
            f = 1.0F + (float) (mc.player.getActivePotionEffect(MobEffects.HASTE).getAmplifier() + 1);
        }

        if (mc.player.isPotionActive(MobEffects.MINING_FATIGUE)) {
            float f1;

            switch (mc.player.getActivePotionEffect(MobEffects.MINING_FATIGUE).getAmplifier()) {
                case 0:
                    f1 = 0.3F;
                    break;

                case 1:
                    f1 = 0.09F;
                    break;

                case 2:
                    f1 = 0.0027F;
                    break;

                case 3:
                default:
                    f1 = 8.1E-4F;
            }

            f *= f1;
        }

        if (mc.player.isInsideOfMaterial(Material.WATER) && !EnchantmentHelper.getAquaAffinityModifier(mc.player)) {
            f /= 5.0F;
        }

        if (!mc.player.onGround) {
            f /= 5.0F;
        }

        return f;
    }

}
