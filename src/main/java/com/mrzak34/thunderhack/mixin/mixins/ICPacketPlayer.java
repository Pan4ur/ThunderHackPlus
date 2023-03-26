package com.mrzak34.thunderhack.mixin.mixins;

import net.minecraft.network.play.client.CPacketPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CPacketPlayer.class)
public interface ICPacketPlayer {
    @Accessor(value = "x")
    void setX(double x);

    @Accessor(value = "y")
    void setY(double y);

    @Accessor(value = "z")
    void setZ(double z);

    @Accessor(value = "yaw")
    void setYaw(float yaw);

    @Accessor(value = "pitch")
    void setPitch(float pitch);

    @Accessor(value = "onGround")
    void setOnGround(boolean onGround);

    @Accessor(value = "moving")
    boolean isMoving();

    @Accessor(value = "moving")
    void setMoving(boolean m);

    @Accessor(value = "rotating")
    boolean isRotating();

}