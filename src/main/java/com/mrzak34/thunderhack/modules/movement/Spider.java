package com.mrzak34.thunderhack.modules.movement;

import com.mrzak34.thunderhack.events.EventPreMotion;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemBlock;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static com.mrzak34.thunderhack.util.MovementUtil.isMoving;

public class Spider extends Module {

    public final Setting<Integer> delay = register(new Setting("delay", 2, 1, 15));
    public Setting<Boolean> dropBlocks = this.register(new Setting<>("DropBlocks", false));
    private final Setting<mode> a = register(new Setting("Mode", mode.Matrix));

    public Spider() {
        super("Spider", "Spider", Category.MOVEMENT);
    }

    public static EnumFacing getPlaceableSide(BlockPos pos) {
        for (EnumFacing side : EnumFacing.values()) {
            BlockPos neighbour = pos.offset(side);
            if (mc.world.isAirBlock(neighbour)) {
                continue;
            }
            IBlockState blockState = mc.world.getBlockState(neighbour);
            if (!blockState.getMaterial().isReplaceable()) {
                return side;
            }
        }
        return null;
    }

    @Override
    public void onTick() {
        if (!mc.player.collidedHorizontally) {
            return;
        }
        if (a.getValue() == mode.Default) {
            mc.player.motionY = 0.2;
            mc.player.isAirBorne = false;
        } else if (a.getValue() == mode.Matrix) {
            if (mc.player.ticksExisted % delay.getValue() == 0) {
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
        if (mc.gameSettings.keyBindJump.isKeyDown() && mc.player.motionY <= -0.3739040364667221 && a.getValue() == mode.MatrixNew) {
            mc.player.onGround = true;
            mc.player.motionY = 0.481145141919180;
        }
        if (mc.player.ticksExisted % delay.getValue() == 0 && mc.player.collidedHorizontally && isMoving() && a.getValue() == mode.Blocks) {
            int find = -2;
            for (int i = 0; i <= 8; i++)
                if (mc.player.inventory.getStackInSlot(i).getItem() instanceof ItemBlock)
                    find = i;

            if (find == -2)
                return;

            BlockPos pos = new BlockPos(mc.player.posX, mc.player.posY + 2, mc.player.posZ);
            EnumFacing side = getPlaceableSide(pos);
            if (side != null) {
                mc.player.connection.sendPacket(new CPacketHeldItemChange(find));

                BlockPos neighbour = new BlockPos(mc.player.posX, mc.player.posY + 2, mc.player.posZ).offset(side);
                EnumFacing opposite = side.getOpposite();

                Vec3d hitVec = new Vec3d(neighbour).add(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));

                float x = (float) (hitVec.x - (double) neighbour.getX());
                float y = (float) (hitVec.y - (double) neighbour.getY());
                float z = (float) (hitVec.z - (double) neighbour.getZ());

                mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(neighbour, opposite, EnumHand.MAIN_HAND, x, y, z));
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
                if (mc.world.getBlockState(new BlockPos(mc.player).add(0, 2, 0)).getBlock() != Blocks.AIR) {
                    mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, neighbour, opposite));
                    mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, neighbour, opposite));
                }
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            }


            mc.player.onGround = true;
            mc.player.isAirBorne = true;
            mc.player.jump();
            mc.player.connection.sendPacket(new CPacketHeldItemChange(mc.player.inventory.currentItem));
            if (dropBlocks.getValue()) {
                for (int i = 9; i < 45; i++) {
                    if (mc.player.inventoryContainer.getSlot(i).getHasStack()) {
                        if (mc.player.inventoryContainer.getSlot(i).getStack().getItem() instanceof ItemBlock) {
                            mc.playerController.windowClick(mc.player.inventoryContainer.windowId, i, 0, ClickType.THROW, mc.player);
                            mc.player.jump();
                            break;
                        }
                    }
                }
            }
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

    public enum mode {
        Default, Matrix, MatrixNew, Blocks
    }

}
