package com.mrzak34.thunderhack.modules.movement;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.events.ElytraEvent;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.Timer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.entity.MoverType;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemElytra;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

import java.util.Random;

public class ElytraFlight extends Module {
    public static ElytraFlight INSTANCE = new ElytraFlight();
    public ElytraFlight() {
        super("ElytraFlight", "бусты для 2б", Category.MOVEMENT);
    }

    private  Setting<Mode> mode =register( new Setting<>("Mode", Mode.BOOST));

    private  Setting<Boolean> groundSafety = register(new Setting<>("GroundSafety", false,v -> mode.getValue() == Mode.FIREWORK));
    private  Setting<Float> packetDelay = register(new Setting<>("Limit", 1F, 0.1F, 5F,v -> mode.getValue() == Mode.BOOST));
    private  Setting<Float> staticDelay = register(new Setting<>("Delay", 5F, 0.1F, 20F,v -> mode.getValue() == Mode.BOOST));
    private  Setting<Float> timeout = register(new Setting<>("Timeout", 0.5F, 0.1F, 1F,v-> mode.getValue() == Mode.BOOST));
    public  Setting<Boolean> stopMotion = register(new Setting<>("StopMotion", true,v -> mode.getValue() == Mode.BOOST));
    public  Setting<Boolean> freeze = register(new Setting<>("Freeze", false,v -> mode.getValue() == Mode.BOOST));
    public  Setting<Boolean> cruiseControl = register(new Setting<>("CruiseControl", false));
    public  Setting<Float> minUpSpeed = register(new Setting<>("MinUpSpeed", 0.5f, 0.1f, 5.0f,v -> mode.getValue() == Mode.BOOST && cruiseControl.getValue()));
    private  Setting<Boolean> autoSwitch = register(new Setting<>("AutoSwitch", false, v-> mode.getValue() == Mode.FIREWORK));
    public  Setting<Float> factor = register(new Setting<>("Factor", Float.valueOf(1.5f), Float.valueOf(0.1f), Float.valueOf(50.0f)));
    private  Setting<Integer> minSpeed = register(new Setting<>("MinSpeed", 20, 1, 50, v-> mode.getValue() == Mode.FIREWORK));
    public  Setting<Float> upFactor = register(new Setting<>("UpFactor", Float.valueOf(1.0f), Float.valueOf(0.0f), Float.valueOf(10.0f)));
    public  Setting<Float> downFactor = register(new Setting<>("DownFactor", Float.valueOf(1.0f), Float.valueOf(0.0f), Float.valueOf(10.0f)));
    public  Setting<Boolean> forceHeight = register(new Setting<>("ForceHeight", false, v-> mode.getValue() == Mode.FIREWORK || (mode.getValue() == Mode.BOOST && cruiseControl.getValue())));
    private  Setting<Integer> manualHeight = register(new Setting<>("Height", 121, 1, 256,v -> (mode.getValue() == Mode.FIREWORK || (mode.getValue() == Mode.BOOST && cruiseControl.getValue())) && forceHeight.getValue()));
    private  Setting<Float> triggerHeight = register(new Setting<>("TriggerHeight", 0.3F, 0.05F, 1F, v -> mode.getValue() == Mode.FIREWORK && groundSafety.getValue()));
    public  Setting<Float> speed = register(new Setting<>("Speed", Float.valueOf(1.0f), Float.valueOf(0.1f), Float.valueOf(10.0f), v-> mode.getValue() == Mode.CONTROL));
    private  Setting<Float> sneakDownSpeed = register(new Setting<>("DownSpeed", 1.0F, 0.1F, 10.0F,v -> mode.getValue() == Mode.CONTROL));
    private  Setting<Boolean> instantFly = register(new Setting<>("InstantFly", true));
    private  Setting<Boolean> boostTimer = register(new Setting<>("Timer", true,v -> mode.getValue() == Mode.BOOST));
    public  Setting<Boolean> speedLimit = register(new Setting<>("SpeedLimit", true,v-> mode.getValue() != Mode.FIREWORK));
    public  Setting<Float> maxSpeed = register(new Setting<>("MaxSpeed", Float.valueOf(2.5f), Float.valueOf(0.1f), Float.valueOf(10.0f), v-> speedLimit.getValue() && mode.getValue() != Mode.FIREWORK));
    public  Setting<Boolean> noDrag = (new Setting<>("NoDrag", false,v-> mode.getValue() != Mode.FIREWORK));


