package com.mrzak34.thunderhack.events;

import net.minecraft.block.Block;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class EventBlockInteract extends Event {
    public Block block;

    public EventBlockInteract(Block block) {
        this.block = block;
    }

    public Block getBlock() {
        return block;
    }
}
