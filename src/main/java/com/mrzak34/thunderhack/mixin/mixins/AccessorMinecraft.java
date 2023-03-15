package com.mrzak34.thunderhack.mixin.mixins;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Minecraft.class)
public interface AccessorMinecraft {

    @Accessor(value = "leftClickCounter")
    void setLeftClickCounter(int val);

    @Invoker(value = "sendClickBlockToController")
    void invokeSendClickBlockToController(boolean val);

}