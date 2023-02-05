package com.mrzak34.thunderhack.modules.funnygame;

import com.mrzak34.thunderhack.events.EventMove;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class LegitStrafe extends Module {
    public LegitStrafe() {
        super("GlideFly", "мsdfsdf", Category.MOVEMENT);
    }

    public  Setting<Float> motionY = this.register(new Setting<Float>("Y Offset", 2.0f, 0.1f, 0.42f));


    public static long lastStartFalling;


    @SubscribeEvent
    public void onEvent(EventMove event) {
                int elytra = getSlotIDFromItem(Items.ELYTRA);
                if (elytra == -1) {
                    return;
                }
                float aaa = (float) mc.player.motionY;
                if (System.currentTimeMillis() - lastStartFalling > 1000) {
                    disabler(elytra);
                }
                if (System.currentTimeMillis() - lastStartFalling < 800 && !mc.player.isSneaking()) {
                    aaa = motionY.getValue();
                } else {
                    aaa = -0.05f;
                }
                mc.player.jump();
                mc.player.motionY = aaa;
    }

    public static void disabler(int elytra) {
        if (elytra != -2) {
            mc.playerController.windowClick(0, elytra, 1, ClickType.PICKUP, mc.player);
            mc.playerController.windowClick(0, 6, 1, ClickType.PICKUP, mc.player);
        }
        mc.getConnection().sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
        mc.getConnection().sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
        if (elytra != -2) {
            mc.playerController.windowClick(0, 6, 1, ClickType.PICKUP, mc.player);
            mc.playerController.windowClick(0, elytra, 1, ClickType.PICKUP, mc.player);
        }
        lastStartFalling = System.currentTimeMillis();
    }

    public static int getSlotIDFromItem(Item item) {
        for (ItemStack stack : mc.player.getArmorInventoryList()) {
            if (stack.getItem() == item) {
                return -2;
            }
        }
        int slot = -1;
        for (int i = 0; i < 36; i++) {
            ItemStack s = mc.player.inventory.getStackInSlot(i);
            if (s.getItem() == item) {
                slot = i;
                break;
            }
        }
        if (slot < 9 && slot != -1) {
            slot = slot + 36;
        }
        return slot;
    }

}
