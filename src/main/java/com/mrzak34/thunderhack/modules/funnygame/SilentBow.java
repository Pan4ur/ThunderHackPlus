package com.mrzak34.thunderhack.modules.funnygame;

import com.mrzak34.thunderhack.command.Command;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.InventoryUtil;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.EnumHand;

public class SilentBow extends Module {
    public Setting<Boolean> bomb = register(new Setting<>("Bomb", false));
    private int prev_slot = -2;
    private int ticks = 4;
    public SilentBow() {
        super("SilentBow", "Стреляет из лука-без свапа", "SilentBow", Category.FUNNYGAME);
    }

    @Override
    public void onEnable() {
        ticks = 4;
        int bowslot = InventoryUtil.getBowAtHotbar();
        prev_slot = mc.player.inventory.currentItem;
        if (bowslot != -1) {
            mc.player.connection.sendPacket(new CPacketHeldItemChange(bowslot));
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SPRINTING));
            mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
            if (bomb.getValue()) {
                for (int i = 0; i < 106; i++) {
                    mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY - 1e-10, mc.player.posZ, true));
                    mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1e-10, mc.player.posZ, false));
                }
            }
        } else {
            Command.sendMessage("У тебя лука в хотбаре нема, дуранчеус");
            toggle();
        }
    }


    @Override
    public void onUpdate() {
        if (ticks > 0) {
            ticks--;
        } else if (prev_slot != -2) {
            mc.player.connection.sendPacket(new CPacketHeldItemChange(prev_slot));
            prev_slot = -2;
            ticks = 4;

            toggle();
        }
    }

}
