package com.mrzak34.thunderhack.event.events;

import com.mrzak34.thunderhack.event.EventStage;
import net.minecraft.util.math.BlockPos;

public class DestroyBlockEvent extends EventStage {

    private BlockPos blockPos;

    public DestroyBlockEvent(BlockPos blockPos) {
        super();
        this.blockPos = blockPos;
    }

    public BlockPos getBlockPos() {
        return this.blockPos;
    }

    public void setBlockPos(BlockPos blockPos) {
        this.blockPos = blockPos;
    }
}