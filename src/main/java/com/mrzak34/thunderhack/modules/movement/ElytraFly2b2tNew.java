package com.mrzak34.thunderhack.modules.movement;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.command.Command;
import com.mrzak34.thunderhack.event.events.*;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.Timer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.*;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.List;

import static com.mrzak34.thunderhack.util.PyroSpeed.isMovingClient;

public class ElytraFly2b2tNew extends Module {

    public ElytraFly2b2tNew() {
        super("ElytraFly2b2tNew", "ElytraFly2b2tNew", Category.MOVEMENT, true, false, false);
    }




    public Setting<Float> speedControl = this.register(new Setting<>("ESpeed", 3.2f, 0.1f, 10.0f));
    public Setting<Boolean> timerControl = this.register(new Setting<>("Timer", true));
    public Setting<Boolean> durabilityWarning = this.register(new Setting<>("ToggleIfLow", true));
    private final Setting<Float> speedSetting = register(new Setting<>("FSpeed", 16F, 0.1F, 20F));
    public Setting<Boolean> glide = register(new Setting<>("Glide", false));
    private final Setting<Float> glideSpeed = register(new Setting<>("GlideSpeed", 1F, 0.1F, 10f ,v ->glide.getValue()));

    private boolean elytraIsEquipped = false;
    private int elytraDurability = 0;
    private boolean isFlying = false;
    private boolean isStandingStillH = false;

