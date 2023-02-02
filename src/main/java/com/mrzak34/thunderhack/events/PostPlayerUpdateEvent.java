package com.mrzak34.thunderhack.events;

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class PostPlayerUpdateEvent extends Event {
    private int iterations;

    public void setIterations(int in) {
        iterations = in;
    }

    public int getIterations() {
        return iterations;
    }
}
