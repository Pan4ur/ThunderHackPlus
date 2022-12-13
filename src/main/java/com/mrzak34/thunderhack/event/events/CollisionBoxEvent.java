package com.mrzak34.thunderhack.event.events;

import com.mrzak34.thunderhack.event.EventStage;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public class CollisionBoxEvent  extends EventStage {
    private static CollisionBoxEvent INSTANCE = new CollisionBoxEvent();

    private Block block;
    private BlockPos pos;
    private AxisAlignedBB boundingBox;
    private  List<AxisAlignedBB> collidingBoxes;
    private Entity entity;

    public static CollisionBoxEvent get(Block block, BlockPos pos, AxisAlignedBB boundingBox, List<AxisAlignedBB> collidingBoxes, Entity entity) {
        INSTANCE.block = block;
        INSTANCE.pos = pos;
        INSTANCE.boundingBox = boundingBox;
        INSTANCE.collidingBoxes = collidingBoxes;
        INSTANCE.entity = entity;
        return INSTANCE;
    }

    public Block getBlock() {
        return block;
    }

    public BlockPos getPos() {
        return pos;
    }

    public AxisAlignedBB getBoundingBox() {
        return boundingBox;
    }

    public List<AxisAlignedBB> getCollidingBoxes() {
        return collidingBoxes;
    }

    public Entity getEntity() {
        return entity;
    }

    public void setBoundingBox(AxisAlignedBB boundingBox) {
        this.boundingBox = boundingBox;
    }
}