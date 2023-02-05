package com.mrzak34.thunderhack.events;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class MatrixMove extends Event {
    private boolean toGround;
    private AxisAlignedBB aabbFrom;

    private double fromX,fromY,fromZ,motionX,motionY,motionZ;

    public MatrixMove(double fromX,double fromY,double fromZ, double motionX,double motionY,double motionZ, boolean toGround, AxisAlignedBB aabbFrom) {
        this.fromX = fromX;
        this.fromY = fromY;
        this.fromZ = fromZ;
        this.motionX = motionX;
        this.motionY = motionY;
        this.motionZ = motionZ;
        this.toGround = toGround;
        this.aabbFrom = aabbFrom;
    }


    public double getFromX() {
        return fromX;
    }

    public double getFromZ() {
        return fromZ;
    }

    public double getMotionX() {
        return motionX;
    }

    public void setMotionX(double motionX) {
        this.motionX = motionX;
    }

    public double getMotionY() {
        return motionY;
    }

    public void setMotionY(double motionY) {
        this.motionY = motionY;
    }

    public double getMotionZ() {
        return motionZ;
    }

    public void setMotionZ(double motionZ) {
        this.motionZ = motionZ;
    }



    public AxisAlignedBB getAABBFrom() {
        return this.aabbFrom;
    }
    public boolean toGround() {
        return this.toGround;
    }


}
