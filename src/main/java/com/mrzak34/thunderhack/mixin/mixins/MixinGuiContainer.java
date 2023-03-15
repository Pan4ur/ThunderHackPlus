package com.mrzak34.thunderhack.mixin.mixins;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.modules.misc.ItemScroller;
import com.mrzak34.thunderhack.util.Timer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = {GuiContainer.class})
public abstract class MixinGuiContainer extends GuiScreen {

    @Shadow
    public Container inventorySlots;
    private final Timer delayTimer = new Timer();

    @Shadow
    protected abstract boolean isMouseOverSlot(Slot slotIn, int mouseX, int mouseY);


    @Shadow
    protected abstract void handleMouseClick(Slot slotIn, int slotId, int mouseButton, ClickType type);

    @Inject(method = "drawScreen", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/inventory/GuiContainer;drawGradientRect(IIIIII)V", shift = At.Shift.BEFORE))
    private void drawScreenHook(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        ItemScroller scroller = Thunderhack.moduleManager.getModuleByClass(ItemScroller.class);

        for (int i1 = 0; i1 < inventorySlots.inventorySlots.size(); ++i1) {
            Slot slot = inventorySlots.inventorySlots.get(i1);
            if (isMouseOverSlot(slot, mouseX, mouseY) && slot.isEnabled()) {
                if (scroller.isEnabled() && Mouse.isButtonDown(0) && Keyboard.isKeyDown(this.mc.gameSettings.keyBindSneak.getKeyCode()) && delayTimer.passedMs(scroller.delay.getValue())) {
                    this.handleMouseClick(slot, slot.slotNumber, 0, ClickType.QUICK_MOVE);
                    delayTimer.reset();
                }
            }
        }
    }
}