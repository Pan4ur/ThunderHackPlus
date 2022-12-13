package com.mrzak34.thunderhack.util;

import com.github.lunatrius.schematica.handler.ConfigurationHandler;
import com.github.lunatrius.schematica.reference.Constants;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

import java.util.Arrays;
import java.util.List;


public class BlockInteractionHelper
{

    public static final List<Block> blackList = Arrays.asList(Blocks.ENDER_CHEST, Blocks.CHEST, Blocks.TRAPPED_CHEST, Blocks.CRAFTING_TABLE, Blocks.ANVIL, Blocks.BREWING_STAND, Blocks.HOPPER,
            Blocks.DROPPER, Blocks.DISPENSER, Blocks.TRAPDOOR, Blocks.ENCHANTING_TABLE);

    public static final List<Block> shulkerList = Arrays.asList(Blocks.WHITE_SHULKER_BOX, Blocks.ORANGE_SHULKER_BOX, Blocks.MAGENTA_SHULKER_BOX, Blocks.LIGHT_BLUE_SHULKER_BOX,
            Blocks.YELLOW_SHULKER_BOX, Blocks.LIME_SHULKER_BOX, Blocks.PINK_SHULKER_BOX, Blocks.GRAY_SHULKER_BOX, Blocks.SILVER_SHULKER_BOX, Blocks.CYAN_SHULKER_BOX, Blocks.PURPLE_SHULKER_BOX,
            Blocks.BLUE_SHULKER_BOX, Blocks.BROWN_SHULKER_BOX, Blocks.GREEN_SHULKER_BOX, Blocks.RED_SHULKER_BOX, Blocks.BLACK_SHULKER_BOX);

    private static final Minecraft mc = Minecraft.getMinecraft();



    public static float[] getLegitRotations(Vec3d vec)
    {
        Vec3d eyesPos = getEyesPos();

        double diffX = vec.x - eyesPos.x;
        double diffY = vec.y - eyesPos.y;
        double diffZ = vec.z - eyesPos.z;

        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);

        float yaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F;
        float pitch = (float) -Math.toDegrees(Math.atan2(diffY, diffXZ));

