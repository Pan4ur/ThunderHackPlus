package com.mrzak34.thunderhack.mixin.mixins;

import net.minecraft.client.entity.EntityPlayerSP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(EntityPlayerSP.class)
public interface IEntityPlayerSP {

    @Accessor(value = "serverSneakState")
    boolean getServerSneakState();

    @Accessor(value = "serverSneakState")
    void setServerSneakState(boolean serverSneakState);

    @Accessor(value = "serverSprintState")
    boolean getServerSprintState();

    @Accessor(value = "serverSprintState")
    void setServerSprintState(boolean serverSprintState);

    @Accessor(value = "wasFallFlying")
    boolean wasFallFlying();

    @Accessor(value = "prevOnGround")
    boolean getPrevOnGround();

    @Accessor(value = "prevOnGround")
    void setPrevOnGround(boolean prevOnGround);

    @Accessor(value = "autoJumpEnabled")
    void setAutoJumpEnabled(boolean autoJumpEnabled);

    @Accessor(value = "lastReportedPosX")
    double getLastReportedPosX();

    @Accessor(value = "lastReportedPosX")
    void setLastReportedPosX(double lastReportedPosX);

    @Accessor(value = "lastReportedPosY")
    double getLastReportedPosY();

    @Accessor(value = "lastReportedPosY")
    void setLastReportedPosY(double lastReportedPosY);

    @Accessor(value = "lastReportedPosZ")
    double getLastReportedPosZ();

    @Accessor(value = "lastReportedPosZ")
    void setLastReportedPosZ(double lastReportedPosZ);

    @Accessor(value = "lastReportedYaw")
    float getLastReportedYaw();

    @Accessor(value = "lastReportedYaw")
    void setLastReportedYaw(float lastReportedYaw);

    @Accessor(value = "lastReportedPitch")
    float getLastReportedPitch();

    @Accessor(value = "lastReportedPitch")
    void setLastReportedPitch(float lastReportedPitch);

    @Accessor(value = "positionUpdateTicks")
    int getPositionUpdateTicks();

    @Accessor(value = "positionUpdateTicks")
    void setPositionUpdateTicks(int positionUpdateTicks);

    @Invoker(value = "onUpdateWalkingPlayer")
    void invokeOnUpdateWalkingPlayer();

}