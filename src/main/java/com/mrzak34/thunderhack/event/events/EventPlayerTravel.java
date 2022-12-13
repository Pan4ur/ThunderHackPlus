package com.mrzak34.thunderhack.event.events;

import com.mrzak34.thunderhack.event.EventStage;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

@Cancelable
public class EventPlayerTravel extends EventStage {
    public float Strafe;
    public float Vertical;
    public float Forward;

    public EventPlayerTravel(final float p_Strafe, final float p_Vertical, final float p_Forward) {
        this.Strafe = p_Strafe;
        this.Vertical = p_Vertical;
        this.Forward = p_Forward;
    }
}
