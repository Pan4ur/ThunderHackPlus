package com.mrzak34.thunderhack.mixin.mixins;

import net.minecraft.block.state.IBlockState;

public interface IBlock {
    void setHarvestLevelNonForge(String toolClass, int level);

    String getHarvestToolNonForge(IBlockState state);

    int getHarvestLevelNonForge(IBlockState state);
}