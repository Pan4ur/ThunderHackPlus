package com.mrzak34.thunderhack.event.events;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import com.mrzak34.thunderhack.event.EventStage;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;


@Cancelable
public class DamageBlockEvent extends EventStage {

    private BlockPos blockPos;
    private EnumFacing enumFacing;

    public DamageBlockEvent(BlockPos blockPos, EnumFacing enumFacing) {
        this.blockPos = blockPos;
        this.enumFacing = enumFacing;
    }

    public BlockPos getBlockPos() {
        return this.blockPos;
    }

    public void setBlockPos(BlockPos blockPos) {
        this.blockPos = blockPos;
    }

    public EnumFacing getEnumFacing() {
        return this.enumFacing;
    }

    public void setEnumFacing(EnumFacing enumFacing) {
        this.enumFacing = enumFacing;
    }
}
