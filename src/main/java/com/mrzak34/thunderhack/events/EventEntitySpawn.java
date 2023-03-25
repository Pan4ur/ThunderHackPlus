package com.mrzak34.thunderhack.events;

import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.eventhandler.Event;


public class EventEntitySpawn extends Event {
    private final Entity entity;
    public EventEntitySpawn(Entity entity) {
        this.entity = entity;
    }

    public Entity getEntity() {
        return this.entity;
    }
}
