package com.mrzak34.thunderhack.util.phobos;

import com.mrzak34.thunderhack.modules.combat.AutoCrystal;
import com.mrzak34.thunderhack.util.BlockUtils;
import com.mrzak34.thunderhack.util.EntityUtil;
import com.mrzak34.thunderhack.util.Util;
import com.mrzak34.thunderhack.util.math.MathUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import java.util.List;

import static com.mrzak34.thunderhack.util.Util.mc;


public class HelperLiquids {
    private final AutoCrystal module;

    public HelperLiquids(AutoCrystal module) {
        this.module = module;
    }

    public static MineSlots getSlots(boolean onGroundCheck) {
        int bestBlock = -1;
        int bestTool = -1;
        float maxSpeed = 0.0f;
        for (int i = 8; i > -1; i--) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack.getItem() instanceof ItemBlock) {
                Block block = ((ItemBlock) stack.getItem()).getBlock();
                int tool = getTool(block);
                final float digSpeed = EnchantmentHelper.getEnchantmentLevel(Enchantments.EFFICIENCY, mc.player.inventory.getStackInSlot(tool));
                final float destroySpeed = mc.player.inventory.getStackInSlot(tool).getDestroySpeed(block.getDefaultState());
                float damage = digSpeed + destroySpeed;

                if (damage > maxSpeed) {
                    bestBlock = i;
                    bestTool = tool;
                    maxSpeed = damage;
                }
            }
        }

        return new MineSlots(bestBlock, bestTool, maxSpeed);
    }

    public PlaceData calculate(HelperPlace placeHelper,
                               PlaceData placeData,
                               List<EntityPlayer> friends,
                               List<EntityPlayer> players,
                               float minDamage) {
        PlaceData newData = new PlaceData(minDamage);
        newData.setTarget(placeData.getTarget());
        for (PositionData data : placeData.getLiquid()) {
            if (placeHelper.validate(placeData, data, friends) != null) {
                placeHelper.calcPositionData(newData, data, players);
            }
        }

        return newData;
    }


    private static int getTool(final Block pos) {
        int index = -1;
        float CurrentFastest = 1.0f;
        for (int i = 0; i < 9; ++i) {
            final ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack != ItemStack.EMPTY) {
                final float digSpeed = EnchantmentHelper.getEnchantmentLevel(Enchantments.EFFICIENCY, stack);
                final float destroySpeed = stack.getDestroySpeed(pos.getDefaultState());

                if (pos instanceof BlockAir) return 0;
                if (digSpeed + destroySpeed > CurrentFastest) {
                    CurrentFastest = digSpeed + destroySpeed;
                    index = i;
                }
            }
        }
        return index;
    }

    public EnumFacing getAbsorbFacing(BlockPos pos,
                                      List<Entity> entities,
                                      IBlockAccess access,
                                      double placeRange) {
        for (EnumFacing facing : EnumFacing.values()) {
            if (facing == EnumFacing.DOWN) {
                continue;
            }

            BlockPos offset = pos.offset(facing);
            if (BlockUtils.getDistanceSq(offset) >= MathUtil.square(placeRange)) {
                continue;
            }

            if (access.getBlockState(offset).getMaterial().isReplaceable()) {
                boolean found = false;
                AxisAlignedBB bb = new AxisAlignedBB(offset);
                for (Entity entity : entities) {
                    if (entity == null
                            || EntityUtil.isDead(entity)
                            || !entity.preventEntitySpawning) {
                        continue;
                    }

                    if (module.bbBlockingHelper.blocksBlock(bb, entity)) {
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    return facing;
                }
            }
        }

        return null;
    }


}