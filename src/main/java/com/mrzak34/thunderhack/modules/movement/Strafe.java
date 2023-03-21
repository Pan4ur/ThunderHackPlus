package com.mrzak34.thunderhack.modules.movement;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.events.*;
import com.mrzak34.thunderhack.manager.EventManager;
import com.mrzak34.thunderhack.mixin.mixins.IEntityPlayerSP;
import com.mrzak34.thunderhack.mixin.mixins.IKeyBinding;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.InventoryUtil;
import com.mrzak34.thunderhack.util.Timer;
import com.mrzak34.thunderhack.util.phobos.IEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemAir;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static com.mrzak34.thunderhack.modules.movement.LegitStrafe.setSpeed;
import static com.mrzak34.thunderhack.util.MovementUtil.getSpeed;
import static com.mrzak34.thunderhack.util.MovementUtil.isMoving;

public class Strafe extends Module {
    public static double oldSpeed, contextFriction;
    public static boolean needSwap, prevSprint, needSprintState;
    public static int counter, noSlowTicks;
    public static float jumpTicks = 0;
    public Setting<Float> setSpeed = this.register(new Setting<>("speed", 1.3F, 0.0F, 2f));
    boolean skip = false;
    private final Setting<Mode> mode = this.register(new Setting<>("Mode", Mode.Matrix));
    public Setting<Boolean> elytra = register(new Setting<>("ElytraBoost", false, v -> mode.getValue() == Mode.Matrix));
    public Setting<Boolean> onlyDown = register(new Setting<>("OnlyDown", false, v -> mode.getValue() == Mode.SunriseFast));
    private final Setting<Float> maxSpeed = this.register(new Setting<>("MaxSpeed", 0.9f, 0.0f, 2f, v -> mode.getValue() == Mode.SunriseFast));
    private float waterTicks = 0;
    private final Timer fixTimer = new Timer();
    private final Timer elytraDelay = new Timer();
    private final Timer startDelay = new Timer();
    public Strafe() {
        super("Strafe", "testMove", Category.MOVEMENT);
    }

    public static float getDirection() {
        float rotationYaw = mc.player.rotationYaw;

        float strafeFactor = 0f;

        if (mc.player.movementInput.moveForward > 0)
            strafeFactor = 1;
        if (mc.player.movementInput.moveForward < 0)
            strafeFactor = -1;

        if (strafeFactor == 0) {
            if (mc.player.movementInput.moveStrafe > 0)
                rotationYaw -= 90;

            if (mc.player.movementInput.moveStrafe < 0)
                rotationYaw += 90;
        } else {
            if (mc.player.movementInput.moveStrafe > 0)
                rotationYaw -= 45 * strafeFactor;

            if (mc.player.movementInput.moveStrafe < 0)
                rotationYaw += 45 * strafeFactor;
        }

        if (strafeFactor < 0)
            rotationYaw -= 180;

        return (float) Math.toRadians(rotationYaw);
    }

    public static void setStrafe(double motion) {
        if (!isMoving()) return;
        double radians = getDirection();
        mc.player.motionX = -Math.sin(radians) * motion;
        mc.player.motionZ = Math.cos(radians) * motion;
    }

    public static float getMotion() {
        return (float) Math.sqrt(mc.player.motionX * mc.player.motionX + mc.player.motionZ * mc.player.motionZ);
    }

    public static void setMotion(double motion) {
        double forward = mc.player.movementInput.moveForward;
        double strafe = mc.player.movementInput.moveStrafe;
        float yaw = mc.player.rotationYaw;
        if (forward == 0 && strafe == 0) {
            mc.player.motionX = 0;
            mc.player.motionZ = 0;
            oldSpeed = 0;
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
            double cosinus = Math.cos(Math.toRadians(yaw + 90.0f));
            double sinus = Math.sin(Math.toRadians(yaw + 90.0f));

            mc.player.motionX = forward * motion * cosinus + strafe * motion * sinus;
            mc.player.motionZ = forward * motion * sinus - strafe * motion * cosinus;
        }
    }

