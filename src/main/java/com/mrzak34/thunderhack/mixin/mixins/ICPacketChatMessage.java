package com.mrzak34.thunderhack.mixin.mixins;


import net.minecraft.network.play.client.CPacketChatMessage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CPacketChatMessage.class)
public interface ICPacketChatMessage {

    @Accessor("message")
    String getMessage();

    @Accessor("message")
    void setMessage(String msg);
}
