package com.mrzak34.thunderhack.event.events;

import com.mrzak34.thunderhack.event.EventStage;
import net.minecraft.entity.Entity;
public class EntityRemovedEvent extends EventStage {
    public Entity entity;

    public EntityRemovedEvent(Entity entity) {
        this.entity = entity;
    }
}
