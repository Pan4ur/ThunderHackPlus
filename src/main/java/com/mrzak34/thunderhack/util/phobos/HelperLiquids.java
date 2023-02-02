package com.mrzak34.thunderhack.util.phobos;

import com.mrzak34.thunderhack.modules.combat.AutoCrystal;
import com.mrzak34.thunderhack.util.BlockUtils;
import com.mrzak34.thunderhack.util.EntityUtil;
import com.mrzak34.thunderhack.util.math.MathUtil;
import com.mrzak34.thunderhack.util.Util;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import java.util.List;


public class HelperLiquids
{
    private final AutoCrystal module;

    public HelperLiquids(AutoCrystal module) {
        this.module = module;
    }

    public PlaceData calculate(HelperPlace placeHelper,
                               PlaceData placeData,
                               List<EntityPlayer> friends,
                               List<EntityPlayer> players,
                               float minDamage)
    {
        PlaceData newData = new PlaceData(minDamage);
        newData.setTarget(placeData.getTarget());
        for (PositionData data : placeData.getLiquid())
        {
            if (placeHelper.validate(placeData, data, friends) != null)
            {
                placeHelper.calcPositionData(newData, data, players);
            }
        }

        return newData;
    }

    public EnumFacing getAbsorbFacing(BlockPos pos,
                                      List<Entity> entities,
                                      IBlockAccess access,
                                      double placeRange)
    {
        for (EnumFacing facing : EnumFacing.values())
        {
            if (facing == EnumFacing.DOWN)
            {
                continue;
            }

            BlockPos offset = pos.offset(facing);
            if (BlockUtils.getDistanceSq(offset) >= MathUtil.square(placeRange))
            {
                continue;
            }

            if (access.getBlockState(offset).getMaterial().isReplaceable())
            {
                boolean found = false;
                AxisAlignedBB bb = new AxisAlignedBB(offset);
                for (Entity entity : entities)
                {
                    if (entity == null
                            || EntityUtil.isDead(entity)
                            || !entity.preventEntitySpawning)
                    {
                        continue;
                    }

                    if (module.bbBlockingHelper.blocksBlock(bb, entity))
                    {
                        found = true;
                        break;
                    }
                }

                if (!found)
                {
                    return facing;
                }
            }
        }

        return null;
    }

    // TODO: make this utility method somewhere else, MineUtil maybe

    public static MineSlots getSlots(boolean onGroundCheck)
    {
        int bestBlock = -1;
        int bestTool  = -1;
        float maxSpeed = 0.0f;
        for (int i = 8; i > -1; i--)
        {
            ItemStack stack = Util.mc.player.inventory.getStackInSlot(i);
            if (stack.getItem() instanceof ItemBlock)
            {
                Block block = ((ItemBlock) stack.getItem()).getBlock();
                int tool = MineUtil.findBestTool(BlockPos.ORIGIN,
                        block.getDefaultState());
                float damage = MineUtil.getDamage(
                        block.getDefaultState(),
                        Util.mc.player.inventory.getStackInSlot(tool),
                        BlockPos.ORIGIN,
                        !onGroundCheck
                                || Util.mc.player.onGround);

                if (damage > maxSpeed)
                {
                    bestBlock = i;
                    bestTool  = tool;
                    maxSpeed  = damage;
                }
            }
        }

        return new MineSlots(bestBlock, bestTool, maxSpeed);
    }



}