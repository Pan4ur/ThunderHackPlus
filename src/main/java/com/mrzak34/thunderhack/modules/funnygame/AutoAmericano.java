package com.mrzak34.thunderhack.modules.funnygame;


import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.modules.combat.Surround;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.InventoryUtil;
import com.mrzak34.thunderhack.util.Timer;
import net.minecraft.init.MobEffects;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.EnumHand;


public class AutoAmericano extends Module {

    public Timer timer = new Timer();


    public AutoAmericano() {
        super("AutoAmericano", "AutoAmericano", Category.FUNNYGAME);
    }

    private final Setting<Mode> mainMode = register(new Setting<>("Version", Mode.New));
    public enum Mode {Old, New}

    @Override
    public void onUpdate() {
        if (timer.passedMs(200) && InventoryUtil.getAmericanoAtHotbar(mainMode.getValue() == Mode.Old) != -1 && !mc.player.isPotionActive(MobEffects.HASTE)) {
            int hotbarslot = mc.player.inventory.currentItem;
            mc.player.connection.sendPacket(new CPacketHeldItemChange(InventoryUtil.getAmericanoAtHotbar(mainMode.getValue() == Mode.Old)));
            mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
            mc.player.connection.sendPacket(new CPacketHeldItemChange(hotbarslot));
            timer.reset();
        }
    }


}
