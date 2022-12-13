package com.mrzak34.thunderhack.util.phobos;

import com.mrzak34.thunderhack.util.Timer;
import com.mrzak34.thunderhack.util.rotations.CastHelper;
import scala.sys.process.ProcessBuilderImpl;

/**
 * Duck interface for {@link net.minecraft.entity.Entity}.
 */
public interface IEntity extends Dummy
{
    /**
     * @return the isInWeb field.
     */
    boolean inWeb();

    /**
     * @return the EntityType of this Entity.
     */
    EntityType getType();

    /**
     * @return time since this Entity has been set dead.
     */
    long getDeathTime();

    /**
     * Alternative to {@link net.minecraft.entity.Entity#isDead}.
     *
     * @return <tt>true</tt> if this Entity is Pseudo Dead.
     */
    boolean isPseudoDead();

    /**
     * Makes {@link IEntity#isPseudoDead()} return the given value.
     *
     * @param pseudoDead the pseudoDeadState
     */
    void setPseudoDead(boolean pseudoDead);

    /**
     * @return the StopWatch used to Un-PseudoDead
     *         an Entity if it hasn't died after a time.
     */
    Timer getPseudoTime();

    /**
     * @return the {@link System#currentTimeMillis()}
     *         this Entity has been created on.
     */
    long getTimeStamp();

    @Override
    default boolean isDummy()
    {
        return false;
    }

    void setDummy(boolean dummy);

    long getOldServerPosX();

    long getOldServerPosY();

    long getOldServerPosZ();

    void setOldServerPos(long x, long y, long z);

}