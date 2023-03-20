package com.mrzak34.thunderhack.util.phobos;

import com.mrzak34.thunderhack.events.EventPostSync;
import com.mrzak34.thunderhack.events.EventSync;
import com.mrzak34.thunderhack.events.NoMotionUpdateEvent;
import com.mrzak34.thunderhack.events.PacketEvent;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Posts a {@link NoMotionUpdateEvent} Event,
 * if onUpdateWalkingPlayer is called,
 * but no CPacketPlayer is sent.
 */
public class NoMotionUpdateService  {
    private boolean awaiting;

    public NoMotionUpdateService() {

    }

    public void init() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void unload() {
        MinecraftForge.EVENT_BUS.unregister(this);
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketPlayer.PositionRotation) {
            setAwaiting(false);
        }
        if (event.getPacket() instanceof CPacketPlayer.Position) {
            setAwaiting(false);
        }
        if (event.getPacket() instanceof CPacketPlayer.Rotation) {
            setAwaiting(false);
        }
        if (event.getPacket() instanceof CPacketPlayer) {
            setAwaiting(false);
        }
    }

    @SubscribeEvent
    public void onMotion(EventSync e) {
        if (e.isCanceled()) {
            return;
        }

        setAwaiting(true);
    }


    @SubscribeEvent
    public void onPost(EventPostSync e) {
        if (e.isCanceled()) {
            return;
        }


        if (isAwaiting()) {
            NoMotionUpdateEvent noMotionUpdate = new NoMotionUpdateEvent();
            MinecraftForge.EVENT_BUS.post(noMotionUpdate);
        }

        setAwaiting(false);

    }

    public boolean isAwaiting() {
        return awaiting;
    }

    public void setAwaiting(boolean awaiting) {
        this.awaiting = awaiting;
    }

}