package com.mrzak34.thunderhack.mixin.mixins;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.modules.funnygame.AutoCaptcha;
import net.minecraft.client.gui.MapItemRenderer;
import net.minecraft.world.storage.MapData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MapItemRenderer.class)
public class MixinMapItemRenderer {
    @Inject(method = "renderMap", at = @At(value = "HEAD"))
    public void renderMap(MapData mapdataIn, boolean noOverlayRendering, CallbackInfo ci) {
        Thunderhack.moduleManager.getModuleByClass(AutoCaptcha.class).currentmap = mapdataIn;
    }
}