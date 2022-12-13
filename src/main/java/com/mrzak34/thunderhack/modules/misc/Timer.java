package com.mrzak34.thunderhack.modules.misc;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.event.events.PacketEvent;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.DynamicAnimation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Timer extends Module {
    public Timer() {
        super("Timer", "Timer", Category.MOVEMENT, true, false, false);
    }

    private DynamicAnimation violation = new DynamicAnimation();
    public static long lastUpdateTime;
    public static double value;

    public Setting<Float> speed = register(new Setting("Speed", 2.0f, 0.1f, 10.0f));
    public Setting<Boolean> smart = register(new Setting<>("Smart", true));
    public Setting<Integer> maxTicks = register(new Setting("Bound", 0, 0, 15));




    public int getMin() {
        return - (15 - maxTicks.getValue());
    }

    @Override
    public void onUpdate(){
        update();
        violation.update();
    }


    @Override
    public void onDisable() {
        Thunderhack.TICK_TIMER =1f;
    }

    public void update() {
        if (!smart.getValue() || canEnableTimer(speed.getValue() + 0.2f)) {
            Thunderhack.TICK_TIMER = Math.max(speed.getValue() + (mc.player.ticksExisted % 2 == 0 ? -0.2f : 0.2f), 0.1f);
        } else {
            Thunderhack.TICK_TIMER = 1;
        }
    }

    public boolean canEnableTimer(float speed) {
        double predictVl = (50.0 - (double) 50 / speed) / 50.0;
        return predictVl + value < 10 - this.speed.getValue();
    }

    public boolean canEnableTimerIgnoreSettings(float speed) {
        double predictVl = (50.0 - (double) 50 / speed) / 50.0;
        return predictVl + value < 10;
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send e){
        if(fullNullCheck()){
            return;
        }
        m();
    }

    public void m() {
        long now = System.currentTimeMillis();
        long timeElapsed = now - lastUpdateTime;
        lastUpdateTime = now;
        value += (50.0 - (double) timeElapsed) / 50.0;
        value -= 0.001;
        value = MathHelper.clamp((double) value, (double) getMin(), (double) 25.0);
    }
}
