package com.mrzak34.thunderhack.modules.movement;

import com.mrzak34.thunderhack.modules.Module;
import net.minecraft.client.gui.GuiChat;
import org.lwjgl.input.Keyboard;

public class GuiMove extends Module {

    public GuiMove() {
        super("GuiMove", "GuiMove", Category.MOVEMENT, true, false, false);
    }

    @Override
    public void onUpdate() {
        if (mc.currentScreen != null) {
            if (!(mc.currentScreen instanceof GuiChat)) {
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
}
