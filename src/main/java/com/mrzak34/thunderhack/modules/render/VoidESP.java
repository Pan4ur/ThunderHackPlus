package com.mrzak34.thunderhack.modules.render;

import com.mrzak34.thunderhack.events.Render3DEvent;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.BlockUtils;
import com.mrzak34.thunderhack.util.render.RenderUtil;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class VoidESP extends Module {
    public Setting<Float> range = this.register(new Setting<>("Range",6.0f, 3.0f, 16.0f));
    public Setting<Boolean> down = this.register(new Setting<>("Up", false));
    private List<BlockPos> holes = new ArrayList<BlockPos>();


    public VoidESP() {
        super("VoidESP", "VoidESP", Module.Category.PLAYER);
    }

    @Override
    public void onUpdate() {
        this.holes = this.calcHoles();
    }

    @SubscribeEvent
    public void onRender3D(Render3DEvent event) {
        for (BlockPos pos : this.holes) {
            RenderUtil.renderCrosses(this.down.getValue() ? pos.up() : pos, new Color(255, 255, 255), 2.0f);
        }
    }

    public List<BlockPos> calcHoles() {
        ArrayList<BlockPos> voidHoles = new ArrayList<BlockPos>();
        List<BlockPos> positions = BlockUtils.getSphere(range.getValue(), false);
        for (BlockPos pos : positions) {
            if (pos.getY() != 0 || mc.world.getBlockState(pos).getBlock() == Blocks.BEDROCK) continue;
            voidHoles.add(pos);
        }
        return voidHoles;
    }


}


