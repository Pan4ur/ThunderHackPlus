package com.mrzak34.thunderhack.util.phobos;

import net.minecraft.entity.Entity;

public class CustomEntityTime extends EntityTime
{
    private final long customTime;

    public CustomEntityTime(Entity entity, long customTime)
    {
        super(entity);
        this.customTime = customTime;
    }

    @Override
    public boolean passed(long ms)
    {
        return System.currentTimeMillis() - time > customTime;
    }

}