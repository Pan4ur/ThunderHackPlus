package com.mrzak34.thunderhack.util.surround;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class BlockPosWithFacing {

        private final BlockPos bp;
        private final EnumFacing facing;

        public BlockPosWithFacing(BlockPos blockPos, EnumFacing enumFacing) {
            this.bp = blockPos;
            this.facing = enumFacing;
        }

        public BlockPos getPosition() {
            return this.bp;
        }

        public EnumFacing getFacing() {
            return this.facing;
        }

}
