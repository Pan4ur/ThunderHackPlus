package com.mrzak34.thunderhack.mixin.mixins;

import net.minecraft.network.play.server.SPacketEntityVelocity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SPacketEntityVelocity.class)
public interface ISPacketEntityVelocity {
    @Accessor(value = "motionX")
    int getMotionX();

    @Accessor(value = "motionY")
    int getMotionY();

    @Accessor(value = "motionZ")
    int getMotionZ();

    @Accessor(value = "motionX")
    void setMotionX(int x);

    @Accessor(value = "motionY")
    void setMotionY(int y);

    @Accessor(value = "motionZ")
    void setMotionZ(int z);
}
