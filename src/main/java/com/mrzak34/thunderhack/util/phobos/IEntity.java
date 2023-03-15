package com.mrzak34.thunderhack.util.phobos;

import com.mrzak34.thunderhack.util.Timer;

/**
 * Duck interface for {@link net.minecraft.entity.Entity}.
 */
public interface IEntity extends Dummy {


    boolean isPseudoDeadT();

    void setPseudoDeadT(boolean pseudoDead);

    Timer getPseudoTimeT();

    long getTimeStampT();

}