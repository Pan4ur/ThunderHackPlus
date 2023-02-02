package com.mrzak34.thunderhack.mixin.mixins;

import com.mrzak34.thunderhack.events.RenderAttackIndicatorEvent;
import com.mrzak34.thunderhack.gui.hud.Potions;
import com.mrzak34.thunderhack.modules.funnygame.AntiTittle;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import org.spongepowered.asm.mixin.injection.*;
import net.minecraft.client.gui.*;
import com.mrzak34.thunderhack.*;

@Mixin({ GuiIngame.class })
public class MixinGuiIngame extends Gui
{

    @Inject(method = { "renderPotionEffects" },  at = { @At("HEAD") },  cancellable = true)
    protected void renderPotionEffectsHook(final ScaledResolution scaledRes,  final CallbackInfo info) {
        if (Thunderhack.moduleManager.getModuleByClass(Potions.class).isOn()) {
            info.cancel();
        }
    }


    @Inject(method = { "renderScoreboard" },  at = { @At("HEAD") },  cancellable = true)
    protected void renderScoreboardHook(ScoreObjective objective, ScaledResolution scaledRes, CallbackInfo ci) {
        if (Thunderhack.moduleManager.getModuleByClass(AntiTittle.class).scoreBoard.getValue()) {
            ci.cancel();
        }
    }




    @Inject(method = "renderAttackIndicator", at = @At("HEAD"), cancellable = true)
    public void onRenderAttackIndicator(float partialTicks, ScaledResolution p_184045_2_, CallbackInfo ci) {
        RenderAttackIndicatorEvent event = new RenderAttackIndicatorEvent();
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) ci.cancel();
    }

}