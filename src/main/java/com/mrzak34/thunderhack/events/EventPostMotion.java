package com.mrzak34.thunderhack.events;

import com.mrzak34.thunderhack.util.phobos.SafeRunnable;
import net.minecraftforge.fml.common.eventhandler.Event;

import java.util.ArrayDeque;
import java.util.Deque;

public class EventPostMotion extends Event {
    public EventPostMotion() {

    }

    private final Deque<Runnable> postEvents = new ArrayDeque<>();


    public void addPostEvent(SafeRunnable runnable)
    {
        postEvents.add(runnable);
    }


    public Deque<Runnable> getPostEvents()
    {
        return postEvents;
    }
}
