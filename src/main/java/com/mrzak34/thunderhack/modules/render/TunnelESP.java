package com.mrzak34.thunderhack.modules.render;

import com.mrzak34.thunderhack.events.Render3DEvent;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.ColorSetting;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.render.RenderUtil;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

import static com.mrzak34.thunderhack.util.BlockUtils.isAir;

public class TunnelESP
        extends Module {
    private final Setting<Integer> boxAlpha = this.register(new Setting<>("BoxAlpha", 125, 0, 255));
    private final Setting<Float> lineWidth = this.register(new Setting<>("LineWidth", 1.0f, 0.1f, 5.0f));
    private final Setting<ColorSetting> Color1 = this.register(new Setting<>("Color1", new ColorSetting(0x8800FF00)));
    private final Setting<ColorSetting> Color2 = this.register(new Setting<>("Color2", new ColorSetting(0x8800FF00)));
    public Setting<Boolean> box = this.register(new Setting<>("Box", true));
    public Setting<Boolean> outline = this.register(new Setting<>("Outline", true));
    List<BlockPos> tunnelbp = new ArrayList<>();
    int delay;

    public TunnelESP() {
        super("TunnelESP", "Подсвечивает туннели", Module.Category.RENDER);
    }

    @Override
    public void onRender3D(Render3DEvent event) {

        try {
            for (BlockPos bp : tunnelbp) {
                RenderUtil.drawBoxESP(bp, Color1.getValue().getColorObject(), this.outline.getValue(), Color2.getValue().getColorObject(), this.lineWidth.getValue(), this.outline.getValue(), this.box.getValue(), this.boxAlpha.getValue(), true, 0);
            }
        } catch (Exception e) {
            System.out.println("Concurrent exception");
        }
    }

    @Override
    public void onUpdate() {
        if (delay++ > 100) {
            (new Thread(() -> {
                for (int x = (int) (mc.player.posX - 124); x < mc.player.posX + 124; ++x) {
                    for (int z = (int) (mc.player.posZ - 124); z < mc.player.posZ + 124; ++z) {
                        for (int y = 1; y < 120; ++y) {
                            if (one_one(new BlockPos(x, y, z))) {
                                tunnelbp.add(new BlockPos(x, y, z));
                            } else if (one_two(new BlockPos(x, y, z))) {
                                tunnelbp.add(new BlockPos(x, y, z));
                                tunnelbp.add(new BlockPos(x, y + 1, z));
                            }
                        }
                    }
                }
            })).start();
            delay = 0;
        }
    }

    /*---------------------- 1 x 2 check -----------------------*/
    private boolean one_two(BlockPos pos) {
        if (tunnelbp.contains(pos)) return false;
        if (!isAir(pos) || !isAir(pos.up())) return false;
        if (isAir(pos.down()) || isAir(pos.up().up())) return false;
        if (isAir(pos.north()) && isAir(pos.south()) && isAir(pos.up().north()) && isAir(pos.up().south())) {
            return !isAir(pos.east()) && !isAir(pos.west()) && !isAir(pos.up().east()) && !isAir(pos.up().west());
        }
        if (isAir(pos.east()) && isAir(pos.west()) && isAir(pos.up().east()) && isAir(pos.up().west())) {
            return !isAir(pos.north()) && !isAir(pos.south()) && !isAir(pos.up().north()) && !isAir(pos.up().south());
        }
        return false;
    }

    /*---------------------- 1 x 1 check -----------------------*/
    private boolean one_one(BlockPos pos) {
        if (tunnelbp.contains(pos)) return false;
        if (!isAir(pos)) return false;
        if (isAir(pos.down()) || isAir(pos.up())) return false;
        if (isAir(pos.north()) && isAir(pos.south()) && isAir(pos.up().north()) && isAir(pos.up().south())) {
            return !isAir(pos.east()) && !isAir(pos.west()) && !isAir(pos.up().east()) && !isAir(pos.up().west());
        }
        if (isAir(pos.east()) && isAir(pos.west()) && isAir(pos.up().east()) && isAir(pos.up().west())) {
            return !isAir(pos.north()) && !isAir(pos.south()) && !isAir(pos.up().north()) && !isAir(pos.up().south());
        }
        return false;
    }

}
