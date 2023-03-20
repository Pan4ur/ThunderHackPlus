package com.mrzak34.thunderhack.modules.misc;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.command.Command;
import com.mrzak34.thunderhack.events.ClickMiddleEvent;
import com.mrzak34.thunderhack.events.EventPostSync;
import com.mrzak34.thunderhack.events.EventSync;
import com.mrzak34.thunderhack.mixin.mixins.IMinecraft;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.InventoryUtil;
import com.mrzak34.thunderhack.util.Timer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemFood;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Mouse;

public class MiddleClick extends Module {
    public Setting<Boolean> fm = register(new Setting<>("FriendMessage", true));
    public Setting<Boolean> friend = register(new Setting<>("Friend", true));
    public Setting<Boolean> rocket = register(new Setting<>("Rocket", false));
    public Setting<Boolean> ep = register(new Setting<>("Pearl", true));
    public Setting<Boolean> silentPearl = register(new Setting<>("SilentPearl", true,v-> ep.getValue()));
    public Setting<Integer> swapDelay = this.register(new Setting<>("SwapDelay", 100, 0, 1000));
    public Setting<Boolean> xp = register(new Setting<>("XP", false));
    public Setting<Boolean> feetExp = register(new Setting<>("FeetXP", false));
    public Setting<Boolean> silent = register(new Setting<>("SilentXP", true));
    public Setting<Boolean> whileEating = register(new Setting<>("WhileEating", true));
    public Setting<Boolean> pickBlock = register(new Setting<>("CancelMC", true));
    public Timer timr = new Timer();
    private int lastSlot = -1;


    public MiddleClick() {
        super("MiddleClick", "действия на колесико-мыши", Category.MISC);
    }

    @SubscribeEvent
    public void onPreMotion(EventSync event) {
        if (mc.player == null || mc.world == null) return;

        if (feetExp.getValue() && Mouse.isButtonDown(2)) {
            if (!xp.getValue()) return;
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
                        mc.player.sendChatMessage("/w " + entity.getName() + " i friended u at ThunderHackPlus");
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
            if(silentPearl.getValue()) {
                int epSlot = findEPSlot();
                int originalSlot = mc.player.inventory.currentItem;
                if (epSlot != -1) {
                    mc.player.inventory.currentItem = epSlot;
                    mc.player.connection.sendPacket(new CPacketHeldItemChange(epSlot));
                    ((IMinecraft)mc).invokeRightClick();
                    mc.player.inventory.currentItem = originalSlot;
                    mc.player.connection.sendPacket(new CPacketHeldItemChange(originalSlot));
                }
            } else {
                int epSlot = findEPSlot();
                int originalSlot = mc.player.inventory.currentItem;
                if (epSlot != -1) {
                    new PearlThread(mc.player, epSlot, originalSlot,swapDelay.getValue()).start();
                }
            }
            timr.reset();

        }

    }

    @SubscribeEvent
    public void onPostMotion(EventPostSync event) {
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
        if (!xp.getValue()) return;
        if (pickBlock.getValue()) {
            int slot = InventoryUtil.findItemAtHotbar(Items.EXPERIENCE_BOTTLE);
            if (slot != -1 && slot != -2 && slot != mc.player.inventory.currentItem) {
                event.setCanceled(true);
            }
        }
    }

    public class PearlThread extends Thread {
        public EntityPlayerSP player;
        int epSlot,originalSlot,delay;

        public PearlThread(EntityPlayerSP entityPlayerSP, int epSlot, int originalSlot,int delay) {
            this.player = entityPlayerSP;
            this.epSlot = epSlot;
            this.originalSlot = originalSlot;
            this.delay = delay;
        }

        @Override
        public void run() {
            mc.player.inventory.currentItem = epSlot;
            mc.player.connection.sendPacket(new CPacketHeldItemChange(epSlot));
            try {sleep(delay);} catch (Exception ignored) {}
            ((IMinecraft)mc).invokeRightClick();
            try {sleep(delay);} catch (Exception ignored) {}
            mc.player.inventory.currentItem = originalSlot;
            mc.player.connection.sendPacket(new CPacketHeldItemChange(originalSlot));
            super.run();
        }
    }
}
