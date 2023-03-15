package com.mrzak34.thunderhack.mixin.ducks;


import com.mrzak34.thunderhack.util.phobos.MotionTracker;

public interface IEntityPlayer {
    MotionTracker getMotionTrackerT();

    void setMotionTrackerT(MotionTracker motionTracker);

    MotionTracker getBreakMotionTrackerT();

    void setBreakMotionTrackerT(MotionTracker motionTracker);

    MotionTracker getBlockMotionTrackerT();

    void setBlockMotionTrackerT(MotionTracker motionTracker);

}