    public static double calculateSpeed(MatrixMove move) {
        Minecraft mc = Minecraft.getMinecraft();
        boolean fromGround = mc.player.onGround;
        boolean toGround = move.toGround();
        boolean jump = move.getMotionY() > 0;
        float speedAttributes = getAIMoveSpeed(mc.player);
        final float frictionFactor = getFrictionFactor(mc.player, move);
        float n6 = mc.player.isPotionActive(MobEffects.JUMP_BOOST) && mc.player.isHandActive() ? 0.88f : (float) (oldSpeed > 0.32 && mc.player.isHandActive() ? 0.88 : 0.91F);
        if (fromGround) {
            n6 = frictionFactor;
        }
        float n7 = (float) (0.16277135908603668 / Math.pow(n6, 3.01));
        float n8;
        if (fromGround) {
            n8 = speedAttributes * n7;
            if (jump) {
                n8 += 0.2f;
            }
        } else {
            n8 = 0.0255f;
        }
        boolean noslow = false;
        double max2 = oldSpeed + n8;
        double max = 0.0;
        if (mc.player.isHandActive() && !jump) {
            double n10 = oldSpeed + n8 * 0.25;
            double motionY2 = move.getMotionY();
            if (motionY2 != 0.0 && Math.abs(motionY2) < 0.08) {
                n10 += 0.055;
            }
            if (max2 > (max = Math.max(0.043, n10))) {
                noslow = true;
                ++noSlowTicks;
            } else {
                noSlowTicks = Math.max(noSlowTicks - 1, 0);
            }
        } else {
            noSlowTicks = 0;
        }
        if (noSlowTicks > 3) {
            max2 = max - 0.019;
        } else {
            max2 = Math.max(noslow ? 0 : 0.25, max2) - (counter++ % 2 == 0 ? 0.001 : 0.002);
        }
        contextFriction = n6;
        if (!toGround && !fromGround) {
            needSwap = true;
        } else {
            prevSprint = false;
        }
        if (!fromGround && !toGround) {
            needSprintState = !((IEntityPlayerSP) mc.player).getServerSprintState();
        }
        if (toGround && fromGround) {
            needSprintState = false;
        }
        return max2;
    }

    public static float getAIMoveSpeed(EntityPlayer contextPlayer) {
        boolean prevSprinting = contextPlayer.isSprinting();
        contextPlayer.setSprinting(false);
        float speed = contextPlayer.getAIMoveSpeed() * 1.3f;
        contextPlayer.setSprinting(prevSprinting);
        return speed;
    }

    private static float getFrictionFactor(EntityPlayer contextPlayer, MatrixMove move) {
        BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = BlockPos.PooledMutableBlockPos.retain(move.getFromX(), move.getAABBFrom().minY - 1.0D, move.getFromZ());
        return contextPlayer.world.getBlockState(blockpos$pooledmutableblockpos).getBlock().slipperiness * 0.91F;
    }

