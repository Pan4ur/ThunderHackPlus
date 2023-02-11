package com.mrzak34.thunderhack.modules.misc;

import com.mrzak34.thunderhack.events.PacketEvent;
import com.mrzak34.thunderhack.modules.Module;
import net.minecraft.network.play.server.SPacketDisconnect;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AntiTimedOut extends Module {
    public AntiTimedOut() {
        super("AntiTimedOut", "AntiTimedOut", Category.MISC);
    }




}
