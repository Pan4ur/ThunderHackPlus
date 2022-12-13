package com.mrzak34.thunderhack.event.events;

import com.mrzak34.thunderhack.event.EventStage;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

import java.awt.*;

@Cancelable
public class ElytraEvent extends EventStage {
    //private static ElytraEvent INSTANCE = new ElytraEvent();

    private Entity entity;

    public ElytraEvent(Entity entity) {
        this.entity = entity;
    }

    public Entity getEntity() {
        return this.entity;
    }
}
