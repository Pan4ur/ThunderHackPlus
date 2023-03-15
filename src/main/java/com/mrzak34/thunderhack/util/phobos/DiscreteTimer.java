package com.mrzak34.thunderhack.util.phobos;

public interface DiscreteTimer extends Passable {
    /**
     * Resets this timer. Passed will return
     * <tt>true</tt> until the delay has been
     * passed.
     *
     * @param delay the delay.
     * @return this.
     */
    DiscreteTimer reset(long delay);

    /**
     * @return time since last reset.
     */
    long getTime();

    void setTime(long time);

}