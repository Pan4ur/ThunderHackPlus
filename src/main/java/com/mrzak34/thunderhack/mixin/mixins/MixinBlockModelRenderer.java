package com.mrzak34.thunderhack.mixin.mixins;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.modules.render.XRay;
import org.spongepowered.asm.mixin.*;
import net.minecraft.world.*;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.block.state.*;
import net.minecraft.util.math.*;
import net.minecraft.client.renderer.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import org.spongepowered.asm.mixin.injection.*;

@Mixin({ BlockModelRenderer.class })
public class MixinBlockModelRenderer
{
    @Inject(method = { "renderModel(Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/client/renderer/block/model/IBakedModel;Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/client/renderer/BufferBuilder;Z)Z" },  at = { @At("HEAD") },  cancellable = true)
    private void renderModelHook(final IBlockAccess blockAccess,  final IBakedModel bakedModel,  final IBlockState blockState,  final BlockPos blockPos,  final BufferBuilder bufferBuilder,  final boolean b,  final CallbackInfoReturnable<Boolean> info) {
        try {
            if (Thunderhack.moduleManager.getModuleByClass(XRay.class).isOn() && !Thunderhack.moduleManager.getModuleByClass(XRay.class).shouldRender(blockState.getBlock())) {
                info.setReturnValue(false);
                info.cancel();
            }
        }
        catch (Exception ex) {}
    }

    @ModifyArg(method = { "renderModel(Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/client/renderer/block/model/IBakedModel;Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/client/renderer/BufferBuilder;ZJ)Z" },  at = @At(value = "INVOKE",  target = "Lnet/minecraft/client/renderer/BlockModelRenderer;renderModelFlat(Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/client/renderer/block/model/IBakedModel;Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/client/renderer/BufferBuilder;ZJ)Z"))
    private boolean renderModelFlatHook(final boolean input) {
        try {
            if (Thunderhack.moduleManager.getModuleByClass(XRay.class).isOn()) {
                return false;
            }

        }
        catch (Exception ex) {}
        return input;
    }

    @ModifyArg(method = { "renderModel(Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/client/renderer/block/model/IBakedModel;Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/client/renderer/BufferBuilder;ZJ)Z" },  at = @At(value = "INVOKE",  target = "Lnet/minecraft/client/renderer/BlockModelRenderer;renderModelSmooth(Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/client/renderer/block/model/IBakedModel;Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/client/renderer/BufferBuilder;ZJ)Z"))
    private boolean renderModelSmoothHook(final boolean input) {
        try {
            if (Thunderhack.moduleManager.getModuleByClass(XRay.class).isOn()) {
                return false;
            }

        }
        catch (Exception ex) {}
        return input;
    }
}