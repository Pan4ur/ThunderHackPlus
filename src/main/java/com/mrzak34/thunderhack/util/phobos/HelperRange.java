package com.mrzak34.thunderhack.util.phobos;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.modules.combat.AutoCrystal;
import com.mrzak34.thunderhack.util.MathUtil;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import static com.mrzak34.thunderhack.util.Util.mc;
import static com.mrzak34.thunderhack.util.phobos.DistanceUtil.distanceSq;

public class HelperRange{
    private final AutoCrystal module;

    public HelperRange(AutoCrystal module) {
        this.module = module;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isCrystalInRange(Entity crystal) {
        return isCrystalInRange(crystal.posX, crystal.posY, crystal.posZ, 0);
    }

    public boolean isCrystalInRangeOfLastPosition(Entity crystal) {
        return isCrystalInRange(crystal.posX, crystal.posY, crystal.posZ,
                Thunderhack.positionManager.getX(),
                Thunderhack.positionManager.getY(),
                Thunderhack.positionManager.getZ());
    }

    public boolean isCrystalInRange(
            double crystalX, double crystalY, double crystalZ, int ticks) {
        if (module.smartBreakTrace.getValue()
                && isOutsideBreakTrace(crystalX, crystalY, crystalZ, ticks)) {
            return false;
        }

        if (module.ncpRange.getValue()) {
            Entity breaker = mc.player;
            double breakerX = breaker.posX + breaker.motionX * ticks;
            double breakerY = breaker.posY + breaker.motionY * ticks;
            double breakerZ = breaker.posZ + breaker.motionZ * ticks;
            return SmartRangeUtil.isInStrictBreakRange(
                    crystalX, crystalY, crystalZ,
                    MathUtil.square(module.breakRange.getValue()),
                    breakerX, breakerY, breakerZ);
        }

        return SmartRangeUtil.isInSmartRange(
                crystalX, crystalY, crystalZ, mc.player,
                MathUtil.square(module.breakRange.getValue()), ticks);
    }

    public boolean isCrystalInRange(
            double crystalX, double crystalY, double crystalZ,
            double breakerX, double breakerY, double breakerZ) {
        if (module.smartBreakTrace.getValue()
                && !isCrystalInBreakTrace(crystalX, crystalY, crystalZ,
                breakerX, breakerY, breakerZ)) {
            return false;
        }

        if (module.ncpRange.getValue()) {
            return SmartRangeUtil.isInStrictBreakRange(
                    crystalX, crystalY, crystalZ,
                    MathUtil.square(module.breakRange.getValue()),
                    breakerX, breakerY, breakerZ);
        }

        return distanceSq(crystalX, crystalY, crystalZ,
                breakerX, breakerY, breakerZ)
                < MathUtil.square(module.breakRange.getValue());
    }

    public boolean isCrystalOutsideNegativeRange(BlockPos pos) {
        int negativeTicks = module.negativeTicks.getValue();
        if (negativeTicks == 0) {
            return false;
        }

        if (module.negativeBreakTrace.getValue()
                && isOutsideBreakTrace(pos, negativeTicks)) {
            return true;
        }

        if (module.ncpRange.getValue()) {
            Entity breaker = mc.player;
            double breakerX = breaker.posX + breaker.motionX * negativeTicks;
            double breakerY = breaker.posY + breaker.motionY * negativeTicks;
            double breakerZ = breaker.posZ + breaker.motionZ * negativeTicks;
            return !SmartRangeUtil.isInStrictBreakRange(
                    pos.getX() + 0.5f, pos.getY() + 1, pos.getZ() + 0.5,
                    MathUtil.square(module.breakRange.getValue()),
                    breakerX, breakerY, breakerZ);
        }

        return !SmartRangeUtil.isInSmartRange(
                pos, mc.player,
                MathUtil.square(module.breakRange.getValue()),
                negativeTicks);
    }

    public boolean isOutsideBreakTrace(BlockPos pos, int ticks) {
        return isOutsideBreakTrace(
                pos.getX() + 0.5f, pos.getY() + 1, pos.getZ() + 0.5f, ticks);
    }

    public boolean isOutsideBreakTrace(double x, double y, double z, int ticks)
    {
        Entity breaker = mc.player;
        double breakerX = breaker.posX + breaker.motionX * ticks;
        double breakerY = breaker.posY + breaker.motionY * ticks;
        double breakerZ = breaker.posZ + breaker.motionZ * ticks;
        return !isCrystalInBreakTrace(x, y, z, breakerX, breakerY, breakerZ);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isCrystalInBreakTrace(
            double crystalX, double crystalY, double crystalZ,
            double breakerX, double breakerY, double breakerZ) {
        return distanceSq(crystalX, crystalY, crystalZ,
                breakerX, breakerY, breakerZ)
                < MathUtil.square(module.breakTrace.getValue())
                || mc.world.rayTraceBlocks(
                new Vec3d(breakerX, breakerY
                        + mc.player.getEyeHeight(), breakerZ),
                new Vec3d(crystalX, crystalY + 1.7, crystalZ),
                false,
                true,
                false) == null;
    }

}