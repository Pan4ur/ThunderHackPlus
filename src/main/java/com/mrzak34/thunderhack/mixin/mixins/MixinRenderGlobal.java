package com.mrzak34.thunderhack.mixin.mixins;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.event.events.PostRenderEntitiesEvent;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.entity.Entity;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderGlobal.class)
public abstract class MixinRenderGlobal
{
    @Inject(
            method = "renderEntities",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/BlockPos$PooledMutableBlockPos;release()V",
                    shift = At.Shift.BEFORE))
    private void renderEntitiesHook(Entity renderViewEntity,
                                    ICamera camera,
                                    float partialTicks,
                                    CallbackInfo ci)
    {
        MinecraftForge.EVENT_BUS.post(new PostRenderEntitiesEvent(partialTicks, 0));
    }

}