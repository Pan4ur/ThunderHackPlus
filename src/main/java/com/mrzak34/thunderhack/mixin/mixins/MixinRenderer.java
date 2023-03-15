package com.mrzak34.thunderhack.mixin.mixins;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = {Render.class})
abstract class MixinRenderer {
    @Shadow
    protected boolean renderOutlines;
    @Shadow
    @Final
    protected RenderManager renderManager;

    MixinRenderer() {
    }

    @Shadow
    protected abstract boolean bindEntityTexture(Entity var1);

    @Shadow
    protected abstract int getTeamColor(Entity var1);
}