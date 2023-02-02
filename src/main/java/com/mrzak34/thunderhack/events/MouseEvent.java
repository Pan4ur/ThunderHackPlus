package com.mrzak34.thunderhack.events;

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class MouseEvent extends Event {
    private final int button;
    private final boolean state;

    public MouseEvent(int button, boolean state) {
        this.button = button;
        this.state = state;
    }

    public boolean getState() {
        return state;
    }

    public int getButton() {
        return button;
    }

}