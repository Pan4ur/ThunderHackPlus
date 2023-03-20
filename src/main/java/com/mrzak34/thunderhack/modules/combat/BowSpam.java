package com.mrzak34.thunderhack.modules.combat;

import com.mrzak34.thunderhack.events.EventSync;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.Timer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBow;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BowSpam
        extends Module {
    private final Timer timer = new Timer();
    public Setting<Mode> mode = this.register(new Setting<>("Mode", Mode.FAST));
    public Setting<Boolean> allowOffhand = this.register(new Setting<Object>("Offhand", Boolean.TRUE, v -> this.mode.getValue() != Mode.AUTORELEASE));
    public Setting<Integer> ticks = this.register(new Setting<Object>("Ticks", 3, 0, 20, v -> this.mode.getValue() == Mode.FAST));
    public Setting<Integer> delay = this.register(new Setting<Object>("Delay", 50, 0, 500, v -> this.mode.getValue() == Mode.AUTORELEASE));
    private boolean offhand = false;


    public BowSpam() {
        super("BowSpam", "Спамит стрелами", Module.Category.COMBAT);
    }


    @SubscribeEvent
    public void onPlayerPre(EventSync event) {
        if (this.mode.getValue() == Mode.FAST && (this.offhand || BowSpam.mc.player.inventory.getCurrentItem().getItem() instanceof ItemBow) && BowSpam.mc.player.isHandActive()) {
            if (BowSpam.mc.player.getItemInUseMaxCount() >= this.ticks.getValue()) {
                BowSpam.mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, BowSpam.mc.player.getHorizontalFacing()));
                BowSpam.mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(this.offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND));
                BowSpam.mc.player.stopActiveHand();
            }
        }
    }

    @Override
    public void onUpdate() {
        this.offhand = BowSpam.mc.player.getHeldItemOffhand().getItem() == Items.BOW && this.allowOffhand.getValue();
        if (this.mode.getValue() == Mode.AUTORELEASE) {
            if (!this.offhand && !(BowSpam.mc.player.inventory.getCurrentItem().getItem() instanceof ItemBow) || !this.timer.passedMs((int) ((float) this.delay.getValue())))
                return;
            BowSpam.mc.playerController.onStoppedUsingItem(BowSpam.mc.player);
            this.timer.reset();
        }
    }

    public enum Mode {
        FAST,
        AUTORELEASE,
    }
}
