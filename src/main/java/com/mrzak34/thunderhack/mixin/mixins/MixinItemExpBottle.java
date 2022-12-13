package com.mrzak34.thunderhack.mixin.mixins;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.modules.misc.MiddleClick;
import net.minecraft.item.ItemExpBottle;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ItemExpBottle.class)
public abstract class MixinItemExpBottle
{


    @Redirect(
            method = "onItemRightClick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;shrink(I)V"))
    public void onItemRightClickHook(ItemStack stack, int quantity)
    {
        if (!Thunderhack.moduleManager.getModuleByClass(MiddleClick.class).isOn() &&Thunderhack.moduleManager.getModuleByClass(MiddleClick.class).cancelShrink() )
        {
            stack.shrink(quantity);
        }
    }

}