    private static boolean hasElytra = false;

    private boolean rSpeed;

    private double curSpeed;
    public double tempSpeed;

    private double height;

    private final Random random = new Random();

    private Timer instantFlyTimer = new Timer();
    private Timer staticTimer = new Timer();

    private Timer rocketTimer = new Timer();

    private Timer strictTimer = new Timer();

    private enum Mode {
        BOOST, CONTROL, FIREWORK
    }



    private boolean isJumping = false;
    private boolean hasTouchedGround = false;


    public void onEnable() {
        rSpeed = false;
        curSpeed = 0.0D;
        if (mc.player != null) {
            height = mc.player.posY;
            if (!mc.player.isCreative()) mc.player.capabilities.allowFlying = false;
            mc.player.capabilities.isFlying = false;
        }
        isJumping = false;
        hasElytra = false;
    }

    public void onDisable() {
        if (mc.player != null) {
            if (!mc.player.isCreative()) mc.player.capabilities.allowFlying = false;
            mc.player.capabilities.isFlying = false;
        }
       // KonasGlobals.INSTANCE.timerManager.resetTimer(this); //TODO
        Thunderhack.TICK_TIMER = 1.0f;
        hasElytra = false;
    }




    @Override
    public void onUpdate() {

            if (mc.world == null || mc.player == null) return;

            // if (event.getPhasea() != TickEvent.Phase.START) return;

            if (mc.player.onGround) {
                hasTouchedGround = true;
            }

            if (!cruiseControl.getValue()) {
                height = mc.player.posY;
            }

            for (ItemStack is : mc.player.getArmorInventoryList()) {
                if (is.getItem() instanceof ItemElytra) {
                    hasElytra = true;
                    break;
                } else {
                    hasElytra = false;
                }
            }

            if (strictTimer.passedMs(1500) && !strictTimer.passedMs(2000)) {
                //  KonasGlobals.INSTANCE.timerManager.resetTimer(this); //TODO
                Thunderhack.TICK_TIMER = 1.0f;
            }

            if (!mc.player.isElytraFlying()) {
                if (hasTouchedGround && boostTimer.getValue()  && !mc.player.onGround) {
                    //KonasGlobals.INSTANCE.timerManager.updateTimer(this, 25, 0.3F);//TODO
                    Thunderhack.TICK_TIMER = 0.3f;
                }
                if (!mc.player.onGround && instantFly.getValue() && mc.player.motionY < 0D) {
                    if (!instantFlyTimer.passedMs((long) (1000 * timeout.getValue()))) //кастанул к лонгу хз чо буит
                        return;
                    instantFlyTimer.reset();
                    mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
                    hasTouchedGround = false;
                    strictTimer.reset();
                }
                return;
            }

            if (mc.player == null) return;

        //if (!mc.player.isElytraFlying()) return;

            if (mode.getValue() != Mode.FIREWORK) return;

            if (mc.gameSettings.keyBindJump.isKeyDown()) {
                height += upFactor.getValue() * 0.5;
            } else if (mc.gameSettings.keyBindSneak.isKeyDown()) {
                height -= downFactor.getValue() * 0.5;
            }

            if (forceHeight.getValue()) {
                height = manualHeight.getValue();
            }

            Vec3d motionVector = new Vec3d(mc.player.motionX, mc.player.motionY, mc.player.motionZ);
            double bps = motionVector.length() * 20;

            double horizSpeed = Math.sqrt(mc.player.motionX * mc.player.motionX + mc.player.motionZ * mc.player.motionZ);
            double horizPct = MathHelper.clamp(horizSpeed / 1.7, 0.0, 1.0);
            double heightPct = 1 - Math.sqrt(horizPct);
            double minAngle = 0.6;

            if (horizPct >= 0.5 || mc.player.posY > height + 1) {
                double pitch = -((45 - minAngle) * heightPct + minAngle);

                double diff = (height + 1 - mc.player.posY) * 2;
                double heightDiffPct = MathHelper.clamp(Math.abs(diff), 0.0, 1.0);
                double pDist = -Math.toDegrees(Math.atan2(Math.abs(diff), horizSpeed * 30.0)) * Math.signum(diff);

                double adjustment = (pDist - pitch) * heightDiffPct;

                mc.player.rotationPitch = (float) pitch;
                mc.player.rotationPitch += (float) adjustment;
                mc.player.prevRotationPitch = mc.player.rotationPitch;
            }

            if (rocketTimer.passedMs((long) (1000 * factor.getValue()))) {
                double heightDiff = height - mc.player.posY;
                boolean shouldBoost = (heightDiff > 0.25 && heightDiff < 1.0) || bps < minSpeed.getValue();

                if (groundSafety.getValue()) {
                    Block bottomBlock = mc.world.getBlockState(new BlockPos(mc.player).down()).getBlock();
                    if (bottomBlock != Blocks.AIR && !(bottomBlock instanceof BlockLiquid)) {
                        if (mc.player.getEntityBoundingBox().minY - Math.floor(mc.player.getEntityBoundingBox().minY) > triggerHeight.getValue()) {
                            shouldBoost = true;
                        }
                    }
                }

                if (autoSwitch.getValue() && shouldBoost && mc.player.getHeldItemMainhand().getItem() != Items.FIREWORKS) {
                    for (int l = 0; l < 9; ++l) {
                        if (mc.player.inventory.getStackInSlot(l).getItem() == Items.FIREWORKS) {
                            mc.player.inventory.currentItem = l;
                            mc.playerController.updateController();
                            break;
                        }
                    }
                }

                if (mc.player.getHeldItemMainhand().getItem() == Items.FIREWORKS && shouldBoost) {
                    mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
                    rocketTimer.reset();
                }
            }

    }

