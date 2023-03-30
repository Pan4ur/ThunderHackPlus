package com.mrzak34.thunderhack.mixin.mixins;

import com.mrzak34.thunderhack.modules.misc.DiscordEmbeds;
import com.mrzak34.thunderhack.modules.misc.ToolTips;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemShulkerBox;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = {GuiScreen.class})
public class MixinGuiScreen extends Gui {
    @Inject(method = {"renderToolTip"}, at = {@At(value = "HEAD")}, cancellable = true)
    public void renderToolTipHook(ItemStack stack, int x, int y, CallbackInfo info) {
        if (ToolTips.getInstance().isOn() && stack.getItem() instanceof ItemShulkerBox) {
            ToolTips.getInstance().renderShulkerToolTip(stack, x, y, null);
            info.cancel();
        }
    }

    @Inject(method = "handleComponentHover", at = @At(value = "HEAD"), cancellable = true)
    private void handleComponentHoverHook(ITextComponent component, int x, int y, CallbackInfo info) {
        if (component != null) {
            DiscordEmbeds.saveDickPick(component.getStyle().getHoverEvent().getValue().getUnformattedText(), "png");
            DiscordEmbeds.nado = true;
            DiscordEmbeds.timer.reset();
        }
    }

    /*
    @Inject(method = "handleComponentClick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiScreen;" + "sendChatMessage(Ljava/lang/String;Z)V", shift = At.Shift.BEFORE),cancellable = true)
    public void handleComponentClick(ITextComponent component, CallbackInfoReturnable<Boolean> info)
    {
        if(component != null ){
            DiscordEmbeds.cring(component.getFormattedText());
        }
    }

     */


}

