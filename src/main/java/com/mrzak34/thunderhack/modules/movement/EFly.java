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
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Iterator;

public class EFly extends Module {

    public EFly() {
        super("EFly", "EFly", Category.MOVEMENT);
    }


    private static int boostTicks;
    public static boolean needSprintState;

    
    private  Setting<Mode> elytraBoostMode =register( new Setting<>("BoostMode", Mode.Vanilla));
    private enum Mode {
        Vanilla, Strafe
    }

    public Setting<Float> speed =this.register( new Setting<>("speed", 0.1F, 0.1F, 1F));
    public Setting<Float> motion =this.register( new Setting<>("motionY", 0.05F, 0F, 0.3F));
    public Setting<Float> motion2 =this.register( new Setting<>("motionY2", 0.05F, 0F, 0.3F));
    public Setting<Float> elytraBoostSpeed =this.register( new Setting<>("SpeedB1", 0.7F, 0.1F, 2F));
    public Setting<Float> elytraBoostSpeed2 =this.register( new Setting<>("SpeedB2", 1.5F, 0.1F, 5F));

    private  Setting<Boolean> ElytraBoost = register(new Setting<>("ElytraBoost", false));


    private  float getElytraBoostSpeed() {
        return elytraBoostMode.getValue() == Mode.Vanilla ? elytraBoostSpeed.getValue() : elytraBoostSpeed2.getValue();
    }


    private boolean strafes() {
        return !mc.player.onGround && boostTicks == 1 && ElytraBoost.getValue() && elytraBoostMode.getValue() == Mode.Strafe;
    }


    @Override
    public void onDisable(){
        this.onswapdisable();
        Command.sendMessage("Swap -> Chestplate");
        mc.player.capabilities.isFlying = false;
        mc.player.capabilities.allowFlying = false;
    }

    @Override
    public void onEnable(){
        Command.sendMessage("Swap -> Elytra");
        this.onswapenable();
    }
    public static long lastStartFalling;

    @SubscribeEvent
    public void onMove(MatrixMove e){
        if (this.strafes()) {
            MatrixMove move = (MatrixMove)e;
            double forward = (double)mc.player.movementInput.moveForward;
            double strafe = (double)mc.player.movementInput.moveStrafe;
            float yaw = mc.player.rotationYaw;
            if (forward == 0.0 && strafe == 0.0) {
                MatrixStrafeMovement.oldSpeed = 0.0;
                move.setMotionX(0);
                move.setMotionZ(0);
            } else {
                boolean ely = true;
                int elytra = getSlotIDFromItem(Items.ELYTRA);
                if (elytra == -1) {
                    ely = false;
                } else if (System.currentTimeMillis() - lastStartFalling > 1000L) {
                    disabler(elytra);
                }

                if (forward != 0.0) {
                    if (strafe > 0.0) {
                        yaw += (float)(forward > 0.0 ? -45 : 45);
                    } else if (strafe < 0.0) {
                        yaw += (float)(forward > 0.0 ? 45 : -45);
                    }

                    strafe = 0.0;
                    if (forward > 0.0) {
                        forward = 1.0;
                    } else if (forward < 0.0) {
                        forward = -1.0;
                    }
                }

                double speed = MatrixStrafeMovement.calculateSpeed2(move, ely, (double)getElytraBoostSpeed());
                move.setMotionX(forward * speed * Math.cos(Math.toRadians((double)(yaw + 90.0F))) + strafe * speed * Math.sin(Math.toRadians((double)(yaw + 90.0F))));
                move.setMotionZ(forward * speed * Math.sin(Math.toRadians((double)(yaw + 90.0F))) - strafe * speed * Math.cos(Math.toRadians((double)(yaw + 90.0F))));
            }
        } else {
            MatrixStrafeMovement.oldSpeed = 0.0;
        }
    }

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

        for(int i = 0; i < 36; ++i) {
            ItemStack s = mc.player.inventory.getStackInSlot(i);
            if (s.getItem() == item) {
                slot = i;
                break;
            }
        }

        if (slot < 9 && slot != -1) {
            slot += 36;
        }

