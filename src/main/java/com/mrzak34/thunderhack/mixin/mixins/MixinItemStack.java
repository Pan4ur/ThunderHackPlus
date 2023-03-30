package com.mrzak34.thunderhack.mixin.mixins;

import com.mrzak34.thunderhack.modules.player.GAppleCooldown;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAppleGold;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = {ItemStack.class})
public abstract class MixinItemStack  {

    @Shadow
    public abstract Item getItem();

    @Inject(method = {"onItemUseFinish"}, at = {@At(value = "HEAD")})
    public void onItemUseFinish(World worldIn, EntityLivingBase entityLiving, CallbackInfoReturnable<ItemStack> cir) {
        if (getItem() instanceof ItemAppleGold) {
            GAppleCooldown.lastConsumeTime = getItem().hasEffect((ItemStack)(Object) this) ? System.currentTimeMillis() + 5000 : System.currentTimeMillis();
        }
    }
}
