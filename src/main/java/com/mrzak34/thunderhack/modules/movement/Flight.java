package com.mrzak34.thunderhack.modules.movement;

import com.mrzak34.thunderhack.event.events.EventPreMotion;
import com.mrzak34.thunderhack.event.events.PacketEvent;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.MathUtil;
import com.mrzak34.thunderhack.util.MovementUtil;
import net.minecraft.block.Block;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;



public class Flight extends Module {
    public Flight() {
        super("Flight",  "Makes you fly.",  Module.Category.MOVEMENT,  true,  false,  false);
    }



    private Setting<Mode> mode = this.register (new Setting<>("Mode", Mode.Vanilla));
    private enum Mode {
        Vanilla, MatrixJump, AirJump
    }
    public Setting<Float> speed = this.register(new Setting("Speed", 0.1f, 0.0f, 10.0f,v-> mode.getValue() == Mode.Vanilla));
    public Setting<Float> speedValue = this.register(new Setting<Float>("Speed", 1.69F, 0.0F, 5F,v-> mode.getValue() == Mode.MatrixJump));
    public Setting<Float> vspeedValue = this.register(new Setting<Float>("Vertical", 0.78F, 0.0F, 5F,v-> mode.getValue() == Mode.MatrixJump));
    public Setting<Boolean> spoofValue = register(new Setting<>("Ground", false,v-> mode.getValue() == Mode.MatrixJump));
    public Setting<Boolean> aboba = register(new Setting<>("AutoToggle", false,v-> mode.getValue() == Mode.MatrixJump));
    public Setting<Float> xxx = register(new Setting("xHitBoxExpand", 1f, 0, 2f,v-> mode.getValue() == Mode.AirJump));


    @SubscribeEvent
    public void onUpdateWalkingPlayer(final EventPreMotion event) {
        if(mode.getValue() == Mode.Vanilla) {

            Flight.mc.player.setVelocity(0.0, 0.0, 0.0);
            Flight.mc.player.jumpMovementFactor = this.speed.getValue();
            final double[] dir = MathUtil.directionSpeed(this.speed.getValue());
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
                motionY = (player4.motionY += this.speed.getValue());
                player3.motionY = motionY;
            }
            if (Flight.mc.gameSettings.keyBindSneak.isKeyDown()) {
                final EntityPlayerSP player5 = Flight.mc.player;
                player5.motionY -= this.speed.getValue();
            }
        } else if(mode.getValue() == Mode.AirJump){
            if(MovementUtil.isMoving() && checkHorizontal() && mc.gameSettings.keyBindJump.isKeyDown()){
                mc.player.onGround = true;  //ахуеть, 2 строчки байпасят матрикс
                mc.player.jump();
            }
        }
    }

    @Override
    public void onUpdate(){

        if(mode.getValue() != Mode.MatrixJump){
            return;
        }
        mc.player.capabilities.isFlying = false;

        mc.player.motionX = 0.0;
        mc.player.motionY = 0.0;
        mc.player.motionZ = 0.0;
        if (mc.gameSettings.keyBindJump.isKeyDown()) {
            mc.player.motionY += vspeedValue.getValue();
        }
        if (mc.gameSettings.keyBindSneak.isKeyDown()) {
            mc.player.motionY -= vspeedValue.getValue();
        }
        LongJump.strafe(speedValue.getValue());
    }


    public boolean pendingFlagApplyPacket = false;
    private double lastMotionX = 0.0;
    private double lastMotionY = 0.0;
    private double lastMotionZ = 0.0;

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive e){
        if(mode.getValue() != Mode.MatrixJump){
            return;
        }
        if(fullNullCheck()){
            return;
        }
        if(e.getPacket() instanceof SPacketPlayerPosLook) {
            pendingFlagApplyPacket = true;
            lastMotionX = mc.player.motionX;
            lastMotionY = mc.player.motionY;
            lastMotionZ = mc.player.motionZ;
        }
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send e){
        if(mode.getValue() == Mode.MatrixJump) {

            if (e.getPacket() instanceof CPacketPlayer.PositionRotation) {
                if (pendingFlagApplyPacket) {
                    mc.player.motionX = lastMotionX;
                    mc.player.motionY = lastMotionY;
                    mc.player.motionZ = lastMotionZ;
                    pendingFlagApplyPacket = false;
                    if (aboba.getValue()) {
                        this.toggle();
                    }
                }
            }

            if (e.getPacket() instanceof CPacketPlayer) {
                CPacketPlayer packet = e.getPacket();
                if (spoofValue.getValue()) {
                    packet.onGround = true;
                }
            }
        } else if(mode.getValue() == Mode.AirJump){
            if(fullNullCheck()){
                return;
            }
            if(e.getPacket() instanceof SPacketPlayerPosLook){
                toggle();
            }
        }
    }

    //когда лезем всегда онграунд и моушн 0.42 пон

    public boolean checkHorizontal() {
        final AxisAlignedBB bb = mc.player.getEntityBoundingBox().grow(xxx.getValue(), 0, xxx.getValue());
        boolean flag = false;
        int y = (int) bb.minY;
        for (int x = MathHelper.floor(bb.minX); x < MathHelper.floor(bb.maxX + 1.0D); x++) {
            for (int z = MathHelper.floor(bb.minZ); z < MathHelper.floor(bb.maxZ + 1.0D); z++) {
                final Block block = mc.world.getBlockState(new BlockPos(x, y, z)).getBlock();
                if (block != Blocks.AIR) {
                    flag = true;
                }
            }
        }
        return flag;

    }

}
