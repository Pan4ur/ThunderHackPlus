package com.mrzak34.thunderhack.mixin.ducks;

import com.mrzak34.thunderhack.modules.combat.BackTrack;
import com.mrzak34.thunderhack.modules.render.PlayerTrails;
import com.mrzak34.thunderhack.util.Timer;
import com.mrzak34.thunderhack.util.phobos.Dummy;

import java.util.List;

/**
 * Duck interface for {@link net.minecraft.entity.Entity}.
 */
public interface IEntity extends Dummy {

    void setInPortal(boolean bool);

    boolean isPseudoDeadT();

    void setPseudoDeadT(boolean pseudoDead);

    Timer getPseudoTimeT();

    long getTimeStampT();

    boolean isInWeb();

    List<BackTrack.Box> getPosition_history();

    List<PlayerTrails.Trail> getTrails();
}