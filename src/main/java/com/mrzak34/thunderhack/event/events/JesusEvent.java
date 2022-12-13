package com.mrzak34.thunderhack.event.events;

import com.mrzak34.thunderhack.event.EventStage;
import net.minecraftforge.fml.common.eventhandler.*;
import net.minecraft.util.math.*;

@Cancelable
public class JesusEvent extends EventStage
{
    private BlockPos pos;
    private AxisAlignedBB boundingBox;

    public JesusEvent(final int stage,  final BlockPos pos) {
        super(stage);
        this.pos = pos;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public void setPos(final BlockPos pos) {
        this.pos = pos;
    }

    public AxisAlignedBB getBoundingBox() {
        return this.boundingBox;
    }

    public void setBoundingBox(final AxisAlignedBB boundingBox) {
        this.boundingBox = boundingBox;
    }
}