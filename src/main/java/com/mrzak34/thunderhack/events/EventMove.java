package com.mrzak34.thunderhack.events;

import net.minecraft.entity.MoverType;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;


@Cancelable
public class EventMove extends Event {


    private MoverType move_type;

    public double x, y, z;

    public EventMove(MoverType type, double x, double y, double z) {
        this.move_type = type;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void set_move_type(MoverType type) {
        this.move_type = type;
    }

    public void set_x(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void set_y(double y) {
        this.y = y;
    }

    public void set_z(double z) {
        this.z = z;
    }

    public MoverType get_move_type() {
        return this.move_type;
    }

    public double get_x() {
        return this.x;
    }

    public double get_y() {
        return this.y;
    }

    public double get_z() {
        return this.z;
    }
}
