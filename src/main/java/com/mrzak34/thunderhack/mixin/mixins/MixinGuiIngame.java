package com.mrzak34.thunderhack.mixin.mixins;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.events.RenderAttackIndicatorEvent;
import com.mrzak34.thunderhack.gui.hud.elements.Potions;
import com.mrzak34.thunderhack.modules.funnygame.AntiTittle;
import com.mrzak34.thunderhack.modules.render.NoRender;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({GuiIngame.class})
public class MixinGuiIngame extends Gui {

    @Inject(method = {"renderPotionEffects"}, at = {@At("HEAD")}, cancellable = true)
    protected void renderPotionEffectsHook(final ScaledResolution scaledRes, final CallbackInfo info) {
        if (Thunderhack.moduleManager.getModuleByClass(Potions.class).isOn()) {
            info.cancel();
        }
    }


    @Inject(method = {"renderScoreboard"}, at = {@At("HEAD")}, cancellable = true)
    protected void renderScoreboardHook(ScoreObjective objective, ScaledResolution scaledRes, CallbackInfo ci) {
        if (Thunderhack.moduleManager.getModuleByClass(AntiTittle.class).scoreBoard.getValue() &&Thunderhack.moduleManager.getModuleByClass(AntiTittle.class).isOn()) {
            ci.cancel();
        }
    }

    @Inject(method = { "renderPortal" }, at = { @At("HEAD") }, cancellable = true)
    protected void renderPortal(final float n, final ScaledResolution scaledResolution, final CallbackInfo callbackInfo) {
        if (Thunderhack.moduleManager.getModuleByClass(NoRender.class).portal.getValue() &&Thunderhack.moduleManager.getModuleByClass(NoRender.class).isOn()) {
            callbackInfo.cancel();
        }
    }


    @Inject(method = "renderAttackIndicator", at = @At("HEAD"), cancellable = true)
    public void onRenderAttackIndicator(float partialTicks, ScaledResolution p_184045_2_, CallbackInfo ci) {
        RenderAttackIndicatorEvent event = new RenderAttackIndicatorEvent();
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) ci.cancel();
    }

}