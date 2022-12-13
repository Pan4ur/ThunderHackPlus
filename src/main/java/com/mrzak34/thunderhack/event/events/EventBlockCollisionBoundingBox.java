package com.mrzak34.thunderhack.event.events;

import com.mrzak34.thunderhack.event.EventStage;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

@Cancelable

public class EventBlockCollisionBoundingBox extends EventStage {
    private BlockPos _pos;
    private AxisAlignedBB _boundingBox;

    public EventBlockCollisionBoundingBox(final BlockPos pos) {
        this._pos = pos;
    }

    public BlockPos getPos() {
        return this._pos;
    }

    public AxisAlignedBB getBoundingBox() {
        return this._boundingBox;
    }

    public void setBoundingBox(final AxisAlignedBB boundingBox) {
        this._boundingBox = boundingBox;
    }
}