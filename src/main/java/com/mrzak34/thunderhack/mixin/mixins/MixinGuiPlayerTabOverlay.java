package com.mrzak34.thunderhack.mixin.mixins;

import com.mrzak34.thunderhack.modules.misc.ExtraTab;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.network.NetworkPlayerInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin({GuiPlayerTabOverlay.class})
public class MixinGuiPlayerTabOverlay extends Gui {
    @Redirect(method = {"renderPlayerlist"}, at = @At(value = "INVOKE", target = "Ljava/util/List;subList(II)Ljava/util/List;"))
    public List<NetworkPlayerInfo> subListHook(List<NetworkPlayerInfo> list, int fromIndex, int toIndex) {
        return list.subList(fromIndex, ExtraTab.getINSTANCE().isEnabled() ? Math.min((ExtraTab.getINSTANCE()).size.getValue().intValue(), list.size()) : toIndex);
    }

    @Inject(method = {"getPlayerName"}, at = {@At("HEAD")}, cancellable = true)
    public void getPlayerNameHook(NetworkPlayerInfo networkPlayerInfoIn, CallbackInfoReturnable<String> info) {
        if (ExtraTab.getINSTANCE().isEnabled())
            info.setReturnValue(ExtraTab.getPlayerName(networkPlayerInfoIn));
    }
}
