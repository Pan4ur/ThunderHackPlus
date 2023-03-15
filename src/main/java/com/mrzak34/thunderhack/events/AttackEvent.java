package com.mrzak34.thunderhack.events;

import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class AttackEvent extends Event {
    short stage;
    Entity entity;

    public AttackEvent(Entity attack, short stage) {
        this.entity = attack;
        this.stage = stage;
    }

    public Entity getEntity() {
        return entity;
    }

    public short getStage() {
        return stage;
    }
}
