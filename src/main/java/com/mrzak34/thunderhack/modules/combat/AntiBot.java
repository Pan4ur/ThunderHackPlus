package com.mrzak34.thunderhack.modules.combat;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.command.Command;
import com.mrzak34.thunderhack.events.EventPreMotion;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.Timer;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.UUID;

public class AntiBot extends Module {
    public AntiBot() {
        super("AntiBot",  "Убирает ботов",  Category.COMBAT);
    }

    public Setting<Boolean> remove = register(new Setting<>("Remove", false));
    public Setting<Boolean> onlyAura = register(new Setting<>("OnlyAura", true));
    private Setting<Mode> mode  = this.register(new Setting("Mode", Mode.MotionCheck));

    public enum Mode {
        UUIDCheck, MotionCheck
    }
    public Setting<Integer> checkticks = register(new Setting("checkTicks", 3, 0, 10,v-> mode.getValue() == Mode.MotionCheck));


    public static ArrayList<EntityPlayer> bots = new ArrayList<>();


    private Timer timer = new Timer();
    private int botsNumber = 0;
    private int ticks = 0;

    @SubscribeEvent
    public void onUpdateWalkingPlayerPre(EventPreMotion e) {

        if(!onlyAura.getValue()) {
            for (EntityPlayer player : AntiBot.mc.world.playerEntities) {
                if(mode.getValue() == Mode.MotionCheck) {
                    if (player != null) {
                        double speed = (player.posX - player.prevPosX) * (player.posX - player.prevPosX) + (player.posZ - player.prevPosZ) * (player.posZ - player.prevPosZ);
                        if (player != mc.player && speed > 0.5 && mc.player.getDistanceSq(player) <= Thunderhack.moduleManager.getModuleByClass(Aura.class).attackDistance.getValue() * Thunderhack.moduleManager.getModuleByClass(Aura.class).attackDistance.getValue() && !bots.contains(player)) {
                            if(!bots.contains(player)) {
                                Command.sendMessage(player.getName() + " is a bot!");
                                ++botsNumber;
                                bots.add(player);
                            }
                        }
                    }
                } else {
                    if (!player.getUniqueID().equals(UUID.nameUUIDFromBytes(("OfflinePlayer:" + player.getName()).getBytes(StandardCharsets.UTF_8))) && player instanceof EntityOtherPlayerMP) {
                        if(!bots.contains(player)) {
                            Command.sendMessage(player.getName() + " is a bot!");
                            ++botsNumber;
                            bots.add(player);
                        }
                    }
                    if (!player.getUniqueID().equals(UUID.nameUUIDFromBytes(("OfflinePlayer:" + player.getName()).getBytes(StandardCharsets.UTF_8))) && player.isInvisible() && player instanceof EntityOtherPlayerMP) {
                        if(!bots.contains(player)) {
                            Command.sendMessage(player.getName() + " is a bot!");
                            ++botsNumber;
                            bots.add(player);
                        }
                    }
                }
            }
        } else {
            if(Aura.target != null){
                if(Aura.target instanceof EntityPlayer) {
                    if(mode.getValue() == Mode.MotionCheck) {
                        double speed = (Aura.target.posX - Aura.target.prevPosX) * (Aura.target.posX - Aura.target.prevPosX) + (Aura.target.posZ - Aura.target.prevPosZ) * (Aura.target.posZ - Aura.target.prevPosZ);
                        if (speed > 0.5 && !bots.contains(Aura.target)) {
                            if (ticks >= checkticks.getValue()) {
                                Command.sendMessage(Aura.target.getName() + " is a bot!");
                                ++botsNumber;
                                bots.add((EntityPlayer) Aura.target);
                            }
                            ticks++;
                        }
                    } else {
                        if (!Aura.target.getUniqueID().equals(UUID.nameUUIDFromBytes(("OfflinePlayer:" + Aura.target.getName()).getBytes(StandardCharsets.UTF_8))) && Aura.target instanceof EntityOtherPlayerMP) {
                            Command.sendMessage(Aura.target.getName() + " is a bot!");
                            ++botsNumber;
                            bots.add((EntityPlayer) Aura.target);
                        }
                        if (!Aura.target.getUniqueID().equals(UUID.nameUUIDFromBytes(("OfflinePlayer:" + Aura.target.getName()).getBytes(StandardCharsets.UTF_8))) && Aura.target.isInvisible() && Aura.target instanceof EntityOtherPlayerMP) {
                            Command.sendMessage(Aura.target.getName() + " is a bot!");
                            ++botsNumber;
                            bots.add((EntityPlayer) Aura.target);
                        }
                    }

                }
            }
        }


        for (EntityPlayer bot : bots) {
            if (remove.getValue()) {
                try {
                    mc.world.removeEntity(bot);
                } catch (Exception ignored) {

                }
            }
        }
        if (timer.passedMs(10000)) {
            bots.clear();
            botsNumber = 0;
            timer.reset();
            ticks = 0;
        }
    }


    @Override
    public String getDisplayInfo(){
        return String.valueOf(botsNumber);
    }



}
