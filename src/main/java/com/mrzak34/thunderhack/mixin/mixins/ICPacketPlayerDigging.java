package com.mrzak34.thunderhack.mixin.mixins;

public interface ICPacketPlayerDigging
{
    void setClientSideBreaking(boolean breaking);

    boolean isClientSideBreaking();

}