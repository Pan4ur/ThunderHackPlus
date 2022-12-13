package com.mrzak34.thunderhack.mixin.mixins;

import com.mrzak34.thunderhack.modules.funnygame.KDShop;
import net.minecraft.client.gui.inventory.GuiChest;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiChest.class)
public abstract class MixinGuiChest{

    @Inject(method = {"drawScreen"}, at = {@At(value = "HEAD")}, cancellable = true)
    private void  drawScreenHook(int mouseX, int mouseY, float partialTicks, CallbackInfo ci){
        if(KDShop.cancelRender){
            ci.cancel();
        }
    }

}