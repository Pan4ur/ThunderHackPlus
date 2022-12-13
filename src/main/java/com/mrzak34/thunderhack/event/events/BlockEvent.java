package com.mrzak34.thunderhack.event.events;

import com.mrzak34.thunderhack.event.EventStage;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

@Cancelable
public class BlockEvent
        extends EventStage {
    public BlockPos pos;
    public EnumFacing facing;

    public BlockEvent(int stage, BlockPos pos, EnumFacing facing) {
        super(stage);
        this.pos = pos;
        this.facing = facing;
    }
}

