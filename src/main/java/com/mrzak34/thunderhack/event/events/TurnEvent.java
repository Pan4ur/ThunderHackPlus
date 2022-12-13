package com.mrzak34.thunderhack.event.events;

import com.mrzak34.thunderhack.event.EventStage;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

@Cancelable
public class TurnEvent extends EventStage {
    private final float yaw;
    private final float pitch;

    public TurnEvent(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }
}