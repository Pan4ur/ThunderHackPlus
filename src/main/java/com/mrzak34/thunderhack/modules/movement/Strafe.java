package com.mrzak34.thunderhack.modules.movement;

import com.google.common.eventbus.Subscribe;
import com.mojang.realmsclient.gui.ChatFormatting;
import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.command.Command;
import com.mrzak34.thunderhack.events.*;
import com.mrzak34.thunderhack.manager.EventManager;
import com.mrzak34.thunderhack.mixin.mixins.IEntityPlayerSP;
import com.mrzak34.thunderhack.mixin.mixins.ISPacketEntityVelocity;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.InventoryUtil;
import com.mrzak34.thunderhack.util.MovementUtil;
import com.mrzak34.thunderhack.util.Timer;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.ClickType;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static com.mrzak34.thunderhack.util.MovementUtil.getSpeed;
import static com.mrzak34.thunderhack.util.MovementUtil.isMoving;

public class Strafe extends Module {
    private final Setting<Mode> mode = this.register(new Setting<>("Mode", Mode.Matrix));
    private final Setting<Boost> boost = this.register(new Setting<>("Boost", Boost.None,v-> mode.getValue() == Mode.Matrix));
    public Setting<Float> setSpeed = this.register(new Setting<>("speed", 1.3F, 0.0F, 2f,v-> boost.getValue() == Boost.Elytra));
    public Setting<Boolean> onlyDown = register(new Setting<>("OnlyDown", false, v -> mode.getValue() == Mode.SunriseFast));
    private final Setting<Float> maxSpeed = this.register(new Setting<>("MaxSpeed", 0.9f, 0.0f, 2f, v -> mode.getValue() == Mode.SunriseFast));
    private final Setting<Float> velReduction = this.register(new Setting<>("Reduction", 6.0f, 0.1f, 10f, v -> boost.getValue() == Boost.Damage));
    private final Setting<Float> maxVelocitySpeed = this.register(new Setting<>("MaxVelocity", 0.8f, 0.1f, 2f, v -> boost.getValue() == Boost.Damage));
    public Setting<Boolean> extra = register(new Setting<>("Extra", false, v -> mode.getValue() == Mode.Matrix && boost.getValue() == Boost.Elytra));
    public Setting<Boolean> resetExtra = register(new Setting<>("ResetExtra", false, v -> extra.getValue()));
    private final Setting<Float> fdl1 = this.register(new Setting<>("Min Falldist", 1f, 0.0f, 3f, v -> extra.getValue()));
    private final Setting<Float> fdl2 = this.register(new Setting<>("Max Falldist", 2f, 0.0f, 5f, v -> extra.getValue()));
    private final Setting<Float> jme = this.register(new Setting<>("JumpMotionElytra", 0.65f, 0.1f, 1f, v -> extra.getValue()));
    private final Setting<Float> jmd = this.register(new Setting<>("JumpMotion", 0.2f, 0.1f, 1f, v -> extra.getValue()));
    private final Setting<Float> dpredict = this.register(new Setting<>("DisablerPredict", 0.5f, 0.01f, 1f, v -> extra.getValue()));
    private final Setting<Float> ogf = this.register(new Setting<>("OffGroundFriction", 2.55f, 0.01f, 3f, v -> extra.getValue()));
    private final Setting<Float> sprintm = this.register(new Setting<>("SprintMultiplier", 1.3f, 0.01f, 3f, v -> extra.getValue()));
    private final Setting<Integer> FrictionFactor = this.register(new Setting<>("FrictionFactor", 1646, 800, 3000, v -> extra.getValue()));


    private float waterTicks = 0;
    public static double oldSpeed, contextFriction;
    public static boolean needSwap, needSprintState;
    public static int noSlowTicks;
    public static float jumpTicks = 0;
    boolean skip = false;

    private final Timer elytraDelay = new Timer();
    private final Timer startDelay = new Timer();

    public Strafe() {
        super("Strafe", "testMove", Category.MOVEMENT);
    }


    @Override
    public void onEnable() {
        oldSpeed = 0;
        startDelay.reset();
        skip = true;
    }

