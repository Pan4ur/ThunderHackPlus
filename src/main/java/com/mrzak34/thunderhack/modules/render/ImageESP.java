package com.mrzak34.thunderhack.modules.render;

import com.mrzak34.thunderhack.events.Render2DEvent;
import com.mrzak34.thunderhack.mixin.mixins.IEntityRenderer;
import com.mrzak34.thunderhack.mixin.mixins.IRenderManager;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.ColorSetting;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.PNGtoResourceLocation;
import com.mrzak34.thunderhack.util.Util;
import com.mrzak34.thunderhack.util.render.PaletteHelper;
import com.mrzak34.thunderhack.util.render.RectHelper;
import com.mrzak34.thunderhack.util.render.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;
import java.awt.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static com.mrzak34.thunderhack.modules.player.ElytraSwap.drawCompleteImage;


public class ImageESP extends Module {
    private final int black = Color.BLACK.getRGB();
    private final Setting<ColorSetting> cc = this.register(new Setting<>("CustomColor", new ColorSetting(0x8800FF00)));


    // public Setting <Integer> cca = this.register ( new Setting <> ( "CustomA", 255, 0, 255));
    public Setting<Float> scalefactor = register(new Setting("Raytrace", Float.valueOf(2.0F), Float.valueOf(0.1F), Float.valueOf(4.0F)));
    public Setting<Boolean> wtf = register(new Setting("Not done", false));
    public Setting<Float> scalefactor1 = register(new Setting("X", Float.valueOf(0.0F), Float.valueOf(-6.0F), Float.valueOf(6.0F)));
    public Setting<Float> scalefactor2 = register(new Setting("Y", Float.valueOf(0.0F), Float.valueOf(-6.0F), Float.valueOf(6.0F)));
    ResourceLocation customImg;
    ResourceLocation customImg2;
    ResourceLocation customImg3;
    private final Setting<mode2> Mode2 = register(new Setting("Color Mode", mode2.Rainbow));
    private final Setting<mode3> Mode3 = register(new Setting("Color Mode", mode3.CAT));


    public ImageESP() {
        super("ImageESP", "ImageESP ImageESP.", Category.RENDER);
    }

