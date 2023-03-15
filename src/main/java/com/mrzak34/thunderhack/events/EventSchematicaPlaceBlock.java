package com.mrzak34.thunderhack.events;

import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.Event;

public class EventSchematicaPlaceBlock extends Event {
    public BlockPos Pos;

    public EventSchematicaPlaceBlock(BlockPos p_Pos) {
        Pos = p_Pos;
    }
}