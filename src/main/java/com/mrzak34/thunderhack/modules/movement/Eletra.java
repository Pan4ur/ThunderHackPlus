package com.mrzak34.thunderhack.modules.movement;


import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.command.Command;
import com.mrzak34.thunderhack.event.events.EventPlayerTravel;
import com.mrzak34.thunderhack.event.events.PacketEvent;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

public class Eletra extends Module {
    public Eletra() {
        super("Eletra", "Eletra", Category.MOVEMENT, true, false, false);
    }




    public Setting<Integer> threshold = this.register(new Setting<>("WarningThreshold", 5, 1, 50));
    public Setting<Float> fastDownSpeedControl = this.register(new Setting<>("DynamicDownSpeedC", 2f, 1f, 5f));
    public Setting<Float> downSpeedControl = this.register(new Setting<>("DownSpeedC", 1f, 1f, 5f));
    public Setting<Float> fallSpeedControl = this.register(new Setting<>("FallSpeedC", 0.00000000000003f, 0f, 0.3f));
    public Setting<Float> speedControl = this.register(new Setting<>("SpeedC", 1.81f, 0.1f, 10.0f));
    public Setting<Integer> boostPitchControl = this.register(new Setting<>("BaseBoostPitch", 20, 0, 90));
    public Setting<Float> swingAmount = this.register(new Setting<>("SwingAmount", 0.8f, 0.0f, 2.0f));
     public Setting<Float> swingSpeed = this.register(new Setting<>("SwingSpeed", 1.0f, 0.0f, 2.0f));
    public Setting<Float> forwardPitch = this.register(new Setting<>("Forward Pitch", 0f, -90f, 90f));
    public Setting<Float> accelerateTime = this.register(new Setting<>("Accelerate Time", 0.0f, 0.0f, 20.0f));
    public Setting<Float> accelerateStartSpeed = this.register(new Setting<>("Start Speed", 100f, 0f, 100f));
    public Setting<Float> minTakeoffHeight = this.register(new Setting<>("Min Takeoff Height", 0.5f, 0.0f, 1.5f));
    public Setting<Boolean> ncpStrict = this.register(new Setting<>("Strict", true));
    public Setting<Boolean> legacyLookBoost = this.register(new Setting<>("LegacyLookBoost", true));
    public Setting<Boolean> altitudeHoldControl = this.register(new Setting<>("AutoControlAlt", true));
    public Setting<Boolean> dynamicDownSpeed = this.register(new Setting<>("DynamicDownSpeed", true));
    public Setting<Boolean> blockInteract = this.register(new Setting<>("BlockInteract", true));
    public Setting<Boolean> autoLanding = this.register(new Setting<>("AutoLanding", true));
    public Setting<Boolean> easyTakeOff = this.register(new Setting<>("EasyTakeoff", true));
    public Setting<Boolean> timerControl = this.register(new Setting<>("Takeoff Timer", true));
    public Setting<Boolean> highPingOptimize = this.register(new Setting<>("HighPingOptimize", true));
    public Setting<Boolean> durabilityWarning = this.register(new Setting<>("DurabilityWarning", true));
    public Setting<Boolean> spoofPitch = this.register(new Setting<>("spoofPitch", true));

    private boolean elytraIsEquipped = false;
    private int elytraDurability = 0;
    private boolean outOfDurability = false;
    private boolean wasInLiquid = false;
    private boolean isFlying = false;
    private boolean isStandingStillH = false;
    private boolean isStandingStill = false;
    private float speedPercentage = 0.0f;

