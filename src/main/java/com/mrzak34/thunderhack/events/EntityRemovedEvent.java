package com.mrzak34.thunderhack.events;

import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.eventhandler.Event;

public class EntityRemovedEvent extends Event {
    public Entity entity;

    public EntityRemovedEvent(Entity entity) {
        this.entity = entity;
    }
}
