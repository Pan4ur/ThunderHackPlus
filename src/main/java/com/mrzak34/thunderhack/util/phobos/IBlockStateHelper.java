package com.mrzak34.thunderhack.util.phobos;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public interface IBlockStateHelper extends IBlockAccess {
    void addBlockState(BlockPos pos, IBlockState state);
}