package com.mrzak34.thunderhack.util.phobos;

import com.mrzak34.thunderhack.modules.combat.AutoCrystal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;

import java.util.List;

/**
 * Represents the Calculation of the {@link AutoCrystal}.
 */
public class Calculation extends AbstractCalculation<CrystalData> {
    public Calculation(AutoCrystal module,
                       List<Entity> entities,
                       List<EntityPlayer> players,
                       BlockPos... blackList) {
        super(module, entities, players, blackList);
    }

    public Calculation(AutoCrystal module,
                       List<Entity> entities,
                       List<EntityPlayer> players,
                       boolean breakOnly,
                       boolean noBreak,
                       BlockPos... blackList) {
        super(module, entities, players, breakOnly, noBreak, blackList);
    }

    @Override
    protected IBreakHelper<CrystalData> getBreakHelper() {
        return module.breakHelper;
    }

}