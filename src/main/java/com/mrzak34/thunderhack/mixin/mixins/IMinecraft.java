package com.mrzak34.thunderhack.mixin.mixins;


import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Minecraft.class)
public interface IMinecraft {
    @Accessor(value = "rightClickDelayTimer")
    void setRightClickDelayTimer(int rightClickDelayTimer);

    @Invoker(value = "rightClickMouse")
    void invokeRightClick();

    @Accessor(value = "rightClickDelayTimer")
    int getRightClickDelayTimer();
}