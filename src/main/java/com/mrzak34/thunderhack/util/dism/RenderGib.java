package com.mrzak34.thunderhack.util.dism;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.modules.misc.Dismemberment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.lang.reflect.Method;


public class RenderGib extends Render<EntityGib>
{
    public ModelGib modelGib;

    public RenderGib(RenderManager manager)
    {
        super(manager);
        modelGib = new ModelGib();
    }

    @Override
    public ResourceLocation getEntityTexture(EntityGib gib)
    {
        if(gib.parent instanceof EntityPlayer) {
            return ((AbstractClientPlayer) gib.parent).getLocationSkin();
        } else {
            Render render = Minecraft.getMinecraft().getRenderManager().getEntityRenderObject(gib.parent);
            return getEntityTexture(render, render.getClass(), gib.parent);
        }
    }

    @SideOnly(Side.CLIENT)
    @Nullable
    public static <T extends Render<V>, V extends Entity> ResourceLocation getEntityTexture(T rend, Class clz, V ent) {
        try {
            Method m = clz.getDeclaredMethod("getEntityTexture", Entity.class);
            m.setAccessible(true);
            return (ResourceLocation)m.invoke(rend, ent);
        } catch (NoSuchMethodException var4) {
            if (clz != Render.class) {
                return getEntityTexture(rend, clz.getSuperclass(), ent);
            }
        } catch (Exception var5) {
            var5.printStackTrace();
        }

        return null;
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