    public boolean canStrafe() {
        if (mc.player.isSneaking()) {
            return false;
        }
        if (mc.player.isInLava()) {
            return false;
        }
        if(Thunderhack.moduleManager.getModuleByClass(RusherScaffold.class).isEnabled()){
            return false;
        }
        if(Thunderhack.moduleManager.getModuleByClass(Speed.class).isEnabled()){
            return false;
        }
        if(Thunderhack.moduleManager.getModuleByClass(CelkaEFly.class).isEnabled()){
            return false;
        }
        if(Thunderhack.moduleManager.getModuleByClass(Sprint.class).isEnabled() && Thunderhack.moduleManager.getModuleByClass(Sprint.class).Mode.getValue() == Sprint.mode.MatrixOmniSprint){
            if(mc.player.ticksExisted % 5 == 0) Command.sendMessage(ChatFormatting.RED +  "ВЫРУБИ БЛЯДСКИЙ СПРИНТ В РЕЖИМЕ ОМНИСПРИНТ!");
            return false;
        }
        if (mc.player.isInWater() || waterTicks > 0) {
            return false;
        }
        return !mc.player.capabilities.isFlying;
    }

    public double calculateSpeed(EventMove move) {
        float speedAttributes = getAIMoveSpeed();
        final float frictionFactor = mc.world.getBlockState(BlockPos.PooledMutableBlockPos.retain(mc.player.posX, mc.player.getEntityBoundingBox().minY - 0.1f, mc.player.posZ)).getBlock().slipperiness * 0.91F;
        float n6 = mc.player.isPotionActive(MobEffects.JUMP_BOOST) && mc.player.isHandActive() ? 0.88f : (float) (oldSpeed > 0.32 && mc.player.isHandActive() ? 0.88 : 0.91F);
        if (mc.player.onGround) {
            n6 = frictionFactor;
        }
        float n7 = (float) (((float) FrictionFactor.getValue() / 10000f ) / Math.pow(n6, 3.0));
        float n8;
        if (mc.player.onGround) {
            n8 = speedAttributes * n7;
            if (move.get_y() > 0) {
                n8 += boost.getValue() == Boost.Elytra && InventoryUtil.getElytra() != -1 && oldSpeed > 0.4 ? jme.getValue() : jmd.getValue(); // хуярим лонг джампами чтоб матрикс не втыкал
            }
        } else {
            n8 = ogf.getValue() / 100f;
        }
        boolean noslow = false;
        double max2 = oldSpeed + n8;
        double max = 0.0;
        if (mc.player.isHandActive() && move.get_y() <= 0) {
            double n10 = oldSpeed + n8 * 0.25;
            double motionY2 = move.get_y();
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
            max2 = Math.max(noslow ? 0 : 0.25, max2) - (mc.player.ticksExisted % 2 == 0 ? 0.001 : 0.002);
        }

        contextFriction = n6;

        if (!mc.player.onGround) {
            needSprintState = !((IEntityPlayerSP) mc.player).getServerSprintState();
            needSwap = true;
        } else {
            needSprintState = false;
        }
        return max2;
    }

    public float getAIMoveSpeed() {
        boolean prevSprinting = mc.player.isSprinting();
        mc.player.setSprinting(false);
        float speed = mc.player.getAIMoveSpeed() * sprintm.getValue();
        mc.player.setSprinting(prevSprinting);
        return speed;
    }

