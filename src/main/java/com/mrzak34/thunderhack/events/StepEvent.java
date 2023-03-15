package com.mrzak34.thunderhack.events;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Called when a player steps up a block
 *
 * @author Doogie13
 * @since 12/27/2021
 * from https://github.com/momentumdevelopment/cosmos/
 */
@Cancelable
public class StepEvent extends Event {

    // info
    private final AxisAlignedBB axisAlignedBB;
    private float height;

    public StepEvent(AxisAlignedBB axisAlignedBB, float height) {
        this.axisAlignedBB = axisAlignedBB;
        this.height = height;
    }

    public AxisAlignedBB getAxisAlignedBB() {
        return axisAlignedBB;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float in) {
        height = in;
    }
}