package com.mrzak34.thunderhack.mixin.mixins;


import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Minecraft.class)
public interface IMinecraft {
    @Accessor(value = "rightClickDelayTimer")
    void setRightClickDelayTimer(int rightClickDelayTimer);

    @Accessor(value = "rightClickDelayTimer")
    int getRightClickDelayTimer();
}