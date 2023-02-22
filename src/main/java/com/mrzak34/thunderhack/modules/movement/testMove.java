package com.mrzak34.thunderhack.modules.movement;

import com.mrzak34.thunderhack.command.Command;
import com.mrzak34.thunderhack.events.*;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.math.MatrixStrafeMovement;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static com.mrzak34.thunderhack.modules.movement.Jesus.isInLiquid;

public class testMove extends Module {
    public testMove() {
        super("testMove", "testMove", Category.MOVEMENT);
    }


    public static boolean needSprintState;
    public static int waterTicks;


    public Setting<Boolean> strafeBoost = register(new Setting<>("StafeBoost", false));
    public Setting<Float> vspeedValue = this.register(new Setting<Float>("Vertical", 0.2F, 0.0F, 1f));
    public Setting<Float> vspeedValue2 = this.register(new Setting<Float>("Vertical2", 0.2F, 0.0F, 2f));
    public Setting<Float> red = this.register(new Setting<Float>("red", 0.2F, 0.0F, 1f));



    public static boolean serversprint = false;

    @SubscribeEvent
    public void onEventAction(EventSprint epm){
        if (strafes()) {
            //if (Aura.hitTick) {
           //     Aura.hitTick = false;
          //      return;
          //  }
            if (strafes()) {
                if (serversprint != needSprintState) {
                    epm.setSprintState(!serversprint);
                }
            }
        }
        MatrixStrafeMovement.actionEvent(epm);
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send e){
        if(e.getPacket() instanceof CPacketEntityAction){
            CPacketEntityAction ent = e.getPacket();
            if(ent.getAction() == CPacketEntityAction.Action.START_SPRINTING) {
                serversprint = true;
            }
            if(ent.getAction() == CPacketEntityAction.Action.STOP_SPRINTING) {
                serversprint = false;
            }
        }
    }

    @SubscribeEvent
    public void onMatrixMove(MatrixMove evve){
        if (isInLiquid()) {
            waterTicks = 10;
        } else {
            waterTicks--;
        }
        if (strafes()) {
            double forward = mc.player.movementInput.moveForward;
            double strafe = mc.player.movementInput.moveStrafe;
            float yaw = mc.player.rotationYaw;
            if (forward == 0.0 && strafe == 0.0) {
                MatrixStrafeMovement.oldSpeed = 0;
                evve.setMotionX(0);
                evve.setMotionZ(0);
            } else {
                boolean ely = strafeBoost.getValue();
                if (ely) {
                    int elytra = getSlotIDFromItem(Items.ELYTRA);
                    if (elytra == -1) {
                        ely = false;
                    } else {
                        if (mc.player.ticksExisted % 4 == 0) {
                            disabler(elytra);
                        }
                    }
                }
                if (forward != 0.0) {
                    if (strafe > 0.0) {
                        yaw += ((forward > 0.0) ? -45 : 45);
                    } else if (strafe < 0.0) {
                        yaw += ((forward > 0.0) ? 45 : -45);
                    }
                    strafe = 0.0;
                    if (forward > 0.0) {
                        forward = 1.0;
                    } else if (forward < 0.0) {
                        forward = -1.0;
                    }
                }
                double speed = MatrixStrafeMovement.calculateSpeed2(evve, ely, vspeedValue.getValue());
                evve.setMotionX( forward * speed * Math.cos(Math.toRadians(yaw + 90.0f)) + strafe * speed * Math.sin(Math.toRadians(yaw + 90.0f)));
                evve.setMotionZ(forward * speed * Math.sin(Math.toRadians(yaw + 90.0f)) - strafe * speed * Math.cos(Math.toRadians(yaw + 90.0f)));
            }
        } else {
            MatrixStrafeMovement.oldSpeed = 0;
        }
        evve.setCanceled(true);
    }

    public static long lastStartFalling;

    public static void disabler(int elytra) {
        if (elytra != -2) {
            mc.playerController.windowClick(0, elytra, 1, ClickType.PICKUP, mc.player);
            mc.playerController.windowClick(0, 6, 1, ClickType.PICKUP, mc.player);
        }
        mc.getConnection().sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
        mc.getConnection().sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
        if (elytra != -2) {
            mc.playerController.windowClick(0, 6, 1, ClickType.PICKUP, mc.player);
            mc.playerController.windowClick(0, elytra, 1, ClickType.PICKUP, mc.player);
        }
        lastStartFalling = System.currentTimeMillis();
    }

    public static int getSlotIDFromItem(Item item) {
        for (ItemStack stack : mc.player.getArmorInventoryList()) {
            if (stack.getItem() == item) {
                return -2;
            }
        }
        int slot = -1;
        for (int i = 0; i < 36; i++) {
            ItemStack s = mc.player.inventory.getStackInSlot(i);
            if (s.getItem() == item) {
                slot = i;
                break;
            }
        }
        if (slot < 9 && slot != -1) {
            slot = slot + 36;
        }
        return slot;
    }


    @SubscribeEvent( priority = EventPriority.HIGHEST)
    public void onPacketReceive(PacketEvent.Receive event) {
            if(event.getPacket() instanceof SPacketPlayerPosLook){
                MatrixStrafeMovement.oldSpeed = 0;
            }
    }


    @SubscribeEvent
    public void updateValues(EventPreMotion e) {
        double distTraveledLastTickX = mc.player.posX - mc.player.prevPosX;
        double distTraveledLastTickZ = mc.player.posZ - mc.player.prevPosZ;
        MatrixStrafeMovement.postMove(Math.sqrt(distTraveledLastTickX * distTraveledLastTickX + distTraveledLastTickZ * distTraveledLastTickZ) * red.getValue());
    }


    /*
    @SubscribeEvent
    public void updateValues(EventPostMove e) {
        MatrixStrafeMovement.postMove(e.getHorizontalMove());
    }


     */


    public boolean strafes() {
        if (mc.player.isSneaking()) {
            return false;
        }
        if (mc.player.isInLava()) {
            return false;
        }
        if (mc.player.isInWater() || waterTicks > 0) {
            return false;
        }
        if (mc.player.isInWeb) {
            return false;
        }
        return !mc.player.capabilities.isFlying;
    }
}
