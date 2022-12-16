package com.mrzak34.thunderhack.event.events;

import com.mrzak34.thunderhack.event.EventStage;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

import static com.mrzak34.thunderhack.util.ItemUtil.mc;

@Cancelable
public class AttackEvent extends EventStage {


        public AttackEvent(Entity attack, int stage) {
            super(stage);
            this.entity = attack;
        }

        Entity entity;


        public Entity getEntity() {
            return entity;
        }


}
