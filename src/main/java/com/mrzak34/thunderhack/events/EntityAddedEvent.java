package com.mrzak34.thunderhack.events;

import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.eventhandler.Event;

public class EntityAddedEvent extends Event {
    public Entity entity;

    public EntityAddedEvent(Entity entity) {
        this.entity = entity;
    }
}
