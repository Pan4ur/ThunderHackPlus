package com.mrzak34.thunderhack.mixin.mixins;

import com.mrzak34.thunderhack.events.ConnectToServerEvent;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(value = GuiConnecting.class, priority = 999)
public class MixinGuiConnecting extends MixinGuiScreen {
    @Inject(method = {"connect"}, at = {@At(value = "HEAD")})
    private void  connectHook(String ip, int port, CallbackInfo ci){
        ConnectToServerEvent event = new ConnectToServerEvent(ip);
        MinecraftForge.EVENT_BUS.post(event);
    }
}
