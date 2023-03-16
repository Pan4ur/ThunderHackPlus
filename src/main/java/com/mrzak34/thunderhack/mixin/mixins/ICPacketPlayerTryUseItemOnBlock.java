package com.mrzak34.thunderhack.mixin.mixins;

import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumHand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CPacketPlayerTryUseItemOnBlock.class)
public interface ICPacketPlayerTryUseItemOnBlock {
    @Accessor(value = "hand")
    void setHand(EnumHand data);
}
