package com.mrzak34.thunderhack.util;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.item.Item;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;

public class PlacementUtil {

    private static final Minecraft mc = Minecraft.getMinecraft();

    private static int placementConnections = 0;
    private static boolean isSneaking = false;

    public static void onEnable() {
        placementConnections++;
    }

    public static void stopSneaking() {
        if (isSneaking) {
            isSneaking = false;
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
        }
    }

    public static void onDisable() {
        placementConnections--;
        if (placementConnections == 0) {
            if (isSneaking) {
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
                isSneaking = false;
            }
        }
    }



    public static boolean place(BlockPos blockPos, EnumHand hand, boolean rotate, ArrayList<EnumFacing> forceSide) {
        return placeBlock(blockPos, hand, rotate, true, forceSide, true);
    }
    public static boolean place(BlockPos blockPos, EnumHand hand, boolean rotate, boolean checkAction) {
        return placeBlock(blockPos, hand, rotate, checkAction, null, true);
    }


    public static EnumFacing getPlaceableSideExlude(BlockPos pos, ArrayList<EnumFacing> excluding) {

        for (EnumFacing side : EnumFacing.values()) {

            if (!excluding.contains(side)) {

                BlockPos neighbour = pos.offset(side);

                if (!mc.world.getBlockState(neighbour).getBlock().canCollideCheck(mc.world.getBlockState(neighbour), false)) {
                    continue;
                }

                IBlockState blockState = mc.world.getBlockState(neighbour);
                if (!blockState.getMaterial().isReplaceable()) {
                    return side;
                }
            }
        }

        return null;
    }

    public static boolean placeBlock(BlockPos blockPos, EnumHand hand, boolean rotate, boolean checkAction, ArrayList<EnumFacing> forceSide, boolean swingArm) {
        EntityPlayerSP player = mc.player;
        WorldClient world = mc.world;
        PlayerControllerMP playerController = mc.playerController;

        if (player == null || world == null || playerController == null) return false;

        if (!world.getBlockState(blockPos).getMaterial().isReplaceable()) {
            return false;
        }

        EnumFacing side = forceSide != null ? getPlaceableSideExlude(blockPos, forceSide) : BlockUtils.getPlaceableSide(blockPos);

        if (side == null) {
            return false;
        }

        BlockPos neighbour = blockPos.offset(side);
        EnumFacing opposite = side.getOpposite();

        if (!BlockUtils.canBeClicked(neighbour)) {
            return false;
        }

        Vec3d hitVec = new Vec3d(neighbour).add(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
        Block neighbourBlock = world.getBlockState(neighbour).getBlock();

        if (!isSneaking && BlockUtils.blackList.contains(neighbourBlock) || BlockUtils.shulkerList.contains(neighbourBlock)) {
            player.connection.sendPacket(new CPacketEntityAction(player, CPacketEntityAction.Action.START_SNEAKING));
            isSneaking = true;
        }

        if (rotate) {
            SilentRotaionUtil.lookAtVector(hitVec);
        }

        EnumActionResult action = playerController.processRightClickBlock(player, world, neighbour, opposite, hitVec, hand);
        if (!checkAction || action == EnumActionResult.SUCCESS) {
            if (swingArm) {
                player.swingArm(hand);
                mc.rightClickDelayTimer = 4;
            }
            else {
                player.connection.sendPacket(new CPacketAnimation(hand));
            }
        }
        return action == EnumActionResult.SUCCESS;
    }

}
