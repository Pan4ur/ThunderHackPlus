package com.mrzak34.thunderhack.mixin.mixins;


import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.modules.movement.GuiMove;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.MovementInput;
import net.minecraft.util.MovementInputFromOptions;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = MovementInputFromOptions.class, priority = 10000)
public abstract class MixinMovementInputFromOptions extends MovementInput {

    @Redirect(method = "updatePlayerMoveState", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/settings/KeyBinding;isKeyDown()Z"))
    public boolean isKeyPressed(KeyBinding keyBinding) {
        int keyCode = keyBinding.getKeyCode();
        if (keyCode > 0 && keyCode < Keyboard.KEYBOARD_SIZE) {
            if (Thunderhack.moduleManager.getModuleByClass(GuiMove.class).isEnabled()
                    && Minecraft.getMinecraft().currentScreen != null
                    && !(Minecraft.getMinecraft().currentScreen instanceof GuiChat)) {
                if (keyCode != Minecraft.getMinecraft().gameSettings.keyBindSneak.getKeyCode()) {
                    return Keyboard.isKeyDown(keyCode);
                }
            }
        }
        return keyBinding.isKeyDown();
    }
}