package com.mrzak34.thunderhack.modules.funnygame;


import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.Timer;
import com.mrzak34.thunderhack.util.Util;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;


public class LegitScaff extends Module {
    private final Setting<Integer> blue = this.register(new Setting<Integer>("CPS timer", 2, 0, 6));
    public Setting<Boolean> shift = this.register(new Setting<>("shift", true));
    public Setting<Boolean> only = this.register(new Setting<>("OnlyBlocks", true));
    public Setting<Boolean> fast = this.register(new Setting<>("RealyDamnFast", true));
    public Setting<Boolean> lt = this.register(new Setting<Boolean>("LegitTower", true));
    public Timer timr = new Timer();

    public LegitScaff() {
        super("LegitScaff", "можно и легитнее", Category.FUNNYGAME);
    }

    @Override
    public void onUpdate() {
        if (Util.mc.player != null && Util.mc.world != null) {
            if (fast.getValue()) {
                mc.rightClickDelayTimer = blue.getValue();
            }
            if (lt.getValue()) {
                if (mc.player.movementInput.jump) {

                    for (int i = (int) mc.player.rotationPitch; i < 83; i = i + 1) {
                        mc.player.rotationPitch = i;
                    }
                    for (int i = (int) mc.player.rotationPitch; i > 83; i = i - 1) {
                        mc.player.rotationPitch = i;
                    }

                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), true);
                }
            }


            if (shift.getValue()) {
                ItemStack i = Util.mc.player.getHeldItemMainhand();
                BlockPos bP = new BlockPos(Util.mc.player.posX, Util.mc.player.posY - 1D, Util.mc.player.posZ);
                if (i != null) {
                    if (!only.getValue() || i.getItem() instanceof ItemBlock) {
                        KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), false);
                        if (LegitScaff.mc.world.getBlockState(bP).getBlock() == Blocks.AIR) {

                            KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), true);

                            if (timr.passedMs(50)) {
                                KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), true);
                                timr.reset();
                            } else {
                                KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
                            }
                        }
                    }

                }
            }
        }
    }

    @Override
    public void onDisable() {

        KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), false);
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);

        super.onDisable();
    }

}
//83
//s 0   w 90  n 180  e -90