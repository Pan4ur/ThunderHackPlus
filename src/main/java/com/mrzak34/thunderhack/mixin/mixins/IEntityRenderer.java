package com.mrzak34.thunderhack.mixin.mixins;

import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value={EntityRenderer.class})
public interface IEntityRenderer {
    @Invoker(value="orientCamera")
    public void orientCam(float var1);

    @Invoker(value="applyBobbing")
    public void viewBob(float var1);

    @Accessor(value="lightmapColors")
    public int[] getLightmapColors();

    @Accessor(value="lightmapTexture")
    public DynamicTexture getLightmapTexture();

    @Accessor(value="torchFlickerX")
    public float getTorchFlickerX();

    @Accessor(value="bossColorModifier")
    public float getBossColorModifier();

    @Accessor(value="bossColorModifierPrev")
    public float getBossColorModifierPrev();

    @Invoker(value="getNightVisionBrightness")
    public float invokeGetNightVisionBrightness(EntityLivingBase var1, float var2);

    @Invoker(value="setupCameraTransform")
    public void invokeSetupCameraTransform(float var1, int var2);


    @Accessor("rendererUpdateCount")
    int getRendererUpdateCount();

    @Accessor("rainXCoords")
    float[] getRainXCoords();

    @Accessor("rainYCoords")
    float[] getRainYCoords();



}