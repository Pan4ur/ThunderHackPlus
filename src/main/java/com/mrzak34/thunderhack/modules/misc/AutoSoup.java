package com.mrzak34.thunderhack.modules.misc;

import com.mrzak34.thunderhack.events.EventPreMotion;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.InventoryUtil;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AutoSoup extends Module {

    public AutoSoup() {
        super("AutoSoup", "Автосуп для-Мигосмси", Category.MISC);
    }

    public Setting<Float> thealth = this.register ( new Setting <> ( "TriggerHealth", 7f, 1f, 20f ) );


    @SubscribeEvent
    public void onUpdateWalkingPlayer(EventPreMotion event){
        if(mc.player.getHealth() <= thealth.getValue()){
            int soupslot = InventoryUtil.findSoupAtHotbar();
            int currentslot = mc.player.inventory.currentItem;
            if( soupslot != -1 ){
                mc.player.connection.sendPacket(new CPacketHeldItemChange(soupslot));
                mc.playerController.connection.sendPacket(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
                InventoryUtil.switchToHotbarSlot(currentslot, true);
            }
        }
    }
}
