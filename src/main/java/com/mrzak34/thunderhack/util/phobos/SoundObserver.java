package com.mrzak34.thunderhack.util.phobos;

import net.minecraft.network.play.server.SPacketSoundEffect;


import java.util.function.BooleanSupplier;

public abstract class SoundObserver implements Observer<SPacketSoundEffect>
{
    private final BooleanSupplier soundRemove;

    public SoundObserver(BooleanSupplier soundRemove)
    {
        this.soundRemove = soundRemove;
    }

    public boolean shouldRemove()
    {
        return soundRemove.getAsBoolean();
    }

    public boolean shouldBeNotified()
    {
        return shouldRemove();
    }

}