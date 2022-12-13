package com.mrzak34.thunderhack.modules.funnygame;

import com.mrzak34.thunderhack.command.Command;
import com.mrzak34.thunderhack.event.events.ConnectToServerEvent;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.*;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Objects;

import static com.mrzak34.thunderhack.util.PlayerUtils.getPlayerPos;


public class AutoPot extends Module{
    public AutoPot() {
        super("AutoCappuccino", "автокаппучино для-фангейма", Category.FUNNYGAME,true,false,false);
    }
    public Setting<Integer> triggerhealth = this.register(new Setting<Integer>("TriggerHealth", 10, 1, 36));
    public Setting<Integer> delay = this.register(new Setting<Integer>("delay", 200, 1, 2000));

    public static int neededCap = 0;
    public Timer timer = new Timer();
    public Timer alerttimer = new Timer();

    @Override
    public void onUpdate(){
        if(mc.player.getHealth() < triggerhealth.getValue() && timer.passedMs(delay.getValue()) && InventoryUtil.getCappuchinoAtHotbar() != -1){
                int hotbarslot = mc.player.inventory.currentItem;

                mc.player.connection.sendPacket(new CPacketHeldItemChange(InventoryUtil.getCappuchinoAtHotbar()));
                mc.playerController.connection.sendPacket(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
                mc.player.connection.sendPacket(new CPacketHeldItemChange(hotbarslot));
                ++neededCap;
                timer.reset();
        }
        if((InventoryUtil.getCappuchinoAtHotbar() == -1) && alerttimer.passedMs(1000)){
            Command.sendMessage("Нема зелек!!!!");
            mc.world.playSound(getPlayerPos(), SoundEvents.ENTITY_BLAZE_HURT, SoundCategory.AMBIENT, 150.0f, 10.0F, true);
            alerttimer.reset();
        }
    }



}
