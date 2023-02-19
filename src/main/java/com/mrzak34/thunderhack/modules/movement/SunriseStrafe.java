package com.mrzak34.thunderhack.modules.movement;

import com.mrzak34.thunderhack.events.*;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;

import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;



import static com.mrzak34.thunderhack.util.MovementUtil.isMoving;


public class SunriseStrafe extends Module {
    public SunriseStrafe() {
        super("SunriseStrafe", "kalrise only!!!", Category.MOVEMENT);
    }

    public Setting<Float> asdasd  = this.register(new Setting<>("GroundFactor", 1.2f, 0f, 3f));
    public Setting<Float> asdasd2  = this.register(new Setting<>("AirFactor ", 1f, 0f, 1f));
    public Setting<Float> vspeedValue2 = this.register(new Setting<Float>("GroundExpand", 0.84F, 0.0F, 2f));


    @SubscribeEvent
    public void onUpdate(PlayerUpdateEvent event) {
        swapElytraToChestplate();
        if (mc.player.ticksExisted % 6 == 0) {
            disabler(getSlotWithElytra());
        }
        if(!mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(0.0,  -vspeedValue2.getValue(),0.0f)).isEmpty())
            setMotion(getSpeed() * asdasd.getValue());
        if (mc.player.onGround && !mc.gameSettings.keyBindJump.pressed) {
            mc.player.jump();
            strafe();
        }
    }

    public  void strafe() {
        if (mc.gameSettings.keyBindBack.isKeyDown()) {
            return;
        }
       strafe(getSpeed() * asdasd2.getValue());
    }
    public static void strafe(float speed) {
        if (!isMoving()) {
            return;
        }
        double yaw = getAllDirection();
        mc.player.motionX = -Math.sin(yaw) * (double) speed;
        mc.player.motionZ = Math.cos(yaw) * (double) speed;
    }

    public static float getAllDirection() {
        float rotationYaw = mc.player.rotationYaw;

        float factor = 0f;

        if (mc.player.movementInput.moveForward > 0)
            factor = 1;
        if (mc.player.movementInput.moveForward < 0)
            factor = -1;

        if (factor == 0) {
            if (mc.player.movementInput.moveStrafe > 0)
                rotationYaw -= 90;

            if (mc.player.movementInput.moveStrafe < 0)
                rotationYaw += 90;
        } else {
            if (mc.player.movementInput.moveStrafe > 0)
                rotationYaw -= 45 * factor;

            if (mc.player.movementInput.moveStrafe < 0)
                rotationYaw += 45 * factor;
        }

        if (factor < 0)
            rotationYaw -= 180;

        return (float) Math.toRadians(rotationYaw);
    }

    public static void setMotion(double speed) {
        double forward = mc.player.movementInput.moveForward;
        double strafe = mc.player.movementInput.moveStrafe;
        float yaw = mc.player.rotationYaw;
        if (forward == 0 && strafe == 0) {
            mc.player.motionX = 0;
            mc.player.motionZ = 0;
        } else {
            if (forward != 0) {
                if (strafe > 0) {
                    yaw += (float) (forward > 0 ? -45 : 45);
                } else if (strafe < 0) {
                    yaw += (float) (forward > 0 ? 45 : -45);
                }
                strafe = 0;
                if (forward > 0) {
                    forward = 1;
                } else if (forward < 0) {
                    forward = -1;
                }
            }
            double sin = MathHelper.sin((float) Math.toRadians(yaw + 90));
            double cos = MathHelper.cos((float) Math.toRadians(yaw + 90));
            mc.player.motionX = forward * speed * cos + strafe * speed * sin;
            mc.player.motionZ = forward * speed * sin - strafe * speed * cos;
        }
    }

    public static float getSpeed() {
        return (float) Math.sqrt(mc.player.motionX * mc.player.motionX + mc.player.motionZ * mc.player.motionZ);
    }

    public static void disabler(int elytra) {
        mc.playerController.windowClick(0, elytra, 0, ClickType.PICKUP, mc.player);
        mc.playerController.windowClick(0, 6, 0, ClickType.PICKUP, mc.player);
        mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
        mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
        mc.playerController.windowClick(0, 6, 0, ClickType.PICKUP, mc.player);
        mc.playerController.windowClick(0, elytra, 0, ClickType.PICKUP, mc.player);
    }

    public static int getSlotWithElytra() {
        for (int i = 0; i < 45; ++i) {
            ItemStack itemStack = mc.player.inventoryContainer.getSlot(i).getStack();
            if (itemStack.getItem() == Items.ELYTRA) {
                return i < 9 ? i + 36 : i;
            }
        }
        return -1;
    }

    public static void swapElytraToChestplate() {
        for (ItemStack stack : mc.player.inventory.armorInventory) {
            if (stack.getItem() == Items.ELYTRA) {

                int slot = getSlowWithArmor() < 9 ? getSlowWithArmor() + 36 : getSlowWithArmor();
                if (getSlowWithArmor() != -1) {
                    mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, mc.player);
                    mc.playerController.windowClick(6, slot, 0, ClickType.PICKUP, mc.player);
                }
            }
        }
    }

    public static int getSlowWithArmor() {
        for (int i = 0; i < 45; ++i) {
            ItemStack itemStack = mc.player.inventoryContainer.getSlot(i).getStack();
            if (itemStack.getItem() == Items.DIAMOND_CHESTPLATE || itemStack.getItem() == Items.GOLDEN_CHESTPLATE || itemStack.getItem() == Items.LEATHER_CHESTPLATE || itemStack.getItem() == Items.CHAINMAIL_CHESTPLATE || itemStack.getItem() == Items.IRON_LEGGINGS) {
                return i < 9 ? i + 36 : i;
            }
        }
        return -1;
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent event) {
        if(fullNullCheck()) return;
        if (event.getPacket() instanceof SPacketPlayerPosLook) {
            this.toggle();
        }
    }


}