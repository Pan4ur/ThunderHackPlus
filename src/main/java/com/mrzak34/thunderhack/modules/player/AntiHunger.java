package com.mrzak34.thunderhack.modules.player;

import com.mrzak34.thunderhack.events.PacketEvent;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AntiHunger extends Module {
    public  Setting<Boolean> sprint = this.register(new Setting<>("Sprint", true));
    public  Setting<Boolean> noGround = this.register(new Setting<>("Ground", true));
    public  Setting<Boolean> grPacket = this.register(new Setting<>("GroundPacket", true));

    private boolean isOnGround = false;

    public AntiHunger() {
        super("AntiHunger", "уменьшает потребление-голода", Category.PLAYER);
    }

    public void onEnable() {
        if (sprint.getValue() && mc.player != null) {
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SPRINTING));
        }
    }

    public void onDisable() {
        if (sprint.getValue() && mc.player != null && mc.player.isSprinting()) {
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SPRINTING));
        }
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketEntityAction) {
            CPacketEntityAction action = (CPacketEntityAction) event.getPacket();
            if (sprint.getValue() && (action.getAction() == CPacketEntityAction.Action.START_SPRINTING || action.getAction() == CPacketEntityAction.Action.STOP_SPRINTING)) {
                event.setCanceled(true);
            }
        }

        if (event.getPacket() instanceof CPacketPlayer) {
            CPacketPlayer player = (CPacketPlayer) event.getPacket();
            boolean ground = mc.player.onGround;
            if (noGround.getValue() && isOnGround && ground && player.getY(0.0) == (!mc.player.isSprinting() ? 0.0 : mc.player.posY)) {
                if(grPacket.getValue()){
                    player.onGround = false;
                } else {
                    mc.player.onGround = false;
                }
            }
            isOnGround = ground;
        }
    }
}