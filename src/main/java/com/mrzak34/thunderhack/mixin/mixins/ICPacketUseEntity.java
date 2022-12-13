package com.mrzak34.thunderhack.mixin.mixins;

import net.minecraft.network.play.client.CPacketUseEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CPacketUseEntity.class)
public interface ICPacketUseEntity {
    @Accessor(value = "entityId")
    void setEntityId(int entityId);

    @Accessor(value = "action")
    void setAction(CPacketUseEntity.Action action);

}