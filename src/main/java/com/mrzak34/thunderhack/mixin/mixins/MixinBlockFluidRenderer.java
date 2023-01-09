package com.mrzak34.thunderhack.mixin.mixins;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.modules.render.XRay;
import org.spongepowered.asm.mixin.*;
import net.minecraft.world.*;
import net.minecraft.block.state.*;
import net.minecraft.util.math.*;
import net.minecraft.client.renderer.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import org.spongepowered.asm.mixin.injection.*;

@Mixin({ BlockFluidRenderer.class })
public class MixinBlockFluidRenderer
{
    @Inject(method = { "renderFluid" },  at = { @At("HEAD") },  cancellable = true)
    public void renderFluidHook(final IBlockAccess blockAccess,  final IBlockState blockState,  final BlockPos blockPos,  final BufferBuilder bufferBuilder,  final CallbackInfoReturnable<Boolean> info) {
        if (Thunderhack.moduleManager.getModuleByClass(XRay.class).isOn() && !Thunderhack.moduleManager.getModuleByClass(XRay.class).shouldRender(blockState.getBlock())) {
            info.setReturnValue(false);
            info.cancel();
        }
    }
}