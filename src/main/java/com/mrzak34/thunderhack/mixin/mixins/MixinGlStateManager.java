package com.mrzak34.thunderhack.mixin.mixins;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.modules.render.NoRender;
import net.minecraft.client.renderer.GlStateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GlStateManager.class)
public class MixinGlStateManager {

    @Inject(method = "enableFog", at = @At("HEAD"), cancellable = true)
    private static void onEnableFog(CallbackInfo info) {
        if (Thunderhack.moduleManager.getModuleByClass(NoRender.class).fog.getValue() && Thunderhack.moduleManager.getModuleByClass(NoRender.class).isOn()) {
            info.cancel();
        }
    }
}