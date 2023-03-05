package com.mrzak34.thunderhack.modules.player;

import com.mrzak34.thunderhack.events.EventPreMotion;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.MovementUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockHopper;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static com.mrzak34.thunderhack.util.MovementUtil.isMoving;

public class NoClip extends Module {
    public NoClip() {
        super("NoClip", "NoClip", Category.PLAYER);
    }

    public Setting<Boolean> sanikHuiSasanik = this.register(new Setting<Boolean>("SunriseBypass", false));

    @SubscribeEvent
    public void onPreSync(EventPreMotion e){
        if (sanikHuiSasanik.getValue() && (mc.player.collidedHorizontally || playerInsideBlock())) {
            if(mc.gameSettings.keyBindSneak.isKeyDown()){
                double[] dir = MovementUtil.forward(0.5);
                NoClip.mc.playerController.onPlayerDamageBlock(new BlockPos(NoClip.mc.player.posX + dir[0], NoClip.mc.player.posY - 1, NoClip.mc.player.posZ + dir[1]), NoClip.mc.player.getHorizontalFacing());
                NoClip.mc.player.swingArm(EnumHand.MAIN_HAND);
            } else
            if(isMoving()) {
                double[] dir = MovementUtil.forward(0.5);
                NoClip.mc.playerController.onPlayerDamageBlock(new BlockPos(NoClip.mc.player.posX + dir[0], NoClip.mc.player.posY, NoClip.mc.player.posZ + dir[1]), NoClip.mc.player.getHorizontalFacing());
                NoClip.mc.player.swingArm(EnumHand.MAIN_HAND);
            }

        }
    }

    public static boolean playerInsideBlock() {
        for (int i = MathHelper.floor(mc.player.boundingBox.minX); i < MathHelper.floor(mc.player.boundingBox.maxX) + 1; ++i) {
            for (int j = MathHelper.floor(mc.player.boundingBox.minY + 1.0); j < MathHelper.floor(mc.player.boundingBox.maxY) + 2; ++j) {
                for (int k = MathHelper.floor(mc.player.boundingBox.minZ); k < MathHelper.floor(mc.player.boundingBox.maxZ) + 1; ++k) {
                    Block block = mc.world.getBlockState(new BlockPos(i, j, k)).getBlock();
                    if (block == null || block instanceof BlockAir) continue;
                    AxisAlignedBB axisAlignedBB = block.getSelectedBoundingBox(mc.world.getBlockState(new BlockPos(i, j, k)), mc.world, new BlockPos(i, j, k));
                    if (block instanceof BlockHopper) {
                        axisAlignedBB = new AxisAlignedBB(i, j, k, i + 1, j + 1, k + 1);
                    }
                    if (axisAlignedBB == null || !mc.player.boundingBox.intersects(axisAlignedBB)) continue;
                    return true;
                }
            }
        }
        return false;
    }
}
