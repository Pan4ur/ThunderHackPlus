package com.mrzak34.thunderhack.mixin.mixins;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.modules.render.XRay;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BlockFluidRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({BlockFluidRenderer.class})
public class MixinBlockFluidRenderer {
    @Inject(method = {"renderFluid"}, at = {@At("HEAD")}, cancellable = true)
    public void renderFluidHook(final IBlockAccess blockAccess, final IBlockState blockState, final BlockPos blockPos, final BufferBuilder bufferBuilder, final CallbackInfoReturnable<Boolean> info) {
        if (Thunderhack.moduleManager.getModuleByClass(XRay.class).isOn() && !Thunderhack.moduleManager.getModuleByClass(XRay.class).shouldRender(blockState.getBlock())) {
            info.setReturnValue(false);
            info.cancel();
        }
    }
}