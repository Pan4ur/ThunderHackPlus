package com.mrzak34.thunderhack.modules.combat;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.command.Command;
import com.mrzak34.thunderhack.event.events.EntityAddedEvent;
import com.mrzak34.thunderhack.event.events.EventPreMotion;
import com.mrzak34.thunderhack.event.events.PostRenderEvent;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.modules.render.NameTags;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.Timer;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.Objects;

public class AntiBot extends Module {
    public AntiBot() {
        super("AntiBot",  "Убирает ботов",  Category.COMBAT,  true,  false,  false);
    }

    public Setting<Boolean> remove = register(new Setting<>("Remove", false));
    public Setting<Boolean> onlyAura = register(new Setting<>("OnlyAura", true));
    public Setting<Integer> checkticks = register(new Setting("checkTicks", 3, 0, 10));


    public static ArrayList<EntityPlayer> bots = new ArrayList<>();


    private Timer timer = new Timer();
    private int botsNumber = 0;
    private int ticks = 0;

    @SubscribeEvent
    public void onUpdateWalkingPlayerPre(EventPreMotion e) {

        if(!onlyAura.getValue()) {
            for (EntityPlayer player : AntiBot.mc.world.playerEntities) {
                if (player != null) {
                    double speed = (player.posX - player.prevPosX) * (player.posX - player.prevPosX) + (player.posZ - player.prevPosZ) * (player.posZ - player.prevPosZ);

                    if (player != mc.player && speed > 0.5 && mc.player.getDistanceSq(player) <= Thunderhack.moduleManager.getModuleByClass(Aura.class).distance.getValue() * Thunderhack.moduleManager.getModuleByClass(Aura.class).distance.getValue() && !bots.contains(player)) {
                        Command.sendMessage(player.getName() + " is a bot!");
                        ++botsNumber;
                        bots.add(player);
                    }
                }
            }
        } else {
            if(Aura.target != null){
                if(Aura.target instanceof EntityPlayer) {
                    double speed = (Aura.target.posX - Aura.target.prevPosX) * (Aura.target.posX - Aura.target.prevPosX) + (Aura.target.posZ - Aura.target.prevPosZ) * (Aura.target.posZ - Aura.target.prevPosZ);
                    if (speed > 0.5 && !bots.contains(Aura.target)) {
                        if(ticks >= checkticks.getValue()) {
                            Command.sendMessage(Aura.target.getName() + " is a bot!");
                            ++botsNumber;
                            bots.add((EntityPlayer) Aura.target);
                        }
                        ticks++;
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
        if (timer.passedMs(5000)) {
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
