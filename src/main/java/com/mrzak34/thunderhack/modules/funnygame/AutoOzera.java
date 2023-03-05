package com.mrzak34.thunderhack.modules.funnygame;


import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.InventoryUtil;
import com.mrzak34.thunderhack.util.Timer;
import net.minecraft.init.MobEffects;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.EnumHand;

public class AutoOzera extends Module {

    public AutoOzera() {
        super("AutoOzera", "Пьёт Родные Озёра", Module.Category.FUNNYGAME);
    }

    public Setting<Integer> delay = this.register(new Setting<>("DelayOnUse", 200, 100, 2000));
    public Setting<Boolean> negativeLakeEff = this.register(new Setting<>("RemoveEffects", false));

    public Timer timer = new Timer();

    @Override
    public void onUpdate() {
        if (timer.passedMs(delay.getValue()) && InventoryUtil.getOzeraAtHotbar() != -1 && !mc.player.isPotionActive(MobEffects.STRENGTH)) {
            int hotbarslot = mc.player.inventory.currentItem;
            mc.player.connection.sendPacket(new CPacketHeldItemChange(InventoryUtil.getOzeraAtHotbar()));
            mc.playerController.connection.sendPacket(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
            mc.player.connection.sendPacket(new CPacketHeldItemChange(hotbarslot));
            timer.reset();
        }
        if (negativeLakeEff.getValue()) {
            if (mc.player.isPotionActive(MobEffects.LEVITATION)) {
                mc.player.removeActivePotionEffect(MobEffects.LEVITATION);
            }
            if (mc.player.isPotionActive(MobEffects.NAUSEA)) {
                mc.player.removeActivePotionEffect(MobEffects.NAUSEA);
            }
            if (mc.player.isPotionActive(MobEffects.BLINDNESS)) {
                mc.player.removeActivePotionEffect(MobEffects.BLINDNESS);
            }
        }

    }

}