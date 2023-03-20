package com.mrzak34.thunderhack.gui.hud.elements;

import com.mojang.realmsclient.gui.ChatFormatting;
import com.mrzak34.thunderhack.events.EventSync;
import com.mrzak34.thunderhack.events.Render2DEvent;
import com.mrzak34.thunderhack.gui.fontstuff.FontRender;
import com.mrzak34.thunderhack.gui.hud.HudElement;
import com.mrzak34.thunderhack.setting.ColorSetting;
import com.mrzak34.thunderhack.setting.Setting;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Speedometer extends HudElement {
    public final Setting<ColorSetting> color = this.register(new Setting<>("Color", new ColorSetting(0x8800FF00)));
    public double speedometerCurrentSpeed = 0.0;
    private final Setting<Boolean> bps = this.register(new Setting<>("BPS", false));
    public Speedometer() {
        super("Speedometer", "Speedometer", 50,10);
    }

    @SubscribeEvent
    public void onRender2D(Render2DEvent e) {
        super.onRender2D(e);
        String str = "";
        if (!bps.getValue()) {
            str = "Speed " + ChatFormatting.WHITE + round(getSpeedKpH()) + " km/h";
        } else {
            str = String.format("Speed " + ChatFormatting.WHITE + round(getSpeedMpS()) + " b/s");
        }
        FontRender.drawString6(str, getPosX(), getPosY(), color.getValue().getRawColor(), true);
    }

    private float round(double value) {
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(1, RoundingMode.HALF_UP);
        return bd.floatValue();
    }

    @SubscribeEvent
    public void updateValues(EventSync e) {
        double distTraveledLastTickX = mc.player.posX - mc.player.prevPosX;
        double distTraveledLastTickZ = mc.player.posZ - mc.player.prevPosZ;
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

    public double getSpeedMpS() {
        double speedometerMpsdouble = this.turnIntoKpH(this.speedometerCurrentSpeed) / 3.6;
        speedometerMpsdouble = (double) Math.round(10.0 * speedometerMpsdouble) / 10.0;
        return speedometerMpsdouble;
    }
}
