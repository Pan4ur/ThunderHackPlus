package com.mrzak34.thunderhack.events;

import net.minecraftforge.fml.common.eventhandler.Event;

public class EventPostMove extends Event {
    private double horizontalMove;

    public EventPostMove(double horizontalMove) {
        this.horizontalMove = horizontalMove;
    }

    public double getHorizontalMove() {
        return this.horizontalMove;
    }
}
