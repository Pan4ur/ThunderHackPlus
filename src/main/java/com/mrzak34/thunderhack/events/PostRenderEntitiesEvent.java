package com.mrzak34.thunderhack.events;

import net.minecraftforge.fml.common.eventhandler.Event;

public class PostRenderEntitiesEvent extends Event {
    private final float partialTicks;
    private final int pass;

    public PostRenderEntitiesEvent(float partialTicks, int pass) {
        this.partialTicks = partialTicks;
        this.pass = pass;
    }

    public float getPartialTicks() {
        return partialTicks;
    }

    public int getPass() {
        return pass;
    }
}