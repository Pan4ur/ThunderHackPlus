package com.mrzak34.thunderhack.events;

import net.minecraft.item.Item;
import net.minecraftforge.fml.common.eventhandler.Event;


public class EventCooldown extends Event {
    private Item stack;
    private float cooldown;

    public EventCooldown(Item stack) {
        this.stack = stack;
    }

    public float getCooldown() {
        return this.cooldown;
    }

    public void setCooldown(float f) {
        this.cooldown = f;
    }

    public Item getStack() {
        return this.stack;
    }
}
