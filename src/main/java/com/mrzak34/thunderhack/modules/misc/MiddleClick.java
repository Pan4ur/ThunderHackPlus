package com.mrzak34.thunderhack.modules.misc;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.command.Command;
import com.mrzak34.thunderhack.events.ClickMiddleEvent;
import com.mrzak34.thunderhack.events.EventPostMotion;
import com.mrzak34.thunderhack.events.EventPreMotion;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.*;

import com.mrzak34.thunderhack.util.Timer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemFood;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Mouse;

public class MiddleClick extends Module{
    public MiddleClick() {
            super("MiddleClick", "действия на колесико-мыши", Category.MISC);
    }


    public Setting<Boolean> fm = register(new Setting<>("FriendMessage", true));
    public Setting<Boolean> friend = register(new Setting<>("Friend", true));
    public Setting<Boolean> rocket = register(new Setting<>("Rocket", false));
    public Setting<Boolean> ep = register(new Setting<>("Pearl", true));
    public Setting<Boolean> xp = register(new Setting<>("XP", false));
    public Setting<Boolean> feetExp = register(new Setting<>("FeetXP", false));
    public Setting<Boolean> silent = register(new Setting<>("SilentXP", true));
    public Setting<Boolean> whileEating = register(new Setting<>("WhileEating", true));
    public Setting<Boolean> pickBlock = register(new Setting<>("CancelMC", true));


    public Timer timr = new Timer();


    @SubscribeEvent
    public void onPreMotion(EventPreMotion event){
        if (mc.player == null || mc.world == null) return;

        if (feetExp.getValue() && Mouse.isButtonDown(2)) {
            mc.player.rotationPitch = 90f;
        }

        if (friend.getValue() && mc.objectMouseOver != null && mc.objectMouseOver.entityHit != null) {
            if (!Mouse.isButtonDown(2)) return;
            Entity entity = mc.objectMouseOver.entityHit;
            if (entity instanceof EntityPlayer && timr.passedMs(2500)) {
                if (Thunderhack.friendManager.isFriend(entity.getName())) {
                    Thunderhack.friendManager.removeFriend(entity.getName());
                    Command.sendMessage("Removed §b" + entity.getName() + "§r as a friend!");
                } else {
                    Thunderhack.friendManager.addFriend(entity.getName());
                    if (fm.getValue()) {
                        mc.player.sendChatMessage("/w "+ entity.getName() + " i friended u at ThunderHackPlus");
                    }
                    Command.sendMessage("Added §b" + entity.getName() + "§r as a friend!");
                }
                timr.reset();
                return;
            }
        }

        if (rocket.getValue() && findRocketSlot() != -1 && timr.passedMs(500)) {
            if (!Mouse.isButtonDown(2)) return;
            int rocketSlot = findRocketSlot();
            int originalSlot = mc.player.inventory.currentItem;

            if (rocketSlot != -1) {
                mc.player.inventory.currentItem = rocketSlot;
                mc.player.connection.sendPacket(new CPacketHeldItemChange(rocketSlot));

                mc.playerController.processRightClick(mc.player, mc.world, EnumHand.MAIN_HAND);

                mc.player.inventory.currentItem = originalSlot;
                mc.player.connection.sendPacket(new CPacketHeldItemChange(originalSlot));
                timr.reset();
                return;
            }
        }

        if (ep.getValue() && timr.passedMs(500) && mc.currentScreen == null) {
            if (!Mouse.isButtonDown(2)) return;
            int epSlot = findEPSlot();
            int originalSlot = mc.player.inventory.currentItem;
            if (epSlot != -1) {
                mc.player.inventory.currentItem = epSlot;
                mc.player.connection.sendPacket(new CPacketHeldItemChange(epSlot));

                mc.playerController.processRightClick(mc.player, mc.world, EnumHand.MAIN_HAND);

                mc.player.inventory.currentItem = originalSlot;
                mc.player.connection.sendPacket(new CPacketHeldItemChange(originalSlot));
            }
            timr.reset();
        }

    }

    @SubscribeEvent
    public void onPostMotion(EventPostMotion event) {
        if (xp.getValue()) {
            if (Mouse.isButtonDown(2) && (whileEating.getValue() || !(mc.player.getActiveItemStack().getItem() instanceof ItemFood))) {
                int slot = InventoryUtil.findItemAtHotbar(Items.EXPERIENCE_BOTTLE);
                if (slot != -1) {
                    int lastSlot = mc.player.inventory.currentItem;
                    InventoryUtil.switchTo(slot);
                    mc.playerController.processRightClick(mc.player, mc.world, InventoryUtil.getHand(slot));

                    if (silent.getValue()) {
                        InventoryUtil.switchTo(lastSlot);
                    }
                } else if (lastSlot != -1) {
                    InventoryUtil.switchTo(lastSlot);
                    lastSlot = -1;
                }
            } else if (lastSlot != -1) {
                InventoryUtil.switchTo(lastSlot);
                lastSlot = -1;
            }
        }
    }




    private int findRocketSlot() {
        int rocketSlot = -1;

        if (mc.player.getHeldItemMainhand().getItem() == Items.FIREWORKS) {
            rocketSlot = mc.player.inventory.currentItem;
        }


        if (rocketSlot == -1) {
            for (int l = 0; l < 9; ++l) {
                if (mc.player.inventory.getStackInSlot(l).getItem() == Items.FIREWORKS) {
                    rocketSlot = l;
                    break;
                }
            }
        }

        return rocketSlot;
    }


    private int findEPSlot() {
        int epSlot = -1;

        if (mc.player.getHeldItemMainhand().getItem() == Items.ENDER_PEARL) {
            epSlot = mc.player.inventory.currentItem;
        }


        if (epSlot == -1) {
            for (int l = 0; l < 9; ++l) {
                if (mc.player.inventory.getStackInSlot(l).getItem() == Items.ENDER_PEARL) {
                    epSlot = l;
                    break;
                }
            }
        }

        return epSlot;
    }

    @SubscribeEvent
    public void onMiddleClick(ClickMiddleEvent event) {
        if(!xp.getValue()) return;
        if (pickBlock.getValue()) {
            int slot = InventoryUtil.findItemAtHotbar(Items.EXPERIENCE_BOTTLE);
            if (slot != -1 && slot != -2 && slot != mc.player.inventory.currentItem) {
                event.setCanceled(true);
            }
        }
    }
    private int lastSlot = -1;
}
