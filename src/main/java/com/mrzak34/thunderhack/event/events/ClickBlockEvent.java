package com.mrzak34.thunderhack.event.events;

import com.mrzak34.thunderhack.event.EventStage;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

@Cancelable
public class ClickBlockEvent extends EventStage {
    private final BlockPos pos;
    private final EnumFacing facing;

    public ClickBlockEvent(BlockPos pos, EnumFacing facing) {
        this.pos = pos;
        this.facing = facing;
    }

    public BlockPos getPos() {
        return pos;
    }

    public EnumFacing getFacing() {
        return facing;
    }

    public static class Right extends ClickBlockEvent {
        private final Vec3d vec;
        private final EnumHand hand;

        public Right(BlockPos pos, EnumFacing facing, Vec3d vec, EnumHand hand) {
            super(pos, facing);
            this.vec = vec;
            this.hand = hand;
        }

        public EnumHand getHand() {
            return hand;
        }

        public Vec3d getVec() {
            return vec;
        }
    }
}