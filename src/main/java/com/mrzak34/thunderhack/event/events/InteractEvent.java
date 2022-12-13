package com.mrzak34.thunderhack.event.events;

import com.mrzak34.thunderhack.event.EventStage;

public class InteractEvent extends EventStage {

    private boolean interacting;

    public InteractEvent(boolean interacting) {
        this.interacting = interacting;
    }

    public boolean isInteracting() {
        return interacting;
    }

    public void setInteracting(boolean interacting) {
        this.interacting = interacting;
    }
}
