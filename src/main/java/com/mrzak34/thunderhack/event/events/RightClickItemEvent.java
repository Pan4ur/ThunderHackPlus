package com.mrzak34.thunderhack.event.events;

import com.mrzak34.thunderhack.event.EventStage;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

@Cancelable
public class RightClickItemEvent extends EventStage
{
    private final EntityPlayer player;
    private final World worldIn;
    private final EnumHand hand;

    public RightClickItemEvent(EntityPlayer player,
                               World worldIn,
                               EnumHand hand)
    {
        this.player = player;
        this.worldIn = worldIn;
        this.hand = hand;
    }

    public EntityPlayer getPlayer()
    {
        return player;
    }

    public World getWorldIn()
    {
        return worldIn;
    }

    public EnumHand getHand()
    {
        return hand;
    }
}