    public static void disabler(int elytra) {
        if (elytra != -2)
        {
            mc.playerController.windowClick(0, elytra, 1, ClickType.PICKUP, mc.player);
            mc.playerController.windowClick(0, 6, 1, ClickType.PICKUP, mc.player);
        }

        mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
        mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_FALL_FLYING));

        if (elytra != -2)
        {
            mc.playerController.windowClick(0, 6, 1, ClickType.PICKUP, mc.player);
            mc.playerController.windowClick(0, elytra, 1, ClickType.PICKUP, mc.player);
        }
    }

    @SubscribeEvent
    public void onMove(EventMove event) {
        if (mode.getValue() == Mode.Matrix) {
            int elytraSlot = InventoryUtil.getElytra();

            if (boost.getValue() == Boost.Elytra && elytraSlot != -1) {
                if (isMoving() && !mc.player.onGround && !mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(0.0, event.get_y(), 0.0f)).isEmpty() && mc.player.fallDistance < fdl2.getValue() && mc.player.fallDistance > fdl1.getValue()) {
                    oldSpeed = setSpeed.getValue();
                }
            }

            if (canStrafe()) {
                if (MovementUtil.isMoving()) {
                    double[] motions = MovementUtil.forward(calculateSpeed(event));
                    event.set_x(motions[0]);
                    event.set_z(motions[1]);
                } else {
                    oldSpeed = 0;
                    event.set_x(0);
                    event.set_z(0);
                }
            } else {
                oldSpeed = 0;
            }
        }
        event.setCanceled(true);
    }

    @SubscribeEvent
    public void updateValues(EventSync e) {
        oldSpeed = Math.hypot(mc.player.posX - mc.player.prevPosX,mc.player.posZ - mc.player.prevPosZ) * contextFriction;
        if (boost.getValue() == Boost.Elytra && isMoving() && !mc.player.onGround && !mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().expand(-0.29,0,-0.29).offset(0.0, -dpredict.getValue(), 0.0f)).isEmpty() && mc.player.fallDistance < fdl2.getDefaultValue() && mc.player.fallDistance > fdl1.getValue() && elytraDelay.passedMs(400)) {
            disabler(InventoryUtil.getElytra());
            elytraDelay.reset();
        }

        if (mc.player.isInWater()) {
            waterTicks = 10;
        } else {
            waterTicks--;
        }

        if (jumpTicks > 0) {
            jumpTicks--;
        }

        if(resetExtra.getValue()){
            fdl1.setValue(1f);
            fdl2.setValue(2f);
            jme.setValue(0.65f);
            jmd.setValue(0.2f);
            dpredict.setValue(0.5f);
            ogf.setValue(2.55f);
            sprintm.setValue(1.3f);
            FrictionFactor.setValue(1646);
            resetExtra.setValue(false);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPacketReceive(PacketEvent.Receive e) {
        if (e.getPacket() instanceof SPacketPlayerPosLook) {
            oldSpeed = 0;
        }
        SPacketEntityVelocity velocity;
        if (e.getPacket() instanceof SPacketEntityVelocity  && (velocity = e.getPacket()).getEntityID() == mc.player.getEntityId() && boost.getValue() == Boost.Damage) {
            if(mc.player.onGround) return;

            int vX =  velocity.getMotionX();
            int vZ =  velocity.getMotionZ();

            if (vX < 0) vX *= -1;
            if (vZ < 0) vZ *= -1;

            oldSpeed = (vX + vZ) / (velReduction.getValue() * 1000f);
            oldSpeed = Math.min(oldSpeed, maxVelocitySpeed.getValue());

            ((ISPacketEntityVelocity) velocity).setMotionX(0);
            ((ISPacketEntityVelocity) velocity).setMotionY(0);
            ((ISPacketEntityVelocity) velocity).setMotionZ(0);
        }
    }


    @SubscribeEvent
    public void actionEvent(EventSprint eventAction) {
        if (mode.getValue() == Mode.SunriseFast) {
            return;
        }
        if (canStrafe()) {
            if (EventManager.serversprint != needSprintState) {
                eventAction.setSprintState(!EventManager.serversprint);
            }
        }
        if (needSwap) {
            eventAction.setSprintState(!((IEntityPlayerSP) mc.player).getServerSprintState());
            needSwap = false;
        }
    }

    @SubscribeEvent
    public void onUpdate(PlayerUpdateEvent event) {
        if (mode.getValue() == Mode.ElytraMiniJump) {
            if (mc.player.onGround) {
                mc.player.jump();
                return;
            }
            if (!mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().expand(-0.29,0,-0.29).offset(0.0, -0.9, 0.0f)).isEmpty() && elytraDelay.passedMs(250) && startDelay.passedMs(500)) {
                int elytra = InventoryUtil.getElytra();
                if (elytra == -1) {
                    toggle();
                } else {
                    disabler(elytra);
                }
                mc.player.motionY = 0f;
                if (isMoving()) {
                    MovementUtil.setMotion(setSpeed.getValue());
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
                if (mc.player.onGround && !mc.player.movementInput.jump) {
                    mc.player.jump();
                    if (jumpTicks != 0) {
                        MovementUtil.setMotion(0.2);
                        return;
                    }
                    jumpTicks = 11;
                    MovementUtil.setMotion((float) (getSpeed() * setSpeed.getValue()));
                }
                if (!mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().expand(-0.29,0,-0.29).offset(0.0, -0.84, 0.0f)).isEmpty() && (!onlyDown.getValue() || mc.player.fallDistance > 0.05)) {
                    MovementUtil.setMotion(Math.min(getSpeed() * setSpeed.getValue(), maxSpeed.getValue()));
                }
            } else {
                if (mc.player.onGround)
                    mc.player.jump();
                if (mc.player.fallDistance > 0.05) {
                    skip = false;
                }
            }
        }
    }

    private enum Mode {
        Matrix, ElytraMiniJump, SunriseFast
    }

    private enum Boost {
        None, Elytra, Damage
    }
}
