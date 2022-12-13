package com.mrzak34.thunderhack.mixin.ducks;


import com.mrzak34.thunderhack.util.phobos.MotionTracker;

public interface IEntityPlayer {
    void setMotionTracker(MotionTracker motionTracker);

    MotionTracker getMotionTracker();

    void setBreakMotionTracker(MotionTracker motionTracker);

    MotionTracker getBreakMotionTracker();

    void setBlockMotionTracker(MotionTracker motionTracker);

    MotionTracker getBlockMotionTracker();

    int getTicksWithoutMotionUpdate();

    void setTicksWithoutMotionUpdate(int ticksWithoutMotionUpdate);

}