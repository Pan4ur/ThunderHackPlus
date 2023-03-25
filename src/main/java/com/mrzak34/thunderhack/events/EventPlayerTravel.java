package com.mrzak34.thunderhack.events;

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class EventPlayerTravel extends Event {
    public float Strafe;

    public float getStrafe() {
        return Strafe;
    }

    public float getVertical() {
        return Vertical;
    }

    public float getForward() {
        return Forward;
    }

    public float Vertical;
    public float Forward;

    public EventPlayerTravel(final float p_Strafe, final float p_Vertical, final float p_Forward) {
        this.Strafe = p_Strafe;
        this.Vertical = p_Vertical;
        this.Forward = p_Forward;
    }
}
