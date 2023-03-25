package com.mrzak34.thunderhack.events;

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class EventMoveDirection extends Event {

    private boolean post;

    public EventMoveDirection(boolean post) {
        this.post = post;
    }

    public boolean isPost() {
        return this.post;
    }
}