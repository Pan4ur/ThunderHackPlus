package com.mrzak34.thunderhack.event.events;

import com.mrzak34.thunderhack.event.EventStage;
import net.minecraft.entity.Entity;

public class EntityAddedEvent extends EventStage {
    public Entity entity;

    public EntityAddedEvent(Entity entity) {
        this.entity = entity;
    }
}
