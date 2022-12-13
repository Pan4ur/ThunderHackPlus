package com.mrzak34.thunderhack.event.events;

import com.mrzak34.thunderhack.event.EventStage;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

@Cancelable
public class ClickMiddleEvent extends EventStage
{
    private boolean moduleCancelled;

    public void setModuleCancelled(boolean cancelled)
    {
        this.moduleCancelled = cancelled;
    }

    public boolean isModuleCancelled()
    {
        return moduleCancelled;
    }

}