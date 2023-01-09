package com.mrzak34.thunderhack.event.events;

import com.mrzak34.thunderhack.event.EventStage;

public class EventSprint extends EventStage {
    private boolean sprintState;

    public EventSprint(boolean sprintState) {
        this.sprintState = sprintState;
    }

    public void setSprintState(boolean sprintState) {
        this.sprintState = sprintState;
    }

    public boolean getSprintState() {
        return this.sprintState;
    }
}
