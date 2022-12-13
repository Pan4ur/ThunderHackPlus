package com.mrzak34.thunderhack.modules.render;

import com.mrzak34.thunderhack.event.events.Render3DEvent;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.BlockUtils;
import com.mrzak34.thunderhack.util.RenderUtil;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class VoidESP extends Module {
    public VoidESP() {super("VoidESP", "VoidESP", Module.Category.PLAYER, false, false, false);}


    public Setting<Float> range = this.register(new Setting<Float>("Range", Float.valueOf(6.0f), Float.valueOf(3.0f), Float.valueOf(16.0f)));
    public Setting<Boolean> down = this.register(new Setting<Boolean>("Up", false));


    private List<BlockPos> holes = new ArrayList<BlockPos>();



    public void onUpdate() {
        if (this.mc.player == null || this.mc.world == null) {
            return;
        }
        this.holes = this.calcHoles();
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        int size = this.holes.size();
        for (int i = 0; i < size; ++i) {
            BlockPos pos = this.holes.get(i);
            RenderUtil.renderCrosses(this.down.getValue() != false ? pos.up() : pos, new Color(255, 255, 255), 2.0f);
        }
    }

    public List<BlockPos> calcHoles() {
        ArrayList<BlockPos> voidHoles = new ArrayList<BlockPos>();
        List<BlockPos> positions = BlockUtils.getSphere(range.getValue(), false);
        int size = positions.size();
        for (int i = 0; i < size; ++i) {
            BlockPos pos = positions.get(i);
            if (pos.getY() != 0 || this.mc.world.getBlockState(pos).getBlock() == Blocks.BEDROCK) continue;
            voidHoles.add(pos);
        }
        return voidHoles;
    }



}


