package com.mrzak34.thunderhack.modules.funnygame;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.command.Command;
import com.mrzak34.thunderhack.event.events.EventPreMotion;
import com.mrzak34.thunderhack.event.events.PacketEvent;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.modules.combat.Aura;
import com.mrzak34.thunderhack.modules.movement.Flight;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.MathUtil;
import com.mrzak34.thunderhack.util.RotationUtil;
import com.mrzak34.thunderhack.util.SilentRotaionUtil;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketVehicleMove;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;


public class GodMode extends Module {

    public GodMode() {
        super("GodMode", "GodMode", Category.FUNNYGAME, true, false, false);
    }
    public Setting<Float> speed = this.register(new Setting("Speed", 0.1f, 0.0f, 10.0f));
    public Setting<Boolean> fl = register(new Setting<>("fly", true));


    private Entity originalRidingEntity;
    Entity Boats;




    @SubscribeEvent
    public void onSendPacket(PacketEvent.Send event) {

        if(Thunderhack.moduleManager.getModuleByClass(Aura.class).isEnabled()) return;
        if (event.getPacket() instanceof CPacketPlayer.Position) {
            event.setCanceled(true);
            CPacketPlayer.Position packet = event.getPacket();
            mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(packet.x, packet.y, packet.z, packet.yaw, packet.pitch, packet.onGround));
        }
        if (event.getPacket() instanceof CPacketPlayer && !(event.getPacket() instanceof CPacketPlayer.PositionRotation))
            event.setCanceled(true);
    }

    @SubscribeEvent
    public void onUpdate(EventPreMotion event) {




        if (mc.world != null && mc.player != null) {
            if (!mc.player.isRiding() && this.hasOriginalRidingEntity()) {
                mc.player.onGround = true;
                originalRidingEntity.setPosition(mc.player.posX, mc.player.posY, mc.player.posZ);
                mc.player.connection.sendPacket(new CPacketVehicleMove(originalRidingEntity));
            }
        }
    }

    @Override
    public void onEnable() {
        if(Thunderhack.moduleManager.getModuleByClass(Aura.class).isEnabled()) {
            Command.sendMessage("Выключи киллку!");
            return;
        }

        originalRidingEntity = null;
        if (mc.player != null && mc.world != null) {
            if (mc.player.isRiding()) {
                originalRidingEntity = mc.player.getRidingEntity();
                mc.player.dismountRidingEntity();
                mc.world.removeEntity(originalRidingEntity);
            } else {
                Boats = findaboat();
                if (Boats != null) {
                    float[] Rots = RotationUtil.getNeededRotations(Boats);
                    SilentRotaionUtil.lookAtAngles(Rots[0], Rots[1]);
                    mc.playerController.interactWithEntity(mc.player, Boats, EnumHand.MAIN_HAND);
                }
            }
        }
        new GodModeThread(this, mc.player).start();

    }

    public Entity findaboat(){
        for(Entity ent : mc.world.loadedEntityList){
            if(isABoat(ent)){
                return ent;
            }
        }
        return null;
    }

    public boolean isABoat(Entity enot){
        if (enot.getDistance(mc.player) > 6f) {
            return false;
        }
        return enot instanceof EntityBoat;
    }

    @Override
    public void onDisable() {
        if (hasOriginalRidingEntity()) {
            originalRidingEntity.isDead = false;
            if (!mc.player.isRiding()) {
                mc.world.spawnEntity(originalRidingEntity);
                mc.player.startRiding(originalRidingEntity, true);
            }
            originalRidingEntity = null;
        }
    }

    private boolean hasOriginalRidingEntity() {
        return originalRidingEntity != null;
    }


    @SubscribeEvent
    public void onUpdateWalkingPlayer(final EventPreMotion event) {
        if(!fl.getValue()) return;
        Flight.mc.player.setVelocity(0.0, 0.0, 0.0);
        Flight.mc.player.jumpMovementFactor = speed.getValue();
        final double[] dir = MathUtil.directionSpeed(speed.getValue());
        if (Flight.mc.player.movementInput.moveStrafe != 0.0f || Flight.mc.player.movementInput.moveForward != 0.0f) {
            Flight.mc.player.motionX = dir[0];
            Flight.mc.player.motionZ = dir[1];
        } else {
            Flight.mc.player.motionX = 0.0;
            Flight.mc.player.motionZ = 0.0;
        }
        if (Flight.mc.gameSettings.keyBindJump.isKeyDown()) {
            final EntityPlayerSP player3 = Flight.mc.player;
            double motionY;
            final EntityPlayerSP player4 = Flight.mc.player;
            motionY = (player4.motionY += speed.getValue());
            player3.motionY = motionY;
        }
        if (Flight.mc.gameSettings.keyBindSneak.isKeyDown()) {
            final EntityPlayerSP player5 = Flight.mc.player;
            player5.motionY -= speed.getValue();
        }
    }


    public class GodModeThread extends Thread {
        public EntityPlayerSP a;
        public GodMode b;

        public GodModeThread(GodMode highJump, EntityPlayerSP entityPlayerSP) {
            this.b = highJump;
            this.a = entityPlayerSP;
        }

        @Override
        public void run() {

            try {
                sleep(2000L);
            }  catch (Exception ignored) {}

            float f = a.rotationYaw * ((float)Math.PI / 180);
            double x = -((double) MathHelper.sin((float)f) * 180);
            double z = (double) MathHelper.cos((float)f) * 180;
            a.setPosition(a.posX + x, a.posY, a.posZ + z);

            try {
                sleep(1000L);
            }  catch (Exception ignored) {}

            Thunderhack.TICK_TIMER = 10;
            try {
                sleep(2000L);
            }  catch (Exception ignored) {}
            Thunderhack.TICK_TIMER = 1;

            for (int i = 0; i < 10; ++i) {
                a.connection.sendPacket(new CPacketPlayer.Position(a.posX, a.posY +100, a.posZ, false));
            }
            a.setPosition(a.posX, a.posY + 100, a.posZ);
            super.run();
        }
    }
}
