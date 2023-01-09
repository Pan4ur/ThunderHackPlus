package com.mrzak34.thunderhack.event.events;

import com.mrzak34.thunderhack.event.EventStage;

public class EventPostMove extends EventStage {
    private double horizontalMove;

    public EventPostMove(double horizontalMove) {
        this.horizontalMove = horizontalMove;
    }

    public double getHorizontalMove() {
        return this.horizontalMove;
    }
}
