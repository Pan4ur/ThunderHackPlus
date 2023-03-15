package com.mrzak34.thunderhack.events;


import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.Event;

public class EventEntityMove extends Event {
    private final Entity ctx;
    private final Vec3d from;

    public EventEntityMove(Entity ctx, Vec3d from) {
        this.ctx = ctx;
        this.from = from;
    }

    public Vec3d from() {
        return this.from;
    }

    public Entity ctx() {
        return this.ctx;
    }
}