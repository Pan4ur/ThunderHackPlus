package com.mrzak34.thunderhack.modules.misc;

import com.mrzak34.thunderhack.events.Render3DEvent;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.util.Random;

public class ItemPhysics extends Module {


    public Setting<Float> rotatespeed = register(new Setting("RotateSpeed", 1.0f, 0.1f, 5.0f));//(antiCheat);


    public static long Field1898;
    private static double Field1899;
    private static Random Field1900 = new Random();



    public ItemPhysics() {
        super("ItemPhysics", "описание",Category.RENDER);
    }


    public void Method2279(Entity entity, double d, double d2, double d3) {
        EntityItem entityItem;
        ItemStack itemStack;
        Field1899 = (double)(System.nanoTime() - Field1898) / 2500000.0 * (double) rotatespeed.getValue();
        if (!ItemPhysics.mc.inGameHasFocus) {
            Field1899 = 0.0;
        }
        int n = (itemStack = (entityItem = (EntityItem)entity).getItem()) != null && itemStack.getItem() != null ? Item.getIdFromItem((Item)itemStack.getItem()) + itemStack.getMetadata() : 187;
        Field1900.setSeed(n);
        mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
        GlStateManager.enableRescaleNormal();
        GlStateManager.alphaFunc((int)516, (float)0.1f);
        GlStateManager.enableBlend();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.tryBlendFuncSeparate((int)770, (int)771, (int)1, (int)0);
        GlStateManager.pushMatrix();
        IBakedModel iBakedModel = mc.getRenderItem().getItemModelMesher().getItemModel(itemStack);
        boolean bl = iBakedModel.isGui3d();
        boolean bl2 = iBakedModel.isGui3d();
        int n2 = ItemPhysics.Method2280(itemStack);
        GlStateManager.translate((float)((float)d), (float)((float)d2), (float)((float)d3));
        if (iBakedModel.isGui3d()) {
            GlStateManager.scale((float)0.5f, (float)0.5f, (float)0.5f);
        }
        GL11.glRotatef((float)90.0f, (float)1.0f, (float)0.0f, (float)0.0f);
        GL11.glRotatef((float)entityItem.rotationYaw, (float)0.0f, (float)0.0f, (float)1.0f);
        GlStateManager.translate((double)0.0, (double)0.0, (double)(bl2 ? -0.08 : -0.04));
        if (bl2 || ItemPhysics.mc.getRenderManager().options != null) {
            double d4;
            if (bl2) {
                if (!entityItem.onGround) {
                    d4 = Field1899 * 2.0;
                    entityItem.rotationPitch = (float)((double)entityItem.rotationPitch + d4);
                }
            } else if (!(Double.isNaN(entityItem.posX) || Double.isNaN(entityItem.posY) || Double.isNaN(entityItem.posZ) || entityItem.world == null)) {
                if (entityItem.onGround) {
                    entityItem.rotationPitch = 0.0f;
                } else {
                    d4 = Field1899 * 2.0;
                    entityItem.rotationPitch = (float)((double)entityItem.rotationPitch + d4);
                }
            }
            GlStateManager.rotate((float)entityItem.rotationPitch, (float)1.0f, (float)0.0f, (float)0.0f);
        }
        GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        for (int i = 0; i < n2; ++i) {
            GlStateManager.pushMatrix();
            if (bl) {
                if (i > 0) {
                    float f = (Field1900.nextFloat() * 2.0f - 1.0f) * 0.15f;
                    float f2 = (Field1900.nextFloat() * 2.0f - 1.0f) * 0.15f;
                    float f3 = (Field1900.nextFloat() * 2.0f - 1.0f) * 0.15f;
                    GlStateManager.translate((float)f, (float)f2, (float)f3);
                }
                mc.getRenderItem().renderItem(itemStack, iBakedModel);
                GlStateManager.popMatrix();
                continue;
            }
            mc.getRenderItem().renderItem(itemStack, iBakedModel);
            GlStateManager.popMatrix();
            GlStateManager.translate((float)0.0f, (float)0.0f, (float)0.05375f);
        }
        GlStateManager.popMatrix();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableBlend();
        mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
    }

    private static int Method2280(ItemStack itemStack) {
        int n = 1;
        if (itemStack.stackSize > 48) {
            n = 5;
        } else if (itemStack.stackSize > 32) {
            n = 4;
        } else if (itemStack.stackSize > 16) {
            n = 3;
        } else if (itemStack.stackSize > 1) {
            n = 2;
        }
        return n;
    }

    @SubscribeEvent
    public void onRender3D(Render3DEvent e) {
        Field1898 = System.nanoTime();
    }

}