    @SubscribeEvent
    public void onRender2D(Render2DEvent event) {
        Float scaleFactor = scalefactor.getValue();
        double scaling = scaleFactor / Math.pow(scaleFactor, 2);
        GlStateManager.scale(scaling, scaling, scaling);
        Color c = cc.getValue().getColorObject();
        int color = 0;

        if (Mode2.getValue() == mode2.Custom) {
            color = c.getRGB();
        }
        if (Mode2.getValue() == mode2.Astolfo) {
            color = PaletteHelper.astolfo(false, 1).getRGB();
        }
        if (Mode2.getValue() == mode2.Rainbow) {
            color = PaletteHelper.rainbow(300, 1, 1).getRGB();
        }

        float scale = 1;

        for (Entity entity : mc.world.loadedEntityList) {
            if (isValid(entity) && RenderHelper.isInViewFrustum(entity)) {
                EntityPlayer entityPlayer = (EntityPlayer) entity;
                if (entityPlayer != ImageESP.mc.player) {
                    double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * mc.getRenderPartialTicks();
                    double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * mc.getRenderPartialTicks();
                    double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * mc.getRenderPartialTicks();
                    AxisAlignedBB axisAlignedBB2 = entity.getEntityBoundingBox();
                    AxisAlignedBB axisAlignedBB = new AxisAlignedBB(axisAlignedBB2.minX - entity.posX + x - 0.05, axisAlignedBB2.minY - entity.posY + y, axisAlignedBB2.minZ - entity.posZ + z - 0.05, axisAlignedBB2.maxX - entity.posX + x + 0.05, axisAlignedBB2.maxY - entity.posY + y + 0.15, axisAlignedBB2.maxZ - entity.posZ + z + 0.05);
                    Vector3d[] vectors = new Vector3d[]{new Vector3d(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ), new Vector3d(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ), new Vector3d(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ), new Vector3d(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ), new Vector3d(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ), new Vector3d(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ), new Vector3d(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ), new Vector3d(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ)};
                    ((IEntityRenderer)mc.entityRenderer).invokeSetupCameraTransform(event.getPartialTicks(), 0);

                    Vector4d position = null;
                    for (Vector3d vector : vectors) {
                        vector = project2D(scaleFactor, vector.x - ((IRenderManager)Util.mc.getRenderManager()).getRenderPosX(), vector.y - ((IRenderManager)Util.mc.getRenderManager()).getRenderPosY(), vector.z - ((IRenderManager)Util.mc.getRenderManager()).getRenderPosZ());
                        if (vector != null && vector.z > 0 && vector.z < 1) {
                            if (position == null)
                                position = new Vector4d(vector.x, vector.y, vector.z, 0);
                            position.x = Math.min(vector.x, position.x);
                            position.y = Math.min(vector.y, position.y);
                            position.z = Math.max(vector.x, position.z);
                            position.w = Math.max(vector.y, position.w);
                        }
                    }

                    if (position != null) {


                        mc.entityRenderer.setupOverlayRendering();
                        double posX = position.x;
                        double posY = position.y;
                        double endPosX = position.z;
                        double endPosY = position.w;

                        if (wtf.getValue()) {
                            RectHelper.drawRect(posX - 1F, posY, posX + 0.5, endPosY + 0.5, black);
                            RectHelper.drawRect(posX - 1F, posY - 0.5, endPosX + 0.5, posY + 0.5 + 0.5, black);
                            RectHelper.drawRect(endPosX - 0.5 - 0.5, posY, endPosX + 0.5, endPosY + 0.5, black);
                            RectHelper.drawRect(posX - 1, endPosY - 0.5 - 0.5, endPosX + 0.5, endPosY + 0.5, black);
                            RectHelper.drawRect(posX - 0.5, posY, posX + 0.5 - 0.5, endPosY, color);
                            RectHelper.drawRect(posX, endPosY - 0.5, endPosX, endPosY, color);
                            RectHelper.drawRect(posX - 0.5, posY, endPosX, posY + 0.5, color);
                            RectHelper.drawRect(endPosX - 0.5, posY, endPosX, endPosY, color);
                        }
                        RectHelper.drawRect(posX, posY, posX, posY, color);

                        if (Mode3.getValue() == mode3.CAT) {
                            Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("textures/image9.png"));
                        }
                        if (Mode3.getValue() == mode3.MrZak) {
                            Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("textures/image10.png"));
                        }
                        if (Mode3.getValue() == mode3.Custom1) {
                            if (customImg == null) {
                                if (PNGtoResourceLocation.getCustomImg("esp1", "png") != null) {
                                    customImg = PNGtoResourceLocation.getCustomImg("esp1", "png");
                                }
                                return;
                            }
                            Util.mc.getTextureManager().bindTexture(this.customImg);
                        }
                        if (Mode3.getValue() == mode3.Custom2) {
                            if (customImg2 == null) {
                                if (PNGtoResourceLocation.getCustomImg("esp2", "png") != null) {
                                    customImg2 = PNGtoResourceLocation.getCustomImg("esp2", "png");
                                }
                                return;
                            }
                            Util.mc.getTextureManager().bindTexture(this.customImg2);
                        }
                        if (Mode3.getValue() == mode3.Custom3) {
                            if (customImg3 == null) {
                                if (PNGtoResourceLocation.getCustomImg("esp3", "png") != null) {
                                    customImg3 = PNGtoResourceLocation.getCustomImg("esp3", "png");
                                }
                                return;
                            }
                            Util.mc.getTextureManager().bindTexture(this.customImg3);
                        }
                        drawCompleteImage((float) posX + scalefactor1.getValue(), (float) posY + scalefactor2.getValue(), (int) ((int) endPosX - posX), (int) ((int) endPosY - posY));
                    }
                }
            }
        }
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        mc.entityRenderer.setupOverlayRendering();
    }

    private Vector3d project2D(Float scaleFactor, double x, double y, double z) {
        float xPos = (float) x;
        float yPos = (float) y;
        float zPos = (float) z;
        IntBuffer viewport = GLAllocation.createDirectIntBuffer(16);
        FloatBuffer modelview = GLAllocation.createDirectFloatBuffer(16);
        FloatBuffer projection = GLAllocation.createDirectFloatBuffer(16);
        FloatBuffer vector = GLAllocation.createDirectFloatBuffer(4);
        GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, modelview);
        GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, projection);
        GL11.glGetInteger(GL11.GL_VIEWPORT, viewport);
        if (GLU.gluProject(xPos, yPos, zPos, modelview, projection, viewport, vector))
            return new Vector3d((vector.get(0) / scaleFactor), ((Display.getHeight() - vector.get(1)) / scaleFactor), vector.get(2));
        return null;
    }

    private boolean isValid(Entity entity) {
        return entity instanceof EntityPlayer;
    }


    public enum mode2 {
        Custom, Rainbow, Astolfo
    }///What the FUCK!!!! the fuck

    public enum mode3 {
        CAT, MrZak, Custom1, Custom2, Custom3
    }

}
