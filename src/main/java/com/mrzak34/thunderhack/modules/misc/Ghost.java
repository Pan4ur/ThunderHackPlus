package com.mrzak34.thunderhack.modules.misc;

import com.mrzak34.thunderhack.events.PacketEvent;
import com.mrzak34.thunderhack.modules.Module;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Ghost extends Module {
    private boolean bypass = false;

    public Ghost() {
        super("Ghost", "Жить после смерти", Category.MISC);
    }

    @Override
    public void onEnable() {
        bypass = false;
    }

    @Override
    public void onDisable() {
        if (mc.player != null) mc.player.respawnPlayer();
        bypass = false;
    }

    @Override
    public void onUpdate() {
        if (mc.player == null || mc.world == null) return;

        if (mc.player.getHealth() == 0.0f) {
            mc.player.setHealth(20.0f);
            mc.player.isDead = false;
            bypass = true;
            mc.displayGuiScreen(null);
            mc.player.setPositionAndUpdate(mc.player.posX, mc.player.posY, mc.player.posZ);
        }
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (bypass && event.getPacket() instanceof CPacketPlayer) event.setCanceled(true);
    }
}
