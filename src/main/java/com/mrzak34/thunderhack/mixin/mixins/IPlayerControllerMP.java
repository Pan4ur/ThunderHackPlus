package com.mrzak34.thunderhack.mixin.mixins;

import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PlayerControllerMP.class)
public interface IPlayerControllerMP {
    @Accessor(value = "curBlockDamageMP")
    float getCurBlockDamageMP();

    @Accessor(value = "curBlockDamageMP")
    void setCurBlockDamageMP(float a);

    @Accessor(value = "currentBlock")
    BlockPos getCurrentBlock();
}