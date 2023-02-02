package com.mrzak34.thunderhack.events;

import net.minecraftforge.fml.common.eventhandler.Event;

public class KeyEvent
        extends Event {
    private final int key;

    public KeyEvent(int key) {
        this.key = key;
    }

    public int getKey() {
        return this.key;
    }
}

