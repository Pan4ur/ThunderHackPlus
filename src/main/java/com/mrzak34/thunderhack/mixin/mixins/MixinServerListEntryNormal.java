package com.mrzak34.thunderhack.mixin.mixins;

import com.mrzak34.thunderhack.modules.client.MultiConnect;
import com.mrzak34.thunderhack.util.Util;
import net.minecraft.client.gui.ServerListEntryNormal;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.awt.*;


@Mixin(ServerListEntryNormal.class)
public abstract class MixinServerListEntryNormal {

    @Inject(method = "mousePressed", at = @At("HEAD"))
    public void Z(int slotIndex, int mouseX, int mouseY, int mouseEvent, int relativeX, int relativeY, CallbackInfoReturnable<Boolean> cir) {

        if(MultiConnect.getInstance().isEnabled()) {
            if (Mouse.isButtonDown(1)) {
                MultiConnect.getInstance().serverData.add(slotIndex);
                System.out.println("THUNDER HACK добавлен слот " + slotIndex);
            }
        }
    }

    @Inject(method = "drawEntry", at = @At("TAIL"))
    public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected, float partialTicks, CallbackInfo ci) {
        if(MultiConnect.getInstance().isEnabled()) {
            if (MultiConnect.getInstance().serverData.contains(slotIndex)) {
                Util.fr.drawString("SELECTED",x - 45,y + 14,-1);
            }
        }
    }


}