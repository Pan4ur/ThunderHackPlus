package com.mrzak34.thunderhack.util.phobos;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.modules.combat.AutoCrystal;
import com.mrzak34.thunderhack.util.EntityUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;

import java.util.Collection;
import java.util.List;

public abstract class AbstractBreakHelper<T extends CrystalData> implements IBreakHelper<T> {
    protected final AutoCrystal module;

    public AbstractBreakHelper(AutoCrystal module) {
        this.module = module;
    }

    protected abstract T newCrystalData(Entity crystal);

    protected abstract boolean isValid(Entity crystal, T data);

    protected abstract boolean calcSelf(BreakData<T> breakData, Entity crystal, T data);

    protected abstract void calcCrystal(BreakData<T> data,
                                        T crystalData,
                                        Entity crystal,
                                        List<EntityPlayer> players);

    @Override
    public BreakData<T> getData(Collection<T> dataSet,
                                List<Entity> entities,
                                List<EntityPlayer> players,
                                List<EntityPlayer> friends) {
        BreakData<T> data = newData(dataSet);
        for (Entity crystal : entities) {
            if (!(crystal instanceof EntityEnderCrystal) || EntityUtil.isDead(crystal) && (!module.countDeadCrystals.getValue() || module.countDeathTime.getValue() && (EntityUtil.isDead(crystal) && Thunderhack.setDeadManager.passedDeathTime(crystal, module.getDeathTime()) || ((IEntity) crystal).isPseudoDeadT() && ((IEntity) crystal).getPseudoTimeT().passedMs(module.getDeathTime())))) {
                continue;
            }
            T crystalData = newCrystalData(crystal);
            if (calcSelf(data, crystal, crystalData)) {
                continue;
            }

            if (!isValid(crystal, crystalData) || module.shouldCalcFuckinBitch(AutoCrystal.AntiFriendPop.Break) && checkFriendPop(crystal, friends)) {
                continue;
            }

            calcCrystal(data, crystalData, crystal, players);
        }

        return data;
    }

    protected boolean checkFriendPop(Entity entity, List<EntityPlayer> friends) {
        for (EntityPlayer friend : friends) {
            float fDamage = module.damageHelper.getDamage(entity, friend);
            if (fDamage > EntityUtil.getHealth(friend) - 1.0f) {
                return true;
            }
        }

        return false;
    }

}