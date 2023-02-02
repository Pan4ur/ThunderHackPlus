package com.mrzak34.thunderhack.modules.funnygame;

import com.mrzak34.thunderhack.command.Command;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import net.minecraft.init.Items;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AutoLeave extends Module {
    public AutoLeave() {
        super("AutoLeave", "ливает если твое хвх-подходит к концу", Category.FUNNYGAME);
    }

    public Setting<Float> health = register(new Setting("health", 4f, 0f, 10f));
    public Setting<Boolean> leaveOnEnable = this.register ( new Setting <> ( "LeaveOnEnable", true ) );


    @Override
    public void onEnable(){
        if(mc.player != null && mc.world != null && leaveOnEnable.getValue())
            mc.player.sendChatMessage("${}");
    }

    @Override
    public void onUpdate(){
        if(fullNullCheck()){
            return;
        }
        if(mc.player.getHealth() <= health.getValue() && (mc.player.getHeldItemOffhand().getItem() == Items.TOTEM_OF_UNDYING || mc.player.getHeldItemMainhand().getItem() == Items.TOTEM_OF_UNDYING)){
            mc.player.sendChatMessage("${}");
        }
    }
}
