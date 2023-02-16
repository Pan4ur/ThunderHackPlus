package com.mrzak34.thunderhack.mixin.mixins;

import net.minecraft.client.entity.EntityPlayerSP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(EntityPlayerSP.class)
public interface IEntityPlayerSP {

    @Accessor(value = "serverSneakState")
    void setServerSneakState(boolean serverSneakState);

    @Accessor(value = "serverSneakState")
    boolean  getServerSneakState();

    @Accessor(value = "serverSprintState")
    void setServerSprintState(boolean serverSprintState);

    @Accessor(value = "serverSprintState")
    boolean getServerSprintState();

    @Accessor(value = "prevOnGround")
    void setPrevOnGround(boolean prevOnGround);

    @Accessor(value = "prevOnGround")
    boolean getPrevOnGround();

    @Accessor(value = "autoJumpEnabled")
    void setAutoJumpEnabled(boolean autoJumpEnabled);

    @Accessor(value = "lastReportedPosX")
    void setLastReportedPosX(double lastReportedPosX);

    @Accessor(value = "lastReportedPosX")
    double getLastReportedPosX();

    @Accessor(value = "lastReportedPosY")
    void setLastReportedPosY(double lastReportedPosY);

    @Accessor(value = "lastReportedPosY")
    double getLastReportedPosY();

    @Accessor(value = "lastReportedPosZ")
    void setLastReportedPosZ(double lastReportedPosZ);

    @Accessor(value = "lastReportedPosZ")
    double getLastReportedPosZ();

    @Accessor(value = "lastReportedYaw")
    void setLastReportedYaw(float lastReportedYaw);

    @Accessor(value = "lastReportedYaw")
    float getLastReportedYaw();

    @Accessor(value = "lastReportedPitch")
    void setLastReportedPitch(float lastReportedPitch);

    @Accessor(value = "lastReportedPitch")
    float getLastReportedPitch();

    @Accessor(value = "positionUpdateTicks")
    void setPositionUpdateTicks(int positionUpdateTicks);

    @Accessor(value = "positionUpdateTicks")
    int getPositionUpdateTicks();

    @Invoker(value = "onUpdateWalkingPlayer")
    void invokeOnUpdateWalkingPlayer();
}