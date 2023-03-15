package com.mrzak34.thunderhack.modules.player;

import com.mrzak34.thunderhack.events.EventPreMotion;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.MovementUtil;
import com.mrzak34.thunderhack.util.math.MathUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockHopper;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static com.mrzak34.thunderhack.util.MovementUtil.isMoving;

public class NoClip extends Module {
    public Setting<Mode> mode = register(new Setting("Mode", Mode.Default));
    private final Setting<Integer> timeout = register(new Setting<>("Timeout", 5, 1, 10, v -> mode.getValue() == Mode.CC));
    private int packets;
    public NoClip() {
        super("NoClip", "NoClip", Category.PLAYER);
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

    @SubscribeEvent
    public void onPreSync(EventPreMotion e) {
        if (mode.getValue() == Mode.SunriseBypass && (mc.player.collidedHorizontally || playerInsideBlock())) {
            if (mc.gameSettings.keyBindSneak.isKeyDown()) {
                double[] dir = MovementUtil.forward(0.5);
                NoClip.mc.playerController.onPlayerDamageBlock(new BlockPos(NoClip.mc.player.posX + dir[0], NoClip.mc.player.posY - 1, NoClip.mc.player.posZ + dir[1]), NoClip.mc.player.getHorizontalFacing());
                NoClip.mc.player.swingArm(EnumHand.MAIN_HAND);
            } else if (isMoving()) {
                double[] dir = MovementUtil.forward(0.5);
                NoClip.mc.playerController.onPlayerDamageBlock(new BlockPos(NoClip.mc.player.posX + dir[0], NoClip.mc.player.posY, NoClip.mc.player.posZ + dir[1]), NoClip.mc.player.getHorizontalFacing());
                NoClip.mc.player.swingArm(EnumHand.MAIN_HAND);
            }

        }
    }

    @Override
    public void onUpdate() {
        if (isMoving()) {
            disable();
            return;
        }
        if (mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().grow(0.01, 0, 0.01)).size() < 2) {
            mc.player.setPosition(MathUtil.roundToClosest(mc.player.posX, Math.floor(mc.player.posX) + 0.301, Math.floor(mc.player.posX) + 0.699), mc.player.posY, MathUtil.roundToClosest(mc.player.posZ, Math.floor(mc.player.posZ) + 0.301, Math.floor(mc.player.posZ) + 0.699));
            packets = 0;
        } else if (mc.player.ticksExisted % timeout.getValue() == 0) {
            mc.player.setPosition(mc.player.posX + MathHelper.clamp(MathUtil.roundToClosest(mc.player.posX, Math.floor(mc.player.posX) + 0.241, Math.floor(mc.player.posX) + 0.759) - mc.player.posX, -0.03, 0.03), mc.player.posY, mc.player.posZ + MathHelper.clamp(MathUtil.roundToClosest(mc.player.posZ, Math.floor(mc.player.posZ) + 0.241, Math.floor(mc.player.posZ) + 0.759) - mc.player.posZ, -0.03, 0.03));
            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, true));
            mc.player.connection.sendPacket(new CPacketPlayer.Position(MathUtil.roundToClosest(mc.player.posX, Math.floor(mc.player.posX) + 0.23, Math.floor(mc.player.posX) + 0.77), mc.player.posY, MathUtil.roundToClosest(mc.player.posZ, Math.floor(mc.player.posZ) + 0.23, Math.floor(mc.player.posZ) + 0.77), true));
            packets++;
        }
    }

    public enum Mode {
        Default, SunriseBypass, CC
    }
}
