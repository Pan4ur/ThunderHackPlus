package com.mrzak34.thunderhack.events;

import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class AttackEvent extends Event {
        public AttackEvent(Entity attack) {
            this.entity = attack;
        }
        Entity entity;

        public Entity getEntity() {
            return entity;
        }
}
