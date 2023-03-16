package com.mrzak34.thunderhack.modules.movement;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.command.Command;
import com.mrzak34.thunderhack.events.EventPlayerTravel;
import com.mrzak34.thunderhack.events.EventPreMotion;
import com.mrzak34.thunderhack.events.PacketEvent;
import com.mrzak34.thunderhack.events.TurnEvent;
import com.mrzak34.thunderhack.mixin.mixins.ISPacketPlayerPosLook;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.Timer;
import com.mrzak34.thunderhack.util.math.MathUtil;
import com.mrzak34.thunderhack.util.render.RenderUtil;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static com.mrzak34.thunderhack.util.PyroSpeed.isMovingClient;

public class ElytraFly2b2tNew extends Module {

    private final Setting<Float> speedSetting = register(new Setting<>("FSpeed", 16F, 0.1F, 20F));
    public Setting<Boolean> timerControl = this.register(new Setting<>("Timer", true));
    public Setting<Boolean> durabilityWarning = this.register(new Setting<>("ToggleIfLow", true));
    public Setting<Boolean> glide = register(new Setting<>("Glide", false));
    private final Setting<Float> glideSpeed = register(new Setting<>("GlideSpeed", 1F, 0.1F, 10f, v -> glide.getValue()));
    public Setting<Float> speed = this.register(new Setting<>("Speed", 0.8F, 0.1F, 5F));
    public Setting<Float> speedM = this.register(new Setting<>("MaxSpeed", 0.8F, 0.1F, 5F));
    public Setting<Integer> acceleration = this.register(new Setting<>("Boost", 60, 0, 100));
    public Setting<Float> boost_delay = this.register(new Setting<>("BoostDelay", 1.5F, 0.1F, 3F));
    int acceleration_ticks = 0;
    double current_speed;
    private boolean elytraIsEquipped = false;
    private int elytraDurability = 0;
    private boolean isFlying = false;
    private boolean isStandingStillH = false;
    private double hoverTarget = -1.0f;
    private boolean hoverState = false;
    private final Timer accelerationDelay = new Timer();
    private float dYaw = 0F;
    private float dPitch = 0F;


    public ElytraFly2b2tNew() {
        super("ElytraFly2b2tNew", "ElytraFly2b2tNew", Category.MOVEMENT);
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive e) {
        if (fullNullCheck()) return;
        if (mc.player.isSpectator() || !elytraIsEquipped || elytraDurability <= 1 || !isFlying) return;
        if (e.getPacket() instanceof SPacketPlayerPosLook) {
            SPacketPlayerPosLook packet = e.getPacket();
            ((ISPacketPlayerPosLook)packet).setPitch(mc.player.rotationPitch);
            acceleration_ticks = 0;
            accelerationDelay.reset();
        }

    }

    public void flyyyyy() {

        final double[] dir = MathUtil.directionSpeed((float) RenderUtil.interpolate(speedM.getValue(), speed.getValue(), ((float) Math.min(acceleration_ticks, acceleration.getValue()) / (float) acceleration.getValue())));
        if (Flight.mc.player.movementInput.moveStrafe != 0.0f || Flight.mc.player.movementInput.moveForward != 0.0f) {
            Flight.mc.player.motionX = dir[0];
            Flight.mc.player.motionZ = dir[1];
        }
        if (glideSpeed.getValue() != 0.0 && !mc.gameSettings.keyBindJump.isKeyDown() && !mc.gameSettings.keyBindSneak.isKeyDown())
            mc.player.motionY = -glideSpeed.getValue();
    }

    @SubscribeEvent
    public void onElytra(EventPlayerTravel event) {
        if (mc.player.isSpectator()) return;
        stateUpdate();
        flyyyyy();
        if (elytraIsEquipped && elytraDurability > 1) {
            if (!isFlying) {
                takeoff();
            } else {
                Thunderhack.TICK_TIMER = 1f;
                mc.player.setSprinting(false);
                controlMode(event);
            }
        } else {
            reset2(true);
        }
        if (accelerationDelay.passedMs((long) (boost_delay.getValue() * 1000)))
            ++acceleration_ticks;
    }

