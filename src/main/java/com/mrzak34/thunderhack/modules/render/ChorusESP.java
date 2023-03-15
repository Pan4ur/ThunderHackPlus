package com.mrzak34.thunderhack.modules.render;

import com.mrzak34.thunderhack.events.ChorusEvent;
import com.mrzak34.thunderhack.events.Render3DEvent;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.ColorSetting;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.Timer;
import com.mrzak34.thunderhack.util.render.RenderUtil;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;


public class ChorusESP
        extends Module {
    private final Setting<Integer> time = this.register(new Setting("Duration", 500, 50, 3000));
    private final Setting<Boolean> box = this.register(new Setting("Box", true));
    private final Setting<Boolean> outline = this.register(new Setting("Outline", true));
    private final Setting<Float> lineWidth = this.register(new Setting("LineWidth", 1.0f, 0.1f, 5.0f, v -> this.outline.getValue()));
    private final Timer timer = new Timer();
    private final Setting<ColorSetting> outlineColor = this.register(new Setting<>("OutlineColor", new ColorSetting(0x2250b4b4)));
    private final Setting<ColorSetting> boxColor = this.register(new Setting<>("BoxColor", new ColorSetting(0x2250b4b4)));
    private double x;
    private double y;
    private double z;
    public ChorusESP() {
        super("ChorusESP", "рендерит звук хоруса", Module.Category.RENDER);
    }

    @SubscribeEvent
    public void onChorus(final ChorusEvent event) {
        this.x = event.getChorusX();
        this.y = event.getChorusY();
        this.z = event.getChorusZ();
        this.timer.reset();
    }

    public void onRender3D(final Render3DEvent render3DEvent) {
        if (this.timer.passedMs(this.time.getValue())) {
            return;
        }
        final AxisAlignedBB pos = RenderUtil.interpolateAxis(new AxisAlignedBB(this.x - 0.3, this.y, this.z - 0.3, this.x + 0.3, this.y + 1.8, this.z + 0.3));
        if (this.outline.getValue()) {
            RenderUtil.drawBlockOutline(pos, outlineColor.getValue().getColorObject(), this.lineWidth.getValue());
        }
        if (this.box.getValue()) {
            RenderUtil.drawFilledBox(pos, boxColor.getValue().getRawColor());
        }
    }
}

