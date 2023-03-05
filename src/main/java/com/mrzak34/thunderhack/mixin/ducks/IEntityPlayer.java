package com.mrzak34.thunderhack.mixin.ducks;


import com.mrzak34.thunderhack.util.phobos.MotionTracker;

public interface IEntityPlayer {
    void setMotionTrackerT(MotionTracker motionTracker);

    MotionTracker getMotionTrackerT();

    void setBreakMotionTrackerT(MotionTracker motionTracker);

    MotionTracker getBreakMotionTrackerT();

    void setBlockMotionTrackerT(MotionTracker motionTracker);

    MotionTracker getBlockMotionTrackerT();

}