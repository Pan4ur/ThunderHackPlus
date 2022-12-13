package com.mrzak34.thunderhack.event.events;

import com.mrzak34.thunderhack.event.EventStage;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

@Cancelable
public class KeyboardEvent extends EventStage {
    private final boolean eventState;
    private final char character;
    private final int key;

    public KeyboardEvent(boolean eventState, int key, char character)
    {
        this.eventState = eventState;
        this.key = key;
        this.character = character;
    }

    public boolean getEventState()
    {
        return eventState;
    }

    public int getKey()
    {
        return key;
    }

    public char getCharacter()
    {
        return character;
    }

    public static class Post
    {
        //Will be send after all KeyBoardEvents have been fired.
    }
}
