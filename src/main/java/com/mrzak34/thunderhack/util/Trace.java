package com.mrzak34.thunderhack.util;

import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DimensionType;

import java.util.List;

public class Trace {
    private String name;
    private int index;
    private Vec3d pos;
    private List<TracePos> trace;
    private DimensionType type;

    public Trace(int index,
                 String name,
                 DimensionType type,
                 Vec3d pos,
                 List<TracePos> trace) {
        this.index = index;
        this.name = name;
        this.type = type;
        this.pos = pos;
        this.trace = trace;
    }

    public int getIndex() {
        return this.index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public DimensionType getType() {
        return this.type;
    }

    public void setType(DimensionType type) {
        this.type = type;
    }

    public List<TracePos> getTrace() {
        return this.trace;
    }

    public void setTrace(List<TracePos> trace) {
        this.trace = trace;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Vec3d getPos() {
        return this.pos;
    }

    public void setPos(Vec3d pos) {
        this.pos = pos;
    }

    public static class TracePos {
        private final Vec3d pos;
        private final Timer stopWatch = new Timer();
        private long time;

        public TracePos(Vec3d pos) {
            this.pos = pos;
            stopWatch.reset();
        }

        public TracePos(Vec3d pos, long time) {
            this.pos = pos;
            stopWatch.reset();
            this.time = time;
        }

        public Vec3d getPos() {
            return pos;
        }

        public boolean shouldRemoveTrace() {
            return stopWatch.passedMs(2000);
        }

        public long getTime() {
            return time;
        }
    }
}