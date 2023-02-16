package com.mrzak34.thunderhack.mixin.mixins;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.modules.render.XRay;
import net.minecraft.block.state.*;
import net.minecraft.world.*;
import org.spongepowered.asm.mixin.*;
import net.minecraft.util.math.*;
import net.minecraft.block.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ Block.class })
public abstract class MixinBlock
{
    @Shadow
    @Deprecated
    public abstract float getBlockHardness(final IBlockState p0,  final World p1,  final BlockPos p2);


    @Inject(method = { "isFullCube" },  at = { @At("HEAD") },  cancellable = true)
    public void isFullCubeHook(final IBlockState blockState,  final CallbackInfoReturnable<Boolean> info) {
    try{
            if (Thunderhack.moduleManager.getModuleByClass(XRay.class).isOn() && Thunderhack.moduleManager.getModuleByClass(XRay.class).wh.getValue() ) {
                info.setReturnValue(Thunderhack.moduleManager.getModuleByClass(XRay.class).shouldRender(Block.class.cast(this)));
            }
    }
        catch (Exception ignored) {}
    }



/*
    @Inject(method = "getPlayerRelativeBlockHardness", at = @At("HEAD"), cancellable = true)
    public void getPlayerRelativeBlockHardness(IBlockState state, EntityPlayer player, World worldIn, BlockPos pos, CallbackInfoReturnable<Float> ci) {
        AutoTool autoTool = Thunderhack.moduleManager.getModuleByClass(AutoTool.class);
        if(autoTool.isEnabled()) {
            float f = state.getBlockHardness(worldIn, pos);
            if (f < 0.0F) {
                ci.setReturnValue(0.0f);
            } else {
                ci.setReturnValue(!player.canHarvestBlock(state) ? getDigSpeed(state, (autoTool.isEnabled() && autoTool.silent.getValue()) ? player.inventory.getStackInSlot(autoTool.itemIndex) : player.getHeldItemMainhand()) / f / 100.0F : getDigSpeed(state, (autoTool.isEnabled() && autoTool.silent.getValue()) ? player.inventory.getStackInSlot(autoTool.itemIndex) : player.getHeldItemMainhand()) / f / 30.0F);
            }
        }
    }





    public float getDigSpeed(IBlockState state, ItemStack is){
        final float digSpeed = EnchantmentHelper.getEnchantmentLevel(Enchantments.EFFICIENCY, is);
        float f = is.getDestroySpeed(state);
        if (digSpeed > 0 && !is.isEmpty()) {
            f += (float) (digSpeed * digSpeed + 1);
        }
        return f;
    }
 */

}