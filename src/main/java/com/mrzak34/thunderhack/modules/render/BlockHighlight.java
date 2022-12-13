package com.mrzak34.thunderhack.modules.render;

import com.mrzak34.thunderhack.command.Command;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.event.events.Render3DEvent;
import com.mrzak34.thunderhack.setting.*;
import com.mrzak34.thunderhack.util.RenderUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;

import java.awt.*;

public class BlockHighlight
        extends Module {
    private final Setting<Float> lineWidth = this.register(new Setting<Float>("LineWidth", Float.valueOf(1.0f), Float.valueOf(0.1f), Float.valueOf(5.0f)));
    public final Setting<ColorSetting> color = this.register(new Setting<>("Color", new ColorSetting(0x2250b4b4)));

    public BlockHighlight() {
        super("BlockHighlight", "подсвечивает блок на-который ты смотришь", Module.Category.RENDER, true, false, false);
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        RayTraceResult ray = BlockHighlight.mc.objectMouseOver;
        if (ray != null && ray.typeOfHit == RayTraceResult.Type.BLOCK) {
            BlockPos blockpos = ray.getBlockPos();
            RenderUtil.drawBlockOutline(blockpos, color.getValue().getColorObject(), this.lineWidth.getValue(), false);
            Command.sendMessage(String.valueOf((new Color(153,136,167,255).getRGB())));
        }
    }
}

