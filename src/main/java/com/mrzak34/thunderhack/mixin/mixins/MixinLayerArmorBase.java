package com.mrzak34.thunderhack.mixin.mixins;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.modules.render.Models;
import com.mrzak34.thunderhack.modules.render.NoRender;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.mrzak34.thunderhack.util.Util.mc;

@Mixin({LayerArmorBase.class})
public class MixinLayerArmorBase {
    @Inject(method = {"doRenderLayer"}, at = {@At("HEAD")}, cancellable = true)
    public void doRenderLayer(final EntityLivingBase entitylivingbaseIn, final float limbSwing, final float limbSwingAmount, final float partialTicks, final float ageInTicks, final float netHeadYaw, final float headPitch, final float scale, final CallbackInfo ci) {
        if (NoRender.getInstance().isEnabled() && NoRender.getInstance().noArmor.getValue() == NoRender.NoArmor.ALL) {
            ci.cancel();
        }
        if (Thunderhack.moduleManager == null) {
            return;
        }
        if (Thunderhack.friendManager == null) {
            return;
        }
        if (Thunderhack.moduleManager.getModuleByClass(Models.class).isOn() && Thunderhack.moduleManager.getModuleByClass(Models.class).onlySelf.getValue() && entitylivingbaseIn == mc.player) {
            ci.cancel();
        } else if (Thunderhack.moduleManager.getModuleByClass(Models.class).isOn() && !Thunderhack.moduleManager.getModuleByClass(Models.class).onlySelf.getValue()) {
            ci.cancel();
        }
    }

    @Inject(method = {"renderArmorLayer"}, at = {@At("HEAD")}, cancellable = true)
    public void renderArmorLayer(final EntityLivingBase entityLivingBaseIn, final float limbSwing, final float limbSwingAmount, final float partialTicks, final float ageInTicks, final float netHeadYaw, final float headPitch, final float scale, final EntityEquipmentSlot slotIn, final CallbackInfo ci) {
        if (NoRender.getInstance().isEnabled() && NoRender.getInstance().noArmor.getValue() == NoRender.NoArmor.HELMET && slotIn == EntityEquipmentSlot.HEAD) {
            ci.cancel();
        }
    }

}