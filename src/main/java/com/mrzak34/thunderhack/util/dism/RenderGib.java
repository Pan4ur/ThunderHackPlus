package com.mrzak34.thunderhack.util.dism;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.modules.misc.Dismemberment;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;


public class RenderGib extends Render<EntityGib>
{
    public ModelGib modelGib;

    public RenderGib(RenderManager manager)
    {
        super(manager);
        modelGib = new ModelGib();
    }


    private static final ResourceLocation zombieTexture = new ResourceLocation("textures/entity/zombie/zombie.png");
    private static final ResourceLocation skeletonTexture = new ResourceLocation("textures/entity/skeleton/skeleton.png");
    private static final ResourceLocation creeperTexture = new ResourceLocation("textures/entity/creeper/creeper.png");

    @Override
    public ResourceLocation getEntityTexture(EntityGib gib)
    {
        if(gib.parent instanceof EntityPlayer) {
            return ((AbstractClientPlayer) gib.parent).getLocationSkin();
        } else {
            if(gib.parent instanceof EntityZombie){
                return zombieTexture;
            }
            if(gib.parent instanceof EntitySkeleton){
                return skeletonTexture;
            }
            if(gib.parent instanceof EntityCreeper){
                return creeperTexture;
            }
            return zombieTexture;
        }
    }




    @Override
    public void doRender(EntityGib gib, double par2, double par4, double par6, float par8, float par9)
    {
        GlStateManager.disableCull();
        GlStateManager.pushMatrix();
         bindEntityTexture(gib);
      //  bindTexture();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.color(1.0F, 1.0F, 1.0F, MathHelper.clamp(gib.groundTime >= Thunderhack.moduleManager.getModuleByClass(Dismemberment.class).gibGroundTime.getValue() ? 1.0F - (gib.groundTime - Thunderhack.moduleManager.getModuleByClass(Dismemberment.class).gibGroundTime.getValue() + par9) / 20F : 1.0F, 0F, 1F));
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.003921569F);

        GlStateManager.translate(par2, par4, par6);

        GlStateManager.translate(0.0F, gib.type == 0 ? 4F / 16F : gib.type <= 2 && gib.parent instanceof EntitySkeleton ? 1F / 16F : 2F / 16F, 0.0F);

        GlStateManager.rotate(interpolateRotation(gib.prevRotationYaw, gib.rotationYaw, par9), 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(interpolateRotation(gib.prevRotationPitch, gib.rotationPitch, par9), -1.0F, 0.0F, 0.0F);

        GlStateManager.translate(0.0F, 24F / 16F - gib.height * 0.5F, 0.0F);

        GlStateManager.scale(-1.0F, -1.0F, 1.0F);

        modelGib.render(gib, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);

        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
        GlStateManager.enableCull();
    }
    public static float interpolateRotation(float par1, float par2, float par3)
    {
        float f3;

        for (f3 = par2 - par1; f3 < -180.0F; f3 += 360.0F)
        {
            ;
        }

        while (f3 >= 180.0F)
        {
            f3 -= 360.0F;
        }

        return par1 + par3 * f3;
    }

}