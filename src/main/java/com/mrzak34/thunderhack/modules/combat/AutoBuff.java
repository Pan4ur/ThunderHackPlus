package com.mrzak34.thunderhack.modules.combat;

import com.mrzak34.thunderhack.command.Command;
import com.mrzak34.thunderhack.event.events.EventPreMotion;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.Timer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Objects;

public class AutoBuff extends Module {


    public AutoBuff() {
        super("AutoBuff", "Кидает бафы", Category.COMBAT, true, true, false);
    }
    
    
    public Timer timer = new Timer();
    public Timer timer2 = new Timer();



    public Setting<Integer> delay = register(new Setting<>("delay", 1000, 0, 5000));
    public Setting<Integer> delay2 = register(new Setting<>("delay2", 0, 0, 1000));

    public Setting<Boolean> strenght = register(new Setting<>("Strenght", true));
    public Setting<Boolean> speed = register(new Setting<>("Speed", true));
    public Setting<Boolean> fire = register(new Setting<>("FireRes", true));


    

    @SubscribeEvent
    public void onUpdate(EventPreMotion event) {
        if (!(mc.currentScreen instanceof GuiInventory)) {
                if (timer.passedMs(delay.getValue()) && timer2.passedMs(delay2.getValue())) {
                    if (!mc.player.isPotionActive(Objects.requireNonNull(Potion.getPotionById(1))) && getPotionSlot(1) != -1) {
                        throwPot(1);
                        timer2.reset();
                        if(delay2.getValue() != 0){
                            return;
                        }
                    }
                    if (!mc.player.isPotionActive(Objects.requireNonNull(Potion.getPotionById(5))) && getPotionSlot(5) != -1) {
                        throwPot(5);
                        timer2.reset();
                        if(delay2.getValue() != 0){
                            return;
                        }
                    }
                    if (!mc.player.isPotionActive(Objects.requireNonNull(Potion.getPotionById(12))) && getPotionSlot(12) != -1) {
                        throwPot(12);
                        timer2.reset();
                    }
                    timer.reset();
                }
        }
    }

    void throwPot(int id) {
        mc.player.connection.sendPacket(new CPacketPlayer.Rotation(mc.player.attackedAtYaw,90,mc.player.onGround));
        int slot = getPotionSlot(id);
        mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
        mc.playerController.updateController();
        mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
        mc.playerController.updateController();
        mc.player.connection.sendPacket(new CPacketHeldItemChange(mc.player.inventory.currentItem));
    }

    public int getPotionSlot(int id) {
        for(int i = 0; i < 9; ++i) {
            if (isStackPotion(mc.player.inventory.getStackInSlot(i), id)) {
                return i;
            }
        }

        return -1;
    }


    public boolean isStackPotion(ItemStack stack, int id) {
        if (stack != null){
            Item item = stack.getItem();
            if (item == Items.SPLASH_POTION) {
                for (PotionEffect effect : PotionUtils.getEffectsFromStack(stack)) {
                    if (effect.getPotion() == Potion.getPotionById(id)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
