package com.mrzak34.thunderhack.modules.misc;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.command.Command;
import com.mrzak34.thunderhack.events.PostPlayerUpdateEvent;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.ColorSetting;
import com.mrzak34.thunderhack.setting.Setting;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;

import static com.mrzak34.thunderhack.util.MovementUtil.isMoving;

public class Timer extends Module {
    public static long lastUpdateTime;
    public static double value;
    public final Setting<ColorSetting> color = this.register(new Setting<>("Color1", new ColorSetting(-2013233153)));
    public final Setting<ColorSetting> color2 = this.register(new Setting<>("Color2", new ColorSetting(-2001657727)));
    public final Setting<Integer> slices = this.register(new Setting<>("colorOffset1", 125, 10, 500));
    public final Setting<Integer> slices1 = this.register(new Setting<>("colorOffset2", 211, 10, 500));
    public final Setting<Integer> slices2 = this.register(new Setting<>("colorOffset3", 162, 10, 500));
    public final Setting<Integer> slices3 = this.register(new Setting<>("colorOffset4", 60, 10, 500));
    public final Setting<Integer> yyy = this.register(new Setting<>("Y", 180, 10, 500));
    private final Setting<Mode> mode = this.register(new Setting<>("Mode", Mode.NORMAL));
    public Setting<Float> speed = register(new Setting("Speed", 2.0f, 0.1f, 10.0f, v -> mode.getValue() == Mode.NORMAL));
    public Setting<Boolean> smart = register(new Setting<>("Smart", true, v -> mode.getValue() == Mode.NORMAL));
    public Setting<Boolean> noMove = register(new Setting<>("NoMove", true, v -> smart.getValue() && mode.getValue() == Mode.NORMAL));
    public Setting<Boolean> autoDisable = register(new Setting<>("AutoDisable", true, v -> smart.getValue() && mode.getValue() == Mode.NORMAL));
    public Setting<Boolean> indicator = register(new Setting<>("Indicator", false, v -> smart.getValue() && mode.getValue() == Mode.NORMAL));
    public Setting<Integer> maxTicks = register(new Setting("Bound", 0, 0, 15, v -> mode.getValue() == Mode.NORMAL));
    public Setting<Float> shiftTicks = register(new Setting("ShiftTicks", 10.0F, 1F, 40f, v -> mode.getValue() == Mode.ReallyWorld));
    public Timer() {
        super("Timer", "делает игру быстрее", Category.MOVEMENT);
    }

    public static Color TwoColoreffect(Color cl1, Color cl2, double speed) {
        double thing = speed / 4.0 % 1.0;
        float val = MathHelper.clamp((float) Math.sin(Math.PI * 6 * thing) / 2.0f + 0.5f, 0.0f, 1.0f);
        return new Color(lerp((float) cl1.getRed() / 255.0f, (float) cl2.getRed() / 255.0f, val), lerp((float) cl1.getGreen() / 255.0f, (float) cl2.getGreen() / 255.0f, val), lerp((float) cl1.getBlue() / 255.0f, (float) cl2.getBlue() / 255.0f, val));
    }

    public static float lerp(float a, float b, float f) {
        return a + f * (b - a);
    }

    public int getMin() {
        return -(15 - maxTicks.getValue());
    }

    @Override
    public void onUpdate() {
        if (mode.getValue() == Mode.ReallyWorld) return;

        if (!isMoving() && mc.player.onGround) {
            if (noMove.getValue()) {
                return;
            }
        }
        if (!smart.getValue() || canEnableTimer(speed.getValue() + 0.2f)) {
            Thunderhack.TICK_TIMER = Math.max(speed.getValue() + (mc.player.ticksExisted % 2 == 0 ? -0.2f : 0.2f), 0.1f);
        } else {
            if (autoDisable.getValue()) {
                toggle();
            }
            Thunderhack.TICK_TIMER = 1;
        }
    }

    @SubscribeEvent
    public void onPostPlayerUpdate(PostPlayerUpdateEvent event) {
        if (mode.getValue() == Mode.ReallyWorld) {
            int status = (int) (((10 - value) / (Math.abs(getMin()) + 10)) * 100);
            if (status < 90) {
                Command.sendMessage("Перед повторным использованием необходимо постоять на месте!");
                toggle();
                return;
            }
            event.setCanceled(true);
            event.setIterations(shiftTicks.getValue().intValue());
            this.toggle();
        }
    }

    @Override
    public void onDisable() {
        Thunderhack.TICK_TIMER = 1f;
    }

    public boolean canEnableTimer(float speed) {
        double predictVl = (50.0 - (double) 50 / speed) / 50.0;
        return predictVl + value < 10 - this.speed.getValue();
    }

    public void m() {
        long now = System.currentTimeMillis();
        long timeElapsed = now - lastUpdateTime;
        lastUpdateTime = now;
        value += (50.0 - (double) timeElapsed) / 50.0;
        value -= 0.001;
        value = MathHelper.clamp(value, getMin(), 25.0);
    }

    public enum Mode {
        NORMAL,
        ReallyWorld
    }
}
