package com.mrzak34.thunderhack.event.events;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

@Cancelable
public class EventSchematicaPlaceBlockFull extends EventSchematicaPlaceBlock
{
    public boolean Result = true;
    public ItemStack ItemStack;

    public EventSchematicaPlaceBlockFull(BlockPos p_Pos, ItemStack itemStack)
    {
        super(p_Pos);
        ItemStack = itemStack;
    }

    public boolean GetResult()
    {
        return Result;
    }
}