    // Normal/Boost/Control mode
    @SubscribeEvent
    public void onElytra(ElytraEvent event) {
        if (mc.world == null || mc.player == null || !hasElytra || !mc.player.isElytraFlying()) return;

        if (mode.getValue() == Mode.FIREWORK) return;

        if (event.getEntity() == mc.player && mc.player.isServerWorld() || mc.player.canPassengerSteer() && !mc.player.isInWater() || mc.player != null && mc.player.capabilities.isFlying && !mc.player.isInLava() || mc.player.capabilities.isFlying && mc.player.isElytraFlying()) {

            event.setCanceled(true);

            if (mode.getValue() != Mode.BOOST) {

                Vec3d lookVec = mc.player.getLookVec();

                float pitch = mc.player.rotationPitch * 0.017453292F;

                double lookDist = Math.sqrt(lookVec.x * lookVec.x + lookVec.z * lookVec.z);
                double motionDist = Math.sqrt(mc.player.motionX * mc.player.motionX + mc.player.motionZ * mc.player.motionZ);
                double lookVecDist = lookVec.length();

                float cosPitch = MathHelper.cos(pitch);
                cosPitch = (float) ((double) cosPitch * (double) cosPitch * Math.min(1.0D, lookVecDist / 0.4D));

                // Vanilla Glide
                if (mode.getValue() != Mode.CONTROL) {
                    mc.player.motionY += -0.08D + (double) cosPitch * (0.06D / downFactor.getValue());
                }

                // Downwards movement
                if (mode.getValue() == Mode.CONTROL) {
                    // Goes down when sneaking, glides otherwise
                    if (mc.gameSettings.keyBindSneak.isKeyDown()) {
                        mc.player.motionY = -sneakDownSpeed.getValue();
                    } else if (!mc.gameSettings.keyBindJump.isKeyDown()) {
                        mc.player.motionY = -0.00000000000003D * downFactor.getValue();
                    }
                } else if (mode.getValue() != Mode.CONTROL && mc.player.motionY < 0.0D && lookDist > 0.0D) {
                    // Uses pitch go go down and gain speed
                    double downSpeed = mc.player.motionY * -0.1D * (double) cosPitch;
                    mc.player.motionY += downSpeed;
                    mc.player.motionX += (lookVec.x * downSpeed / lookDist) * factor.getValue();
                    mc.player.motionZ += (lookVec.z * downSpeed / lookDist) * factor.getValue();
                }

                // Upwards Movement
                if (pitch < 0.0F && mode.getValue() != Mode.CONTROL) {
                    // Normal/Boost mode - uses pitch to go up
                    double rawUpSpeed = motionDist * (double) (-MathHelper.sin(pitch)) * 0.04D;
                    mc.player.motionY += rawUpSpeed * 3.2D * upFactor.getValue();
                    mc.player.motionX -= lookVec.x * rawUpSpeed / lookDist;
                    mc.player.motionZ -= lookVec.z * rawUpSpeed / lookDist;
                } else if (mode.getValue() == Mode.CONTROL && mc.gameSettings.keyBindJump.isKeyDown()) {
                    // Control mode - goes up for as long as possible, then accelerates, then goes up again
                    if (motionDist > upFactor.getValue() / upFactor.getMax()) {
                        double rawUpSpeed = motionDist * 0.01325D;
                        mc.player.motionY += rawUpSpeed * 3.2D;
                        mc.player.motionX -= lookVec.x * rawUpSpeed / lookDist;
                        mc.player.motionZ -= lookVec.z * rawUpSpeed / lookDist;
                    } else {
                        double[] dir = directionSpeed(speed.getValue());
                        mc.player.motionX = dir[0];
                        mc.player.motionZ = dir[1];
                    }
                }

                // Turning
                if (lookDist > 0.0D) {
                    mc.player.motionX += (lookVec.x / lookDist * motionDist - mc.player.motionX) * 0.1D;
                    mc.player.motionZ += (lookVec.z / lookDist * motionDist - mc.player.motionZ) * 0.1D;
                }

                if (mode.getValue() == Mode.CONTROL && !mc.gameSettings.keyBindJump.isKeyDown()) {
                    // Sets motion in control mode
                    double[] dir = directionSpeed(speed.getValue());
                    mc.player.motionX = dir[0];
                    mc.player.motionZ = dir[1];
                }

                if (!noDrag.getValue()) {
                    mc.player.motionX *= 0.9900000095367432D;
                    mc.player.motionY *= 0.9800000190734863D;
                    mc.player.motionZ *= 0.9900000095367432D;
                }

                // Max speed
                double finalDist = Math.sqrt(mc.player.motionX * mc.player.motionX + mc.player.motionZ * mc.player.motionZ);

                if (speedLimit.getValue() && finalDist > maxSpeed.getValue()) {
                    mc.player.motionX *= maxSpeed.getValue() / finalDist;
                    mc.player.motionZ *= maxSpeed.getValue() / finalDist;
                }

                mc.player.move(MoverType.SELF, mc.player.motionX, mc.player.motionY, mc.player.motionZ);
            } else {
                float moveForward = mc.player.movementInput.moveForward;

                if (cruiseControl.getValue()) {
                    if (mc.gameSettings.keyBindJump.isKeyDown()) {
                        height += upFactor.getValue() * 0.5;
                    } else if (mc.gameSettings.keyBindSneak.isKeyDown()) {
                        height -= downFactor.getValue() * 0.5;
                    }

                    if (forceHeight.getValue()) {
                        height = manualHeight.getValue();
                    }

                    double horizSpeed = Math.sqrt(mc.player.motionX * mc.player.motionX + mc.player.motionZ * mc.player.motionZ);
                    double horizPct = MathHelper.clamp(horizSpeed / 1.7, 0.0, 1.0);
                    double heightPct = 1 - Math.sqrt(horizPct);
                    double minAngle = 0.6;

                    if (horizSpeed >= minUpSpeed.getValue() && instantFlyTimer.passedMs((long) (2000 * packetDelay.getValue()))) {
                        double pitch = -((45 - minAngle) * heightPct + minAngle);

                        double diff = (height + 1 - mc.player.posY) * 2;
                        double heightDiffPct = MathHelper.clamp(Math.abs(diff), 0.0, 1.0);
                        double pDist = -Math.toDegrees(Math.atan2(Math.abs(diff), horizSpeed * 30.0)) * Math.signum(diff);

                        double adjustment = (pDist - pitch) * heightDiffPct;

                        mc.player.rotationPitch = (float) pitch;
                        mc.player.rotationPitch += (float) adjustment;
                        mc.player.prevRotationPitch = mc.player.rotationPitch;
                    } else {
                        mc.player.rotationPitch = 0.25F;
                        mc.player.prevRotationPitch = 0.25F;
                        moveForward = 1F;
                    }
                }

                Vec3d vec3d = mc.player.getLookVec();

                float f = mc.player.rotationPitch * 0.017453292F;

                double d6 = Math.sqrt(vec3d.x * vec3d.x + vec3d.z * vec3d.z);
                double d8 = Math.sqrt(mc.player.motionX * mc.player.motionX + mc.player.motionZ * mc.player.motionZ);
                double d1 = vec3d.length();
                float f4 = MathHelper.cos(f);
                f4 = (float)((double)f4 * (double)f4 * Math.min(1.0D, d1 / 0.4D));
                mc.player.motionY += -0.08D + (double)f4 * 0.06D;

                if (mc.player.motionY < 0.0D && d6 > 0.0D) {
                    double d2 = mc.player.motionY * -0.1D * (double)f4;
                    mc.player.motionY += d2;
                    mc.player.motionX += vec3d.x * d2 / d6;
                    mc.player.motionZ += vec3d.z * d2 / d6;
                }

                if (f < 0.0F) {
                    double d10 = d8 * (double)(-MathHelper.sin(f)) * 0.04D;
                    mc.player.motionY += d10 * 3.2D;
                    mc.player.motionX -= vec3d.x * d10 / d6;
                    mc.player.motionZ -= vec3d.z * d10 / d6;
                }

                if (d6 > 0.0D) {
                    mc.player.motionX += (vec3d.x / d6 * d8 - mc.player.motionX) * 0.1D;
                    mc.player.motionZ += (vec3d.z / d6 * d8 - mc.player.motionZ) * 0.1D;
                }

                if (!noDrag.getValue()) {
                    mc.player.motionX *= 0.9900000095367432D;
                    mc.player.motionY *= 0.9800000190734863D;
                    mc.player.motionZ *= 0.9900000095367432D;
                }

                float yaw = mc.player.rotationYaw * 0.017453292F;

                if (f > 0F && mc.player.motionY < 0D) {
                    if (moveForward != 0F && instantFlyTimer.passedMs((long) (2000 * packetDelay.getValue())) && staticTimer.passedMs((long) (1000 * staticDelay.getValue()))) {
                        if (stopMotion.getValue()) {
                            mc.player.motionX = 0;
                            mc.player.motionZ = 0;
                        }
                        instantFlyTimer.reset();
                        mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
                    } else if (!instantFlyTimer.passedMs((long) (2000 * packetDelay.getValue()))) {
                        mc.player.motionX -= moveForward * Math.sin(yaw) * factor.getValue() / 20F;
                        mc.player.motionZ += moveForward * Math.cos(yaw) * factor.getValue() / 20F;
                        staticTimer.reset();
                    }
                }

                double finalDist = Math.sqrt(mc.player.motionX * mc.player.motionX + mc.player.motionZ * mc.player.motionZ);

                if (speedLimit.getValue() && finalDist > maxSpeed.getValue()) {
                    mc.player.motionX *= maxSpeed.getValue() / finalDist;
                    mc.player.motionZ *= maxSpeed.getValue() / finalDist;
                }

                if (freeze.getValue() && Keyboard.isKeyDown(Keyboard.KEY_LMENU)) {
                    mc.player.setVelocity(0, 0, 0);
                }

                mc.player.move(MoverType.SELF, mc.player.motionX, mc.player.motionY, mc.player.motionZ);
            }
        }
    }



    public static double[] directionSpeed(double speed) {
        float forward = mc.player.movementInput.moveForward;
        float side = mc.player.movementInput.moveStrafe;
        float yaw = mc.player.prevRotationYaw + (mc.player.rotationYaw - mc.player.prevRotationYaw) * mc.getRenderPartialTicks();

        if (forward != 0.0f) {
            if (side > 0.0f) {
                yaw += ((forward > 0.0f) ? -45 : 45);
            } else if (side < 0.0f) {
                yaw += ((forward > 0.0f) ? 45 : -45);
            }
            side = 0.0f;
            if (forward > 0.0f) {
                forward = 1.0f;
            } else if (forward < 0.0f) {
                forward = -1.0f;
            }
        }

        final double sin = Math.sin(Math.toRadians(yaw + 90.0f));
        final double cos = Math.cos(Math.toRadians(yaw + 90.0f));
        final double posX = forward * speed * cos + side * speed * sin;
        final double posZ = forward * speed * sin - side * speed * cos;
        return new double[]{posX, posZ};
    }
}
