package com.mrzak34.thunderhack.util.phobos;

import com.mrzak34.thunderhack.modules.combat.AutoCrystal;
import net.minecraft.network.play.server.SPacketSoundEffect;

public final class ListenerSound extends SoundObserver
{
    private final AutoCrystal module;

    public ListenerSound(AutoCrystal module)
    {
        super(module.soundRemove::getValue);
        this.module = module;
    }

    @Override
    public void onChange(SPacketSoundEffect value)
    {
        // TODO: check that sound is in range!
        if (module.soundThread.getValue())
        {
            module.threadHelper.startThread();
        }
    }

    @Override
    public boolean shouldBeNotified()
    {
        return true;
    }

}