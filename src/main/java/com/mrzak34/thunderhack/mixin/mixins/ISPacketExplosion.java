package com.mrzak34.thunderhack.mixin.mixins;

import net.minecraft.network.play.server.SPacketExplosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SPacketExplosion.class)
public interface ISPacketExplosion {
    @Accessor(value = "motionX")
    float getMotionX();

    @Accessor(value = "motionY")
    float getMotionY();

    @Accessor(value = "motionZ")
    float getMotionZ();

    @Accessor(value = "motionX")
    void setMotionX(float x);

    @Accessor(value = "motionY")
    void setMotionY(float y);

    @Accessor(value = "motionZ")
    void setMotionZ(float z);

}
