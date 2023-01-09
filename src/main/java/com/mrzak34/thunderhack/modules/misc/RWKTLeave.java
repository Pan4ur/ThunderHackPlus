package com.mrzak34.thunderhack.modules.misc;

import com.mrzak34.thunderhack.command.Command;
import com.mrzak34.thunderhack.event.events.EventPreMotion;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class RWKTLeave extends Module {
    public RWKTLeave() {
        super("RWKTLeave", "лив в кт для рилика", Category.MISC, true, false, false);
    }

    private  final Setting<Integer> X = this.register( new Setting<>("X", 15000, -16000, 16000));
    private  final Setting<Integer> Z = this.register( new Setting<>("Z", -12000, -16000, 16000));


    @SubscribeEvent
    public void onPreMotion(EventPreMotion event) {
        if (mc.player.ticksExisted % 2 == 0) {
            mc.player.connection.sendPacket(new CPacketPlayer.Position(X.getValue() + 0.10D,  100.26D, Z.getValue() - 0.10D, true));
            mc.player.connection.sendPacket(new CPacketPlayer.Position(X.getValue(),  100.0F, Z.getValue(), false));
            mc.player.connection.sendPacket(new CPacketPlayer.Position(X.getValue() + 0.07D,  100.17f, Z.getValue() - 0.07D, true));
            return;
        }
        if (mc.player.posX == X.getValue() && mc.player.posZ == Z.getValue()) {
            Command.sendMessage("Ливер, вгетай целку");
            this.toggle();
        }
    }
}
