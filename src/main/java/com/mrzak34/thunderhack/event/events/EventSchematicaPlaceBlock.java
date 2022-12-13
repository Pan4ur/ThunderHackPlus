package com.mrzak34.thunderhack.event.events;

import com.mrzak34.thunderhack.event.EventStage;
import net.minecraft.util.math.BlockPos;

public class EventSchematicaPlaceBlock extends EventStage
{
    public BlockPos Pos;

    public EventSchematicaPlaceBlock(BlockPos p_Pos)
    {
        Pos = p_Pos;
    }
}