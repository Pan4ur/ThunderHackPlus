package com.mrzak34.thunderhack.mixin.mixins;

import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ItemRenderer.class)
public interface IItemRenderer {
    @Accessor(value="equippedProgressMainHand")
    void setEquippedProgressMainHand(float var1);

    @Accessor(value="equippedProgressMainHand")
    float getEquippedProgressMainHand();

    @Accessor(value="itemStackMainHand")
    void setItemStackMainHand(ItemStack var1);
}
