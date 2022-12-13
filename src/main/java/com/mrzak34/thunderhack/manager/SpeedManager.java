package com.mrzak34.thunderhack.manager;

import com.mrzak34.thunderhack.modules.Feature;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;

import java.util.HashMap;

public class SpeedManager extends Feature {

    public double speedometerCurrentSpeed = 0.0;

    public void updateValues() {
        double distTraveledLastTickX = SpeedManager.mc.player.posX - SpeedManager.mc.player.prevPosX;
        double distTraveledLastTickZ = SpeedManager.mc.player.posZ - SpeedManager.mc.player.prevPosZ;
        this.speedometerCurrentSpeed = distTraveledLastTickX * distTraveledLastTickX + distTraveledLastTickZ * distTraveledLastTickZ;
    }



    public double turnIntoKpH(double input) {
        return (double) MathHelper.sqrt(input) * 71.2729367892;
    }

    public double getSpeedKpH() {
        double speedometerkphdouble = this.turnIntoKpH(this.speedometerCurrentSpeed);
        speedometerkphdouble = (double) Math.round(10.0 * speedometerkphdouble) / 10.0;
        return speedometerkphdouble;
    }
}