    private double hoverTarget = -1.0f;
    private float packetYaw = 0.0f;
    private double packetPitch = 0.0f;
    private boolean hoverState = false;
    private float boostingTick = 0;


    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive e){


        if(fullNullCheck())return;
        if (mc.player.isSpectator() || !elytraIsEquipped || elytraDurability <= 1 || !isFlying) return;
        if (e.getPacket() instanceof SPacketPlayerPosLook) {
            SPacketPlayerPosLook packet = e.getPacket();
            packet.pitch = mc.player.rotationPitch;
        }

        e.getPacket();
    }


    @SubscribeEvent
    public void onElytra(EventPlayerTravel event) {
        if (mc.player.isSpectator()) return;
        stateUpdate(event); //TODO SDVIGI
        //flyyyyy();
        if (elytraIsEquipped && elytraDurability > 1) {
            if (autoLanding.getValue()) {
                landing(event);
                return;
            }
            if (!isFlying) {
                takeoff(event);
            } else {
                mc.timer.tickLength = 50.0f;
                mc.player.setSprinting(false);
                controlMode(event);
            }
            spoofRotation();
        } else if (!outOfDurability) {
            reset2(true);
        }
    }

    private final Setting<Float> speedSetting = register(new Setting<>("FSpeed", 16F, 0.1F, 20F));
    private final Setting<Float> glideSpeed = register(new Setting<>("GlideSpeed", 1F, 0.01F, 10f));

    public void flyyyyy(){
        mc.player.capabilities.isFlying = true;
        mc.player.capabilities.setFlySpeed(speedSetting.getValue() / 11.11f);

        if (glideSpeed.getValue() != 0.0
                && !mc.gameSettings.keyBindJump.isKeyDown()
                && !mc.gameSettings.keyBindSneak.isKeyDown()) mc.player.motionY = -glideSpeed.getValue();
    }


    public void stateUpdate(EventPlayerTravel event) {
        ItemStack armorSlot = mc.player.inventory.armorInventory.get(2);
        elytraIsEquipped = armorSlot.item == Items.ELYTRA;

        if (elytraIsEquipped) {
            int oldDurability = elytraDurability;
            elytraDurability = armorSlot.getMaxDamage() - armorSlot.itemDamage;

            if (!mc.player.onGround && oldDurability != elytraDurability) {
                if (durabilityWarning.getValue() && elytraDurability > 1 && elytraDurability < threshold.getValue() * armorSlot.getMaxDamage() / 100) {
                    mc.soundHandler.playSound(PositionedSoundRecord.getRecord(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f));
                    Command.sendMessage("$chatName Warning: Elytra has " + (elytraDurability - 1) + " durability remaining");
                } else if (elytraDurability <= 1 && !outOfDurability) {
                    outOfDurability = true;
                    if (durabilityWarning.getValue()) {
                        mc.soundHandler.playSound(PositionedSoundRecord.getRecord(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f));
                        Command.sendMessage("$chatName Elytra is out of durability, holding mc.player in the air");
                    }
                }
            }
        } else elytraDurability = 0;

        if (!mc.player.onGround && elytraDurability <= 1 && outOfDurability) {
            holdplayer(event);
        } else if (outOfDurability) outOfDurability = false ;

        if (mc.player.isInWater() || mc.player.isInLava()) {
            wasInLiquid = true;
        } else if (mc.player.onGround || isFlying) {
            wasInLiquid = false;
        }

        isFlying = mc.player.isElytraFlying();

        isStandingStillH = mc.player.movementInput.moveForward == 0f && mc.player.movementInput.moveStrafe == 0f;
        isStandingStill = isStandingStillH && !mc.player.movementInput.jump && !mc.player.movementInput.sneak;

        if (!isFlying || isStandingStill) speedPercentage = accelerateStartSpeed.getValue();

        if (shouldSwing()) {
            mc.player.prevLimbSwingAmount = mc.player.limbSwingAmount;
            mc.player.limbSwing += swingSpeed.getValue();
            float speedRatio = (float) Math.sqrt(mc.player.motionX * mc.player.motionX + mc.player.motionZ * mc.player.motionZ) / speedControl.getValue(); //TODO BETTER SPEED
            mc.player.limbSwingAmount += ((speedRatio * swingAmount.getValue()) - mc.player.limbSwingAmount) * 0.4f;
        }
    }

    private void reset2(boolean cancelflu) {
        wasInLiquid = false;
        isFlying = false;
        mc.timer.tickLength = 50.0f;
        mc.player.capabilities.setFlySpeed(0.05f);
        if (cancelflu) mc.player.capabilities.isFlying = false;
    }

    private void holdplayer(EventPlayerTravel event) {
        event.setCanceled(true);
        mc.timer.tickLength = 50.0f;
        mc.player.setVelocity(0.0, -0.01, 0.0);
    }

    public void landing(EventPlayerTravel event) {

        if(mc.player.onGround){
            Command.sendMessage("$chatName Landed!");
            autoLanding.setValue(false);
            return;
        } else if(mc.player.isElytraFlying() || mc.player.capabilities.isFlying){
            reset2(true);
            takeoff(event);
            return;
        } else {
            if(mc.player.posY > getGroundPos(mc.player).y + 1.0){
                Thunderhack.TICK_TIMER = 1f;
                mc.player.motionY = Math.max(Math.min(-(mc.player.posY -  mc.player.lastTickPosY) / 20.0, -0.5), -5.0);
            } else if(mc.player.motionY != 0.0 ){
                if (!mc.isSingleplayer()) Thunderhack.TICK_TIMER = 0.2f;;
                mc.player.motionY = 0.0;
            } else {
                mc.player.motionY = -0.2;

            }
        }

        mc.player.setVelocity(0.0, mc.player.motionY, 0.0) ;
        event.setCanceled(true);
    }

    private void takeoff(EventPlayerTravel event) {

        float timerSpeed =  (highPingOptimize.getValue()) ? 400.0f : 200.0f;
        float height =  (highPingOptimize.getValue()) ? 0.0f : minTakeoffHeight.getValue();
        boolean closeToGround = mc.player.posY <= getGroundPos(mc.player).y + height && !wasInLiquid && !mc.isSingleplayer();

        if (!easyTakeOff.getValue()  || mc.player.onGround) {
            reset2(mc.player.onGround);
            return;
        }

        if (mc.player.motionY < 0 && !highPingOptimize.getValue() || mc.player.motionY < -0.02) {
            if (closeToGround) {
                mc.timer.tickLength = 25.0f;
                return;
            }

            if (!highPingOptimize.getValue() && !wasInLiquid && !mc.isSingleplayer()) {
                event.setCanceled(true);
                mc.player.setVelocity(0.0, -0.02, 0.0);
            }

            if (timerControl.getValue() && !mc.isSingleplayer()) mc.timer.tickLength = timerSpeed * 2.0f;
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
            hoverTarget = (float) (mc.player.posY + 0.2);
        } else if (highPingOptimize.getValue() && !closeToGround) {
            mc.timer.tickLength = timerSpeed;
        }
    }


    private double getYaw() {
        double yawRad = calcMoveYaw();
        packetYaw = (float) Math.toDegrees(yawRad);
        return yawRad;
    }


    private double getSpeed(boolean boosting){
        if(boosting){
            return  (ncpStrict.getValue()) ? Math.min(speedControl.getValue(), 2.0f) : speedControl.getValue();
        }else if(accelerateTime.getValue() != 0.0f && accelerateStartSpeed.getValue() != 100){
            speedPercentage = Math.min(speedPercentage + (100.0f - accelerateStartSpeed.getValue()) / (accelerateTime.getValue() * 20.0f), 100.0f);
            double speedMultiplier = speedPercentage / 100.0;
            return speedControl.getValue() * speedMultiplier * (Math.cos(speedMultiplier * Math.PI) * -0.5 + 0.5);
        } else {
            return speedControl.getValue();
        }
    }



    private void setSpeed(double yaw,boolean boosting) {
        double acceleratedSpeed = getSpeed(boosting);
        mc.player.setVelocity(Math.sin(-yaw) * acceleratedSpeed, mc.player.motionY, Math.cos(yaw) * acceleratedSpeed);
    }



    private void controlMode(EventPlayerTravel event) {
        double currentSpeed = Math.sqrt(mc.player.motionX * mc.player.motionX + mc.player.motionZ * mc.player.motionZ);
        boolean moveUp =  (!legacyLookBoost.getValue()) ? mc.player.movementInput.jump : mc.player.rotationPitch < -10.0f && !isStandingStillH;
        boolean moveDown = mc.currentScreen == null && !moveUp && mc.player.movementInput.sneak;

        double calcDownSpeed = getCalcDownSpeed();

        if (hoverTarget < 0.0 || moveUp) {
            hoverTarget = mc.player.posY;
        } else if (moveDown){
            hoverTarget = mc.player.posY - calcDownSpeed;
        }

        hoverState = getHoverState();

        if (!isStandingStillH || moveUp) {
            if ((moveUp || hoverState) && (currentSpeed >= 0.8 || mc.player.motionY > 1.0)) {
                upwardFlight(currentSpeed, getYaw());
            } else if (!isStandingStillH || moveUp) {
                packetPitch = forwardPitch.getValue();
                mc.player.motionY = -fallSpeedControl.getValue();
                setSpeed(getYaw(), moveUp);
                boostingTick = 0;
            }
        } else mc.player.setVelocity(0.0, 0.0, 0.0);

        if (moveDown) mc.player.motionY = -calcDownSpeed ;

        event.setCanceled(true);
    }

    private void upwardFlight(double currentSpeed,double yaw) {
        double multipliedSpeed = 0.128 * Math.min(speedControl.getValue(), 2.0f);
        double strictPitch = Math.toDegrees(Math.asin((multipliedSpeed - Math.sqrt(multipliedSpeed * multipliedSpeed - 0.0348)) / 0.12));
        double basePitch =  (ncpStrict.getValue() && strictPitch < boostPitchControl.getValue() && !(strictPitch == 0)) ? -strictPitch : -boostPitchControl.getValue();
        double targetPitch = getTargetPitch();

        packetPitch = getPacketPitch(basePitch,targetPitch);
        boostingTick++;

        double pitch = Math.toRadians(packetPitch);
        double targetMotionX = Math.sin(-yaw) * Math.sin(-pitch);
        double targetMotionZ = Math.cos(yaw) * Math.sin(-pitch);
        double targetSpeed = Math.sqrt(targetMotionX * targetMotionX + targetMotionZ * targetMotionZ);
        double upSpeed = currentSpeed * Math.sin(-pitch) * 0.04;
        double fallSpeed = Math.cos(pitch) * Math.cos(pitch) * 0.06 - 0.08;

        mc.player.motionX -= upSpeed * targetMotionX / targetSpeed - (targetMotionX / targetSpeed * currentSpeed - mc.player.motionX) * 0.1;
        mc.player.motionY += upSpeed * 3.2 + fallSpeed;
        mc.player.motionZ -= upSpeed * targetMotionZ / targetSpeed - (targetMotionZ / targetSpeed * currentSpeed - mc.player.motionZ) * 0.1;

        mc.player.motionX *= 0.99;
        mc.player.motionY *= 0.98;
        mc.player.motionZ *= 0.99;
    }



    public boolean shouldSwing(){
        return isFlying && !autoLanding.getValue();
    }

    private void spoofRotation() {
        if (mc.player.isSpectator() || !elytraIsEquipped || elytraDurability <= 1 || !isFlying) return;
        boolean cancelRotation = false;
        Vec2f rotation = new Vec2f(mc.player.rotationYaw,mc.player.rotationPitch);

        if (autoLanding.getValue()) {
            rotation = new Vec2f(rotation.x, -20f);
        } else {
            if (!isStandingStill) rotation = new Vec2f(packetYaw, rotation.y);
            if (spoofPitch.getValue()) {
                if (!isStandingStill) rotation = new Vec2f(rotation.x, (float) packetPitch);

                cancelRotation = isStandingStill && ((!mc.gameSettings.keyBindUseItem.isKeyDown() && !mc.gameSettings.keyBindAttack.isKeyDown() && blockInteract.getValue()) || !blockInteract.getValue());
            }
        }


        if(cancelRotation){

        } else {
            mc.player.connection.sendPacket(new CPacketPlayer.Rotation(rotation.x,rotation.y,false));
        }
    }

    public double getCalcDownSpeed(){
        if (dynamicDownSpeed.getValue()) {
            double minDownSpeed = Math.min(downSpeedControl.getValue(), fastDownSpeedControl.getValue());
            double maxDownSpeed = Math.max(downSpeedControl.getValue(), fastDownSpeedControl.getValue());
            if (mc.player.rotationPitch > 0) {
                return mc.player.rotationPitch / 90.0 * (maxDownSpeed - minDownSpeed) + minDownSpeed;
            } else return minDownSpeed;
        } else return downSpeedControl.getValue();
    }

    public boolean getHoverState(){
        if (hoverState) {
            return mc.player.posY < hoverTarget ;
        } else {
            return  (mc.player.posY < hoverTarget - 0.1) && altitudeHoldControl.getValue();
        }
    }
    public double getTargetPitch(){
        if (mc.player.rotationPitch < 0.0f) {
            return Math.max(mc.player.rotationPitch * (90.0f - boostPitchControl.getValue()) / 90.0f - boostPitchControl.getValue(), -90.0f);
        } else return -boostPitchControl.getValue();
    }

    public double getPacketPitch(double basePitch, double targetPitch){
        if (packetPitch <= basePitch && boostingTick > 2) {
            if (packetPitch < targetPitch) packetPitch += 17.0f;
            if (packetPitch > targetPitch) packetPitch -= 17.0f;
            return Math.max(packetPitch, targetPitch);
        } else return basePitch;
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

        List<RayTraceResult> results = rayTraceBoundingBoxToGround(entity, false);
        //    if (results.all { it.typeOfHit == RayTraceResult.Type.MISS || it.hitVec?.y ?: 911.0 < 0.0 }) {
        //       return Vec3d(0.0, -999.0, 0.0)
        //    }

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




    private List<RayTraceResult> rayTraceBoundingBoxToGround(Entity entity, boolean stopOnLiquid) {
        AxisAlignedBB boundingBox = entity.boundingBox;

        List<RayTraceResult> results = new ArrayList<>(4);

        for (double niggaX = boundingBox.minX; niggaX < boundingBox.maxX; niggaX = niggaX + 0.01) {
            for (double niggaZ = boundingBox.minZ; niggaZ < boundingBox.maxZ; niggaZ = niggaZ + 0.01) {
                RayTraceResult result = rayTraceToGround(new Vec3d(niggaX, boundingBox.minY, niggaZ), stopOnLiquid);
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


}