    private double hoverTarget = -1.0f;
    private boolean hoverState = false;


    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive e){
        if(fullNullCheck())return;
        if (mc.player.isSpectator() || !elytraIsEquipped || elytraDurability <= 1 || !isFlying) return;
        if (e.getPacket() instanceof SPacketPlayerPosLook) {
            SPacketPlayerPosLook packet = e.getPacket();
            packet.pitch = mc.player.rotationPitch;
        }

    }

    public void flyyyyy(){
        mc.player.capabilities.isFlying = true;
        mc.player.capabilities.setFlySpeed(speedSetting.getValue() / 11.11f);

        if (glideSpeed.getValue() != 0.0
                && !mc.gameSettings.keyBindJump.isKeyDown()
                && !mc.gameSettings.keyBindSneak.isKeyDown()) mc.player.motionY = -glideSpeed.getValue();
    }


    @SubscribeEvent
    public void onElytra(EventPlayerTravel event) {
        if (mc.player.isSpectator()) return;
        stateUpdate(event);
        flyyyyy();
        if (elytraIsEquipped && elytraDurability > 1) {
            if (!isFlying) {
                takeoff(event);
            } else {
                Thunderhack.TICK_TIMER = 1f;
                mc.player.setSprinting(false);
                controlMode(event);
            }
        } else {
            reset2(true);
        }
    }

    public void stateUpdate(EventPlayerTravel event) {
        ItemStack armorSlot = mc.player.inventory.armorInventory.get(2);
        elytraIsEquipped = armorSlot.item == Items.ELYTRA;
        if (elytraIsEquipped) {
            int oldDurability = elytraDurability;
            elytraDurability = armorSlot.getMaxDamage() - armorSlot.itemDamage;
            if (!mc.player.onGround && oldDurability != elytraDurability) {
                if (elytraDurability <= 1) {
                    if (durabilityWarning.getValue()) {
                        mc.soundHandler.playSound(PositionedSoundRecord.getRecord(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f));
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
            float speedRatio = (float) (Thunderhack.speedManager.getSpeedKpH() / speedControl.getValue()); //TODO BETTER SPEED
            mc.player.limbSwingAmount += ((speedRatio * 1.2) - mc.player.limbSwingAmount) * 0.4f;
        }
    }

    private void reset2(boolean cancelflu) {
        isFlying = false;
        Thunderhack.TICK_TIMER = 1f;
        mc.player.capabilities.setFlySpeed(0.05f);
        if (cancelflu) mc.player.capabilities.isFlying = false;
    }



    private void takeoff(EventPlayerTravel event) {
        boolean closeToGround = mc.player.posY <= getGroundPos(mc.player).y + 0.1f  && !mc.isSingleplayer();

        if (mc.player.onGround) {
            reset2(mc.player.onGround);
            return;
        }

        if (mc.player.motionY < 0) {
            if (closeToGround) {
                Thunderhack.TICK_TIMER = 0.5f;
                return;
            }

            if (!mc.isSingleplayer()) {
                event.setCanceled(true);
                mc.player.setVelocity(0.0, -0.02, 0.0);
            }

            if (timerControl.getValue() && !mc.isSingleplayer()) Thunderhack.TICK_TIMER = 0.125f;
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
            hoverTarget = (float) (mc.player.posY + 0.2);
        }
    }



    private double getSpeed(){
        return speedControl.getValue();
    }



    private void setSpeed(double yaw) {
        double acceleratedSpeed = getSpeed();
        mc.player.setVelocity(Math.sin(-yaw) * acceleratedSpeed, mc.player.motionY, Math.cos(yaw) * acceleratedSpeed);
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





    private boolean shouldSwing(){
        return isFlying;
    }




    @SubscribeEvent
    public void Skid(EventPreMotion e){
        mc.player.rotationPitch = -2.3f;
    }

    public boolean getHoverState(){
        if (hoverState) {
           return mc.player.posY < hoverTarget ;
        } else {
           return false;
        }
    }




    @Override
    public void onDisable(){
        reset2(true);
        mc.player.capabilities.isFlying = false;
        mc.player.capabilities.setFlySpeed(0.05f);
    }

    public double calcMoveYaw(){
        double strafe = 90 * mc.player.moveStrafing;
        strafe *= (mc.player.moveForward != 0F) ? mc.player.moveForward * 0.5F : 1F;

        double yaw = mc.player.rotationYaw - strafe;
        yaw -=  (mc.player.moveForward < 0F) ? 180 : 0;

        return Math.toRadians(yaw);
    }


    public Vec3d getGroundPos(Entity entity){
            List<RayTraceResult> results = rayTraceBoundingBoxToGround(entity);
            double minY = 0;
            Vec3d returnresult = null;
            for (RayTraceResult result : results){
                if(result.typeOfHit == RayTraceResult.Type.MISS){
                    return new Vec3d(0.0, -999.0, 0.0);
                } else {
                    if(minY < result.hitVec.y){
                        minY = result.hitVec.y;
                        returnresult = new Vec3d( result.hitVec.x, result.hitVec.y, result.hitVec.z);
                    }
                }
            }
            if(returnresult == null){
                returnresult = new Vec3d( mc.player.posX, -69420, mc.player.posZ);
            }
            return returnresult;
    }




    private List<RayTraceResult> rayTraceBoundingBoxToGround(Entity entity) {
        AxisAlignedBB boundingBox = entity.boundingBox;
        List<RayTraceResult> results = new ArrayList<>(4);
        for (double niggaX = boundingBox.minX; niggaX < boundingBox.maxX; niggaX = niggaX + 0.01) {
            for (double niggaZ = boundingBox.minZ; niggaZ < boundingBox.maxZ; niggaZ = niggaZ + 0.01) {
                RayTraceResult result = rayTraceToGround(new Vec3d(niggaX, boundingBox.minY, niggaZ), false);
                if (result != null) {
                    results.add(result);
                }
            }
        }

        return results;
    }

    private RayTraceResult rayTraceToGround(Vec3d vec3d, boolean stopOnLiquid) {
        return mc.world.rayTraceBlocks(vec3d, new Vec3d(vec3d.x, -1.0, vec3d.z), stopOnLiquid, true, false);
    }



    private float dYaw = 0F;
    private float dPitch = 0F;


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
    public void onEnable ( ) {
        dYaw = 0;
        dPitch = 0;
    }
}
