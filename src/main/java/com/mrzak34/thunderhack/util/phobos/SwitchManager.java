package com.mrzak34.thunderhack.util.phobos;

import com.mrzak34.thunderhack.events.PacketEvent;
import com.mrzak34.thunderhack.modules.Feature;
import com.mrzak34.thunderhack.util.Timer;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.server.SPacketHeldItemChange;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Some servers block certain packets, especially
 * CPacketUseEntity for around 10 ticks (~ 500 ms) after you
 * switched your mainhand slot. If you attack during this time you
 * might flag the anticheat. This class manages the time that
 * passed after the last switch.
 */
public class SwitchManager extends Feature
{
    public void unload() {
        MinecraftForge.EVENT_BUS.unregister(this);
    }
    public void init() {
        MinecraftForge.EVENT_BUS.register(this);
    }
    private final Timer timer = new Timer();
    private volatile int last_slot;

    public SwitchManager()
    {

    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send e){
        if(e.getPacket() instanceof CPacketHeldItemChange){
            timer.reset();
            last_slot = ((CPacketHeldItemChange)e.getPacket()).getSlotId();
        }
    }
    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive e){
        if(fullNullCheck()) return;
        if(e.getPacket() instanceof SPacketHeldItemChange){
            last_slot = ((SPacketHeldItemChange) e.getPacket()).getHeldItemHotbarIndex();
        }
    }


    /**
     * @return the time in ms that passed since the last
     *         {@link CPacketHeldItemChange} has been send.
     */
    public long getLastSwitch()
    {
        return timer.getTime();
    }

    /**
     * @return the last slot reported to the server.
     */
    public int getSlot()
    {
        return last_slot;
    }

}