package com.mrzak34.thunderhack.mixin.mixins;

import com.mrzak34.thunderhack.events.EventCooldown;
import net.minecraft.item.Item;
import net.minecraft.util.CooldownTracker;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = {CooldownTracker.class})
public abstract class MixinCooldownTracker  {

    @Inject(method = {"getCooldown"}, at = {@At(value = "HEAD")}, cancellable = true)
    public void getCooldown(Item itemIn, float partialTicks, CallbackInfoReturnable<Float> cir) {
        EventCooldown eventCooldown = new EventCooldown(itemIn);
        MinecraftForge.EVENT_BUS.post(eventCooldown);
        if (eventCooldown.getCooldown() != 0) {
            cir.setReturnValue(eventCooldown.getCooldown());
        }
    }

}
