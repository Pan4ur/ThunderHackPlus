package com.mrzak34.thunderhack.event.events;

import com.mrzak34.thunderhack.event.EventStage;
import com.mrzak34.thunderhack.util.phobos.SafeRunnable;
import net.minecraft.network.Packet;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

import java.util.ArrayDeque;
import java.util.Deque;

public class PacketEvent
        extends EventStage {
    private final Packet<?> packet;

    public PacketEvent(int stage, Packet<?> packet) {
        super(stage);
        this.packet = packet;
    }

    public <T extends Packet<?>> T getPacket() {
        return (T) this.packet;
    }

    @Cancelable
    public static class Send
            extends PacketEvent {
        public Send(int stage, Packet<?> packet) {
            super(stage, packet);
        }
    }

    @Cancelable
    public static class Receive
            extends PacketEvent {
        public Receive(int stage, Packet<?> packet) {
            super(stage, packet);
        }

        private final Deque<Runnable> postEvents = new ArrayDeque<>();


        public void addPostEvent(SafeRunnable runnable)
        {
            postEvents.add(runnable);
        }

        /**
         * @return all PostEvents for this event.
         */
        public Deque<Runnable> getPostEvents()
        {
            return postEvents;
        }
    }

    @Cancelable
    public static class SendPost extends PacketEvent {
        public SendPost(int stage, Packet<?> packet) {
            super(stage, packet);
        }
    }

    @Cancelable
    public static class ReceivePost extends PacketEvent {
        public ReceivePost(int stage, Packet<?> packet) {
            super(stage, packet);
        }
    }
}

