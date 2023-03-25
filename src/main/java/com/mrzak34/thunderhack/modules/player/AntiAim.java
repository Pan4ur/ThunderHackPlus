package com.mrzak34.thunderhack.modules.player;

import com.mrzak34.thunderhack.events.EventSync;
import com.mrzak34.thunderhack.events.PlayerUpdateEvent;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.math.MathUtil;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;


public class AntiAim extends Module {
    public AntiAim() {
        super("AntiAim", "утро 1 января", "can break CA predict", Category.PLAYER);
    }

    private final Setting<Mode> pitchMode = register(new Setting<>("PitchMode", Mode.None));
    private final Setting<Mode> yawMode = register(new Setting<>("YawMode", Mode.None));

    public enum Mode {None, RandomAngle, Spin, Sinus, Fixed, Static}


    public Setting<Integer> Speed = this.register(new Setting<>("Speed", 1, 1, 45));
    public Setting<Integer> yawDelta = this.register(new Setting<>("YawDelta", 60, -360, 360));
    public Setting<Integer> pitchDelta = this.register(new Setting<>("PitchDelta", 10, -90, 90));
    public final Setting<Boolean> bodySync = register(new Setting<>("BodySync", true));
    public final Setting<Boolean> allowInteract = register(new Setting<>("AllowInteract", true));

    private float rotationYaw, rotationPitch, pitch_sinus_step, yaw_sinus_step;

    // высокий приоритет для того чтоб не портить ротации важных модулей (CA,KA,Surround)
    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onSync(EventSync e) {
        if(allowInteract.getValue() && (mc.gameSettings.keyBindAttack.isKeyDown() || mc.gameSettings.keyBindUseItem.isKeyDown())) return;
        if (yawMode.getValue() != Mode.None) {
            mc.player.rotationYaw = rotationYaw;
            if (bodySync.getValue())
                mc.player.renderYawOffset = rotationYaw;
        }
        if (pitchMode.getValue() != Mode.None)
            mc.player.rotationPitch = rotationPitch;
    }

    @SubscribeEvent
    public void onCalc(PlayerUpdateEvent e) {
        if (pitchMode.getValue() == Mode.RandomAngle)
            if (mc.player.ticksExisted % Speed.getValue() == 0)
                rotationPitch = MathUtil.random(90, -90);

        if (yawMode.getValue() == Mode.RandomAngle)
            if (mc.player.ticksExisted % Speed.getValue() == 0)
                rotationYaw = MathUtil.random(0, 360);

        if (yawMode.getValue() == Mode.Spin)
            if (mc.player.ticksExisted % Speed.getValue() == 0) {
                rotationYaw += yawDelta.getValue();
                if (rotationYaw > 360) rotationYaw = 0;
                if (rotationYaw < 0) rotationYaw = 360;
            }

        if (pitchMode.getValue() == Mode.Spin)
            if (mc.player.ticksExisted % Speed.getValue() == 0) {
                rotationPitch += pitchDelta.getValue();
                if (rotationPitch > 90) rotationPitch = -90;
                if (rotationPitch < -90) rotationPitch = 90;
            }

        if (pitchMode.getValue() == Mode.Sinus) {
            pitch_sinus_step += Speed.getValue() / 10f;
            rotationPitch = (float) (mc.player.rotationPitch + pitchDelta.getValue() * Math.sin(pitch_sinus_step));
            rotationPitch = MathUtil.clamp(rotationPitch, -90, 90);
        }

        if (yawMode.getValue() == Mode.Sinus) {
            yaw_sinus_step += Speed.getValue() / 10f;
            rotationYaw = (float) (mc.player.rotationYaw + yawDelta.getValue() * Math.sin(yaw_sinus_step));
        }

        if (pitchMode.getValue() == Mode.Fixed)
            rotationPitch = pitchDelta.getValue();

        if (yawMode.getValue() == Mode.Fixed)
            rotationYaw = yawDelta.getValue();

        if (pitchMode.getValue() == Mode.Static) {
            rotationPitch = mc.player.rotationPitch + pitchDelta.getValue();
            rotationPitch = MathUtil.clamp(rotationPitch, -90, 90);
        }
        if (yawMode.getValue() == Mode.Static)
            rotationYaw =  mc.player.rotationYaw % 360 + yawDelta.getValue();

    }
}
