package com.mrzak34.thunderhack.util.DeadCodeUtils;

import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.EnumHand;

import static com.mrzak34.thunderhack.util.CrystalUtils.mc;

public class cs {
    public static volatile cs a = new cs();

    public InventoryPlayer a() {
        return mc.player.inventory;
    }

    public PlayerControllerMP b() {
        return mc.playerController;
    }

    public void c() {
        mc.player.swingArm(EnumHand.MAIN_HAND);
    }

    public void a(Entity entity) {
        a.b().attackEntity((EntityPlayer)mc.player, entity);
    }

    public double d() {
        return mc.player.posX;
    }

    public double e() {
        return mc.player.posY;
    }

    public double f() {
        return mc.player.posZ;
    }
}