package com.mrzak34.thunderhack.modules.misc;

import com.mojang.realmsclient.gui.ChatFormatting;
import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.command.Command;
import com.mrzak34.thunderhack.events.EntityAddedEvent;
import com.mrzak34.thunderhack.events.EntityRemovedEvent;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.notification.Notification;
import com.mrzak34.thunderhack.notification.NotificationManager;
import com.mrzak34.thunderhack.setting.Setting;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.Objects;

import static com.mrzak34.thunderhack.util.PlayerUtils.getPlayerPos;


public class VisualRange extends Module {

    private static final ArrayList<String> entities = new ArrayList<String>();
    public Setting<Boolean> leave = this.register(new Setting<>("Leave", true));
    public Setting<Boolean> enter = this.register(new Setting<>("Enter", true));
    public Setting<Boolean> friends = this.register(new Setting<>("Friends", true));
    public Setting<Boolean> soundpl = this.register(new Setting<>("Sound", true));
    public Setting<Boolean> funnyGame = this.register(new Setting<>("FunnyGame", false));
    public Setting<mode> Mode = register(new Setting("Mode", mode.Notification));


    public VisualRange() {
        super("VisualRange", "оповещает о игроках-в зоне прогрузки", Module.Category.MISC);
    }

    @SubscribeEvent
    public void onEntityAdded(EntityAddedEvent event) {
        if (funnyGame.getValue() && !mc.isSingleplayer()) {
            if (Objects.equals(Minecraft.getMinecraft().currentServerData.serverIP, "mcfunny.su")) {
                return;
            }
        }
        if (!isValid(event.entity)) {
            return;
        }

        if (!entities.contains(event.entity.getName())) {
            entities.add(event.entity.getName());
        } else {
            return;
        }

        if (enter.getValue()) {
            notify(event.entity, true);
        }
    }

    @SubscribeEvent
    public void onEntityRemoved(EntityRemovedEvent event) {
        if (!isValid(event.entity)) {
            return;
        }

        if (entities.contains(event.entity.getName())) {
            entities.remove(event.entity.getName());
        } else {
            return;
        }

        if (leave.getValue()) {
            notify(event.entity, false);
        }
    }

    public void notify(Entity entity, boolean enter) {
        String message = "";
        if (Thunderhack.friendManager.isFriend(entity.getName())) {
            message = ChatFormatting.AQUA + entity.getName();
        } else {
            message = ChatFormatting.GRAY + entity.getName();
        }

        if (enter) {
            message += ChatFormatting.GREEN + " was found!";
        } else {
            message += ChatFormatting.RED + " left!";
        }

        if (Mode.getValue() == mode.Chat) {
            Command.sendMessage(message);
        }
        if (Mode.getValue() == mode.Notification) {
            NotificationManager.publicity(message, 2, Notification.Type.WARNING);
        }

        if (soundpl.getValue()) {
            try {
                if (enter) {
                    mc.world.playSound(getPlayerPos(), SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.AMBIENT, 150.0f, 10.0F, true);
                } else {
                    mc.world.playSound(getPlayerPos(), SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.NEUTRAL, 150.0f, 10.0F, true);
                }
            } catch (Exception ignored) {

            }
        }

    }

    public boolean isValid(Entity entity) {
        if (mc.player == null || !(entity instanceof EntityPlayer)) {
            return false;
        }

        if (entity.isEntityEqual(mc.player) || Thunderhack.friendManager.isFriend(entity.getName()) && !friends.getValue() || entity.getName().equals(mc.player.getName())) {
            return false;
        }

        //Fakeplayer
        return entity.getEntityId() != -100;
    }

    public enum mode {
        Chat, Notification
    }

}
