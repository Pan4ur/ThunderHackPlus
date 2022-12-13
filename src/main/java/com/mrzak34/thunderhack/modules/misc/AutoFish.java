package com.mrzak34.thunderhack.modules.misc;

import com.mrzak34.thunderhack.event.events.PacketEvent;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.util.ItemUtil;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AutoFish extends Module{
    public AutoFish() {
        super("AutoFish", "признайся захотел", Category.MISC, true, false, false);
    }


    private int rodSlot = -1;

    @Override
    public void onEnable() {
        if (nullCheck()) {
            toggle();
            return;
        }
        rodSlot = ItemUtil.findItem(ItemFishingRod.class);
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event){
        if (event.getPacket() instanceof SPacketSoundEffect) {
            SPacketSoundEffect packet = (SPacketSoundEffect) event.getPacket();
            if (packet.getCategory() == SoundCategory.NEUTRAL && packet.getSound() == SoundEvents.ENTITY_BOBBER_SPLASH) {
                if( rodSlot == -1 )
                    rodSlot = ItemUtil.findItem(ItemFishingRod.class);
                if( rodSlot != -1 )
                {
                    int startSlot = mc.player.inventory.currentItem;
                    mc.player.connection.sendPacket(new CPacketHeldItemChange(rodSlot));
                    mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
                    mc.player.swingArm(EnumHand.MAIN_HAND);
                    mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
                    mc.player.swingArm(EnumHand.MAIN_HAND);
                    if (startSlot != -1)
                        mc.getConnection().sendPacket(new CPacketHeldItemChange(startSlot));
                }
            }
        }
    }
}

