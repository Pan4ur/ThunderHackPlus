package com.mrzak34.thunderhack.mixin.mixins;

import com.mrzak34.thunderhack.mixin.ducks.ISPacketSpawnObject;
import net.minecraft.network.play.server.SPacketSpawnObject;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(SPacketSpawnObject.class)
public abstract class MixinSPacketSpawnObject implements ISPacketSpawnObject {
    @Unique
    private boolean attacked;

    @Override
    public boolean isAttacked() {
        return attacked;
    }

    @Override
    public void setAttacked(boolean attacked) {
        this.attacked = attacked;
    }

}