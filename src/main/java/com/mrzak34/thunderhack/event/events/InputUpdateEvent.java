package com.mrzak34.thunderhack.event.events;

import com.mrzak34.thunderhack.event.EventStage;
import net.minecraft.util.MovementInput;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

@Cancelable
public class InputUpdateEvent extends EventStage {
    MovementInput movementInput;

    public InputUpdateEvent(MovementInput movementInput) {
        this.movementInput = movementInput;
    }

    public MovementInput getMovementInput() {
        return movementInput;
    }
}
