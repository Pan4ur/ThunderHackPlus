package com.mrzak34.thunderhack.util.math;


import com.mrzak34.thunderhack.events.EventSprint;
import com.mrzak34.thunderhack.events.MatrixMove;
import com.mrzak34.thunderhack.manager.EventManager;
import com.mrzak34.thunderhack.modules.movement.Speed;
import com.mrzak34.thunderhack.modules.movement.Strafe;
import com.mrzak34.thunderhack.modules.movement.testMove;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;

import static com.mrzak34.thunderhack.util.Util.mc;


public class MatrixStrafeMovement {
    public static double oldSpeed, contextFriction;
    public static boolean needSwap, prevSprint;
    public static int counter, noSlowTicks;

    public static double calculateSpeed(MatrixMove move) {
        boolean fromGround = mc.player.onGround;
        boolean toGround = move.toGround();
        boolean jump = move.getMotionY() > 0;
        float speedAttributes = getAIMoveSpeed(mc.player);
        final float frictionFactor = getFrictionFactor(mc.player, move);
        float n6 = 0.91f;
        if (fromGround) {
            n6 = frictionFactor;
        }
        final float n7 = 0.16277136f / (n6 * n6 * n6);
        float n8;
        if (fromGround) {
            n8 = speedAttributes * n7;
            if (jump) {
                n8 += 0.2f;
            }
        } else {
            n8 = 0.0255f;
        }
        boolean noslow = false;
        double max2 = oldSpeed + n8;
        double max = 0.0;
        if (mc.player.isHandActive() && !jump) {
            double n10 = oldSpeed + n8 * 0.5 + 0.004999999888241291;
            double motionY2 = move.getMotionY();
            if (motionY2 != 0.0 && Math.abs(motionY2) < 0.08) {
                n10 += 0.055;
            }
            if (max2 > (max = Math.max(0.043, n10))) {
                noslow = true;
                ++noSlowTicks;
            } else {
                noSlowTicks = Math.max(noSlowTicks - 1, 0);
            }
        } else {
            noSlowTicks = 0;
        }
        if (noSlowTicks > 3) {
            max2 = max - 0.019;
        } else {
            max2 = Math.max(noslow ? 0 : 0.25, max2) - (counter++ % 2 == 0 ? 0.001 : 0.002);
        }
        contextFriction = n6;
        if (!toGround && !fromGround) {
            needSwap = true;
        } else {
            prevSprint = false;
        }
        if (!fromGround && !toGround) {
            Speed.needSprintState = !EventManager.serversprint;
            Strafe.needSprintState = !EventManager.serversprint;
        }
        if (toGround && fromGround) {
            Speed.needSprintState = false;
            Strafe.needSprintState = false;

        }
        return max2;
    }
    public static double calculateSpeed(MatrixMove move,boolean ely, float speed) {
        boolean fromGround = mc.player.onGround;
        boolean toGround = move.toGround();
        boolean jump = move.getMotionY() > 0;
        float speedAttributes = getAIMoveSpeed(mc.player);
        final float frictionFactor = getFrictionFactor(mc.player, move);
        float n6 = 0.91f;
        if (fromGround) {
            n6 = frictionFactor;
        }
        final float n7 = (float) (0.16277135908603668 / (n6 * n6 * n6));
        float n8;
        if (fromGround) {
            n8 = speedAttributes * n7;
            if (jump) {
                n8 += 0.2f;
            }
        } else {
            n8 = 0.0255f;
        }
        boolean noslow = false;
        double max2 = oldSpeed + n8;
        double max = 0.0;
        if (mc.player.isHandActive() && !jump) {
            double n10 = oldSpeed + n8 * 0.5 + 0.004999999888241291;
            double motionY2 = move.getMotionY();
            if (motionY2 != 0.0 && Math.abs(motionY2) < 0.08) {
                n10 += 0.055;
            }
            if (max2 > (max = Math.max(0.043, n10))) {
                noslow = true;
                ++noSlowTicks;
            } else {
                noSlowTicks = Math.max(noSlowTicks - 1, 0);
            }
        } else {
            noSlowTicks = 0;
        }
        if (noSlowTicks > 3) {
            max2 = max - 0.019;
        } else {
            max2 = Math.max(noslow ? 0 : 0.25, max2) - (counter++ % 2 == 0 ? 0.001 : 0.002);
        }
        contextFriction = n6;
        if (!toGround && !fromGround) {
            needSwap = true;
        } else {
            prevSprint = false;
        }
        if (!fromGround && !toGround) {
            Speed.needSprintState = !EventManager.serversprint;
            Strafe.needSprintState = !EventManager.serversprint;
            testMove.needSprintState = !testMove.serversprint;
        }
        if (toGround && fromGround) {
            Speed.needSprintState = false;
            Strafe.needSprintState = false;
            testMove.needSprintState = false;

        }
        return max2 + ((ely && toGround) ? speed : 0);
    }


    public static double calculateSpeed2(MatrixMove move,boolean ely, float speed) {
        boolean var2 = mc.player.onGround;
        boolean var3 = move.toGround();
        boolean var4 = move.getMotionY() > 0.0;
        float var5 = getAIMoveSpeed(mc.player);
        float var6 = getFrictionFactor(mc.player, move);
        float var7 = 0.911F;
        if (var2) {
            var7 = var6;
        }

        float var8 = (float)(0.16277135908603668 / Math.pow(var7, 3.0));
        float var9;
        if (var2) {
            var9 = var5 * var8;
            if (var4) {
                var9 += 0.13F;
            }
        } else {
            var9 =  0.022F;
        }

        double var11 = oldSpeed + (double)var9;
        var11 = Math.max(0.244, var11);

        contextFriction = var7;
        if (!var3 && !var2) {
            needSwap = true;
        } else {
            prevSprint = false;
        }

        if (!var2 && !var3) {
            testMove.needSprintState = !testMove.serversprint;
        }

        if (var3 && var2) {
            testMove.needSprintState = false;
        }

        return var11 + ((ely && move.toGround() && !mc.player.onGround) ? speed : 0);    }


    public static void postMove(double horizontal) {
        oldSpeed = horizontal * contextFriction;
    }

    public static float getAIMoveSpeed(EntityPlayer contextPlayer) {
        boolean prevSprinting = contextPlayer.isSprinting();
        contextPlayer.setSprinting(false);
        float speed = contextPlayer.getAIMoveSpeed() * 1.3f;
        contextPlayer.setSprinting(prevSprinting);
        return speed;
    }

    public static void actionEvent(EventSprint eventAction) {
        if (needSwap) {
            eventAction.setSprintState(!EventManager.serversprint);
            needSwap = false;
        }
    }


    private static float getFrictionFactor(EntityPlayer contextPlayer, MatrixMove move) {
        BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = BlockPos.PooledMutableBlockPos.retain(move.getFromX(), move.getAABBFrom().minY - 1.0D, move.getFromZ());
        return contextPlayer.world.getBlockState(blockpos$pooledmutableblockpos).getBlock().slipperiness * 0.91F;
    }
}