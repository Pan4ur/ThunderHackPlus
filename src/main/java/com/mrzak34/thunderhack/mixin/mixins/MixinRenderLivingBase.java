package com.mrzak34.thunderhack.mixin.mixins;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.modules.funnygame.AntiTittle;
import com.mrzak34.thunderhack.modules.render.EzingKids;
import com.mrzak34.thunderhack.modules.render.NoRender;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({RenderLivingBase.class})
public abstract class MixinRenderLivingBase<T extends EntityLivingBase> extends Render<T> {


    protected MixinRenderLivingBase(RenderManager renderManager) {
        super(renderManager);
    }

    @Shadow
    protected ModelBase mainModel;

    @Inject(method = "doRender", at = @At("HEAD"), cancellable = true)
    private <T extends EntityLivingBase> void injectChamsPre(T entity, double x, double y, double z, float entityYaw, float partialTicks, CallbackInfo callbackInfo) {

        if ((entity instanceof EntityArmorStand) && ((NoRender.getInstance().isOn() && NoRender.getInstance().noarmorstands.getValue()) || ( Thunderhack.moduleManager.getModuleByClass(AntiTittle.class).isOn() && Thunderhack.moduleManager.getModuleByClass(AntiTittle.class).armorstands.getValue()))) {
            callbackInfo.cancel();
        }
    }

    @Inject(method = "interpolateRotation", at = @At("HEAD"))
    protected void interpolateRotation(float prevYawOffset, float yawOffset, float partialTicks, CallbackInfoReturnable<Float> cir) {
       if(Thunderhack.moduleManager.getModuleByClass(EzingKids.class).isOn() ){
           mainModel.isChild = true;
       }
    }


}
