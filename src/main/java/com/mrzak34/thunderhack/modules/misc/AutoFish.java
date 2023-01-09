package com.mrzak34.thunderhack.modules.misc;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.command.Command;
import com.mrzak34.thunderhack.event.events.EntityAddedEvent;
import com.mrzak34.thunderhack.event.events.PacketEvent;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.InventoryUtil;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.network.play.server.SPacketDisconnect;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import com.mrzak34.thunderhack.util.Timer;

public class AutoFish extends Module{
    public AutoFish() {
        super("AutoFish", "признайся захотел", Category.MISC, true, false, false);
    }

    public Setting<Boolean> rodSave = register(new Setting<>("RodSave", true));
    public Setting<Boolean> changeRod = register(new Setting<>("ChangeRod", false));
    public Setting<Boolean> autoSell = register(new Setting<>("AutoSell", false));
    public Setting<Boolean> autoLeave = register(new Setting<>("AutoLeave", false));

    private int rodSlot = -1;
    private Timer timeout = new Timer();


    @Override
    public void onEnable() {
        if (nullCheck()) {
            toggle();
            return;
        }
        rodSlot = InventoryUtil.findItem(ItemFishingRod.class);
    }

    @Override
    public void onUpdate(){
        if(mc.player.getHeldItemMainhand().getItem() instanceof ItemFishingRod){
            if(mc.player.getHeldItemMainhand().getItemDamage() > 52){
                if(rodSave.getValue() && !changeRod.getValue()){
                    Command.sendMessage("Saving rod...");
                    toggle();
                } else if(changeRod.getValue() && InventoryUtil.getRodSlot() != -1){
                    Command.sendMessage("Swapped to a new rod");
                    mc.player.inventory.currentItem = (InventoryUtil.getRodSlot());
                } else {
                    Command.sendMessage("Saving rod...");
                    toggle();
                }
            }
        }
        if(timeout.passedMs(60000)){
            if( rodSlot == -1 )
                rodSlot = InventoryUtil.findItem(ItemFishingRod.class);
            if( rodSlot != -1 )
            {
                int startSlot = mc.player.inventory.currentItem;
                mc.player.connection.sendPacket(new CPacketHeldItemChange(rodSlot));
                mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
                mc.player.swingArm(EnumHand.MAIN_HAND);
                mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
                mc.player.swingArm(EnumHand.MAIN_HAND);
                if (startSlot != -1)
                    mc.player.connection.sendPacket(new CPacketHeldItemChange(startSlot));
                timeout.reset();
            }
        }
    }




    @SubscribeEvent
    public void onEntityAdded(EntityAddedEvent event) {
        if(autoLeave.getValue() && !Thunderhack.friendManager.isFriend(event.entity.getName())){
            toggle();
            mc.player.connection.handleDisconnect(new SPacketDisconnect(new TextComponentString("AutoFish (log)")));
        }
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event){
        if(fullNullCheck()){
            return;
        }
        if (event.getPacket() instanceof SPacketSoundEffect) {
            SPacketSoundEffect packet = (SPacketSoundEffect) event.getPacket();
            if (packet.getCategory() == SoundCategory.NEUTRAL && packet.getSound() == SoundEvents.ENTITY_BOBBER_SPLASH) {
                if( rodSlot == -1 )
                    rodSlot = InventoryUtil.findItem(ItemFishingRod.class);
                if( rodSlot != -1 )
                {
                    int startSlot = mc.player.inventory.currentItem;
                    mc.player.connection.sendPacket(new CPacketHeldItemChange(rodSlot));
                    mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
                    mc.player.swingArm(EnumHand.MAIN_HAND);
                    mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
                    mc.player.swingArm(EnumHand.MAIN_HAND);
                    if (startSlot != -1)
                        mc.player.connection.sendPacket(new CPacketHeldItemChange(startSlot));
                    if(autoSell.getValue()){
                        if(timeout.passedMs(1000)) {
                            mc.player.sendChatMessage("/sellfish");
                        }
                    }
                    timeout.reset();

                }

            }

        }
    }
}

