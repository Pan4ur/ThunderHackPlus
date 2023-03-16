package com.mrzak34.thunderhack.mixin.mixins;

import net.minecraft.client.settings.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(KeyBinding.class)
public interface IKeyBinding {

    @Accessor(value = "pressed")
    boolean isPressed();

    @Accessor(value = "pressed")
    void setPressed(boolean value);
}