    public void stateUpdate() {
        ItemStack armorSlot = mc.player.inventory.armorInventory.get(2);
        elytraIsEquipped = armorSlot.getItem() == Items.ELYTRA;
        if (elytraIsEquipped) {
            int oldDurability = elytraDurability;
            elytraDurability = armorSlot.getMaxDamage() - armorSlot.getItemDamage();
            if (!mc.player.onGround && oldDurability != elytraDurability) {
                if (elytraDurability <= 1) {
                    if (durabilityWarning.getValue()) {
                        mc.getSoundHandler().playSound(PositionedSoundRecord.getRecord(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f));
                        Command.sendMessage("Elytra is low, disabling!");
                        toggle();
                    }
                }
            }
        } else elytraDurability = 0;

        isFlying = mc.player.isElytraFlying();

        isStandingStillH = mc.player.movementInput.moveForward == 0f && mc.player.movementInput.moveStrafe == 0f;

        if (shouldSwing()) {
            mc.player.prevLimbSwingAmount = mc.player.limbSwingAmount;
            mc.player.limbSwing += 1.3;
            float speedRatio = (float) (current_speed / (float) RenderUtil.interpolate(speedM.getValue(), speed.getValue(), ((float) Math.min(acceleration_ticks, acceleration.getValue()) / (float) acceleration.getValue()))); //TODO BETTER SPEED
            mc.player.limbSwingAmount += ((speedRatio * 1.2) - mc.player.limbSwingAmount) * 0.4f;
        }
    }

    @SubscribeEvent
    public void updateValues(EventPreMotion e) {
        double distTraveledLastTickX = mc.player.posX - mc.player.prevPosX;
        double distTraveledLastTickZ = mc.player.posZ - mc.player.prevPosZ;
        current_speed = (Math.sqrt(distTraveledLastTickX * distTraveledLastTickX + distTraveledLastTickZ * distTraveledLastTickZ));
    }

    private void reset2(boolean cancelflu) {
        Thunderhack.TICK_TIMER = 1f;
        acceleration_ticks = 0;
        accelerationDelay.reset();
    }

    private void takeoff() {
        if (mc.player.onGround) {
            reset2(mc.player.onGround);
            return;
        }
        if (mc.player.motionY < 0) {
            if (timerControl.getValue() && !mc.isSingleplayer())
                Thunderhack.TICK_TIMER = 0.1f;
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
            hoverTarget = (float) (mc.player.posY + 0.2);
        }
    }

    private void setSpeed(double yaw) {
        mc.player.setVelocity(Math.sin(-yaw) * (float) RenderUtil.interpolate(speedM.getValue(), speed.getValue(), ((float) Math.min(acceleration_ticks, acceleration.getValue()) / (float) acceleration.getValue())), mc.player.motionY, Math.cos(yaw) * (float) RenderUtil.interpolate(speedM.getValue(), speed.getValue(), ((float) Math.min(acceleration_ticks, acceleration.getValue()) / (float) acceleration.getValue())));
    }

    private void controlMode(EventPlayerTravel event) {
        double currentSpeed = Math.sqrt(mc.player.motionX * mc.player.motionX + mc.player.motionZ * mc.player.motionZ);
        if (hoverTarget < 0.0) {
            hoverTarget = mc.player.posY;
        }
        hoverState = getHoverState();
        if (!isStandingStillH) {
            if ((hoverState) && (currentSpeed >= 0.8 || mc.player.motionY > 1.0)) {

            } else {
                mc.player.motionY = -0.00000000000003;
                setSpeed(calcMoveYaw());
            }
        } else mc.player.setVelocity(0.0, 0.0, 0.0); /* Stop moving if no inputs are pressed */
        event.setCanceled(true);
    }

    private boolean shouldSwing() {
        return isFlying;
    }

    @SubscribeEvent
    public void Skid(EventPreMotion e) {
        mc.player.rotationPitch = -2.3f;
    }

    public boolean getHoverState() {
        if (hoverState) {
            return mc.player.posY < hoverTarget;
        } else {
            return false;
        }
    }

    @Override
    public void onDisable() {
        reset2(true);
        mc.player.capabilities.isFlying = false;
        mc.player.capabilities.setFlySpeed(0.05f);
    }

    public double calcMoveYaw() {
        double strafe = 90 * mc.player.moveStrafing;
        strafe *= (mc.player.moveForward != 0F) ? mc.player.moveForward * 0.5F : 1F;

        double yaw = mc.player.rotationYaw - strafe;
        yaw -= (mc.player.moveForward < 0F) ? 180 : 0;

        return Math.toRadians(yaw);
    }

    @SubscribeEvent
    public void onCameraSetup(EntityViewRenderEvent.CameraSetup event) {
        if (isMovingClient()) {
            event.setYaw(event.getYaw() + dYaw);
            event.setPitch(event.getPitch() + dPitch);
        } else {
            dYaw = 0;
            dPitch = 0;
        }
    }

    @SubscribeEvent
    public void onTurnEvent(TurnEvent event) {
        if (isMovingClient()) {
            dYaw = (float) ((double) dYaw + (double) event.getYaw() * 0.15D);
            dPitch = (float) ((double) dPitch - (double) event.getPitch() * 0.15D);
            dPitch = MathHelper.clamp(dPitch, -90.0F, 90.0F);
            event.setCanceled(true);
        } else {
            dYaw = 0;
            dPitch = 0;
        }
    }


    @Override
    public void onEnable() {
        dYaw = 0;
        dPitch = 0;
    }


}
