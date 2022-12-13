package com.mrzak34.thunderhack.modules.combat;

import com.mrzak34.thunderhack.event.events.PacketEvent;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.util.Timer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AutoTotem extends Module {
    public AutoTotem() {
        super("AutoTotem", "AutoTotem", Category.COMBAT, true, false, false);
    }


    Timer timer = new Timer();

    @Override
    public void onUpdate() {
        if (mc.currentScreen instanceof GuiContainer && !(mc.currentScreen instanceof GuiInventory)) return;
        if(getItemSlot(Items.TOTEM_OF_UNDYING,false) != -1) {
            if (mc.player.getHeldItemOffhand().getItem() != Items.TOTEM_OF_UNDYING) {
                moveToOffhand(getItemSlot(Items.TOTEM_OF_UNDYING,false));
            }
        } else if(getItemSlot(Items.GOLDEN_APPLE,true) != -1) {
            if (mc.player.getHeldItemOffhand().getItem() != Items.GOLDEN_APPLE && mc.player.getHeldItemOffhand().getItem() != Items.TOTEM_OF_UNDYING) {
                moveToOffhand(getItemSlot(Items.GOLDEN_APPLE,true));
            }
        }
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketClickWindow) {
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SPRINTING));
        }
    }

    public void moveToOffhand(int from){
        if(!timer.passedMs(100)) return;
        if(from == -1) return;
        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, from, 0, ClickType.PICKUP, mc.player);
        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, 45, 0, ClickType.PICKUP, mc.player);
        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, from, 0, ClickType.PICKUP, mc.player);
        mc.playerController.updateController();
        timer.reset();
    }

    public static int getItemSlot(Item item, boolean gappleCheck) {
        for (int i = 0; i < 36; ++i) {
            ItemStack itemStackInSlot = mc.player.inventory.getStackInSlot(i);
            if(!gappleCheck) {
                if (item == itemStackInSlot.getItem()) {
                    if (i < 9) i += 36;
                    return i;
                }
            } else {
                if (item == itemStackInSlot.getItem() && !item.getRarity(itemStackInSlot).equals(EnumRarity.RARE)) {
                    if (i < 9) i += 36;
                    return i;
                }
            }
        }
        return -1;
    }
}
