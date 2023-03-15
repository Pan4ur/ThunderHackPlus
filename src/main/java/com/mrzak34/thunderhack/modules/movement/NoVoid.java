package com.mrzak34.thunderhack.modules.movement;

import com.mrzak34.thunderhack.events.PacketEvent;
import com.mrzak34.thunderhack.mixin.mixins.ISPacketPlayerPosLook;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.util.PlayerUtils;
import com.mrzak34.thunderhack.util.Timer;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class NoVoid
        extends Module {
    boolean aboveVoid = true;
    private final Timer voidTimer = new Timer();
    public NoVoid() {
        super("NoVoid", "рубербендит если ты-упал в пустоту", Module.Category.MOVEMENT);
    }

    @Override
    public void onUpdate() {
        if (mc.player == null || mc.world == null) return;
        if (PlayerUtils.isPlayerAboveVoid() && mc.player.posY <= 1.0D) {
            if (aboveVoid && voidTimer.passedMs(1000)) {
                aboveVoid = false;
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.1, mc.player.posZ, false));
            }
        } else {
            aboveVoid = true;
        }
    }

    @SubscribeEvent
    public void onReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketPlayerPosLook) {
            if (!(mc.currentScreen instanceof GuiDownloadTerrain)) {
                SPacketPlayerPosLook packet = event.getPacket();
                ((ISPacketPlayerPosLook) packet).setYaw(mc.player.rotationYaw);
                ((ISPacketPlayerPosLook) packet).setPitch(mc.player.rotationPitch);
                packet.getFlags().remove(SPacketPlayerPosLook.EnumFlags.X_ROT);
                packet.getFlags().remove(SPacketPlayerPosLook.EnumFlags.Y_ROT);
            }
        }
    }

}

