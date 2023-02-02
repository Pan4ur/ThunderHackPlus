package com.mrzak34.thunderhack.modules.movement;

import com.mrzak34.thunderhack.events.EventPreMotion;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static com.mrzak34.thunderhack.util.MovementUtil.isMoving;

public class Spider extends Module {

    public Spider() {
        super("Spider", "Spider", Category.MOVEMENT);
    }



    private Setting<mode> a = register(new Setting("Mode", mode.Matrix));
    public enum mode {
        Default, Matrix, MatrixNew;
    }
  //  public Setting<Float> yyyy = register(new Setting("yHitBoxExpand", 1f, -2, 2f));

    @Override
    public void onTick() {
        if (!mc.player.collidedHorizontally) {
            return;
        }
        if (a.getValue() == mode.Default) {
            mc.player.motionY = 0.2;
            mc.player.isAirBorne = false;
        } else if(a.getValue() == mode.Matrix) {
            if (mc.player.ticksExisted % 8 == 0) {
                mc.player.onGround = true;
                mc.player.isAirBorne = false;
            } else {
                mc.player.onGround = false;
            }
            mc.player.prevPosY -= 2.0E-232;
            if (mc.player.onGround) {
                mc.player.motionY = 0.42f;
            }
        }
    }



    @SubscribeEvent
    public void onMotion(EventPreMotion event) {
        if (mc.gameSettings.keyBindJump.isKeyDown() && mc.player.motionY <= -0.3739040364667221) {
            mc.player.onGround = true;
            mc.player.motionY = 0.481145141919180;
        }
    }

/*
    public boolean checkVertical() {
        final AxisAlignedBB bb = mc.player.getEntityBoundingBox().offset(0, yyyy.getValue(), 0);
        boolean flag = false;
        int y = (int) bb.minY;
        for (int x = MathHelper.floor(bb.minX); x < MathHelper.floor(bb.maxX + 1.0D); x++) {
            for (int z = MathHelper.floor(bb.minZ); z < MathHelper.floor(bb.maxZ + 1.0D); z++) {
                final Block block = mc.world.getBlockState(new BlockPos(x, y, z)).getBlock();
                if (block != Blocks.AIR) {
                    flag = true;
                }
            }
        }
        return flag;
    }

    later

 */

}
