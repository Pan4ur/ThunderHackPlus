package com.mrzak34.thunderhack.mixin.mixins;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.command.commands.ChangeSkinCommand;
import com.mrzak34.thunderhack.events.FreecamEvent;
import com.mrzak34.thunderhack.manager.EventManager;
import com.mrzak34.thunderhack.modules.client.MainSettings;
import com.mrzak34.thunderhack.modules.render.Models;
import com.mrzak34.thunderhack.modules.render.NameTags;
import com.mrzak34.thunderhack.util.PNGtoResourceLocation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.mrzak34.thunderhack.gui.hud.elements.RadarRewrite.interp;
import static com.mrzak34.thunderhack.modules.Module.mc;

@Mixin({RenderPlayer.class})
public class MixinRenderPlayer {
    private final ResourceLocation amogus = new ResourceLocation("textures/amogus.png");
    private final ResourceLocation rabbit = new ResourceLocation("textures/rabbit.png");
    private final ResourceLocation fred = new ResourceLocation("textures/freddy.png");
    private float
            renderPitch,
            renderYaw,
            renderHeadYaw,
            prevRenderHeadYaw,
            prevRenderPitch;

    @Inject(method = {"renderEntityName"}, at = {@At("HEAD")}, cancellable = true)
    public void renderEntityNameHook(final AbstractClientPlayer entityIn, final double x, final double y, final double z, final String name, final double distanceSq, final CallbackInfo info) {
        if (Thunderhack.moduleManager.getModuleByClass(NameTags.class).isEnabled()) {
            info.cancel();
        }
    }

    @Redirect(method = "doRender", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/AbstractClientPlayer;isUser()Z"))
    private boolean isUserRedirect(AbstractClientPlayer abstractClientPlayer) {
        Minecraft mc = Minecraft.getMinecraft();
        FreecamEvent event = new FreecamEvent();
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            return abstractClientPlayer.isUser() && abstractClientPlayer == mc.getRenderViewEntity();
        }
        return abstractClientPlayer.isUser();
    }

    @Inject(method = "doRender", at = @At("HEAD"))
    private void rotateBegin(AbstractClientPlayer entity, double x, double y, double z, float entityYaw, float partialTicks, CallbackInfo ci) {
        if (Thunderhack.moduleManager.getModuleByClass(MainSettings.class).renderRotations.getValue() && entity == Minecraft.getMinecraft().player) {
            if(Minecraft.getMinecraft().player.getRidingEntity() != null) return;
            prevRenderHeadYaw = entity.prevRotationYawHead;
            prevRenderPitch = entity.prevRotationPitch;
            renderPitch = entity.rotationPitch;
            renderYaw = entity.rotationYaw;
            renderHeadYaw = entity.rotationYawHead;

            float interpYaw = (float) interp(EventManager.visualYaw, EventManager.prevVisualYaw);
            float interpPitch = (float) interp(EventManager.visualPitch, EventManager.prevVisualPitch);
            entity.rotationPitch = interpPitch;
            entity.prevRotationPitch = interpPitch;
            entity.rotationYaw = interpYaw;
            entity.rotationYawHead = interpYaw;
            entity.prevRotationYawHead = interpYaw;

        }
    }

    @Inject(method = "doRender", at = @At("RETURN"))
    private void rotateEnd(AbstractClientPlayer entity, double x, double y, double z, float entityYaw, float partialTicks, CallbackInfo ci) {
        if (Thunderhack.moduleManager.getModuleByClass(MainSettings.class).renderRotations.getValue() && entity == Minecraft.getMinecraft().player) {
            if(Minecraft.getMinecraft().player.getRidingEntity() != null) return;
            entity.rotationPitch = renderPitch;
            entity.rotationYaw = renderYaw;
            entity.rotationYawHead = renderHeadYaw;
            entity.prevRotationYawHead = prevRenderHeadYaw;
            entity.prevRotationPitch = prevRenderPitch;
        }
    }

    @Inject(method = {"getEntityTexture"}, at = {@At("HEAD")}, cancellable = true)
    public void getEntityTexture(AbstractClientPlayer entity, CallbackInfoReturnable<ResourceLocation> ci) {
        if (Thunderhack.moduleManager.getModuleByClass(Models.class).isEnabled() && (!Thunderhack.moduleManager.getModuleByClass(Models.class).onlySelf.getValue() || entity == Minecraft.getMinecraft().player || Thunderhack.friendManager.isFriend(entity.getName()) && Thunderhack.moduleManager.getModuleByClass(Models.class).friends.getValue())) {
            if (Thunderhack.moduleManager.getModuleByClass(Models.class).Mode.getValue() == Models.mode.Amogus) {
                ci.setReturnValue(amogus);
            }

            if (Thunderhack.moduleManager.getModuleByClass(Models.class).Mode.getValue() == Models.mode.Rabbit) {
                ci.setReturnValue(rabbit);
            }
            if (Thunderhack.moduleManager.getModuleByClass(Models.class).Mode.getValue() == Models.mode.Freddy) {
                ci.setReturnValue(fred);
            }
        } else {
            if (ChangeSkinCommand.getInstance().changedplayers.contains(entity.getName())) {
                GL11.glColor4f(1f, 1f, 1f, 1f);
                ci.setReturnValue(PNGtoResourceLocation.getTexture3(entity.getName(), "png"));

            } else {
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                ci.setReturnValue(entity.getLocationSkin());
            }
        }
    }
}