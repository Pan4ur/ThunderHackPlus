package com.mrzak34.thunderhack.modules.movement;

import com.mrzak34.thunderhack.event.events.PacketEvent;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class MatrixJump extends Module {

    public MatrixJump() {
        super("MatrixJump", "MatrixJump", Category.MOVEMENT, true, false, false);
    }


    @Override
    public void onUpdate(){
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

        MatrixZoom.strafe(speedValue.getValue());
    }

    
    public Setting<Float> speedValue = this.register(new Setting<Float>("Speed", 1.69F, 0.0F, 5F));
    public Setting<Float> vspeedValue = this.register(new Setting<Float>("Vertical", 0.78F, 0.0F, 5F));
    public Setting<Boolean> spoofValue = register(new Setting<>("Ground", false));
    public Setting<Boolean> aboba = register(new Setting<>("aboba", false));


    public boolean pendingFlagApplyPacket = false;
    private double lastMotionX = 0.0;
    private double lastMotionY = 0.0;
    private double lastMotionZ = 0.0;

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive e){
        if(e.getPacket() instanceof SPacketPlayerPosLook) {
            pendingFlagApplyPacket = true;
            lastMotionX = mc.player.motionX;
            lastMotionY = mc.player.motionY;
            lastMotionZ = mc.player.motionZ;
        }
    }





    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send e){
        if(e.getPacket() instanceof CPacketPlayer.PositionRotation) {
            if (pendingFlagApplyPacket){
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
            if(spoofValue.getValue()) {
                packet.onGround = true;
            }
        }
    }


}
