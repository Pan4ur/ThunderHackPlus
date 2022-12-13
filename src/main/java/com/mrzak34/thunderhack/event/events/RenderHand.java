package com.mrzak34.thunderhack.event.events;

import com.mrzak34.thunderhack.event.EventStage;

public class RenderHand extends EventStage {
    private final float ticks;

    public RenderHand(float ticks) {
        this.ticks = ticks;
    }

    public float getPartialTicks() {
        return ticks;
    }


    public static class PostOutline extends RenderHand {
        public PostOutline(float ticks) {
            super(ticks);
        }
    }

    public static class PreOutline extends RenderHand {
        public PreOutline(float ticks) {
            super(ticks);
        }
    }

    public static class PostFill extends RenderHand {
        public PostFill(float ticks) {
            super(ticks);
        }
    }

    public static class PreFill extends RenderHand {
        public PreFill(float ticks) {
            super(ticks);
        }
    }

    public static class PostBoth extends RenderHand {
        public PostBoth(float ticks) {
            super(ticks);
        }
    }

    public static class PreBoth extends RenderHand {
        public PreBoth(float ticks) {
            super(ticks);
        }
    }



}