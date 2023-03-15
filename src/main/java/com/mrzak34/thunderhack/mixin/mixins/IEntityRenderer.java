package com.mrzak34.thunderhack.mixin.mixins;

import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = {EntityRenderer.class})
public interface IEntityRenderer {
    @Invoker(value = "orientCamera")
    void orientCam(float var1);

    @Invoker(value = "applyBobbing")
    void viewBob(float var1);

    @Accessor(value = "lightmapColors")
    int[] getLightmapColors();

    @Accessor(value = "lightmapTexture")
    DynamicTexture getLightmapTexture();

    @Accessor(value = "torchFlickerX")
    float getTorchFlickerX();

    @Accessor(value = "bossColorModifier")
    float getBossColorModifier();

    @Accessor(value = "bossColorModifierPrev")
    float getBossColorModifierPrev();

    @Invoker(value = "getNightVisionBrightness")
    float invokeGetNightVisionBrightness(EntityLivingBase var1, float var2);

    @Invoker(value = "setupCameraTransform")
    void invokeSetupCameraTransform(float var1, int var2);


    @Accessor("rendererUpdateCount")
    int getRendererUpdateCount();

    @Accessor("rainXCoords")
    float[] getRainXCoords();

    @Accessor("rainYCoords")
    float[] getRainYCoords();


}