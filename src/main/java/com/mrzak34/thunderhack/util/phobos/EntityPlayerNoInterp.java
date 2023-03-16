package com.mrzak34.thunderhack.util.phobos;

import com.mojang.authlib.GameProfile;
import com.mrzak34.thunderhack.util.Util;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.world.World;


public class EntityPlayerNoInterp extends EntityOtherPlayerMP implements IEntityNoInterp {
    public EntityPlayerNoInterp(World worldIn) {
        this(worldIn, Util.mc.player.getGameProfile());
    }

    public EntityPlayerNoInterp(World worldIn, GameProfile gameProfileIn) {
        super(worldIn, gameProfileIn);
    }


    @Override
    public void setNoInterpX(double x) {
    }


    @Override
    public void setNoInterpY(double y) {
    }


    @Override
    public void setNoInterpZ(double z) {
    }
}