        return slot;
    }

    @SubscribeEvent
    public void onUpdate(PlayerUpdateEvent e){
        if (mc.player.onGround) {
            mc.player.jump();
            boostTicks = 0;
        }

        if (mc.player.isAirBorne && mc.player.ticksExisted % 2 == 0) {
            mc.getConnection().sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
        }

        if (!mc.player.onGround && boostTicks == 1) {
            if (ElytraBoost.getValue()) {
                if (elytraBoostMode.getValue() == Mode.Vanilla) {
                    mc.player.jumpMovementFactor = getElytraBoostSpeed();
                }
            } else {
                setSpeed(speed.getValue());
            }

            if (mc.gameSettings.keyBindJump.pressed) {
                mc.player.motionY = (double)this.motion.getValue();
            } else if (mc.gameSettings.keyBindSneak.pressed) {
                mc.player.motionY = (double)(-this.motion.getValue());
            } else {
                mc.player.motionY = mc.player.ticksExisted % 2 != 0 ? (double)(-this.motion2.getValue()) : (double)this.motion2.getValue();
            }
        }

        if (mc.player.isElytraFlying()) {
            boostTicks = 1;
        }
    }

    public static void setSpeed(float speed) {
        float yaw = mc.player.rotationYaw;
        float forward = mc.player.movementInput.moveForward;
        float strafe = mc.player.movementInput.moveStrafe;
        if (forward != 0.0F) {
            if (strafe > 0.0F) {
                yaw += (float)(forward > 0.0F ? -45 : 45);
            } else if (strafe < 0.0F) {
                yaw += (float)(forward > 0.0F ? 45 : -45);
            }

            strafe = 0.0F;
            if (forward > 0.0F) {
                forward = 1.0F;
            } else if (forward < 0.0F) {
                forward = -1.0F;
            }
        }

        double cos = Math.cos(Math.toRadians((double)(yaw + 90.0F)));
        double sin = Math.sin(Math.toRadians((double)(yaw + 90.0F)));
        mc.player.motionX = (double)(forward * speed) * cos + (double)(strafe * speed) * sin;
        mc.player.motionZ = (double)(forward * speed) * sin - (double)(strafe * speed) * cos;
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive e){
        if (e.getPacket() instanceof SPacketPlayerPosLook) {
            MatrixStrafeMovement.oldSpeed = 0.0;
        }
    }
    public static boolean serversprint = false;

    @SubscribeEvent
    public void onSprint(EventSprint e){
        MatrixStrafeMovement.actionEvent(e);
        if (strafes()) {
            if (serversprint != needSprintState) {
                e.setSprintState(!serversprint);
            }
        }
    }

    @SubscribeEvent
    public void onMove(EventPreMotion e){
        double d2 = mc.player.posX - mc.player.prevPosX;
        double d3 = mc.player.posZ - mc.player.prevPosZ;
        double d4 = d2 * d2 + d3 * d3;
        double distance = Math.sqrt(d4);
        MatrixStrafeMovement.postMove(distance);
    }



    private void onswapdisable() {
        for(int i = 0; i < 45; ++i) {
            if (mc.player.inventory.getStackInSlot(i).getItem() == Items.ELYTRA) {
                mc.playerController.windowClick(0, 6, 1, ClickType.PICKUP, mc.player);
                mc.playerController.windowClick(0, this.findarmor(), 0, ClickType.PICKUP, mc.player);
                mc.playerController.windowClick(0, 6, 1, ClickType.PICKUP, mc.player);
                return;
            }
        }

    }

    private void onswapenable() {
        int slot = this.findElytraSlot();
        for(int i = 0; i < 45; ++i) {
            if (mc.player.inventory.getStackInSlot(i).getItem() == Items.ELYTRA) {
                mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, mc.player);
                mc.playerController.windowClick(0, 6, 0, ClickType.PICKUP, mc.player);
                mc.playerController.windowClick(0, 9, 1, ClickType.PICKUP, mc.player);
                return;
            }
        }

    }

    private int findElytraSlot() {
        for(int i = 0; i < 45; ++i) {
            if (mc.player.inventoryContainer.getSlot(i).getStack() != null && mc.player.inventoryContainer.getSlot(i).getStack().getItem() == Items.ELYTRA) {
                return i;
            }
        }

        return -1;
    }

    private int findarmor() {
        for(int i = 0; i < 45; ++i) {
            if (mc.player.inventoryContainer.getSlot(i).getStack() != null && (mc.player.inventoryContainer.getSlot(i).getStack().getItem() == Items.CHAINMAIL_CHESTPLATE ||mc.player.inventoryContainer.getSlot(i).getStack().getItem() == Items.LEATHER_CHESTPLATE ||mc.player.inventoryContainer.getSlot(i).getStack().getItem() == Items.DIAMOND_CHESTPLATE || mc.player.inventoryContainer.getSlot(i).getStack().getItem() == Items.IRON_CHESTPLATE)) {
                return i;
            }
        }

        return -1;
    }
}
