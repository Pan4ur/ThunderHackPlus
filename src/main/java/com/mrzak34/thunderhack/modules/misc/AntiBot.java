package com.mrzak34.thunderhack.modules.misc;

import com.mrzak34.thunderhack.modules.Module;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AntiBot extends Module {

    public AntiBot( ) {
        super ( "AntiBot" , "антибот типа" , Category.MISC , false , false , false );
    }

    public static ArrayList<EntityPlayer> bots = new ArrayList<>();
    public static ArrayList<String> botsbyname = new ArrayList<>();

    @Override
    public void onDisable() {
        bots.clear();
    }

    public static List<EntityPlayer> getBots() {
        return bots;
    }
    public static List<String> getBotsByName() {
        return botsbyname;
    }

    @Override
    public void onUpdate() {
        if (mc.currentScreen instanceof GuiDownloadTerrain && !bots.isEmpty())
            bots.clear();
        if (mc.world != null) {
            mc.world.loadedEntityList.stream().filter(e -> e instanceof EntityPlayer).filter(e -> e != mc.player && e != null).filter(e -> isBot((EntityPlayer)e)).forEach(entity -> {
                if (!bots.contains(entity))
                    bots.add((EntityPlayer)entity);
                    botsbyname.add(entity.getName());
            });
        }
    }

    public String getDisplayInfo() {
        return String.valueOf(bots.size());
    }

    public static boolean isBot(EntityPlayer player) {
        NetworkPlayerInfo npi = Objects.requireNonNull(mc.getConnection()).getPlayerInfo(player.getGameProfile().getId());
        return npi == null || npi.getResponseTime() <= 0 && !player.equals(mc.player) && npi.getGameProfile() == null && player.hasCustomName();
    }

    public static boolean isBot(EntityLivingBase player) {
        if(player instanceof EntityPlayer) {
            EntityPlayer player2 = (EntityPlayer) player;

            NetworkPlayerInfo npi = Objects.requireNonNull(mc.getConnection()).getPlayerInfo(player2.getGameProfile().getId());
            return npi == null || npi.getResponseTime() <= 0 && !player2.equals(mc.player) && npi.getGameProfile() == null && player2.hasCustomName();
        }
        return false;
    }

}
