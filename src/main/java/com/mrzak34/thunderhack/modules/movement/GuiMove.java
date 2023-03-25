package com.mrzak34.thunderhack.modules.movement;

import com.mrzak34.thunderhack.events.PacketEvent;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.MovementUtil;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

public class GuiMove extends Module {

    public Setting<Boolean> clickBypass = register(new Setting<>("strict", false));


    public GuiMove() {
        super("GuiMove", "GuiMove", Category.MOVEMENT);
    }

    @Override
    public void onUpdate() {
        if (mc.currentScreen != null) {
            if (!(mc.currentScreen instanceof GuiChat)) {
                mc.player.setSprinting(true);
                if (Keyboard.isKeyDown(200)) {
                    mc.player.rotationPitch -= 5;
                }
                if (Keyboard.isKeyDown(208)) {
                    mc.player.rotationPitch += 5;
                }
                if (Keyboard.isKeyDown(205)) {
                    mc.player.rotationYaw += 5;
                }
                if (Keyboard.isKeyDown(203)) {
                    mc.player.rotationYaw -= 5;
                }
                if (mc.player.rotationPitch > 90) {
                    mc.player.rotationPitch = 90;
                }
                if (mc.player.rotationPitch < -90) {
                    mc.player.rotationPitch = -90;
                }
            }
        }
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send e) {
        if (e.getPacket() instanceof CPacketClickWindow) {
            if (clickBypass.getValue()
                    && mc.player.onGround
                    && MovementUtil.isMoving()
                    && mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(0.0, 0.0656, 0.0)).isEmpty()
            ) {
                if (mc.player.isSprinting()) {
                    mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SPRINTING));
                }
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.0656, mc.player.posZ, false));
            }
        }
    }

    @SubscribeEvent
    public void onPacketSendPost(PacketEvent.SendPost e) {
        if (e.getPacket() instanceof CPacketClickWindow) {
            if (mc.player.isSprinting()) {
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SPRINTING));
            }
        }
    }


}
