package com.mrzak34.thunderhack.modules.movement;


import com.mrzak34.thunderhack.command.Command;
import com.mrzak34.thunderhack.events.PacketEvent;
import com.mrzak34.thunderhack.events.PlayerUpdateEvent;
import com.mrzak34.thunderhack.mixin.mixins.IKeyBinding;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.Timer;
import com.mrzak34.thunderhack.util.render.RenderUtil;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static com.mrzak34.thunderhack.modules.movement.Strafe.findNullSlot;
import static com.mrzak34.thunderhack.modules.player.ElytraSwap.*;
import static com.mrzak34.thunderhack.util.MovementUtil.isMoving;

public class LegitStrafe extends Module {

    public Setting<Float> motion2 = this.register(new Setting<>("motionY", 0.42F, 0F, 0.84F));
    public Setting<Float> speed = this.register(new Setting<>("Speed", 0.8F, 0.1F, 3F));
    public Setting<Float> speedM = this.register(new Setting<>("MaxSpeed", 0.8F, 0.1F, 3F));
    public Setting<Integer> acceleration = this.register(new Setting<>("Acceleration", 60, 0, 100));
    public Setting<Boolean> onlyDown = register(new Setting<>("Silent", true));
    public Setting<Float> jitterY = this.register(new Setting<>("JitterY", 0.2F, 0F, 0.42));
    int prevElytraSlot = -1;
    int acceleration_ticks = 0;
    private final Timer timer = new Timer();
    private final Timer fixTimer = new Timer();
    public LegitStrafe() {
        super("GlideFly", "флай на саник-хуй пососаник", Category.MOVEMENT);
    }

    public static int getElly() {
        for (ItemStack stack : mc.player.getArmorInventoryList()) {
            if (stack.getItem() == Items.ELYTRA) {
                return -2;
            }
        }
        int slot = -1;
        for (int i = 0; i < 36; i++) {
            ItemStack s = mc.player.inventory.getStackInSlot(i);
            if (s.getItem() == Items.ELYTRA) {
                slot = i;
                break;
            }
        }
        if (slot < 9 && slot != -1) {
            slot = slot + 36;
        }
        return slot;
    }

    public static void setSpeed(float speed) {
        float yaw = mc.player.rotationYaw;
        float forward = mc.player.movementInput.moveForward;
        float strafe = mc.player.movementInput.moveStrafe;
        if (forward != 0.0F) {
            if (strafe > 0.0F) {
                yaw += (float) (forward > 0.0F ? -45 : 45);
            } else if (strafe < 0.0F) {
                yaw += (float) (forward > 0.0F ? 45 : -45);
            }

            strafe = 0.0F;
            if (forward > 0.0F) {
                forward = 1.0F;
            } else if (forward < 0.0F) {
                forward = -1.0F;
            }
        }

        double cos = Math.cos(Math.toRadians(yaw + 90.0F));
        double sin = Math.sin(Math.toRadians(yaw + 90.0F));
        mc.player.motionX = (double) (forward * speed) * cos + (double) (strafe * speed) * sin;
        mc.player.motionZ = (double) (forward * speed) * sin - (double) (strafe * speed) * cos;
    }

    public static void setSpeed2(float speed) {
        float yaw = mc.player.rotationYaw;
        float forward = 1.0F;
        double cos = Math.cos(Math.toRadians(yaw + 90.0F));
        double sin = Math.sin(Math.toRadians(yaw + 90.0F));
        mc.player.motionX = (double) (forward * speed) * cos;
        mc.player.motionZ = (double) (forward * speed) * sin;
    }