    public static int findNullSlot() {
        for (int i = 0; i < 36; i++) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack.getItem() instanceof ItemAir) {
                if (i < 9) {
                    i += 36;
                }
                return i;
            }
        }
        return 999;
    }

    public static void strafe(float speed) {
        if (!isMoving()) {
            return;
        }
        double yaw = getDirection();
        mc.player.motionX = -Math.sin(yaw) * (double) speed;
        mc.player.motionZ = Math.cos(yaw) * (double) speed;
    }

    public static void disabler(int elytra) {
        if (elytra != -2) {
            mc.playerController.windowClick(0, elytra, 1, ClickType.PICKUP, mc.player);
            mc.playerController.windowClick(0, 6, 1, ClickType.PICKUP, mc.player);
        }
        mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
        mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
        if (elytra != -2) {
            mc.playerController.windowClick(0, 6, 1, ClickType.PICKUP, mc.player);
            mc.playerController.windowClick(0, elytra, 1, ClickType.PICKUP, mc.player);
        }
    }

    @SubscribeEvent
    public void onPlayerUpdate(PlayerUpdateEvent e) {
        if (mode.getValue() == Mode.Matrix) {
            if (!elytra.getValue()) return;
            int elytra = getHotbarSlotOfItem();

            if (mc.player.isInWater() || mc.player.isInLava() || waterTicks > 0 || elytra == -1 || ((IEntity)mc.player).isInWeb())
                return;
            if (mc.player.fallDistance != 0 && mc.player.fallDistance < 0.1 && mc.player.motionY < -0.1) {
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
            }
        }
        if (jumpTicks > 0) {
            jumpTicks--;
        }
    }

    @SubscribeEvent
    public void onMove(MatrixMove event) {
        if (mode.getValue() == Mode.Matrix) {
            int elytraSlot = getHotbarSlotOfItem();

            if (elytra.getValue() && elytraSlot != -1) {
                if (isMoving() && !mc.player.onGround && mc.player.fallDistance >= 0.15 && event.toGround()) {
                    setMotion(setSpeed.getValue());
                    oldSpeed = (setSpeed.getValue() / 1.06);
                }
            }

            if (mc.player.isInWater()) {
                waterTicks = 10;
            } else {
                waterTicks--;
            }

            if (strafes()) {
                double forward = mc.player.movementInput.moveForward;
                double strafe = mc.player.movementInput.moveStrafe;
                float yaw = mc.player.rotationYaw;
                if (forward == 0.0 && strafe == 0.0) {
                    oldSpeed = 0;
                    event.setMotionX(0);
                    event.setMotionZ(0);
                } else {

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
                    double speed = calculateSpeed(event);
                    double cos = Math.cos(Math.toRadians(yaw + 90.0f)), sin = Math.sin(Math.toRadians(yaw + 90.0f));
                    event.setMotionX(forward * speed * cos + strafe * speed * sin);
                    event.setMotionZ(forward * speed * sin - strafe * speed * cos);
                    event.setCanceled(true);
                }
            } else {
                oldSpeed = 0;
            }
        }
    }

    @SubscribeEvent
    public void updateValues(EventSync e) {
        double distTraveledLastTickX = mc.player.posX - mc.player.prevPosX;
        double distTraveledLastTickZ = mc.player.posZ - mc.player.prevPosZ;
        oldSpeed = (Math.sqrt(distTraveledLastTickX * distTraveledLastTickX + distTraveledLastTickZ * distTraveledLastTickZ)) * contextFriction;
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive e) {
        if (e.getPacket() instanceof SPacketPlayerPosLook) {
            oldSpeed = 0;
        }
    }

    @Override
    public void onEnable() {
        oldSpeed = 0;
        startDelay.reset();
        skip = true;
    }

    public boolean strafes() {
        if (mc.player == null)
            return false;
        if (mc.player.isSneaking()) {
            return false;
        }
        if (mc.player.isInLava()) {
            return false;
        }
        if(Thunderhack.moduleManager.getModuleByClass(RusherScaffold.class).isEnabled()){
            return false;
        }
        if (mc.player.isInWater() || waterTicks > 0) {
            return false;
        }
        return !mc.player.capabilities.isFlying;
    }

    @SubscribeEvent
    public void actionEvent(EventSprint eventAction) {
        if (mode.getValue() == Mode.SunriseFast) {
            return;
        }
        if (strafes()) {
            if (EventManager.serversprint != needSprintState) {
                eventAction.setSprintState(!EventManager.serversprint);
            }
        }
        if (needSwap) {
            eventAction.setSprintState(!((IEntityPlayerSP) mc.player).getServerSprintState());
            needSwap = false;
        }
    }

    private int getHotbarSlotOfItem() {
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

    @SubscribeEvent
    public void onUpdate(PlayerUpdateEvent event) {
        if (mode.getValue() == Mode.ElytraMiniJump) {
            if (mc.player.onGround) {
                mc.player.jump();
                return;
            }
            if (!mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(0.0, -0.9, 0.0f)).isEmpty() && elytraDelay.passedMs(250) && startDelay.passedMs(500)) {
                int elytra = InventoryUtil.getElytra();
                if (elytra == -1) {
                    this.toggle();
                } else {
                    mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SPRINTING));
                    disabler(elytra);
                }
                mc.player.motionY = 0;
                if (isMoving()) {
                    setSpeed(setSpeed.getValue());
                }
                elytraDelay.reset();
            }
        }
        if (mode.getValue() == Mode.SunriseFast) {
            if (mc.player.ticksExisted % 6 == 0) {
                int elytra = InventoryUtil.getElytra();
                if (elytra == -1) {
                    this.toggle();
                } else {
                    disabler(elytra);
                }
            }
            if (!skip) {
                if (mc.player.onGround && !((IKeyBinding)mc.gameSettings.keyBindJump).isPressed()) {
                    mc.player.jump();
                    if (jumpTicks != 0) {
                        strafe((float) (0.2));
                        return;
                    }
                    jumpTicks = 11;
                    strafe((float) (getSpeed() * setSpeed.getValue()));
                }
                if (!mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(0.0, -0.84, 0.0f)).isEmpty() && (!onlyDown.getValue() || mc.player.fallDistance > 0.05)) {
                    setMotion(Math.min(getSpeed() * setSpeed.getValue(), maxSpeed.getValue()));
                }
            } else {
                if (mc.player.onGround)
                    mc.player.jump();
                if (mc.player.fallDistance > 0.05) {
                    skip = false;
                }
            }
        }
        fixElytra();
    }


    private enum Mode {
        Matrix, ElytraMiniJump, SunriseFast
    }


}
