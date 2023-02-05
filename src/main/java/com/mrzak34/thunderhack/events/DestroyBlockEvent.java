package com.mrzak34.thunderhack.events;

import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.Event;

public class DestroyBlockEvent extends Event {

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