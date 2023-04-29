package com.mrzak34.thunderhack.modules.player;

import com.mrzak34.thunderhack.events.PlayerUpdateEvent;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.util.Timer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemAir;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ElytraFix extends Module {
    public ElytraFix() {
        super("ElytraFix", "ElytraFix", Category.PLAYER);
    }

    private Timer delay = new Timer();

    @SubscribeEvent
    public void onPlayerEvent(PlayerUpdateEvent event){
        ItemStack stack = mc.player.inventory.getItemStack();
        if (stack.getItem() instanceof ItemArmor && delay.passedMs(300)) {
            if (((ItemArmor) stack.getItem()).armorType == EntityEquipmentSlot.CHEST && mc.player.inventory.armorItemInSlot(2).getItem() == Items.ELYTRA) {
                mc.playerController.windowClick(0, 6, 0, ClickType.PICKUP, mc.player);
                int nullSlot = findNullSlot();
                boolean needDrop = nullSlot == 999;
                if (needDrop) nullSlot = 9;
                mc.playerController.windowClick(0, nullSlot, 1, ClickType.PICKUP, mc.player);
                if (needDrop) mc.playerController.windowClick(0, -999, 0, ClickType.PICKUP, mc.player);
                delay.reset();
            }
        }
    }

    public static int findNullSlot() {
        for (int i = 0; i < 36; i++) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack.getItem() instanceof ItemAir) {
                if (i < 9) {
                    i += 36;
                }
                return i;
            }
        }
        return 999;
    }
}
