package com.mrzak34.thunderhack.util;

import com.mrzak34.thunderhack.events.EventSync;
import com.mrzak34.thunderhack.mixin.mixins.IEntityPlayerSP;
import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InteractionUtil {
    private static final List<Block> shiftBlocks = Arrays.asList(
            Blocks.ENDER_CHEST,
            Blocks.CHEST,
            Blocks.TRAPPED_CHEST,
            Blocks.CRAFTING_TABLE,
            Blocks.ANVIL,
            Blocks.BREWING_STAND,
            Blocks.HOPPER,
            Blocks.DROPPER,
            Blocks.DISPENSER,
            Blocks.TRAPDOOR,
            Blocks.ENCHANTING_TABLE,
            Blocks.WHITE_SHULKER_BOX,
            Blocks.ORANGE_SHULKER_BOX,
            Blocks.MAGENTA_SHULKER_BOX,
            Blocks.LIGHT_BLUE_SHULKER_BOX,
            Blocks.YELLOW_SHULKER_BOX,
            Blocks.LIME_SHULKER_BOX,
            Blocks.PINK_SHULKER_BOX,
            Blocks.GRAY_SHULKER_BOX,
            Blocks.SILVER_SHULKER_BOX,
            Blocks.CYAN_SHULKER_BOX,
            Blocks.PURPLE_SHULKER_BOX,
            Blocks.BLUE_SHULKER_BOX,
            Blocks.BROWN_SHULKER_BOX,
            Blocks.GREEN_SHULKER_BOX,
            Blocks.RED_SHULKER_BOX,
            Blocks.BLACK_SHULKER_BOX
    );
    private static final Minecraft mc = Minecraft.getMinecraft();

    public static boolean canPlaceNormally() {
        return true;
    }

    public static Placement preparePlacement(BlockPos pos, boolean rotate, EventSync e) {
        return preparePlacement(pos, rotate, false, e);
    }

    public static boolean canPlaceNormally(boolean rotate) {
        if (!rotate) return true;
        return true;
    }

    public static Placement preparePlacement(BlockPos pos, boolean rotate, boolean instant, EventSync e) {
        return preparePlacement(pos, rotate, instant, false, e);
    }

    public static Placement preparePlacement(BlockPos pos, boolean rotate, boolean instant, boolean strictDirection, EventSync e) {
        return preparePlacement(pos, rotate, instant, strictDirection, false, e);
    }

    public static Placement preparePlacement(BlockPos pos, boolean rotate, boolean instant, boolean strictDirection, boolean rayTrace, EventSync e) {
        EnumFacing side = null;
        Vec3d hitVec = null;
        double dist = 69420D;
        for (EnumFacing facing : getPlacableFacings(pos, strictDirection, rayTrace)) {
            BlockPos tempNeighbour = pos.offset(facing);
            Vec3d tempVec = new Vec3d(tempNeighbour).add(0.5, 0.5, 0.5).add(new Vec3d(facing.getDirectionVec()).scale(0.5));
            if (mc.player.getPositionVector().add(0, mc.player.getEyeHeight(), 0).distanceTo(tempVec) < dist) {
                side = facing;
                hitVec = tempVec;
            }
        }
        if (side == null) {
            return null;
        }
        BlockPos neighbour = pos.offset(side);
        EnumFacing opposite = side.getOpposite();
        float[] angle = SilentRotationUtil.calculateAngle(mc.player.getPositionEyes(mc.getRenderPartialTicks()), hitVec);
        if (rotate) {
            if (instant) {
                mc.player.connection.sendPacket(new CPacketPlayer.Rotation(angle[0], angle[1], mc.player.onGround));
                ((IEntityPlayerSP) mc.player).setLastReportedYaw(angle[0]);
                ((IEntityPlayerSP) mc.player).setLastReportedPitch(angle[1]);
            } else {
                mc.player.rotationPitch = (angle[1]);
                mc.player.rotationYaw = (angle[0]);

            }
        }
        return new Placement(neighbour, opposite, hitVec, angle[0], angle[1]);
    }

    public static Placement preparePlacement(BlockPos pos, boolean rotate, boolean instant) {
        return preparePlacement(pos, rotate, instant, false);
    }

    public static Placement preparePlacement(BlockPos pos, boolean rotate, boolean instant, boolean strictDirection) {
        return preparePlacement(pos, rotate, instant, strictDirection, false);
    }

    public static Placement preparePlacement(BlockPos pos, boolean rotate, boolean instant, boolean strictDirection, boolean rayTrace) {
        EnumFacing side = null;
        Vec3d hitVec = null;
        double dist = 69420D;
        for (EnumFacing facing : getPlacableFacings(pos, strictDirection, rayTrace)) {
            BlockPos tempNeighbour = pos.offset(facing);
            Vec3d tempVec = new Vec3d(tempNeighbour).add(0.5, 0.5, 0.5).add(new Vec3d(facing.getDirectionVec()).scale(0.5));
            if (mc.player.getPositionVector().add(0, mc.player.getEyeHeight(), 0).distanceTo(tempVec) < dist) {
                side = facing;
                hitVec = tempVec;
            }
        }
        if (side == null) {
            return null;
        }
        BlockPos neighbour = pos.offset(side);
        EnumFacing opposite = side.getOpposite();
        float[] angle = SilentRotationUtil.calculateAngle(mc.player.getPositionEyes(mc.getRenderPartialTicks()), hitVec);
        if (rotate) {
            if (instant) {
                mc.player.connection.sendPacket(new CPacketPlayer.Rotation(angle[0], angle[1], mc.player.onGround));
                ((IEntityPlayerSP) mc.player).setLastReportedYaw(angle[0]);
                ((IEntityPlayerSP) mc.player).setLastReportedPitch(angle[1]);
            } else {
                mc.player.rotationYaw = angle[0];
                mc.player.rotationPitch = angle[1];

            }
        }
        return new Placement(neighbour, opposite, hitVec, angle[0], angle[1]);
    }

    public static void placeBlockSafely(Placement placement, EnumHand hand, boolean packet) {
        boolean isSprinting = mc.player.isSprinting();
        boolean shouldSneak = BlockUtils.shouldSneakWhileRightClicking(placement.getNeighbour());

        if (isSprinting) {
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SPRINTING));
        }

        if (shouldSneak) {
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
        }

        placeBlock(placement, hand, packet);

        if (shouldSneak) {
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
        }

        if (isSprinting) {
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SPRINTING));
        }
    }

    public static void placeBlock(Placement placement, EnumHand hand, boolean packet) {
        rightClickBlock(placement.getNeighbour(), placement.getHitVec(), hand, placement.getOpposite(), packet, true);
    }

    public static void rightClickBlock(BlockPos pos, Vec3d vec, EnumHand hand, EnumFacing direction, boolean packet, boolean swing) {
        if (packet) {
            float dX = (float) (vec.x - (double) pos.getX());
            float dY = (float) (vec.y - (double) pos.getY());
            float dZ = (float) (vec.z - (double) pos.getZ());
            mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(pos, direction, hand, dX, dY, dZ));
        } else {
            mc.playerController.processRightClickBlock(mc.player, mc.world, pos, direction, vec, hand);
        }
        if (swing) {
            mc.player.connection.sendPacket(new CPacketAnimation(hand));
        }
        // Util.mc.setRightClickDelayTimer(4);
    }

    public static boolean canPlaceBlock(BlockPos pos, boolean strictDirection) {
        return canPlaceBlock(pos, strictDirection, true);
    }

    public static boolean canPlaceBlock(BlockPos pos, boolean strictDirection, boolean checkEntities) {
        return canPlaceBlock(pos, strictDirection, false, checkEntities);
    }

    public static boolean canPlaceBlock(BlockPos pos, boolean strictDirection, boolean rayTrace, boolean checkEntities) {
        Block block = mc.world.getBlockState(pos).getBlock();
        if (!(block instanceof BlockAir || block instanceof BlockLiquid || block instanceof BlockTallGrass || block instanceof BlockFire || block instanceof BlockDeadBush || block instanceof BlockSnow)) {
            return false;
        }
        if (checkEntities) {
            for (Entity entity : mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos))) {
                if (entity instanceof EntityItem || entity instanceof EntityXPOrb) continue;
                return false;
            }
        }
        for (EnumFacing side : getPlacableFacings(pos, strictDirection, rayTrace)) {
            if (!canClick(pos.offset(side))) continue;
            return true;
        }
        return false;
    }

    public static boolean canClick(BlockPos pos) {
        return mc.world.getBlockState(pos).getBlock().canCollideCheck(mc.world.getBlockState(pos), false);
    }

    public static List<EnumFacing> getPlacableFacings(BlockPos pos, boolean strictDirection, boolean rayTrace) {
        ArrayList<EnumFacing> validFacings = new ArrayList<>();
        for (EnumFacing side : EnumFacing.values()) {
            if (rayTrace) {
                Vec3d testVec = new Vec3d(pos).add(0.5, 0.5, 0.5).add(new Vec3d(side.getDirectionVec()).scale(0.5));
                RayTraceResult result = mc.world.rayTraceBlocks(mc.player.getPositionEyes(1F), testVec);
                if (result != null && result.typeOfHit != RayTraceResult.Type.MISS) {
                    System.out.println("weary");
                    continue;
                }
            }
            BlockPos neighbour = pos.offset(side);
            if (strictDirection) {
                Vec3d eyePos = mc.player.getPositionEyes(1.0f);
                Vec3d blockCenter = new Vec3d(neighbour.getX() + 0.5, neighbour.getY() + 0.5, neighbour.getZ() + 0.5);
                IBlockState blockState = mc.world.getBlockState(neighbour);
                boolean isFullBox = blockState.getBlock() == Blocks.AIR || blockState.isFullBlock();
                ArrayList<EnumFacing> validAxis = new ArrayList<>();
                validAxis.addAll(checkAxis(eyePos.x - blockCenter.x, EnumFacing.WEST, EnumFacing.EAST, !isFullBox));
                validAxis.addAll(checkAxis(eyePos.y - blockCenter.y, EnumFacing.DOWN, EnumFacing.UP, true));
                validAxis.addAll(checkAxis(eyePos.z - blockCenter.z, EnumFacing.NORTH, EnumFacing.SOUTH, !isFullBox));
                if (!validAxis.contains(side.getOpposite())) continue;
            }
            IBlockState blockState = mc.world.getBlockState(neighbour);
            if ((blockState == null || !blockState.getBlock().canCollideCheck(blockState, false) || blockState.getMaterial().isReplaceable()))
                continue;
            validFacings.add(side);
        }
        return validFacings;
    }

    public static ArrayList<EnumFacing> checkAxis(double diff, EnumFacing negativeSide, EnumFacing positiveSide, boolean bothIfInRange) {
        ArrayList<EnumFacing> valid = new ArrayList<>();
        if (diff < -0.5) {
            valid.add(negativeSide);
        }
        if (diff > 0.5) {
            valid.add(positiveSide);
        }
        if (bothIfInRange) {
            if (!valid.contains(negativeSide)) valid.add(negativeSide);
            if (!valid.contains(positiveSide)) valid.add(positiveSide);
        }
        return valid;
    }


    public static void placeBlock(BlockPos position, boolean strict) {
        for (EnumFacing direction : EnumFacing.values()) {
            BlockPos directionOffset = position.offset(direction);

            for (Entity entity : mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(position))) {
                if (!(entity instanceof EntityItem) && !(entity instanceof EntityXPOrb)) {
                    return;
                }
            }

            EnumFacing oppositeFacing = direction.getOpposite();
            if (strict && !getVisibleSides(directionOffset).contains(direction.getOpposite())) {
                continue;
            }
            if (mc.world.getBlockState(directionOffset).getMaterial().isReplaceable()) {
                continue;
            }

            boolean sneak = shiftBlocks.contains(mc.world.getBlockState(directionOffset).getBlock()) && !mc.player.isSneaking();
            if (sneak) {
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
            }

            float[] angle = getAnglesToBlock(directionOffset, oppositeFacing);

            Vec3d interactVector = null;

            if (strict) {
                RayTraceResult result = getTraceResult(mc.playerController.getBlockReachDistance(), angle[0], angle[1]);
                if (result != null && result.typeOfHit.equals(RayTraceResult.Type.BLOCK)) {
                    interactVector = result.hitVec;
                }
            }

            if (interactVector == null) {
                interactVector = new Vec3d(directionOffset).add(0.5, 0.5, 0.5);
            }

            mc.playerController.processRightClickBlock(mc.player, mc.world, directionOffset, direction.getOpposite(), interactVector, EnumHand.MAIN_HAND);

            if (sneak) {
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            }
            mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
            break;
        }
    }

    public static void placeBlock(BlockPos position, boolean strict, boolean rotate) {
        for (EnumFacing direction : EnumFacing.values()) {
            BlockPos directionOffset = position.offset(direction);

            for (Entity entity : mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(position))) {
                if (!(entity instanceof EntityItem) && !(entity instanceof EntityXPOrb)) {
                    return;
                }
            }

            EnumFacing oppositeFacing = direction.getOpposite();
            if (strict && !getVisibleSides(directionOffset).contains(direction.getOpposite())) {
                continue;
            }
            if (mc.world.getBlockState(directionOffset).getMaterial().isReplaceable()) {
                continue;
            }

            boolean sneak = shiftBlocks.contains(mc.world.getBlockState(directionOffset).getBlock()) && !mc.player.isSneaking();
            if (sneak) {
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
            }

            float[] angle = getAnglesToBlock(directionOffset, oppositeFacing);

            if (rotate) {
                mc.player.rotationYaw = angle[0];
                mc.player.rotationPitch = angle[1];
            }

            Vec3d interactVector = null;

            if (strict) {
                RayTraceResult result = getTraceResult(mc.playerController.getBlockReachDistance(), angle[0], angle[1]);
                if (result != null && result.typeOfHit.equals(RayTraceResult.Type.BLOCK)) {
                    interactVector = result.hitVec;
                }
            }

            if (interactVector == null) {
                interactVector = new Vec3d(directionOffset).add(0.5, 0.5, 0.5);
            }

            mc.playerController.processRightClickBlock(mc.player, mc.world, directionOffset, direction.getOpposite(), interactVector, EnumHand.MAIN_HAND);

            if (sneak) {
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            }
            mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
            break;
        }
    }

    public static float[] getAnglesToBlock(BlockPos pos, EnumFacing facing) {
        Vec3d diff = new Vec3d(pos.getX() + 0.5 - mc.player.posX + facing.getXOffset() / 2.0, pos.getY() + 0.5, pos.getZ() + 0.5 - mc.player.posZ + facing.getZOffset() / 2.0);
        double distance = Math.sqrt(diff.x * diff.x + diff.z * diff.z);
        float yaw = (float) (Math.atan2(diff.z, diff.x) * 180.0 / Math.PI - 90.0);
        float pitch = (float) (Math.atan2(mc.player.posY + mc.player.getEyeHeight() - diff.y, distance) * 180.0 / Math.PI);
        return new float[]{MathHelper.wrapDegrees(yaw), MathHelper.wrapDegrees(pitch)};
    }

    public static RayTraceResult getTraceResult(double distance, float yaw, float pitch) {
        Vec3d eyes = mc.player.getPositionEyes(1.0f);
        Vec3d rotationVector = getVectorForRotation(yaw, pitch);
        return mc.world.rayTraceBlocks(eyes, eyes.add(rotationVector.x * distance, rotationVector.y * distance, rotationVector.z * distance), false, false, true);
    }

    public static Vec3d getVectorForRotation(float yaw, float pitch) {
        float yawCos = MathHelper.cos(-yaw * 0.017453292F - (float) Math.PI);
        float yawSin = MathHelper.sin(-yaw * 0.017453292F - (float) Math.PI);
        float pitchCos = -MathHelper.cos(-pitch * 0.017453292F);
        float pitchSin = MathHelper.sin(-pitch * 0.017453292F);
        return new Vec3d(yawSin * pitchCos, pitchSin, yawCos * pitchCos);
    }

    public static List<EnumFacing> getVisibleSides(BlockPos position) {
        List<EnumFacing> visibleSides = new ArrayList<>();

        Vec3d positionVector = new Vec3d(position).add(0.5, 0.5, 0.5);

        double facingX = mc.player.getPositionEyes(1).x - positionVector.x;
        double facingY = mc.player.getPositionEyes(1).y - positionVector.y;
        double facingZ = mc.player.getPositionEyes(1).z - positionVector.z;

        if (facingX < -0.5) {
            visibleSides.add(EnumFacing.WEST);
        } else if (facingX > 0.5) {
            visibleSides.add(EnumFacing.EAST);
        } else if (!mc.world.getBlockState(position).isFullBlock() || !mc.world.isAirBlock(position)) {
            visibleSides.add(EnumFacing.WEST);
            visibleSides.add(EnumFacing.EAST);
        }

        if (facingY < -0.5) {
            visibleSides.add(EnumFacing.DOWN);
        } else if (facingY > 0.5) {
            visibleSides.add(EnumFacing.UP);
        } else {
            visibleSides.add(EnumFacing.DOWN);
            visibleSides.add(EnumFacing.UP);
        }

        if (facingZ < -0.5) {
            visibleSides.add(EnumFacing.NORTH);
        } else if (facingZ > 0.5) {
            visibleSides.add(EnumFacing.SOUTH);
        } else if (!mc.world.getBlockState(position).isFullBlock() || !mc.world.isAirBlock(position)) {
            visibleSides.add(EnumFacing.NORTH);
            visibleSides.add(EnumFacing.SOUTH);
        }

        return visibleSides;
    }

    public static class Placement {
        private final BlockPos neighbour;
        private final EnumFacing opposite;
        private final Vec3d hitVec;
        private final float yaw;
        private final float pitch;

        public Placement(BlockPos neighbour, EnumFacing opposite, Vec3d hitVec, float yaw, float pitch) {
            this.neighbour = neighbour;
            this.opposite = opposite;
            this.hitVec = hitVec;
            this.yaw = yaw;
            this.pitch = pitch;
        }

        public BlockPos getNeighbour() {
            return neighbour;
        }

        public EnumFacing getOpposite() {
            return opposite;
        }

        public Vec3d getHitVec() {
            return hitVec;
        }

        public float getYaw() {
            return yaw;
        }

        public float getPitch() {
            return pitch;
        }
    }

}