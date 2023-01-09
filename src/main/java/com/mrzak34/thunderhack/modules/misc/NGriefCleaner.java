package com.mrzak34.thunderhack.modules.misc;

import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;

import com.mrzak34.thunderhack.util.Timer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class NGriefCleaner extends Module {
    public NGriefCleaner() {
        super("NGriefCleaner", "убирает топорики и головы", Category.MISC, true, false, false);
    }
    public Setting<Boolean> openinv = this.register ( new Setting <> ( "OpenInv", true));
    private final Timer timer = new Timer();
    public final Setting<Float> delay1 = this.register(new Setting<Float>("Delay", 1.0f, 0.0f, 10.0f));

    @Override
    public void onUpdate(){
        long delay = (long) (delay1.getValue() * 50);
        if (!(mc.currentScreen instanceof GuiInventory) && (openinv.getValue()))
            return;
        if (timer.passedMs(delay)) {
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


    public void drop(int slot) {
        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, slot, 1, ClickType.THROW, mc.player);
    }

    public boolean shouldDrop(ItemStack stack, int slot) {
        if (stack.getItem() == Items.SKULL ) {
            return true;
        }
        if (stack.getItem() == Items.WOODEN_SHOVEL ) {
            return true;
        }
        if (stack.getItem() == Items.STICK ) {
            return true;
        }
        if (stack.getItem() == Items.PAPER ) {
            return true;
        }
        if (stack.getItem() == Items.FLINT_AND_STEEL ) {
            return true;
        }
        if (stack.getItem() == Items.ROTTEN_FLESH ) {
            return true;
        }
        if (stack.getItem() == Items.WHEAT_SEEDS ) {
            return true;
        }
        if (stack.getItem() == Items.BUCKET ) {
            return true;
        }
        if (stack.getItem() == Items.KNOWLEDGE_BOOK ) {
            return true;
        }
        if (stack.getItem() == Item.getItemById(6) ) { //саженцы блять
            return true;
        }
        if (stack.getItem() == Item.getItemById(50) ) { //факела в рот они еблись
            return true;
        }
        if(stack.getItem() == Items.WOODEN_AXE){
            return slot < 36;
        }

        return false;
    }
}
