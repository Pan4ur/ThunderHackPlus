package com.mrzak34.thunderhack.util.phobos;

import com.mrzak34.thunderhack.util.Timer;
import com.mrzak34.thunderhack.util.rotations.CastHelper;
import scala.sys.process.ProcessBuilderImpl;

/**
 * Duck interface for {@link net.minecraft.entity.Entity}.
 */
public interface IEntity extends Dummy
{


    boolean isPseudoDeadT();

    void setPseudoDeadT(boolean pseudoDead);

    Timer getPseudoTimeT();

    long getTimeStampT();

}