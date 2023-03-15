package com.mrzak34.thunderhack.modules.funnygame;

import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketPlayer;

public class AutoLeave extends Module {
    public Setting<Float> health = register(new Setting("health", 4f, 0f, 10f));
    public Setting<Boolean> leaveOnEnable = this.register(new Setting<>("LeaveOnEnable", true));
    public AutoLeave() {
        super("AutoLeave", "ливает если твое хвх-подходит к концу", Category.FUNNYGAME);
    }

    @Override
    public void onEnable() {
        if (mc.player != null && mc.world != null && leaveOnEnable.getValue()) {
            for (int i = 0; i < 1000; i++) {
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX + 100, mc.player.posY + 100, mc.player.posZ + 100, false));
            }
            this.toggle();
        }
    }

    @Override
    public void onUpdate() {
        if (fullNullCheck()) {
            return;
        }
        if (mc.player.getHealth() <= health.getValue() && mc.player.getHeldItemOffhand().getItem() != Items.TOTEM_OF_UNDYING && mc.player.getHeldItemMainhand().getItem() != Items.TOTEM_OF_UNDYING) {
            for (int i = 0; i < 1000; i++) {
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX + 100, mc.player.posY + 100, mc.player.posZ + 100, false));
            }
            this.toggle();
        }
    }
}
