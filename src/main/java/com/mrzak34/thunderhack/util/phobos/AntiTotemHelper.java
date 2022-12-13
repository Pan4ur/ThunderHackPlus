package com.mrzak34.thunderhack.util.phobos;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.setting.Setting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;

public class AntiTotemHelper
{
    private final Setting<Float> health;
    private EntityPlayer target;
    private BlockPos targetPos;

    public AntiTotemHelper(Setting<Float> health)
    {
        this.health = health;
    }

    public boolean isDoublePoppable(EntityPlayer player)
    {
        return Thunderhack.combatManager.lastPop(player) > 500 && player.getHealth() <= health.getValue();
    }

    public BlockPos getTargetPos()
    {
        return targetPos;
    }

    public void setTargetPos(BlockPos targetPos)
    {
        this.targetPos = targetPos;
    }

    public EntityPlayer getTarget()
    {
        return target;
    }

    public void setTarget(EntityPlayer target)
    {
        this.target = target;
    }

}