package com.mrzak34.thunderhack.modules.movement;

import com.mrzak34.thunderhack.modules.*;
import com.mrzak34.thunderhack.setting.*;
import net.minecraft.init.*;

public class IceSpeed extends Module
{
    private final Setting<Float> speed = this.register(new Setting<>("Speed", 0.4f, 0.1f, 1.5f));

    public IceSpeed() {
        super("IceSpeed",  "+скорость если на льду",  Module.Category.MOVEMENT);
    }


    public void onUpdate() {
        Blocks.ICE.slipperiness = this.speed.getValue();
        Blocks.PACKED_ICE.slipperiness = this.speed.getValue();
        Blocks.FROSTED_ICE.slipperiness = this.speed.getValue();
    }

    public void onDisable() {
        Blocks.ICE.slipperiness = 0.98f;
        Blocks.PACKED_ICE.slipperiness = 0.98f;
        Blocks.FROSTED_ICE.slipperiness = 0.98f;
    }
}