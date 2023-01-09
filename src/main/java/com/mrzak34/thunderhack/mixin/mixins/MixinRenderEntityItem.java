package com.mrzak34.thunderhack.mixin.mixins;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.modules.misc.ItemPhysics;
import net.minecraft.client.renderer.entity.RenderEntityItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={RenderEntityItem.class})
public abstract class MixinRenderEntityItem extends MixinRenderer {
    @Inject(method={"doRender"}, at={@At(value="HEAD")}, cancellable=true)
    private void doRender(EntityItem entityItem, double d, double d2, double d3, float f, float f2, CallbackInfo callbackInfo) {
        if (Thunderhack.moduleManager.getModuleByClass(ItemPhysics.class).isEnabled()) {
            Thunderhack.moduleManager.getModuleByClass(ItemPhysics.class).Method2279((Entity)entityItem, d, d2, d3);
            callbackInfo.cancel();
        }
    }
}