    @SubscribeEvent
    public void onEvent222(PlayerUpdateEvent event) {
        if (!onlyDown.getValue()) {
            if (mc.player.ticksExisted % 2 != 0) return;
            ItemStack itemStack = getItemStack(38);
            if (itemStack == null) return;
            if (mc.player.onGround) return;

            if (itemStack.getItem() == Items.ELYTRA) {
                if (prevElytraSlot != -1) {
                    clickSlot(prevElytraSlot);
                    clickSlot(38);
                    clickSlot(prevElytraSlot);
                }
            } else if (hasItem(Items.ELYTRA)) {
                prevElytraSlot = getSlot(Items.ELYTRA);
                clickSlot(prevElytraSlot);
                clickSlot(38);
                clickSlot(prevElytraSlot);
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_FALL_FLYING));

                mc.player.motionY = jitterY.getValue();

                if (((IKeyBinding)mc.gameSettings.keyBindJump).isPressed()) {
                    mc.player.motionY = motion2.getValue();
                } else if (((IKeyBinding)mc.gameSettings.keyBindSneak).isPressed()) {
                    mc.player.motionY = -motion2.getValue();
                }
                if (isMoving()) {
                    setSpeed(speed.getValue());
                } else {
                    setSpeed2(0.1f);
                }
            }
        } else {
            int elytra = getElly();

            if (elytra == -1) {
                Command.sendMessage("Нет элитр!");
                toggle();
                return;
            }

            if (mc.player.onGround) {
                mc.player.jump();
                timer.reset();
                acceleration_ticks = 0;
            } else if (timer.passedMs(350)) {

                if (mc.player.ticksExisted % 2 == 0) {
                    disabler2(elytra);
                }

                mc.player.motionY = mc.player.ticksExisted % 2 != 0 ? -0.25 : 0.25;


                if (!mc.player.isSneaking() && ((IKeyBinding)mc.gameSettings.keyBindJump).isPressed()) {
                    mc.player.motionY = motion2.getValue();
                }
                if (mc.gameSettings.keyBindSneak.isKeyDown()) {
                    mc.player.motionY = -motion2.getValue();
                }
                if (isMoving()) {
                    setSpeed((float) RenderUtil.interpolate(speedM.getValue(), speed.getValue(), ((float) Math.min(acceleration_ticks, acceleration.getValue()) / (float) acceleration.getValue())));
                } else {
                    acceleration_ticks = 0;
                    setSpeed2(0.1f);
                }
            }
        }
        acceleration_ticks++;
        fixElytra();
    }

    public void fixElytra() {
        ItemStack stack = mc.player.inventory.getItemStack();
        if (stack != null && stack.getItem() instanceof ItemArmor && fixTimer.passed(300)) {
            ItemArmor ia = (ItemArmor) stack.getItem();
            if (ia.armorType == EntityEquipmentSlot.CHEST && mc.player.inventory.armorItemInSlot(2).getItem() == Items.ELYTRA) {
                mc.playerController.windowClick(0, 6, 1, ClickType.PICKUP, mc.player);
                int nullSlot = findNullSlot();
                boolean needDrop = nullSlot == 999;
                if (needDrop) {
                    nullSlot = 9;
                }
                mc.playerController.windowClick(0, nullSlot, 1, ClickType.PICKUP, mc.player);
                if (needDrop) {
                    mc.playerController.windowClick(0, -999, 1, ClickType.PICKUP, mc.player);
                }
                fixTimer.reset();
            }
        }
    }

    public void disabler2(int elytra) {
        if (elytra != -2) {
            mc.playerController.windowClick(0, elytra, 1, ClickType.PICKUP, mc.player);
            mc.playerController.windowClick(0, 6, 1, ClickType.PICKUP, mc.player);
        }

        if (!onlyDown.getValue())
            mc.getConnection().sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
        mc.getConnection().sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_FALL_FLYING));

        if (elytra != -2) {
            mc.playerController.windowClick(0, 6, 1, ClickType.PICKUP, mc.player);
            mc.playerController.windowClick(0, elytra, 1, ClickType.PICKUP, mc.player);
        }
    }

    @Override
    public void onEnable() {
        acceleration_ticks = 0;
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive e) {
        if (e.getPacket() instanceof SPacketPlayerPosLook) {
            acceleration_ticks = 0;
        }
    }

}
