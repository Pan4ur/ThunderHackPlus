package com.mrzak34.thunderhack.mixin.mixins;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.event.events.FreecamEvent;
import com.mrzak34.thunderhack.command.commands.ChangeSkinCommand;
import com.mrzak34.thunderhack.modules.player.PacketRender;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.entity.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import org.spongepowered.asm.mixin.injection.*;
import net.minecraft.client.*;
import com.mrzak34.thunderhack.modules.render.*;
import org.lwjgl.opengl.*;
import net.minecraft.client.renderer.*;
import com.mrzak34.thunderhack.util.*;

import static com.mrzak34.thunderhack.util.Util.mc;

@Mixin({ RenderPlayer.class })
public class MixinRenderPlayer
{
    @Inject(method = { "renderEntityName" },  at = { @At("HEAD") },  cancellable = true)
    public void renderEntityNameHook(final AbstractClientPlayer entityIn,  final double x,  final double y,  final double z,  final String name,  final double distanceSq,  final CallbackInfo info) {
        if (NameTags.getInstance().isOn()) {
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


    private float
            renderPitch,
            renderYaw,
            renderHeadYaw,
            prevRenderHeadYaw,
            lastRenderHeadYaw = 0,
            prevRenderPitch,
            lastRenderPitch = 0;


    @Inject(method = "doRender", at = @At("HEAD"))
    private void rotateBegin(AbstractClientPlayer entity, double x, double y, double z, float entityYaw, float partialTicks, CallbackInfo ci) {
        if (Thunderhack.moduleManager.getModuleByClass(PacketRender.class).isEnabled() && entity == Minecraft.getMinecraft().player) {
            prevRenderHeadYaw = entity.prevRotationYawHead;
            prevRenderPitch = entity.prevRotationPitch;
            renderPitch = entity.rotationPitch;
            renderYaw = entity.rotationYaw;
            renderHeadYaw = entity.rotationYawHead;
            entity.rotationPitch = PacketRender.getPitch();
            entity.prevRotationPitch = lastRenderPitch;
            entity.rotationYaw = PacketRender.getYaw();
            entity.rotationYawHead = PacketRender.getYaw();
            entity.prevRotationYawHead = lastRenderHeadYaw;
        }
    }

    @Inject(method = "doRender", at = @At("RETURN"))
    private void rotateEnd(AbstractClientPlayer entity, double x, double y, double z, float entityYaw, float partialTicks, CallbackInfo ci) {
        if (Thunderhack.moduleManager.getModuleByClass(PacketRender.class).isEnabled() && entity == Minecraft.getMinecraft().player) {
            lastRenderHeadYaw = entity.rotationYawHead;
            lastRenderPitch = entity.rotationPitch;
            entity.rotationPitch = renderPitch;
            entity.rotationYaw = renderYaw;
            entity.rotationYawHead = renderHeadYaw;
            entity.prevRotationYawHead = prevRenderHeadYaw;
            entity.prevRotationPitch = prevRenderPitch;
        }
    }

/*


    @Redirect(method = "renderRightArm", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;color(FFF)V"))
    public void renderRightArmHook(float colorRed, float colorGreen, float colorBlue) {
        if (HandChams.getInstance().isEnabled() && HandChams.getInstance().Mode.getValue() == HandChams.ChamsMode.Gradient) {
            GlStateManager.color(1.0f, 1.0f, 1.0f, HandChams.getInstance().coloralpha.getValue() / 255.0f);

        }
    }

 */

    /*
    @Inject(method = "doRender", at = @At("HEAD"))
    public void doRender(AbstractClientPlayer entity, double x, double y, double z, float entityYaw, float partialTicks, CallbackInfo ci) {
        if(EzingKids.INSTANCE.isEnabled()) {

           // GlStateManager.pushMatrix();
            GlStateManager.scale((float) EzingKids.INSTANCE.scale.getValue(), (float) EzingKids.INSTANCE.scale.getValue(), (float) EzingKids.INSTANCE.scale.getValue());
           // doRender(entity, x, y, z, entityYaw, partialTicks, ci);
           // GlStateManager.scale(1.0f / (float)CrystalModifier.INSTANCE.scale.getValue(),  1.0f / (float)CrystalModifier.INSTANCE.scale.getValue(),  1.0f / (float)CrystalModifier.INSTANCE.scale.getValue());
          //  GlStateManager.popMatrix();
          //  model.render(entity,  limbSwing,  limbSwingAmount,  ageInTicks,  netHeadYaw,  headPitch,  scale);

           // GlStateManager.scale(1.0f / (float) EzingKids.INSTANCE.scale.getValue(), 1.0f / (float) EzingKids.INSTANCE.scale.getValue(), 1.0f / (float) EzingKids.INSTANCE.scale.getValue());

        }
    }


     */

    @Inject(method = "renderLivingAt", at = @At("HEAD"))
    protected void renderLivingAt(AbstractClientPlayer entityLivingBaseIn, double x, double y, double z, CallbackInfo callbackInfo) {
      //  if (Thunderhack.moduleManager.getModuleByClass(EzingKids.class).isOn() && entityLivingBaseIn.equals(Minecraft.getMinecraft().player)) {
        if (Thunderhack.moduleManager.getModuleByClass(EzingKids.class).isOn() ) {
            if(EzingKids.INSTANCE.onlyme.getValue() && !(entityLivingBaseIn.equals(Minecraft.getMinecraft().player)) )
                return;
            GlStateManager.scale(EzingKids.INSTANCE.scale.getValue(), EzingKids.INSTANCE.scale.getValue(), EzingKids.INSTANCE.scale.getValue());
            GlStateManager.translate(0f,EzingKids.INSTANCE.translatey.getValue(),0f);
        }
    }

/*
    @Redirect(method = "renderLeftArm", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;color(FFF)V"))
    public void renderLeftArmHook(float colorRed, float colorGreen, float colorBlue) {
        if (HandChams.getInstance().isEnabled() && HandChams.getInstance().Mode.getValue() == HandChams.ChamsMode.Gradient) {
            GlStateManager.color(1.0f, 1.0f, 1.0f, HandChams.getInstance().coloralpha.getValue() / 255.0f);
        }
    }

 */

    private final ResourceLocation amogus = new ResourceLocation("textures/amogus.png");
    private final ResourceLocation demon = new ResourceLocation("textures/demon.png");
    private final ResourceLocation rabbit = new ResourceLocation("textures/rabbit.png");
    private final ResourceLocation fred = new ResourceLocation("textures/freddy.png");


    /**
     * @hzchtoeto
     */



    @Overwrite
    public ResourceLocation getEntityTexture(AbstractClientPlayer entity){

        if(Thunderhack.moduleManager.getModuleByClass(Models.class).isEnabled() && (!Thunderhack.moduleManager.getModuleByClass(Models.class).onlySelf.getValue() || entity == Minecraft.getMinecraft().player || Thunderhack.friendManager.isFriend(entity.getName()) && Thunderhack.moduleManager.getModuleByClass(Models.class).friends.getValue())){
            if (Thunderhack.moduleManager.getModuleByClass(Models.class).Mode.getValue() == Models.mode.Amogus) {
                return amogus;

                //return new ResourceLocation("assets/minecraft/textures/amogus.png");
            }

            if (Thunderhack.moduleManager.getModuleByClass(Models.class).Mode.getValue() == Models.mode.Rabbit) {
                return rabbit;
            }
            if (Thunderhack.moduleManager.getModuleByClass(Models.class).Mode.getValue() == Models.mode.Freddy) {
                return fred;
            }
        } else {
            if (ChangeSkinCommand.getInstance().changedplayers.contains(entity.getName())) {
                GL11.glColor4f(1f, 1f, 1f, 1f);
                return PNGtoResourceLocation.getTexture3(entity.getName(), "png");
            } else {
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                return entity.getLocationSkin();
            }
        }
        return entity.getLocationSkin();
    }

}