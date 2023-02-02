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
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Mouse;

public class MiddleClick extends Module{
    public MiddleClick() {
            super("MiddleClick", "действия на колесико-мыши", Category.MISC);
    }



    private  Setting<Action> action = register(new Setting<>("Action", Action.FRIEND));
    public Setting<Boolean> fm = register(new Setting("FriendMessage", true));


    public Timer timr = new Timer();


    public Setting<Boolean> rocket = register(new Setting("Rocket", false));
    public Setting<Boolean> ep = register(new Setting("EP", false));
    public Setting<Boolean> xp = register(new Setting("XP", false));

    public Setting<Boolean> xpInHoles = register(new Setting("XPInHoles", false, v-> xp.getValue()));
    public Setting<Boolean> feetExp = register(new Setting<>("FeetExp", false));
    public Setting<Boolean> silent = register(new Setting<>("Silent", true));
    public Setting<Boolean> whileEating = register(new Setting<>("WhileEating", true));
    public Setting<Boolean> pickBlock = register(new Setting<>("CancelMC", true));


    private enum Action {
         FRIEND, MISC,
    }




    @Override
    public void onUpdate() {
        if (mc.player == null || mc.world == null) return;
        if (action.getValue() == Action.FRIEND && mc.objectMouseOver != null && mc.objectMouseOver.entityHit != null) { //TODO
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

        if (ep.getValue() && (!xp.getValue() || (xpInHoles.getValue() && !BlockUtils.isHole(new BlockPos(mc.player))))) {
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
        }
    }


    @SubscribeEvent
    public void onPreMotion(EventPreMotion event){
        if(!xp.getValue()) return;
        if (feetExp.getValue() && (InventoryUtil.isHolding(Items.EXPERIENCE_BOTTLE) && Mouse.isButtonDown(1) || Mouse.isButtonDown(2)))
        {
            mc.player.rotationPitch = 90f;
        }
    }

    @SubscribeEvent
    public void onPostMotion(EventPostMotion event) {
        if (xp.getValue()) {
            if (Mouse.isButtonDown(2) && (whileEating.getValue() || !(mc.player.getActiveItemStack().getItem() instanceof ItemFood)))
            {
                int slot = InventoryUtil.findItemAtHotbar(Items.EXPERIENCE_BOTTLE);
                if (slot != -1)
                {

                        int lastSlot = mc.player.inventory.currentItem;


                        InventoryUtil.switchTo(slot);

                        mc.playerController.processRightClick(
                                mc.player,
                                mc.world,
                                InventoryUtil.getHand(slot));

                        if (silent.getValue())
                        {
                            InventoryUtil.switchTo(lastSlot);
                        }
                }
                else if (lastSlot != -1)
                {
                        InventoryUtil.switchTo(lastSlot);
                        lastSlot = -1;
                }
            }
            else if (lastSlot != -1)
            {
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
        if (pickBlock.getValue())
        {
            int slot = InventoryUtil.findItemAtHotbar(Items.EXPERIENCE_BOTTLE);
            if (slot != -1 && slot != -2 && slot != mc.player.inventory.currentItem)
            {
                event.setCanceled(true);
            }
        }
    }

    protected int lastSlot = -1;



    @Override
    public void onEnable()
    {
        lastSlot = -1;
    }

    @Override
    public void onDisable()
    {
        if (lastSlot != -1)
        {
            InventoryUtil.switchTo(lastSlot);
            lastSlot = -1;
        }
    }
}
