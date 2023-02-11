package com.mrzak34.thunderhack.modules.player;

import com.mrzak34.thunderhack.command.Command;
import com.mrzak34.thunderhack.events.FinishUseItemEvent;
import com.mrzak34.thunderhack.events.PlayerUpdateEvent;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.EntityUtil;
import com.mrzak34.thunderhack.util.Timer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemAppleGold;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;



public class AutoGApple extends Module {
    public AutoGApple() {
        super("AutoGApple", "AutoGApple", Category.PLAYER);
    }


    private boolean isActive;
    public static boolean stopAura = false;
    private int antiLag = 0;

    private final Setting<Float> health = this.register(new Setting<>("health", 20f, 1f, 36f));
    public final Setting<Boolean> fg = register(new Setting<>("FunnyGame", false));
    public final Setting<Integer> Delay = register(new Setting("UseDelay", 200, 0, 2000));

    private Timer useDelay = new Timer();

    @SubscribeEvent
    public void onUpdate(PlayerUpdateEvent e) {
        if (fullNullCheck()) return;
        if (GapInOffHand()) {
            stopAura = false;
            if (EntityUtil.getHealth(mc.player) <= health.getValue() && useDelay.passedMs(Delay.getValue())) {
                stopAura = true;
                isActive = true;
                mc.gameSettings.keyBindUseItem.pressed = true;
            } else if (isActive) {
                stopAura = false;
                isActive = false;
                mc.gameSettings.keyBindUseItem.pressed = false;
                antiLag = 0;
            } else {
                stopAura = false;
            }
            if(mc.gameSettings.keyBindUseItem.pressed && fg.getValue()){
                ++antiLag;
                if(antiLag > 50){
                    Command.sendMessage("AntiGapLAG");
                    mc.gameSettings.keyBindUseItem.pressed = false;
                    antiLag = 0;
                }
            }
        }
    }

    @SubscribeEvent
    public void onFinishEating(FinishUseItemEvent e){
        useDelay.reset();
    }

    @Override
    public void onDisable(){
        stopAura = false;
    }

    private boolean GapInOffHand() {
        return !mc.player.getHeldItemOffhand().isEmpty() && mc.player.getHeldItemOffhand().getItem() instanceof ItemAppleGold;
    }
}
