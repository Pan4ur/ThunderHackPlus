package com.mrzak34.thunderhack.util.phobos;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.modules.combat.AutoCrystal;
import com.mrzak34.thunderhack.setting.Setting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import static com.mrzak34.thunderhack.util.Util.mc;

public class DamageHelper
{
    private final Setting<Boolean> terrainCalc;
    private final Setting<Integer> bExtrapolation;
    private final Setting<Integer> pExtrapolation;
    private final Setting<Boolean> selfExtrapolation;
    private final Setting<Boolean> obbyTerrain;
    private final ExtrapolationHelper positionHelper;
    private final AutoCrystal module;

    public DamageHelper(AutoCrystal module,
                        ExtrapolationHelper positionHelper,
                        Setting<Boolean> terrainCalc,
                        Setting<Integer> extrapolation,
                        Setting<Integer> bExtrapolation,
                        Setting<Boolean> selfExtrapolation,
                        Setting<Boolean> obbyTerrain)
    {
        this.module = module;
        this.positionHelper = positionHelper;
        this.terrainCalc       = terrainCalc;
        this.pExtrapolation    = extrapolation;
        this.bExtrapolation    = bExtrapolation;
        this.selfExtrapolation = selfExtrapolation;
        this.obbyTerrain       = obbyTerrain;
    }

    // Break damage for ourselves //
    public float getDamage(Entity crystal)
    {
        if (module.isSuicideModule())
        {
            return 0.0f;
        }

        return DamageUtil.calculate(crystal.posX,
                crystal.posY,
                crystal.posZ,
                Thunderhack.positionManager.getBB(),
                mc.player);
    }

    public float getDamage(Entity crystal, AxisAlignedBB bb)
    {
        if (module.isSuicideModule())
        {
            return 0.0f;
        }

        return DamageUtil.calculate(crystal.posX,
                crystal.posY,
                crystal.posZ,
                bb,
                mc.player);
    }

    // Break damage for other entities //
    public float getDamage(Entity crystal, EntityLivingBase base)
    {
        return getDamage(crystal.posX, crystal.posY, crystal.posZ, base,
                mc.world, bExtrapolation.getValue(),
                module.avgBreakExtra.getValue(), false, false,
                terrainCalc.getValue());
    }

    // Place damage for ourselves //
    public float getDamage(BlockPos pos)
    {
        if (module.isSuicideModule())
        {
            return 0.0f;
        }

        return getDamage(pos, mc.player, mc.world,
                pExtrapolation.getValue(),
                module.avgPlaceDamage.getValue(), true,
                terrainCalc.getValue());
    }

    // Place damage for other entities //
    public float getDamage(BlockPos pos, EntityLivingBase base)
    {
        return getDamage(pos, base, mc.world,
                pExtrapolation.getValue(),
                module.avgPlaceDamage.getValue(), false,
                terrainCalc.getValue());
    }

    // Obby place damage for ourselves //
    public float getObbyDamage(BlockPos pos,
                               IBlockAccess world)
    {
        if (module.isSuicideModule())
        {
            return 0.0f;
        }

        return getDamage(pos, mc.player, world,
                pExtrapolation.getValue(),
                module.avgPlaceDamage.getValue(), true,
                obbyTerrain.getValue());
    }

    //  Obby place damage for other entities  //
    public float getObbyDamage(BlockPos pos,
                               EntityLivingBase base,
                               IBlockAccess world)
    {
        return getDamage(pos, base, world, pExtrapolation.getValue(),
                module.avgPlaceDamage.getValue(), false,
                obbyTerrain.getValue());
    }

    // Takes a BlockPos, for placing only //
    private float getDamage(BlockPos pos, EntityLivingBase base,
                            IBlockAccess world, int ticks, boolean avg,
                            boolean self, boolean terrain)
    {
        return getDamage(pos.getX() + 0.5f, pos.getY() + 1, pos.getZ() + 0.5f,
                base, world, ticks, avg, self, true, terrain);
    }

    // Where it all comes together //
    private float getDamage(double x, double y, double z, EntityLivingBase base,
                            IBlockAccess world, int ticks, boolean avg,
                            boolean self, boolean place, boolean terrain)
    {
        MotionTracker tracker;
        if (ticks == 0
                || self && !selfExtrapolation.getValue()
                || (tracker = place
                ? positionHelper.getTrackerFromEntity(base)
                : positionHelper.getBreakTrackerFromEntity(base)) == null
                || !tracker.active) {
            return DamageUtil.calculate(x, y, z, base.getEntityBoundingBox(),
                    base, world, terrain);
        }

        float dmg = DamageUtil.calculate(x, y, z,
                tracker.getEntityBoundingBox(),
                base, world, terrain);
        if (avg) {
            double extraWeight = place
                    ? module.placeExtraWeight.getValue()
                    : module.breakExtraWeight.getValue();
            double normWeight  = place
                    ? module.placeNormalWeight.getValue()
                    : module.breakNormalWeight.getValue();

            float normDmg = DamageUtil.calculate(x, y, z,
                    base.getEntityBoundingBox(),
                    base, world, terrain);
            return (float) ((normDmg * normWeight + dmg * extraWeight) / 2.0);
        }

        return dmg;
    }

}