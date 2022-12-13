package com.mrzak34.thunderhack.mixin.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Minecraft.class)
public interface AccessorMinecraft {

    @Accessor("rightClickDelayTimer")
    void setRightClickDelayTimer(int rightClickDelayTimer);

    @Accessor( value = "leftClickCounter" )
    void setLeftClickCounter( int val );

    @Invoker( value = "sendClickBlockToController" )
    void invokeSendClickBlockToController( boolean val );

}