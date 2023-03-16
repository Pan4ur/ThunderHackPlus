package com.mrzak34.thunderhack.mixin.mixins;

import net.minecraft.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Block.class)
public interface IBlock {
    @Accessor("blockResistance")
    float getBlockResistance();

}