        return new float[]
                { Minecraft.getMinecraft().player.rotationYaw + MathHelper.wrapDegrees(yaw - Minecraft.getMinecraft().player.rotationYaw),
                        Minecraft.getMinecraft().player.rotationPitch + MathHelper.wrapDegrees(pitch - Minecraft.getMinecraft().player.rotationPitch) };
    }

    private static Vec3d getEyesPos()
    {
        return new Vec3d(Minecraft.getMinecraft().player.posX, Minecraft.getMinecraft().player.posY + Minecraft.getMinecraft().player.getEyeHeight(), Minecraft.getMinecraft().player.posZ);
    }

    public static void faceVectorPacketInstant(Vec3d vec)
    {
        float[] rotations = getLegitRotations(vec);

        Minecraft.getMinecraft().player.connection.sendPacket(new CPacketPlayer.Rotation(rotations[0], rotations[1], Minecraft.getMinecraft().player.onGround));
    }






    public static boolean checkForNeighbours(BlockPos blockPos)
    {
        // check if we don't have a block adjacent to blockpos
        if (!hasNeighbour(blockPos))
        {
            // find air adjacent to blockpos that does have a block adjacent to it, let's fill this first as to form a bridge between the player and the original blockpos. necessary if the player is
            // going diagonal.
            for (EnumFacing side : EnumFacing.values())
            {
                BlockPos neighbour = blockPos.offset(side);
                if (hasNeighbour(neighbour))
                {
                    return true;
                }

                if (side == EnumFacing.UP && mc.world.getBlockState(blockPos).getBlock() == Blocks.WATER)
                {
                    if (mc.world.getBlockState(blockPos.up()).getBlock() == Blocks.AIR && false)
                        return true;
                }
            }
            return false;
        }
        return true;
    }

    public static boolean hasNeighbour(BlockPos blockPos)
    {
        for (EnumFacing side : EnumFacing.values())
        {
            BlockPos neighbour = blockPos.offset(side);
            if (!Minecraft.getMinecraft().world.getBlockState(neighbour).getMaterial().isReplaceable())
            {
                return true;
            }
        }
        return false;
    }





    public enum ValidResult
    {
        NoEntityCollision,
        AlreadyBlockThere,
        NoNeighbors,
        Ok,
    }

    public static ValidResult valid(BlockPos pos)
    {
        // There are no entities to block placement,
        if (!mc.world.checkNoEntityCollision(new AxisAlignedBB(pos)))
            return ValidResult.NoEntityCollision;

        if (mc.world.getBlockState(pos.down()).getBlock() == Blocks.WATER)
            if (false)
                return ValidResult.Ok;

        if (!BlockInteractionHelper.checkForNeighbours(pos))
            return ValidResult.NoNeighbors;

        IBlockState l_State = mc.world.getBlockState(pos);

        if (l_State.getBlock() == Blocks.AIR)
        {
            final BlockPos[] l_Blocks =
                    { pos.north(), pos.south(), pos.east(), pos.west(), pos.up(), pos.down() };

            for (BlockPos l_Pos : l_Blocks)
            {
                IBlockState l_State2 = mc.world.getBlockState(l_Pos);

                if (l_State2.getBlock() == Blocks.AIR)
                    continue;

                for (final EnumFacing side : EnumFacing.values())
                {
                    final BlockPos neighbor = pos.offset(side);

                    boolean l_IsWater = mc.world.getBlockState(neighbor).getBlock() == Blocks.WATER;

                    if (mc.world.getBlockState(neighbor).getBlock().canCollideCheck(mc.world.getBlockState(neighbor), false))
                    {
                        return ValidResult.Ok;
                    }
                }
            }

            return ValidResult.NoNeighbors;
        }

        return ValidResult.AlreadyBlockThere;
    }

    public enum PlaceResult
    {
        NotReplaceable,
        Neighbors,
        CantPlace,
        Placed,
    }

    public static PlaceResult place(BlockPos pos, float p_Distance, boolean p_Rotate, boolean p_UseSlabRule, ItemStack item)
    {
        return place(pos, p_Distance, p_Rotate, p_UseSlabRule, false,item);
    }







    private static boolean swapToItem(final InventoryPlayer inventory, final ItemStack itemStack)
    {
        return swapToItem(inventory, itemStack, true);
    }

    private static boolean swapToItem(final InventoryPlayer inventory, final ItemStack itemStack, final boolean swapSlots)
    {
        final int slot = getInventorySlotWithItem(inventory, itemStack);

        if (mc.playerController.isInCreativeMode()
                && (slot < Constants.Inventory.InventoryOffset.HOTBAR || slot >= Constants.Inventory.InventoryOffset.HOTBAR + Constants.Inventory.Size.HOTBAR)
                && ConfigurationHandler.swapSlotsQueue.size() > 0)
        {
            inventory.currentItem = getNextSlot();
            inventory.setInventorySlotContents(inventory.currentItem, itemStack.copy());
            mc.playerController.sendSlotPacket(inventory.getStackInSlot(inventory.currentItem), Constants.Inventory.SlotOffset.HOTBAR + inventory.currentItem);
            return true;
        }

        if (slot >= Constants.Inventory.InventoryOffset.HOTBAR && slot < Constants.Inventory.InventoryOffset.HOTBAR + Constants.Inventory.Size.HOTBAR)
        {
            inventory.currentItem = slot;
            return true;
        }
        else if (swapSlots && slot >= Constants.Inventory.InventoryOffset.INVENTORY && slot < Constants.Inventory.InventoryOffset.INVENTORY + Constants.Inventory.Size.INVENTORY)
        {
            if (swapSlots(inventory, slot))
            {
                return swapToItem(inventory, itemStack, false);
            }
        }

        return false;
    }

    private static int getInventorySlotWithItem(final InventoryPlayer inventory, final ItemStack itemStack)
    {
        for (int i = 0; i < inventory.mainInventory.size(); i++)
        {
            if (inventory.mainInventory.get(i).isItemEqual(itemStack))
            {
                return i;
            }
        }
        return -1;
    }

    private static boolean swapSlots(final InventoryPlayer inventory, final int from)
    {
        if (ConfigurationHandler.swapSlotsQueue.size() > 0)
        {
            final int slot = getNextSlot();

            swapSlots(from, slot);
            return true;
        }

        return false;
    }

    private static int getNextSlot()
    {
        final int slot = ConfigurationHandler.swapSlotsQueue.poll() % Constants.Inventory.Size.HOTBAR;
        ConfigurationHandler.swapSlotsQueue.offer(slot);
        return slot;
    }

    private static boolean swapSlots(final int from, final int to)
    {
        return mc.playerController.windowClick(mc.player.inventoryContainer.windowId, from, to, ClickType.SWAP, mc.player) == ItemStack.EMPTY;
    }

    public static PlaceResult place(BlockPos pos, float p_Distance, boolean p_Rotate, boolean p_UseSlabRule, boolean packetSwing,ItemStack item)
    {
        if (mc.player.getHeldItemMainhand().getItem() != item.getItem()) {
            swapToItem(mc.player.inventory, item);
        }
        IBlockState l_State = mc.world.getBlockState(pos);

        boolean l_Replaceable = l_State.getMaterial().isReplaceable();

        boolean l_IsSlabAtBlock = l_State.getBlock() instanceof BlockSlab;

        if (!l_Replaceable && !l_IsSlabAtBlock)
            return PlaceResult.NotReplaceable;
        if (!BlockInteractionHelper.checkForNeighbours(pos))
            return PlaceResult.Neighbors;

        if (!l_IsSlabAtBlock)
        {
            ValidResult l_Result = valid(pos);

            if (l_Result != ValidResult.Ok && !l_Replaceable)
                return PlaceResult.CantPlace;
        }

        if (p_UseSlabRule)
        {
            if (l_IsSlabAtBlock && !l_State.isFullCube())
                return PlaceResult.CantPlace;
        }

        final Vec3d eyesPos = new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ);

        for (final EnumFacing side : EnumFacing.values())
        {
            final BlockPos neighbor = pos.offset(side);
            final EnumFacing side2 = side.getOpposite();

            boolean l_IsWater = mc.world.getBlockState(neighbor).getBlock() == Blocks.WATER;

            if (mc.world.getBlockState(neighbor).getBlock().canCollideCheck(mc.world.getBlockState(neighbor), false))
            {
                final Vec3d hitVec = new Vec3d((Vec3i) neighbor).add(0.5, 0.5, 0.5).add(new Vec3d(side2.getDirectionVec()).scale(0.5));
                if (eyesPos.distanceTo(hitVec) <= p_Distance)
                {
                    final Block neighborPos = mc.world.getBlockState(neighbor).getBlock();
                    boolean activated = false;
                    try {
                        activated = neighborPos.onBlockActivated(mc.world, pos, mc.world.getBlockState(pos), mc.player, EnumHand.MAIN_HAND, side, 0, 0, 0);
                    } catch (Exception e){

                    }

                    if (BlockInteractionHelper.blackList.contains(neighborPos) || BlockInteractionHelper.shulkerList.contains(neighborPos) || activated)
                    {
                        mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
                    }
                    if (p_Rotate)
                    {
                        BlockInteractionHelper.faceVectorPacketInstant(hitVec);
                    }
                    EnumActionResult l_Result2 = mc.playerController.processRightClickBlock(mc.player, mc.world, neighbor, side2, hitVec, EnumHand.MAIN_HAND);

                    if (l_Result2 != EnumActionResult.FAIL)
                    {
                        if (packetSwing)
                            mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
                        else
                            mc.player.swingArm(EnumHand.MAIN_HAND);
                        if (activated)
                        {
                            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
                        }
                        return PlaceResult.Placed;
                    }
                }
            }
        }
        return PlaceResult.CantPlace;
    }







}