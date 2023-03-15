package com.mrzak34.thunderhack.modules.funnygame;

import com.mrzak34.thunderhack.events.EventPreMotion;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.Timer;
import com.mrzak34.thunderhack.util.math.MathUtil;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AutoFlyme extends Module {
    public final Setting<Boolean> space = register(new Setting<>("OnlySpace", true));//(antiCheat);
    public final Setting<Boolean> instantSpeed = register(new Setting<>("InstantSpeed", true));//(antiCheat);
    private final Timer timer = new Timer();


    public AutoFlyme() {
        super("AutoFlyme", "Автоматически пишет /flyme", Category.FUNNYGAME);
    }

    @Override
    public void onEnable() {
        if (fullNullCheck()) return;
        mc.player.sendChatMessage("/flyme");
    }

    @Override
    public void onUpdate() {
        if (!mc.player.capabilities.isFlying && timer.passedMs(1000) && !mc.player.onGround && (!space.getValue() || mc.gameSettings.keyBindJump.isKeyDown())) {
            mc.player.sendChatMessage("/flyme");
            timer.reset();
        }
    }


    @SubscribeEvent
    public void onUpdateWalkingPlayer(final EventPreMotion event) {
        if (!instantSpeed.getValue() || !mc.player.capabilities.isFlying) return;
        final double[] dir = MathUtil.directionSpeed(1.05f);
        if (mc.player.movementInput.moveStrafe != 0.0f || mc.player.movementInput.moveForward != 0.0f) {
            mc.player.motionX = dir[0];
            mc.player.motionZ = dir[1];
        } else {
            mc.player.motionX = 0.0;
            mc.player.motionZ = 0.0;
        }
    }

}
